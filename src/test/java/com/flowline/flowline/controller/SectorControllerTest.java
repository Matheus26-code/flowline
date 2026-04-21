package com.flowline.flowline.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowline.flowline.model.*;
import com.flowline.flowline.repository.SectorRepository;
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
public class SectorControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SectorRepository sectorRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ObjectMapper objectMapper;

    private Warehouse warehouse;
    private User user;
    private Sector sector;
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

        sector = new Sector();
        sector.setName("New Sector");
        sector.setDescription("Test description");
        sector.setBuilding("Test building");
        sector.setResponsible(user);
        sector.setWarehouse(warehouse);
        sectorRepository.save(sector);

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
    public void mustCreateSectorAsManage() throws Exception {
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

       mockMvc.perform(post("/api/sector")
               .header("Authorization", "Bearer " + manageToken)
               .contentType(APPLICATION_JSON)
               .content("""
                    {
                    "name": "New Sector",
                    "description": "Test description",
                    "building": "Test building",
                    "responsibleId": %d,
                    "warehouseId": %d
                    }
                    """.formatted(user.getId(), warehouse.getId())))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.name").value("New Sector"));
    }

    @Test
    public void mustReturn403WhenOperatorTriesToCreateSector() throws Exception {
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

        mockMvc.perform(post("/api/sector")
                .header("Authorization", "Bearer " + operatorToken)
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                    "name": "New Sector",
                    "description": "Test description",
                    "building": "Test building",
                    "responsibleId": %d,
                    "warehouseId": %d
                    }
                    """.formatted(user.getId(), warehouse.getId())))
                .andExpect(status().isForbidden());
    }

    @Test
    public void mustReadSectorWithIdAsAdmin() throws Exception {
        mockMvc.perform(get("/api/sector/" + sector.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    public void mustUpdateSectorAsManage() throws Exception {
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

        mockMvc.perform(put("/api/sector/" + sector.getId())
                .header("Authorization", "Bearer " + manageToken)
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                    "name": "New Sector2",
                    "description": "Test description2",
                    "building": "Test building2",
                    "responsibleId": %d,
                    "warehouseId": %d
                    }
                    """.formatted(user.getId(), warehouse.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Sector2"));
    }

    @Test
    public void mustDeleteSectorAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/sector/" + sector.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}
