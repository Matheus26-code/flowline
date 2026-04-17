package com.flowline.flowline.controller;


import com.flowline.flowline.dto.DashboardResponseDTO;
import com.flowline.flowline.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGE')")
    public ResponseEntity<DashboardResponseDTO> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }
}
