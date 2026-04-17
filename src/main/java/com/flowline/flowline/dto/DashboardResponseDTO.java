package com.flowline.flowline.dto;

public record DashboardResponseDTO(
        OrderSummaryDTO orders,
        Long totalProducts,
        Long totalUsers

) {}
