package com.flowline.flowline.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowline.flowline.model.*;
import com.flowline.flowline.repository.*;
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
import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MovementOrderControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    SectorRepository sectorRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ObjectMapper objectMapper;

    private String adminToken;
    private Warehouse warehouse;
    private User user;
    private Product product;
    private MovementOrder movementOrder;
    private Sector sector;


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


        product = new Product();
        product.setName("Test product");
        product.setWeight(new BigDecimal("100"));
        product.setUnit("Unit test");
        product.setLocation("Location test");
        product.setWarehouse(warehouse);
        productRepository.save(product);

        sector = new Sector();
        sector.setName("New Sector");
        sector.setDescription("Test description");
        sector.setBuilding("Test building");
        sector.setResponsible(user);
        sector.setWarehouse(warehouse);
        sectorRepository.save(sector);

        movementOrder = new MovementOrder();
        movementOrder.setOriginSector(sector);
        movementOrder.setDestinationSector(sector);
        movementOrder.setUser(user);
        movementOrder.setProduct(product);
        movementOrder.setStatus(MovementStatus.PENDING);
        movementOrder.setQuantity(new BigDecimal(100));
        movementOrder.setCreatedAt(LocalDateTime.now());
        orderRepository.save(movementOrder);
    }

    @Test
    public void mustCreateOrderAsOperator() throws Exception {
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
                        {
                            "email": "operator@test.com",
                            "password": "password"
                        }
                        """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String operatorToken = objectMapper.readTree(response).get("token").asText();

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + operatorToken)
                .contentType(APPLICATION_JSON)
                .content(""" 
                {
                "originSectorId": %d,
                "destinationSectorId": %d,
                "userId": %d,
                "productId": %d,
                "quantity": 100
                }
                """.formatted(sector.getId(), sector.getId(), user.getId(), product.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void mustReturn403WhenOperatorTriesToDeleteOrder() throws Exception {
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
                        {
                            "email": "operator@test.com",
                            "password": "password"
                        }
                        """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String operatorToken = objectMapper.readTree(response).get("token").asText();
        mockMvc.perform(delete("/api/orders/" + movementOrder.getId())
                .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isForbidden());
    }

    @Test
    public void mustGetOrderByIdAsAssistant() throws Exception {
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

        mockMvc.perform(get("/api/orders/" + movementOrder.getId())
                .header("Authorization", "Bearer " + assistantToken))
                .andExpect(status().isOk());
    }

    @Test
    public void mustListAllOrdersAsOperator() throws Exception {
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
                        {
                            "email": "operator@test.com",
                            "password": "password"
                        }
                        """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String operatorToken = objectMapper.readTree(response).get("token").asText();
        mockMvc.perform(get("/api/orders")
                .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    public void mustUpdateOrderAsManage() throws Exception {
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
        mockMvc.perform(put("/api/orders/" + movementOrder.getId())
                .header("Authorization", "Bearer " + managerToken)
                .contentType(APPLICATION_JSON)
                .content(""" 
                {
                "originSectorId": %d,
                "destinationSectorId": %d,
                "userId": %d,
                "productId": %d,
                "quantity": 100
                }
                """.formatted(sector.getId(), sector.getId(), user.getId(), product.getId())))
                .andExpect(status().isOk());
    }
}
