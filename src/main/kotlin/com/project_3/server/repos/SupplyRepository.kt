package com.project_3.server.repos

import com.project_3.server.models.enums.TransportationStatus
import com.project_3.server.models.logistics.transportation.Supply
import org.springframework.data.jpa.repository.JpaRepository

interface SupplyRepository : JpaRepository<Supply, Long>{
    fun findALLByStatus(status: TransportationStatus) : List<Supply>
}
