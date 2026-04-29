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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SoftDelete;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Slf4j
@SoftDelete
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    private WarehouseResponseDTO toResponse(Warehouse warehouse) {
        return new WarehouseResponseDTO(
        warehouse.getId(),
        warehouse.getName(),
        warehouse.getDescription(),
        warehouse.getStreet(),
        warehouse.getCity(),
        warehouse.getState(),
        warehouse.getZipCode());
    }

    public WarehouseResponseDTO create(WarehouseRequestDTO warehouseRequestDTO) {
        log.info("Creating Warehouse request: {}", warehouseRequestDTO);
        Warehouse warehouse = new Warehouse();
        warehouse.setName(warehouseRequestDTO.name());
        warehouse.setDescription(warehouseRequestDTO.description());
        warehouse.setStreet(warehouseRequestDTO.street());
        warehouse.setCity(warehouseRequestDTO.city());
        warehouse.setState(warehouseRequestDTO.state());
        warehouse.setZipCode(warehouseRequestDTO.zipCode());
        WarehouseResponseDTO result = toResponse(warehouseRepository.save(warehouse));
        log.info("Warehouse created successfully: id={}, name={}",
                result.id(), result.name());
        return result;
    }

    public WarehouseResponseDTO findWareById(Long id, User loggedUser) {
        log.info("Finding Warehouse by ID: {}", id);
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Warehouse not found with id {}", id);
                    return new ResourceNotFoundException("Warehouse not found with id: " + id);
                });
        if (loggedUser.getRole() == UserRole.MANAGE
                && !loggedUser.getWarehouse().getId().equals(id)) {
            throw new ForbiddenAccessException(
                    "Access denied: you can only access your own warehouse");
        }
        WarehouseResponseDTO result = toResponse(warehouse);
        log.info("Warehouse find successfully: id={}, name={}",
                result.id(), result.name());
        return result;
    }

    public PageResponseDTO<WarehouseResponseDTO> findAll(Pageable pageable) {
        log.info("Finding all warehouses");
        Page<WarehouseResponseDTO> page = warehouseRepository.findAll(pageable)
                .map(this::toResponse);
        return new PageResponseDTO<>(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isFirst(),
                page.isLast()
        );
    }

    public WarehouseResponseDTO updateWarehouse(Long id, WarehouseRequestDTO warehouseRequestDTO) {
        log.info("Updating Warehouse by ID: {}", id);
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Warehouse not found with id {}", id);
                    return new ResourceNotFoundException("Warehouse not found with id: " + id);
                });
        warehouse.setName(warehouseRequestDTO.name());
        warehouse.setDescription(warehouseRequestDTO.description());
        warehouse.setStreet(warehouseRequestDTO.street());
        warehouse.setCity(warehouseRequestDTO.city());
        warehouse.setState(warehouseRequestDTO.state());
        warehouse.setZipCode(warehouseRequestDTO.zipCode());

        WarehouseResponseDTO result = toResponse(warehouseRepository.save(warehouse));
        log.info("Warehouse updated successfully: id={}, name={}",
                result.id(), result.name());
        return result;
    }

    public void deleteById(Long id) {
        log.info("Deleting warehouse by id: {}", id);
        if (!warehouseRepository.existsById(id)) {
            log.warn("Warehouse not found for deletion: id={}", id);
            throw new ResourceNotFoundException("Warehouse not found with id: " + id);
        }
        warehouseRepository.deleteById(id);
        log.info("Warehouse deleted: id={}", id);
    }
}