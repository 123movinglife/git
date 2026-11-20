-- 商品主图字段(本地与服务器各执行一次)
-- 执行方式: SQLyog 连上对应库后运行,或 mysql -u账号 -p 库名 < 本文件
ALTER TABLE product ADD COLUMN image_url VARCHAR(255) COMMENT '商品主图相对路径' AFTER category;
