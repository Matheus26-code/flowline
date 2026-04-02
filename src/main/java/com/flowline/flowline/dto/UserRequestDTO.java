package com.flowline.flowline.dto;

import com.flowline.flowline.model.UserRole;
import com.flowline.flowline.model.Warehouse;

public record UserRequestDTO(
        String username,
        String email,
        String password,
        UserRole role,
        Long warehouseId
) {}
