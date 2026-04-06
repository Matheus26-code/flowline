package com.flowline.flowline.service;

import com.flowline.flowline.dto.PageResponseDTO;
import com.flowline.flowline.dto.SectorRequestDTO;
import com.flowline.flowline.dto.SectorResponseDTO;
import com.flowline.flowline.exception.ResourceNotFoundException;
import com.flowline.flowline.model.Sector;
import com.flowline.flowline.model.User;
import com.flowline.flowline.model.Warehouse;
import com.flowline.flowline.repository.SectorRepository;
import com.flowline.flowline.repository.UserRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SectorServiceTest {

    private Warehouse warehouse;
    private User user;
    private Sector sector;
    private SectorRequestDTO sectorRequestDTO;

    @Mock
    WarehouseRepository warehouseRepository;

    @Mock
    SectorRepository sectorRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    SectorService sectorService;

    @BeforeEach
    public void setUpMocks() {
        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Warehouse mock");

        user = new User();
        user.setId(1L);
        user.setUsername("test");

        sector = new Sector();
        sector.setName("Sector mock");
        sector.setBuilding("Sector building");
        sector.setResponsible(user);
        sector.setWarehouse(warehouse);

        sectorRequestDTO = new SectorRequestDTO(
                "Sector mock", "Sector description", 1L, 1L);
    }

    @Test
    public void mustCreateSuccessfullySector() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sectorRepository.save(any(Sector.class))).thenReturn(sector);

        SectorResponseDTO result = sectorService.createSector(sectorRequestDTO);

        assertNotNull(result);
        assertEquals("Sector mock", result.name());
        assertEquals("Sector building", result.building());
    }

    @Test
    public void mustFindSectorById() {
        when(sectorRepository.findById(1L)).thenReturn(Optional.of(sector));
        SectorResponseDTO result = sectorService.findSectorById(1L);

        assertNotNull(result);
        assertEquals("Sector mock", result.name());
    }

    @Test
    public void mustErrorMessageFindSector() {
        when(sectorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> sectorService.findSectorById(99L));
    }

    @Test
    public void mustFindAllSectors() {
        Page<Sector> sectorPage = new PageImpl<>(Collections.singletonList(sector));
        when(sectorRepository.findAll(any(Pageable.class))).thenReturn(sectorPage);

        PageResponseDTO<SectorResponseDTO> result = sectorService.findAllSectors(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.content().size());
    }

    @Test
    public void mustSuccessfullyUpdateSector() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sectorRepository.findById(1L)).thenReturn(Optional.of(sector));
        when(sectorRepository.save(any(Sector.class))).thenReturn(sector);

        SectorResponseDTO result = sectorService.updateSector(1L, sectorRequestDTO);

        assertNotNull(result);
        assertEquals("Sector mock", result.name());
    }

    @Test
    public void mustDeleteSectorById() {
        sectorService.deleteById(1L);
        verify(sectorRepository).deleteById(1L);
    }
}
