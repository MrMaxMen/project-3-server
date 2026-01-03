package com.project_3.server.repos

import com.project_3.server.models.enums.ProductInOrderStatus
import com.project_3.server.models.order.ProductInOrder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductInOrderRepository : JpaRepository<ProductInOrder, Long> {

    fun findAllByStatus(status: ProductInOrderStatus): List<ProductInOrder>
    fun findAllByStatusAndDeliveryIsNull(status: ProductInOrderStatus): List<ProductInOrder>
}