package com.example.market.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 图片上传与访问配置。
 * 上传的图片保存在 upload.dir 指定的磁盘目录(本地是 Windows 路径,服务器是 Linux 路径,
 * 通过配置项区分,代码不用改);再通过 /uploads/** 这个 URL 把磁盘目录映射出去,
 * 浏览器才能直接打开图片。
 */
@Configuration
public class UploadConfig implements WebMvcConfigurer {

    @Value("${upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 访问 /uploads/xxx.jpg → 读取 upload.dir 目录下的 xxx.jpg
        // 注意:拦截器只拦 /api/**,所以 /uploads/** 不需要登录就能看图(商品图本来就是公开的)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir);
    }

    public String getUploadDir() {
        return uploadDir;
    }
}
