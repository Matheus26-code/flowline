package com.flowline.flowline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WarehouseRequestDTO(
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String street,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank @Size(min = 8, max = 9) String zipCode
) {}
