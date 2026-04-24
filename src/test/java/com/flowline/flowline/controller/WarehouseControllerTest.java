package com.flowline.flowline.controller;

import com.flowline.flowline.model.User;
import com.flowline.flowline.model.UserRole;
import com.flowline.flowline.model.Warehouse;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class WarehouseControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    ObjectMapper objectMapper;

    private String adminToken;
    private Warehouse warehouse;
    private User user;

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
    }

    @Test
    public void mustCreateWarehouseAsAdmin() throws Exception {
        mockMvc.perform(post("/api/warehouse")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(APPLICATION_JSON)
                .content("""
                {
                  "name": "New Warehouse",
                  "description": "Test description",
                  "street": "Test street",
                  "city": "Test city",
                  "state": "RS",
                  "zipCode": "12345678"
                 }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Warehouse"));
    }

    @Test
    public void mustReturn403WhenOperatorTriesToCreateWarehouse() throws Exception {
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

        mockMvc.perform(post("/api/warehouse")
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(APPLICATION_JSON)
                        .content("""
                        {
                            "name": "Unauthorized Warehouse",
                            "description": "Should not be created",
                            "street": "Test street",
                            "city": "Test city",
                            "state": "RS",
                            "zipCode": "12345678"
                        }
                    """))
                .andExpect(status().isForbidden());
    }

    @Test
    public void mustGetWarehouseByIdAsAdmin() throws Exception {
        mockMvc.perform(get("/api/warehouse/" + warehouse.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Warehouse"));
    }

    @Test
    public void mustReturn404WhenWarehouseNotFound() throws Exception {
        mockMvc.perform(get("/api/warehouse/999")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
