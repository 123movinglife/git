package com.example.market.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.market.common.Result;
import com.example.market.dto.CreateOrderDTO;
import com.example.market.entity.Order;
import com.example.market.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    public Result<Long> create(@Validated @RequestBody CreateOrderDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long orderId = orderService.createOrder(dto.getProductId(), userId);
        return Result.ok(orderId);
    }

    /**
     * 取消订单
     */
    @PutMapping("/{orderId}/cancel")
    public Result<Void> cancel(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.cancelOrder(orderId, userId);
        return Result.ok();
    }

    /**
     * 我的订单列表(买家视角)
     */
    @GetMapping("/my")
    public Result<Page<Order>> myOrders(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Order> orderPage = orderService.myOrders(userId, page, size);
        return Result.ok(orderPage);
    }

    /**
     * 我卖出的订单列表(卖家视角)
     */
    @GetMapping("/sales")
    public Result<Page<Order>> mySales(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Order> orderPage = orderService.mySales(userId, page, size);
        return Result.ok(orderPage);
    }
}
