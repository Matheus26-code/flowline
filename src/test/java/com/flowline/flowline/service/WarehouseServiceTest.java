package com.flowline.flowline.service;

import com.flowline.flowline.dto.PageResponseDTO;
import com.flowline.flowline.dto.WarehouseRequestDTO;
import com.flowline.flowline.dto.WarehouseResponseDTO;
import com.flowline.flowline.exception.ForbiddenAccessException;
import com.flowline.flowline.exception.ResourceNotFoundException;
import com.flowline.flowline.model.User;
import com.flowline.flowline.model.UserRole;
import com.flowline.flowline.model.Warehouse;
import com.flowline.flowline.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WarehouseServiceTest {

    @Mock
    WarehouseRepository warehouseRepository;

    @InjectMocks
    WarehouseService warehouseService;

    private User user;
    private Warehouse warehouse;
    private WarehouseRequestDTO warehouseRequestDTO;

    @BeforeEach
    public void setUp() {
        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Empresa teste mock");
        warehouse.setDescription("Testando Mock");
        warehouse.setStreet("rua teste");
        warehouse.setCity("Chachoeira");
        warehouse.setState("RS");
        warehouse.setZipCode("000000");

        user = new User();
        user.setId(1L);
        user.setRole(UserRole.ADMIN);
        user.setWarehouse(warehouse);

        warehouseRequestDTO = new WarehouseRequestDTO(
                "Empresa teste mock", "Testando Mock",
                "rua teste", "Chachoeira", "RS", "000000");

    }

    @Test
    public void mustSuccessfullyCreateWarehouse() {

        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);
        WarehouseResponseDTO result = warehouseService.create(warehouseRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Empresa teste mock", result.name());
        assertEquals("Testando Mock", result.description());
    }

    @Test
    public void mustFindWarehouseWithIdAsAdmin() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        WarehouseResponseDTO result = warehouseService.findWareById(1L, user);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    public void mustFindWarehouseWithIdAsManage() {
        User manage = new User();
        manage.setId(1L);
        manage.setRole(UserRole.MANAGE);
        manage.setWarehouse(warehouse);

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        WarehouseResponseDTO result =  warehouseService.findWareById(1L, manage);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    public void mustThrowForbiddenWhenManageAccessesOtherWarehouse() {
        Warehouse otherWarehouse = new Warehouse();
        otherWarehouse.setId(99L);

        User manage = new User();
        manage.setRole(UserRole.MANAGE);
        manage.setWarehouse(otherWarehouse);

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        assertThrows(
                ForbiddenAccessException.class,
                () -> warehouseService.findWareById(1L, manage));
    }

    @Test
    public void mustReturnErrorWithWarehouseNotFound() {

        when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> warehouseService.findWareById(99L, user)
        );
    }

    @Test
    public void mustSuccessfullyUpdateWarehouse() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        WarehouseResponseDTO result = warehouseService.updateWarehouse(1L, warehouseRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(warehouseRequestDTO.name(), result.name());
        assertEquals(warehouseRequestDTO.description(), result.description());
    }

    @Test
    public void mustFindAllWarehouses() {
        Page<Warehouse> warehousePage = new PageImpl<>(Collections.singletonList(warehouse));
        when(warehouseRepository.findAll(any(Pageable.class))).thenReturn(warehousePage);

        PageResponseDTO<WarehouseResponseDTO> result = warehouseService.findAll(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.content().size());
    }

    @Test
    public void mustDeleteWarehouseById() {
        when(warehouseRepository.existsById(1L)).thenReturn(true);
        warehouseService.deleteById(1L);
        verify(warehouseRepository).deleteById(1L);
    }

}
