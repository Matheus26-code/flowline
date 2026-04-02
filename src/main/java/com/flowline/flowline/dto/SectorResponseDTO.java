package com.flowline.flowline.dto;

import com.flowline.flowline.model.User;
import com.flowline.flowline.model.Warehouse;

public record SectorResponseDTO(
        Long id,
        String name,
        String building,
        Long responsibleId,
        Long warehouseId
) {}
