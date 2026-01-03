package com.project_3.server.repos

import com.project_3.server.models.ProductBatch
import org.springframework.data.jpa.repository.JpaRepository

interface ProductBatchRepository : JpaRepository<ProductBatch, Long>