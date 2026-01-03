package com.project_3.server.repos

import com.project_3.server.models.logistics.transportation.Vehicle
import org.springframework.data.jpa.repository.JpaRepository

interface VehicleRepository : JpaRepository<Vehicle, Long> {

    // Find free vehicles (assuming we can filter by some status or logic later)
    // For now simple finder
    fun findAllByIsFreeTrue(): List<Vehicle>
}
