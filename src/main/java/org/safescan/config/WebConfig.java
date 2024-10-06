package org.safescan.config;

import org.safescan.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Value("${app.uploadAvatars.dir}")
    private String avatarUploadUrl;

    @Value("${app.uploadVideos.dir}")
    private String videoUploadUrl;

    @Value("${app.generatedKeyFrame.dir}")
    private String generatedKeyFrameUrl;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Add an interceptor
        // Give permission to login and register interface
        registry.addInterceptor(loginInterceptor).excludePathPatterns(
                "/user/login",
                "/user/register",
                "/forum/public/get",
                "/forum/public/detail",
                "/comment/public/get",
                "/comment/public/get/son",
                "/uploads/**",
                "/generated/**"
        );
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + avatarUploadUrl + "/")
                .addResourceLocations("file:" + videoUploadUrl + "/");

        registry.addResourceHandler("/generated/**")
                .addResourceLocations("file:" + generatedKeyFrameUrl + "/");
    }
}
