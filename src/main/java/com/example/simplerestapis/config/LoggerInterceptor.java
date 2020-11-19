//package com.example.simplerestapis.config;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import com.example.simplerestapis.service.SalesforceService;
//
//@Component
//public class LoggerInterceptor implements HandlerInterceptor{
//	
//	@Autowired
//	SalesforceService SFservice;
//	
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
//			String user_id = SFservice.readCookie(request, "user_id");
//			if(user_id == null) {
//				System.out.println("Not Logged in");
//				return false;
//			}
//			System.out.println("Valid user");
//			return true;
//	}
//}
