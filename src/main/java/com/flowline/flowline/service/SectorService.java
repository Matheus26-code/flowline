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

    public SectorResponseDTO createSector(SectorRequestDTO request) {
        Sector sector = new Sector();
        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + request.warehouseId()));
        User user = userRepository.findById(request.responsibleId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.responsibleId()));

        sector.setName(request.name());
        sector.setBuilding(request.building());
        sector.setWarehouse(warehouse);
        sector.setResponsible(user);
        Sector savedSector =  sectorRepository.save(sector);
        return new SectorResponseDTO(
                savedSector.getId(),
                savedSector.getName(),
                savedSector.getBuilding(),
                savedSector.getResponsible().getId(),
                savedSector.getWarehouse().getId());
    }

    public SectorResponseDTO findSectorById(Long id) {
        Sector sector = sectorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sector not found with id: " + id));
        return new SectorResponseDTO(
                sector.getId(),
                sector.getName(),
                sector.getBuilding(),
                sector.getResponsible().getId(),
                sector.getWarehouse().getId());
    }

    public PageResponseDTO<SectorResponseDTO> findAllSectors(Pageable pageable) {
        Page<SectorResponseDTO> page = sectorRepository.findAll(pageable)
                .map(sector -> new SectorResponseDTO(
                        sector.getId(),
                        sector.getName(),
                        sector.getBuilding(),
                        sector.getResponsible().getId(),
                        sector.getWarehouse().getId()));
        return new PageResponseDTO<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast()
        );
    }

    public SectorResponseDTO updateSector(Long id, SectorRequestDTO request) {
        Sector sector = sectorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sector not found with id: " + id));
        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + request.warehouseId()));
        User user = userRepository.findById(request.responsibleId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.responsibleId()));

        sector.setName(request.name());
        sector.setBuilding(request.building());
        sector.setWarehouse(warehouse);
        sector.setResponsible(user);
        Sector savedSector =  sectorRepository.save(sector);
        return new SectorResponseDTO(
                savedSector.getId(),
                savedSector.getName(),
                savedSector.getBuilding(),
                savedSector.getResponsible().getId(),
                savedSector.getWarehouse().getId());
    }

    public void deleteById(Long id) {
        sectorRepository.deleteById(id);
    }
}