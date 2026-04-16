package com.flowline.flowline.dto;

import com.flowline.flowline.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequestDTO(
        @NotBlank String username,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotNull UserRole role,
        @NotNull Long warehouseId
) {}
