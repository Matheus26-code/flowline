package com.flowline.flowline.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowline.flowline.model.Product;
import com.flowline.flowline.model.User;
import com.flowline.flowline.model.UserRole;
import com.flowline.flowline.model.Warehouse;
import com.flowline.flowline.repository.ProductRepository;
import com.flowline.flowline.repository.UserRepository;
import com.flowline.flowline.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ObjectMapper objectMapper;

    private Warehouse warehouse;
    private User user;
    private Product product;
    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {

        warehouse = new Warehouse();
        warehouse.setName("New Warehouse");
        warehouse.setDescription("Test description");
        warehouse.setStreet("Test street");
        warehouse.setCity("Test city");
        warehouse.setState("RS");
        warehouse.setZipCode("12345678");
        warehouseRepository.save(warehouse);

        user = new User();
        user.setUsername("admin");
        user.setEmail("admin@test.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(UserRole.ADMIN);
        user.setWarehouse(warehouse);
        userRepository.save(user);

        product = new Product();
        product.setName("Test product");
        product.setWeight(new BigDecimal("100"));
        product.setUnit("Unit test");
        product.setLocation("Location test");
        product.setWarehouse(warehouse);
        productRepository.save(product);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                        {
                            "email": "admin@test.com",
                            "password": "password"}
                        """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        adminToken = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    public void mustCreateProductAsOperator() throws Exception {
        User operatorUser = new User();
        operatorUser.setUsername("operator");
        operatorUser.setEmail("operator@test.com");
        operatorUser.setPassword(passwordEncoder.encode("password"));
        operatorUser.setRole(UserRole.OPERATOR);
        operatorUser.setWarehouse(warehouse);
        userRepository.save(operatorUser);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                        {"email": "operator@test.com", "password": "password"}
                    """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String operatorToken = objectMapper.readTree(response).get("token").asText();

        mockMvc.perform(post("/api/product")
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(APPLICATION_JSON)
                        .content("""
                {
                "name": "Test product",
                "weight": 100,
                "unit": "Unit test",
                "location": "Location test",
                "warehouseId": %d
                }
                """.formatted(warehouse.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test product"))
                .andExpect(jsonPath("$.weight").value("100"));
    }

    @Test
    public void mustReadProductAsAssistantWithId() throws Exception {
        User assistantUser = new User();
        assistantUser.setUsername("assistant");
        assistantUser.setEmail("assistant@test.com");
        assistantUser.setPassword(passwordEncoder.encode("password"));
        assistantUser.setRole(UserRole.ASSISTANT);
        assistantUser.setWarehouse(warehouse);
        userRepository.save(assistantUser);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                        {"email": "assistant@test.com", "password": "password"}
                    """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String assistantToken = objectMapper.readTree(response).get("token").asText();

        mockMvc.perform(get("/api/product/" + product.getId())
                        .header("Authorization", "Bearer " + assistantToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.weight").value(100));
    }

    @Test
    public void mustListAllProductsAsOperator() throws Exception {
        User operatorUser = new User();
        operatorUser.setUsername("operator");
        operatorUser.setEmail("operator@test.com");
        operatorUser.setPassword(passwordEncoder.encode("password"));
        operatorUser.setRole(UserRole.OPERATOR);
        operatorUser.setWarehouse(warehouse);
        userRepository.save(operatorUser);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                        {"email": "operator@test.com", "password": "password"}
                    """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String operatorToken = objectMapper.readTree(response).get("token").asText();

        mockMvc.perform(get("/api/product")
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.totalElements").value(1));

    }

    @Test
    public void mustReturn403WhenOperatorTriesToDeleteProduct() throws Exception {
        User operatorUser = new User();
        operatorUser.setUsername("operator");
        operatorUser.setEmail("operator@test.com");
        operatorUser.setPassword(passwordEncoder.encode("password"));
        operatorUser.setRole(UserRole.OPERATOR);
        operatorUser.setWarehouse(warehouse);
        userRepository.save(operatorUser);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                        {"email": "operator@test.com", "password": "password"}
                    """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String operatorToken = objectMapper.readTree(response).get("token").asText();

        mockMvc.perform(delete("/api/product/" + product.getId())
                        .header("Authorization", "Bearer " + operatorToken))
                    .andExpect(status().isForbidden());
    }

    @Test
    public void mustUpdateProductAsManager() throws Exception {
        User manager = new User();
        manager.setUsername("manager");
        manager.setEmail("manager@test.com");
        manager.setPassword(passwordEncoder.encode("password"));
        manager.setRole(UserRole.MANAGE);
        manager.setWarehouse(warehouse);
        userRepository.save(manager);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                        {"email": "manager@test.com", "password": "password"}
                    """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String managerToken = objectMapper.readTree(response).get("token").asText();

        mockMvc.perform(put("/api/product/" + product.getId())
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(APPLICATION_JSON)
                        .content("""
                        {
                            "name": "Updated product",
                            "weight": 200,
                            "unit": "Unit test",
                            "location": "Location test",
                            "warehouseId": %d
                        }
                    """.formatted(warehouse.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated product"))
                .andExpect(jsonPath("$.weight").value(200));
    }
}
