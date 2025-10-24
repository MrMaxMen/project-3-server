package com.project_3.server.repos

import com.project_3.server.models.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository: JpaRepository<Order, Long>