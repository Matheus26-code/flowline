package com.flowline.flowline.dto;

public record SectorRequestDTO(
        String name,
        String description,
        String building,
        Long responsibleId,
        Long warehouseId
) {}
