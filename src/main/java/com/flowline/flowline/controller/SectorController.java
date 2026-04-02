package com.flowline.flowline.controller;

import com.flowline.flowline.dto.PageResponseDTO;
import com.flowline.flowline.dto.SectorRequestDTO;
import com.flowline.flowline.dto.SectorResponseDTO;
import com.flowline.flowline.service.SectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sector")
@RequiredArgsConstructor
public class SectorController {

    private final SectorService sectorService;

    @PostMapping
    public ResponseEntity<SectorResponseDTO> createSector(
            @RequestBody @Valid SectorRequestDTO request) {
        SectorResponseDTO result = sectorService.createSector(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectorResponseDTO> getSectorById(@PathVariable Long id) {
        SectorResponseDTO result = sectorService.findSectorById(id);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<SectorResponseDTO>> getAllSectors(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        PageResponseDTO<SectorResponseDTO> result = sectorService.findAllSectors(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SectorResponseDTO> updateSector(
            @PathVariable Long id, @RequestBody @Valid SectorRequestDTO request) {
        SectorResponseDTO result = sectorService.updateSector(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSector(@PathVariable Long id) {
        sectorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
