package com.flowline.flowline.dto;

public record OrderSummaryDTO(
        Long total,
        Long pending,
        Long delivering,
        Long delivered,
        Long cancelled,
        Long createdToday,
        Long deliveredToday
) {
}
