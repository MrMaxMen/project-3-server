package com.project_3.server.repos

import com.project_3.server.models.Stock
import org.springframework.data.jpa.repository.JpaRepository

interface StockRepository : JpaRepository<Stock, Long>