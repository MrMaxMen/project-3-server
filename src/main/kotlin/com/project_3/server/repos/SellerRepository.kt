package com.project_3.server.repos

import com.project_3.server.models.Seller
import org.springframework.data.jpa.repository.JpaRepository

interface SellerRepository : JpaRepository<Seller,Long>