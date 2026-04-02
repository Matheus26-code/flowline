package com.flowline.flowline.service;

import com.flowline.flowline.dto.PageResponseDTO;
import com.flowline.flowline.dto.WarehouseRequestDTO;
import com.flowline.flowline.dto.WarehouseResponseDTO;
import com.flowline.flowline.exception.ResourceNotFoundException;
import com.flowline.flowline.model.Warehouse;
import com.flowline.flowline.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseResponseDTO create(WarehouseRequestDTO warehouseRequestDTO) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(warehouseRequestDTO.name());
        warehouse.setDescription(warehouseRequestDTO.description());
        warehouse.setStreet(warehouseRequestDTO.street());
        warehouse.setCity(warehouseRequestDTO.city());
        warehouse.setState(warehouseRequestDTO.state());
        warehouse.setZipCode(warehouseRequestDTO.zipCode());
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return new WarehouseResponseDTO(
                savedWarehouse.getId(),
                savedWarehouse.getName(),
                savedWarehouse.getDescription(),
                savedWarehouse.getStreet(),
                savedWarehouse.getCity(),
                savedWarehouse.getState(),
                savedWarehouse.getZipCode()
        );
    }

    public WarehouseResponseDTO findWareById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
        return new WarehouseResponseDTO(
                warehouse.getId(),
                warehouse.getName(),
                warehouse.getDescription(),
                warehouse.getStreet(),
                warehouse.getCity(),
                warehouse.getState(),
                warehouse.getZipCode());
    }

    public PageResponseDTO<WarehouseResponseDTO> findAll(Pageable pageable) {
        Page<WarehouseResponseDTO> page = warehouseRepository.findAll(pageable)
                .map(warehouse -> new WarehouseResponseDTO(
                        warehouse.getId(),
                        warehouse.getName(),
                        warehouse.getDescription(),
                        warehouse.getStreet(),
                        warehouse.getCity(),
                        warehouse.getState(),
                        warehouse.getZipCode()
                ));
        return new PageResponseDTO<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast()
        );
    }

    public WarehouseResponseDTO updateWarehouse(Long id, WarehouseRequestDTO warehouseRequestDTO) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
        warehouse.setName(warehouseRequestDTO.name());
        warehouse.setDescription(warehouseRequestDTO.description());
        warehouse.setStreet(warehouseRequestDTO.street());
        warehouse.setCity(warehouseRequestDTO.city());
        warehouse.setState(warehouseRequestDTO.state());
        warehouse.setZipCode(warehouseRequestDTO.zipCode());
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return new WarehouseResponseDTO(
                savedWarehouse.getId(),
                savedWarehouse.getName(),
                savedWarehouse.getDescription(),
                savedWarehouse.getStreet(),
                savedWarehouse.getCity(),
                savedWarehouse.getState(),
                savedWarehouse.getZipCode()
        );
    }

    public void deleteById(Long id) {
        warehouseRepository.deleteById(id);
    }
}