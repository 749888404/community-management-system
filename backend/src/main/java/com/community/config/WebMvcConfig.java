package com.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * 前端静态资源托管 + SPA 路由回退（单服务部署用）。
 *
 * 规则：
 *  - /api/** 由各 Controller 处理（Controller 优先级高于静态资源映射）
 *  - 其它路径优先返回 classpath:/static 下的真实文件（js/css/index.html 等）
 *  - 找不到真实文件时：
 *      · 若属于 /api/** → 返回 null（真正 404，不落回首页）
 *      · 否则回退到 index.html，交给前端 Vue Router 处理深链接
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        // 根路径 / 与未知非接口路径统一回退到前端入口
                        if (resourcePath == null || resourcePath.isEmpty() || resourcePath.equals("/")) {
                            Resource index = new ClassPathResource("/static/index.html");
                            return index.exists() ? index : null;
                        }
                        Resource requested = location.createRelative(resourcePath);
                        if (requested.exists() && requested.isReadable()) {
                            return requested;
                        }
                        // 未匹配到的接口请求返回 404，不回退首页
                        if (resourcePath.equals("api") || resourcePath.startsWith("api/")) {
                            return null;
                        }
                        // 其余未知路径回退到前端入口
                        Resource index = new ClassPathResource("/static/index.html");
                        return index.exists() ? index : null;
                    }
                });
    }
}
