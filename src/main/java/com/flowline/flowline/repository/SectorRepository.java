package com.flowline.flowline.repository;

import com.flowline.flowline.model.Sector;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectorRepository extends JpaRepository<Sector, Long>  {
    List<Sector> findByWarehouseId(Long warehouseId);
    Page<Sector> findByWarehouseId(Long warehouseId, Pageable pageable);
}
