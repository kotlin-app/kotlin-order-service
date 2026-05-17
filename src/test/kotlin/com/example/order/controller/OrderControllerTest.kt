package com.example.order.controller

import com.example.order.event.OrderCreatedEvent
import com.example.order.model.Order
import com.example.order.repository.OrderRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var repository: OrderRepository
    @Autowired lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var kafkaTemplate: KafkaTemplate<String, OrderCreatedEvent>

    @BeforeEach
    fun setUp() {
        whenever(kafkaTemplate.send(any<String>(), any())).thenReturn(CompletableFuture.completedFuture(null))
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
    fun `注文を作成するとKafkaイベントが発行される`() {
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
