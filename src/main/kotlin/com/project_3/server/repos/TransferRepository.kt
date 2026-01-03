package com.project_3.server.repos

import com.project_3.server.models.logistics.transportation.Transfer
import org.springframework.data.jpa.repository.JpaRepository

interface TransferRepository : JpaRepository<Transfer, Long>
