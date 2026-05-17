package com.example.order.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class Order(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(nullable = false)
    val quantity: Int,

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    val totalPrice: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: OrderStatus = OrderStatus.PENDING,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
