package com.flowline.flowline.dto;

public record WarehouseResponseDTO(
        Long id,
        String name,
        String description,
        String street,
        String city,
        String state,
        String zipCode
) {}
