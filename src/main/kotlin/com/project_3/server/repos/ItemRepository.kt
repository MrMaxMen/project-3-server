package com.project_3.server.repos

import com.project_3.server.models.Item
import org.springframework.data.jpa.repository.JpaRepository

interface ItemRepository : JpaRepository<Item, Long>