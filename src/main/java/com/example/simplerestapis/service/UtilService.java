package com.example.simplerestapis.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.simplerestapis.config.JwtTokenUtil;

@Service
public class UtilService {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	public String readCookie(HttpServletRequest request , String key) {
		String value = null;
		Cookie cookies[] = request.getCookies();
		if(cookies == null ||  cookies.length == 0) return null;
		for(Cookie c: cookies) {
			if(c.getName().equals(key)) {
				value = c.getValue();
			}
		}
		return value;
	}
	
	public int readIdFromToken(HttpServletRequest request)
	{
//		final String requestTokenHeader = this.readCookie(request, "token");
		
		 
		String username2 = "";
//		String jwtToken = null;
//		// JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
//		if (requestTokenHeader != null) {
//			jwtToken = requestTokenHeader;
//			try {
//				username2 = jwtTokenUtil.getUsernameFromToken(jwtToken);
//			} catch (IllegalArgumentException e) {
//				System.out.println("Unable to get JWT Token");
//			} catch (Exception e) {
//				System.out.println("JWT Token has expired");
//			}
//		} else {
//			System.out.println("JWT Token does not begin with Bearer String");
//		}
		username2 = request.getAttribute("email").toString();
		String userId = userService.getIdByEmail(username2) + "";
		return Integer.parseInt(userId);
	}
}
