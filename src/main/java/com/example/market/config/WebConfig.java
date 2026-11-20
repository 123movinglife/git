package com.example.market.config;

import com.example.market.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")                       // 拦所有api
                .excludePathPatterns("/api/user/register",        // 放行注册
                        "/api/user/login",          // 放行登录
                        "/api/stats/**",            // 数据看板(演示用公开;生产环境应加管理员权限)
                        "/api/product/list",        // 商品列表公开浏览(不登录也能看商品)
                        "/api/product/*");          // 商品详情公开(精确到一级路径,不会放行 POST /api/product 发布接口)
    }

    /** 允许前端页面(不同端口/本地文件)访问接口。Apifox 不受同源策略限制,浏览器页面才需要 */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
