package com.example.market.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.market.common.Result;
import com.example.market.entity.Product;
import com.example.market.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 发布商品。卖家身份取自拦截器塞进request的userId,而不是请求体——
    // 前端说"我是谁"不可信,登录态说了才算,这是安全底线
    @PostMapping
    public Result<Void> publish(@RequestBody Product product, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        product.setSellerId(userId);
        productService.publish(product);
        return Result.ok();
    }

    // 分页搜索:/api/product/list?page=1&size=5&category=数码&keyword=键盘
    @GetMapping("/list")
    public Result<Page<Product>> list(@RequestParam(defaultValue = "1") long page,
                                      @RequestParam(defaultValue = "5") long size,
                                      @RequestParam(required = false) String category,
                                      @RequestParam(required = false) String keyword) {
        return Result.ok(productService.list(page, size, category, keyword));
    }

    // 商品详情:/api/product/1
    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.ok(productService.getById(id));
    }
}

