package com.flowline.flowline.dto;

import com.flowline.flowline.model.UserRole;

public record UserResponseDTO(
        Long id,
        String username,
        String email,
        UserRole role,
        Long warehouseId
) {}
