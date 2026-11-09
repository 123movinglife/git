-- 订单表
CREATE TABLE `order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `buyer_id` BIGINT NOT NULL COMMENT '买家ID',
  `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `product_title` VARCHAR(200) NOT NULL COMMENT '商品标题快照',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0待支付 1已支付 2已取消 3超时取消',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_buyer` (`buyer_id`, `status`),
  INDEX `idx_seller` (`seller_id`, `status`),
  INDEX `idx_timeout` (`status`, `create_time`)  -- 定时任务扫描超时订单用
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
