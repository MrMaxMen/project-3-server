package com.project_3.server.repos

import com.project_3.server.models.users.Seller
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SellerRepository : JpaRepository<Seller,Long>{
    fun findByEmail(email: String): Seller?
}