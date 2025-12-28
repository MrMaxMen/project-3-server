package com.project_3.server.repos

import com.project_3.server.models.Orthodrome
import org.springframework.data.jpa.repository.JpaRepository

interface OrthodromeRepository : JpaRepository<Orthodrome, Long> {


    fun findByStockIdAndPickupPointId(stockId: Long, pickupPointId: Long): Orthodrome?


    fun findByPickupPointIdOrderByDistanceKmAsc(pickupPointId: Long): List<Orthodrome>


    fun findByStockId(warehouseId: Long): List<Orthodrome>


    fun findByPickupPointId(pickupPointId: Long): List<Orthodrome>


    fun findTop5ByPickupPointIdOrderByDistanceKmAsc(pickupPointId: Long): List<Orthodrome>


    fun existsByStockIdAndPickupPointId(warehouseId: Long, pickupPointId: Long): Boolean
}