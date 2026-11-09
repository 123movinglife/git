package com.example.market.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.market.entity.Product;
import com.example.market.mapper.ProductMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductMapper productMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_KEY_PREFIX = "product:detail:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    public void publish(Product product) {
        product.setStatus(1); //新商品默认在售,由后端决定,不信任前端
        productMapper.insert(product);
    }

    //分页+动态条件搜索
    //wrapper 第一个参数是Boolean:条件成立才把这段拼进SQL,是就是动态查询
    public Page<Product> list(long page, long size, String category, String keyword) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(hasText(category), Product::getCategory, category)
                .like(hasText(keyword), Product::getTitle, keyword)
                .orderByDesc(Product::getCreateTime);   // 最新发布的排前面
        return productMapper.selectPage(new Page<>(page, size), wrapper);
    }

    /**
     * 商品详情(带 Redis 缓存):
     * 1. 先查 Redis,key = product:detail:{id}
     * 2. 缓存命中 → 直接返回(不查数据库,响应从 ~120ms 降到 ~5ms)
     * 3. 缓存未命中 → 查 MySQL → 写入 Redis(带 30 分钟过期) → 返回
     */
    public Product getById(Long id) {
        String cacheKey = CACHE_KEY_PREFIX + id;

        // 1. 先查缓存
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, Product.class);
            } catch (JsonProcessingException e) {
                // 缓存数据损坏(比如改过实体字段),当作未命中,走数据库
            }
        }

        // 2. 缓存未命中,查数据库
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 3. 写入缓存,设置过期时间防止数据长期陈旧
        try {
            redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(product), CACHE_TTL);
        } catch (JsonProcessingException e) {
            // 序列化失败不影响主流程,只是这次没缓存到
        }

        return product;
    }

    /**
     * 更新/下架商品时必须删除对应缓存,否则缓存里是旧数据(缓存一致性)
     */
    public void evictCache(Long id) {
        redisTemplate.delete(CACHE_KEY_PREFIX + id);
    }

    // 判空小工具:null、空串、纯空格都算"没传这个条件"
    private boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}
