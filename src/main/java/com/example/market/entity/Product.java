package com.example.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;
@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long sellerId;  //对应数据库表里的seller_id,驼峰映射自处理
    private String title;
    private String description;
    private BigDecimal price;  //金额永远用BigDecimal,不用double-->容易造成精度丢失
    private String category;
    private Integer status;          //1在售,0下架
    private LocalDateTime createTime;

}
