package com.example.simplerestapis.controller;

import java.security.NoSuchAlgorithmException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.simplerestapis.models.User;
import com.example.simplerestapis.models.UserResponse;
import com.example.simplerestapis.models.userCredentials;
import com.example.simplerestapis.service.UserService;

@CrossOrigin(origins = "http://localhost:4200" , allowCredentials = "true")
@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	@GetMapping("/check-existence")
	public int checkExistence(@RequestParam(name="email") String email)
	{
		return userService.checkExistence(email);
		// -1: not exists, 1: exists
	}
	
	@PostMapping("/sign-up")
	public ResponseEntity<UserResponse> signUp(@RequestBody User user) {
		
		int id = userService.addUser(user);
		UserResponse userResponse = new UserResponse();
		userResponse.setuser_id(id);
		
		if (id != -1)
		{	
			userResponse.setMessage("User added Successfully");
			return new ResponseEntity<UserResponse>(userResponse, HttpStatus.OK);
		}
		
		userResponse.setMessage("Couldn't add user");
		return new ResponseEntity<UserResponse>(userResponse, HttpStatus.EXPECTATION_FAILED);
	}
	
	@PostMapping("/login")
	public int login(@RequestBody userCredentials user, HttpServletResponse response) throws NoSuchAlgorithmException {
		return userService.validateUser(user, response);
	}

	@GetMapping("/add-cookie")
	public String addCookie(@RequestParam String id, HttpServletRequest request , HttpServletResponse response) {
		Cookie cookie = new Cookie("user_id", id);
		cookie.setPath("/");
		response.addCookie(cookie);
		return  id;
	}
}
