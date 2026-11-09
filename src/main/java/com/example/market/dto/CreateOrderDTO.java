package com.example.market.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class CreateOrderDTO {
    @NotNull(message = "商品ID不能为空")
    private Long productId;
}
