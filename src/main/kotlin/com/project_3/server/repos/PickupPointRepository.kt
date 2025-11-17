package com.project_3.server.repos

import com.project_3.server.models.PickupPoint
import org.springframework.data.jpa.repository.JpaRepository

interface PickupPointRepository : JpaRepository<PickupPoint, Long>