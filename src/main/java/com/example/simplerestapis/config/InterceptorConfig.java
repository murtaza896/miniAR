//package com.example.simplerestapis.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//
//@Configuration
//public class InterceptorConfig implements WebMvcConfigurer {
//	@Autowired
//	LoggerInterceptor logInterceptor;
//
//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(logInterceptor).excludePathPatterns("/login" , "/sign-up" , "/add-cookie", "/check-existence");
//	}
//}
