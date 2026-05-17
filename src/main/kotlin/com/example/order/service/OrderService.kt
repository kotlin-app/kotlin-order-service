package com.example.order.service

import com.example.order.dto.OrderRequest
import com.example.order.event.OrderCreatedEvent
import com.example.order.model.Order
import com.example.order.model.OrderStatus
import com.example.order.repository.OrderRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val repository: OrderRepository,
    private val kafkaTemplate: KafkaTemplate<String, OrderCreatedEvent>,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun findAll(): List<Order> = repository.findAll()

    fun findById(id: Long): Order = repository.findById(id)
        .orElseThrow { NoSuchElementException("Order not found: $id") }

    fun findByUserId(userId: String): List<Order> = repository.findByUserId(userId)

    @Transactional
    fun create(req: OrderRequest): Order {
        val order = repository.save(
            Order(
                userId = req.userId,
                productId = req.productId,
                quantity = req.quantity,
                totalPrice = req.totalPrice
            )
        )
        kafkaTemplate.send("order.created", OrderCreatedEvent(order.id, order.productId, order.quantity))
        log.info("OrderCreatedEvent published: orderId=${order.id}, productId=${order.productId}")
        return order
    }

    @Transactional
    fun cancel(id: Long): Order {
        val order = findById(id)
        require(order.status == OrderStatus.PENDING) { "Only PENDING orders can be cancelled" }
        return repository.save(order.copy(status = OrderStatus.CANCELLED))
    }
}
