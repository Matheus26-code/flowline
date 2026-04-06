package com.flowline.flowline.dto;

public record SectorRequestDTO(
        String name,
        String building,
        Long responsibleId,
        Long warehouseId
) {}
