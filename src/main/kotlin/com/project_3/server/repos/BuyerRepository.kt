package com.project_3.server.repos

import com.project_3.server.models.Buyer
import org.springframework.data.jpa.repository.JpaRepository

interface BuyerRepository : JpaRepository<Buyer, Long>