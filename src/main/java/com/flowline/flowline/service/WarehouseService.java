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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    private WarehouseResponseDTO toResponseDTO(Warehouse warehouse) {
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
        Warehouse warehouse = new Warehouse();
        warehouse.setName(warehouseRequestDTO.name());
        warehouse.setDescription(warehouseRequestDTO.description());
        warehouse.setStreet(warehouseRequestDTO.street());
        warehouse.setCity(warehouseRequestDTO.city());
        warehouse.setState(warehouseRequestDTO.state());
        warehouse.setZipCode(warehouseRequestDTO.zipCode());
        return toResponseDTO(warehouseRepository.save(warehouse));
    }

    public WarehouseResponseDTO findWareById(Long id, User loggedUser) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));

        if (loggedUser.getRole() == UserRole.MANAGE
                && !loggedUser.getWarehouse().getId().equals(id)) {
            throw new ForbiddenAccessException(
                    "Access denied: you can only access your own warehouse");
        }

        return toResponseDTO(warehouse);
    }

    public PageResponseDTO<WarehouseResponseDTO> findAll(Pageable pageable) {
        Page<WarehouseResponseDTO> page = warehouseRepository.findAll(pageable)
                .map(this::toResponseDTO);
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
        return toResponseDTO(warehouseRepository.save(warehouse));
    }

    public void deleteById(Long id) {
        warehouseRepository.deleteById(id);
    }
}