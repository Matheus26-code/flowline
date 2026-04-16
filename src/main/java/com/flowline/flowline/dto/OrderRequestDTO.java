package com.flowline.flowline.dto;

import com.flowline.flowline.model.MovementStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderRequestDTO(
       @NotNull Long originSectorId,
       @NotNull Long destinationSectorId,
       @NotNull Long userId,
       @NotNull Long productId,
       @NotNull @Positive BigDecimal quantity
) {}
