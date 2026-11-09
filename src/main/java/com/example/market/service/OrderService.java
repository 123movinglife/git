package com.example.market.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.market.entity.Order;
import com.example.market.entity.Product;
import com.example.market.mapper.OrderMapper;
import com.example.market.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor//lombok注解对 final 修饰的成员变量生成构造函数，实现依赖注入
public class OrderService {
    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;

    /**
     * 创建订单
     */
    @Transactional
    public Long createOrder(Long productId, Long buyerId) {
        // 1. 查询商品是否存在且在售
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStatus() != 1) {
            throw new RuntimeException("商品已下架");
        }
        
        // 2. 不能买自己发布的商品
        if (product.getSellerId().equals(buyerId)) {
            throw new RuntimeException("不能购买自己的商品");
        }
        
        // 3. 创建订单
        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setSellerId(product.getSellerId());
        order.setProductId(productId);
        order.setProductTitle(product.getTitle());  // 保存商品标题快照
        order.setAmount(product.getPrice());
        order.setStatus(0);  // 待支付
        
        orderMapper.insert(order);
        return order.getId();
    }

    /**
     * 取消订单
     */
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 只有买家本人能取消
        if (!order.getBuyerId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        
        // 只有待支付状态能取消
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不允许取消");
        }
        
        order.setStatus(2);  // 已取消
        orderMapper.updateById(order);
    }

    /**
     * 我的订单列表(买家视角)
     */
    public Page<Order> myOrders(Long userId, int page, int size) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getBuyerId, userId)
               .orderByDesc(Order::getCreateTime);
        return orderMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 我卖出的订单列表(卖家视角)
     */
    public Page<Order> mySales(Long userId, int page, int size) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getSellerId, userId)
               .orderByDesc(Order::getCreateTime);
        return orderMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 定时任务:超时未支付订单自动取消
     *
     * 业务规则:下单后 30 分钟内未支付 → 状态改为 3(超时取消)
     * cron 含义:每分钟的 0 秒触发一次(秒 分 时 日 月 周)
     * 面试点:为什么不用用户下单时开个线程睡 30 分钟?
     *   ① 项目重启线程就没了,任务丢失;
     *   ② 一万个订单就是一万个线程,资源撑不住;
     *   ③ 定时扫描统一处理,可靠且简单(生产环境会用延迟消息/RocketMQ,此处是单机简化版)
     */
    @Scheduled(cron = "0 * * * * ?")  // 每分钟执行一次
    public void cancelTimeoutOrders() {
        // 30 分钟前的时间点:创建时间早于它的待支付订单都是超时订单
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(30);

        // 查出所有"待支付 + 创建超过30分钟"的订单
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, 0)
                .lt(Order::getCreateTime, deadline);
        List<Order> timeoutOrders = orderMapper.selectList(wrapper);

        if (timeoutOrders.isEmpty()) {
            return;  // 没有超时订单,直接返回
        }

        for (Order order : timeoutOrders) {
            order.setStatus(3);  // 3 = 超时取消
            orderMapper.updateById(order);
        }
        System.out.println("[定时任务] 超时取消了 " + timeoutOrders.size() + " 个订单: "
                + timeoutOrders.stream().map(Order::getId).toList());
    }
}
