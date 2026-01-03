package com.project_3.server.repos

import com.project_3.server.models.logistics.transportation.Transportation
import org.springframework.data.jpa.repository.JpaRepository

interface TransportationRepository : JpaRepository<Transportation, Long>
