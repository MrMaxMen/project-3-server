package com.project_3.server.repos

import com.project_3.server.models.delivery.PickupPoint
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PickupPointRepository : JpaRepository<PickupPoint, Long>