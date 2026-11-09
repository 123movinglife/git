package com.example.market.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 数据看板统计查询。
 * 用 @Select 直接写聚合 SQL:统计类需求用一条 SQL 算完,比"查出来再在 Java 里循环"高效得多。
 */
@Mapper
public interface StatsMapper {

    /** 顶部指标卡:用户数/商品数/订单数/累计金额/今日订单 */
    @Select("""
            SELECT (SELECT COUNT(*) FROM `user`)                              AS userCount,
                   (SELECT COUNT(*) FROM product)                             AS productCount,
                   (SELECT COUNT(*) FROM `order`)                             AS orderCount,
                   (SELECT IFNULL(SUM(amount), 0) FROM `order`)               AS totalAmount,
                   (SELECT COUNT(*) FROM `order` WHERE DATE(create_time) = CURDATE()) AS todayOrders
            """)
    Map<String, Object> overview();

    /** 订单状态分布(0待支付 1已支付 2已取消 3超时取消) */
    @Select("SELECT status, COUNT(*) AS cnt FROM `order` GROUP BY status")
    List<Map<String, Object>> orderStatus();

    /** 商品分类分布 */
    @Select("SELECT IFNULL(category, '未分类') AS category, COUNT(*) AS cnt FROM product GROUP BY category")
    List<Map<String, Object>> productCategory();

    /** 近 7 天订单趋势:每天的单量和金额 */
    @Select("""
            SELECT DATE_FORMAT(create_time, '%m-%d') AS day,
                   COUNT(*)                          AS cnt,
                   IFNULL(SUM(amount), 0)            AS amount
            FROM `order`
            WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY)
            GROUP BY DATE(create_time), DATE_FORMAT(create_time, '%m-%d')
            ORDER BY DATE(create_time)
            """)
    List<Map<String, Object>> orderTrend();

    /** 热门商品 TOP8:按被下单次数排序 */
    @Select("""
            SELECT product_title        AS title,
                   COUNT(*)             AS cnt,
                   IFNULL(SUM(amount), 0) AS amount
            FROM `order`
            GROUP BY product_title
            ORDER BY cnt DESC
            LIMIT 8
            """)
    List<Map<String, Object>> topProducts();
}
