package com.example.order.service

import com.example.order.dto.OrderRequest
import com.example.order.model.Order
import com.example.order.model.OrderStatus
import com.example.order.repository.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(private val repository: OrderRepository) {

    fun findAll(): List<Order> = repository.findAll()

    fun findById(id: Long): Order = repository.findById(id)
        .orElseThrow { NoSuchElementException("Order not found: $id") }

    fun findByUserId(userId: String): List<Order> = repository.findByUserId(userId)

    @Transactional
    fun create(req: OrderRequest): Order =
        repository.save(
            Order(
                userId = req.userId,
                productId = req.productId,
                quantity = req.quantity,
                totalPrice = req.totalPrice
            )
        )

    @Transactional
    fun cancel(id: Long): Order {
        val order = findById(id)
        require(order.status == OrderStatus.PENDING) { "Only PENDING orders can be cancelled" }
        return repository.save(order.copy(status = OrderStatus.CANCELLED))
    }
}
