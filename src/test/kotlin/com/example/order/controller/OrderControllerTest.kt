package com.example.order.controller

import com.example.order.model.Order
import com.example.order.model.OrderStatus
import com.example.order.repository.OrderRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var repository: OrderRepository
    @Autowired lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
        repository.saveAll(listOf(
            Order(userId = "user1", productId = 1L, quantity = 2, totalPrice = BigDecimal("2400.00")),
            Order(userId = "user1", productId = 2L, quantity = 1, totalPrice = BigDecimal("8000.00")),
            Order(userId = "user2", productId = 1L, quantity = 3, totalPrice = BigDecimal("3600.00")),
        ))
    }

    @Test
    fun `全注文一覧を取得できる`() {
        mockMvc.perform(get("/api/orders"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(3))
    }

    @Test
    fun `ユーザーの注文一覧を取得できる`() {
        mockMvc.perform(get("/api/orders/user/user1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
    }

    @Test
    fun `注文を作成できる`() {
        val body = mapOf(
            "userId" to "user3",
            "productId" to 1,
            "quantity" to 1,
            "totalPrice" to 1200.00
        )
        mockMvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.userId").value("user3"))
            .andExpect(jsonPath("$.status").value("PENDING"))
    }

    @Test
    fun `注文をキャンセルできる`() {
        val order = repository.save(
            Order(userId = "user1", productId = 1L, quantity = 1, totalPrice = BigDecimal("1200.00"))
        )
        mockMvc.perform(delete("/api/orders/${order.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("CANCELLED"))
    }
}
