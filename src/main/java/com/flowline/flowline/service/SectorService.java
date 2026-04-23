package com.flowline.flowline.service;

import com.flowline.flowline.dto.*;
import com.flowline.flowline.exception.ResourceNotFoundException;
import com.flowline.flowline.model.Sector;
import com.flowline.flowline.model.User;
import com.flowline.flowline.model.Warehouse;
import com.flowline.flowline.repository.SectorRepository;
import com.flowline.flowline.repository.UserRepository;
import com.flowline.flowline.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class SectorService {

    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final SectorRepository sectorRepository;

    private record SectorDependencies(
            Warehouse warehouse,
            User user
    ) {}

    private SectorDependencies resolveDepencies(SectorRequestDTO request) {
        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Warehouse not found with id: " + request.warehouseId()));

        User user = userRepository.findById(request.responsibleId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("User not found with id: " + request.responsibleId()));

        return new SectorDependencies(warehouse, user);
    }

    private SectorResponseDTO toResponse(Sector sector) {
        return new SectorResponseDTO(
            sector.getId(),
            sector.getName(),
            sector.getDescription(),
            sector.getBuilding(),
            sector.getResponsible().getId(),
            sector.getWarehouse().getId());
    }

    public SectorResponseDTO createSector(SectorRequestDTO request) {
        SectorDependencies deps =  resolveDepencies(request);

        Sector sector = new Sector();
        sector.setName(request.name());
        sector.setDescription(request.description());
        sector.setBuilding(request.building());
        sector.setWarehouse(deps.warehouse);
        sector.setResponsible(deps.user);
        return toResponse(sectorRepository.save(sector));
    }

    public SectorResponseDTO findSectorById(Long id) {
        Sector sector = sectorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sector not found with id: " + id));
        return toResponse(sector);
    }

    public PageResponseDTO<SectorResponseDTO> findAllSectors(Pageable pageable) {
        Page<SectorResponseDTO> page = sectorRepository.findAll(pageable)
                .map(this::toResponse);
        return new PageResponseDTO<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast()
        );
    }

    public SectorResponseDTO updateSector(Long id, SectorRequestDTO request) {
        SectorDependencies deps = resolveDepencies(request);

        Sector sector = sectorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Sector not found with id: " + id));
        sector.setName(request.name());
        sector.setDescription(request.description());
        sector.setBuilding(request.building());
        sector.setWarehouse(deps.warehouse);
        sector.setResponsible(deps.user);
        return toResponse(sectorRepository.save(sector));
    }

    public void deleteById(Long id) {
        sectorRepository.deleteById(id);
    }
}