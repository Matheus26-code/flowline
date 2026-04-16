package com.flowline.flowline.dto;

import com.flowline.flowline.model.Warehouse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequestDTO(
        @NotBlank String name,
        @NotNull @Positive BigDecimal weight,
        @NotBlank String unit,
        @NotBlank String location,
        @NotNull Long warehouseId
) {}
