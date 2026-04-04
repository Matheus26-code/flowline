package com.flowline.flowline.service;

import com.flowline.flowline.dto.WarehouseRequestDTO;
import com.flowline.flowline.dto.WarehouseResponseDTO;
import com.flowline.flowline.model.Warehouse;
import com.flowline.flowline.repository.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WarehouseServiceTest {

    @Mock
    WarehouseRepository warehouseRepository;

    @InjectMocks
    WarehouseService warehouseService;

    @Test
    public void deveCriarWarehouseComSucesso() {
        WarehouseRequestDTO warehouseRequestDTO = new WarehouseRequestDTO(
                "Empresa teste mock", "Testando Mock",
                "rua teste", "Chachoeira", "RS", "000000");
        Warehouse warehouse = new Warehouse();
        warehouse.setName("Empresa teste mock");
        warehouse.setDescription("Testando Mock");
        warehouse.setStreet("rua teste");
        warehouse.setCity("Chachoeira");
        warehouse.setState("RS");
        warehouse.setZipCode("000000");

        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        WarehouseResponseDTO result = warehouseService.create(warehouseRequestDTO);

        assertNotNull(result);
        assertEquals("Empresa teste mock", warehouse.getName());
        assertEquals("Testando Mock", warehouse.getDescription());
    }
}
