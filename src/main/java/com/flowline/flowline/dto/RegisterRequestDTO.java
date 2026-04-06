package com.flowline.flowline.dto;

public record RegisterRequestDTO(
    String username,
    String email,
    String password
) {}
