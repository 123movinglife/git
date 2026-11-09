package com.example.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("`order`")   // order 是MySQL保留字,必须用反引号包裹,否则生成的SQL语法错误
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long buyerId;      // 买家ID
    private Long sellerId;     // 卖家ID
    private Long productId;    // 商品ID
    private String productTitle; // 商品标题快照(下单时保存,防止商品改名后订单信息对不上)
    private BigDecimal amount;  // 订单金额
    private Integer status;     // 订单状态: 0待支付 1已支付 2已取消 3超时取消
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
