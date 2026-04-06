package com.flowline.flowline.controller;

import com.flowline.flowline.dto.PageResponseDTO;
import com.flowline.flowline.dto.WarehouseRequestDTO;
import com.flowline.flowline.dto.WarehouseResponseDTO;
import com.flowline.flowline.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<WarehouseResponseDTO> createWarehouse (@RequestBody @Valid WarehouseRequestDTO request) {
        WarehouseResponseDTO result = warehouseService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponseDTO> getWarehouseById (@PathVariable Long id) {
        WarehouseResponseDTO result = warehouseService.findWareById(id);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<WarehouseResponseDTO>> getAllWarehouse(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        PageResponseDTO<WarehouseResponseDTO> result = warehouseService.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponseDTO> updateWarehouse (
            @PathVariable Long id, @Valid @RequestBody WarehouseRequestDTO request) {
       WarehouseResponseDTO result = warehouseService.updateWarehouse(id, request);
       return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse (@PathVariable Long id) {
       warehouseService.deleteById(id);
       return ResponseEntity.noContent().build();
    }
}
