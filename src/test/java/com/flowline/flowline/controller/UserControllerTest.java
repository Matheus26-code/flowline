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
public class UserControllerTest {

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
    public void mustCreateUserAsAdmin() throws Exception {
        mockMvc.perform(post("/api/user")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(APPLICATION_JSON)
                .content("""
                {
                "username": "admin",
                "email": "admin@test.com",
                "password": "password",
                "role": "ADMIN",
                "warehouseId": %d
                }
                """.formatted(warehouse.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    public void mustReturn403WhenOperatorTriesToCreateUser() throws Exception {
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

        mockMvc.perform(post("/api/user")
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(APPLICATION_JSON)
                        .content("""
                            {
                            "username": "admin",
                            "email": "admin@test.com",
                            "password": "password",
                            "role": "ADMIN",
                            "warehouseId": %d
                            }
                            """.formatted(warehouse.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    public void mustFindUserByIdAsManage() throws Exception {
        User manage = new User();
        manage.setUsername("manage");
        manage.setEmail("manage@test.com");
        manage.setPassword(passwordEncoder.encode("password"));
        manage.setRole(UserRole.MANAGE);
        manage.setWarehouse(warehouse);
        userRepository.save(manage);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                        {
                            "email": "manage@test.com",
                            "password": "password"}
                        """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String manageToken = objectMapper.readTree(response).get("token").asText();

        mockMvc.perform(get("/api/user/" + manage.getId())
                .header("Authorization", "Bearer " + manageToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("manage"))
                .andExpect(jsonPath("$.role").value("MANAGE"));
    }

    @Test
    public void mustReturn404WhenUserNotFound() throws Exception {
        mockMvc.perform(get("/api/user/9999")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void mustDeleteUserAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/user/" + user.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}
