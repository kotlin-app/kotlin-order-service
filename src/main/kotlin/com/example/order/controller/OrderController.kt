package com.example.order.controller

import com.example.order.dto.OrderRequest
import com.example.order.model.Order
import com.example.order.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "注文管理 API")
class OrderController(private val service: OrderService) {

    @GetMapping
    @Operation(summary = "全注文一覧取得")
    fun getAll(): List<Order> = service.findAll()

    @GetMapping("/{id}")
    @Operation(summary = "注文詳細取得")
    fun getById(@PathVariable id: Long): Order = service.findById(id)

    @GetMapping("/user/{userId}")
    @Operation(summary = "ユーザーの注文一覧取得")
    fun getByUser(@PathVariable userId: String): List<Order> = service.findByUserId(userId)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "注文作成")
    fun create(@RequestBody req: OrderRequest): Order = service.create(req)

    @DeleteMapping("/{id}")
    @Operation(summary = "注文キャンセル")
    fun cancel(@PathVariable id: Long): Order = service.cancel(id)
}
