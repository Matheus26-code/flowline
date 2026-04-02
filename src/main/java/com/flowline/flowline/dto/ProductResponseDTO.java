package com.flowline.flowline.dto;

import com.flowline.flowline.model.Warehouse;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String name,
        BigDecimal weight,
        String unit,
        String location,
        Long warehouseId
) {}
