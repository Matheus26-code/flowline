package com.flowline.flowline.controller;

import com.flowline.flowline.config.SecurityConfig;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    private Warehouse warehouse;
    private User user;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        warehouse.setName("Unit Test");
        warehouse.setDescription("Testing real warehouse");
        warehouse.setStreet("test street");
        warehouse.setCity("test city");
        warehouse.setState("test state");
        warehouse.setZipCode("8888888");
        warehouseRepository.save(warehouse);

        user = new User();
        user.setUsername("User test");
        user.setEmail("emailtest@gmail.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(UserRole.ADMIN);
        user.setWarehouse(warehouse);
        userRepository.save(user);

    }

    @Test
    void mustReturnTokenWhenCredentialsAreValid() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content("""
                {
                    "email": "emailtest@gmail.com",
                    "password": "password"
                }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void mustReturn403WhenPasswordIsWrong() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content("""
                {
                    "email": "emailtest@gmail.com",
                    "password": "wrongPassword"
                }
                """))
                .andExpect(status().isForbidden());

    }

    @Test
    void mustReturn403WhenUserNotFound() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                {
                    "email": "test@gmail.com",
                    "password": "password"
                }
                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void mustReturn400WhenBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                {
                
                }
                """))
                .andExpect(status().isBadRequest());
    }
}
