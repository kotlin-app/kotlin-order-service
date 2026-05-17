package com.example.order.dto

import java.math.BigDecimal

data class OrderRequest(
    val userId: String,
    val productId: Long,
    val quantity: Int,
    val totalPrice: BigDecimal
)
