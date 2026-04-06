package com.flowline.flowline.dto;

import java.util.List;

public record PageResponseDTO<T>(
    List<T> content,
    int totalPages,
    long totalElements,
    boolean first,
    boolean last
) {}
