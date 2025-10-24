package com.project_3.server.repos

import com.project_3.server.models.Review
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository : JpaRepository<Review, Long>