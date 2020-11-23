package com.example.simplerestapis.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public class UtilService {
	
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
}
