package com.flowline.flowline.dto;

public record WarehouseRequestDTO(
        String name,
        String description,
        String street,
        String city,
        String state,
        String zipCode
) {}
