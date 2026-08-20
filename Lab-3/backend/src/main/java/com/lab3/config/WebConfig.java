package com.lab3.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
@Configuration public class WebConfig implements WebMvcConfigurer { public void addCorsMappings(CorsRegistry registry){registry.addMapping("/api/**").allowedOrigins("http://localhost:13003").allowedMethods("GET","PATCH");} }
