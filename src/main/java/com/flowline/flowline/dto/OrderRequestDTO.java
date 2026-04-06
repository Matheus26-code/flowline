package com.flowline.flowline.dto;

import com.flowline.flowline.model.MovementStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderRequestDTO(
       Long originSectorId,
       Long destinationSectorId,
       Long userId,
       Long productId,
       MovementStatus status,
       BigDecimal quantity,
       LocalDateTime createdAt
) {}
