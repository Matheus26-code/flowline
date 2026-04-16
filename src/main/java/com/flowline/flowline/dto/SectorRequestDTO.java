package com.flowline.flowline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SectorRequestDTO(
        @NotBlank String name,
        @NotBlank String description,
        String building,
        @NotNull Long responsibleId,
        @NotNull Long warehouseId
) {
}
