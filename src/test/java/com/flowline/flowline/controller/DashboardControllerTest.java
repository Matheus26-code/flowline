package com.flowline.flowline.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class DashboardControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
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
    public void mustGetDashboardAsAdmin() throws Exception {
        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orders").exists())
                .andExpect(jsonPath("$.totalProducts").exists())
                .andExpect(jsonPath("$.totalUsers").exists());
    }

    @Test
    public void mustReturn403WhenOperatorTriesToAccessDashboard() throws Exception {
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
        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isForbidden());
    }
}
