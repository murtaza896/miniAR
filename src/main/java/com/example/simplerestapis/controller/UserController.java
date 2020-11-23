package com.example.simplerestapis.controller;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.simplerestapis.config.JwtTokenUtil;
import com.example.simplerestapis.models.JwtRequest;
import com.example.simplerestapis.models.JwtResponse;
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
	
	@Autowired
	private PasswordEncoder bcryptEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserService jwtInMemoryUserDetailsService;
	
	@GetMapping("/check-existence")
	public int checkExistence(@RequestParam(name="email") String email)
	{
		return userService.checkExistence(email);
		// -1: not exists, 1: exists
	}
	
	@PostMapping("/sign-up")
	public ResponseEntity<UserResponse> signUp(@RequestBody User user) {
		
		user.setPassword(bcryptEncoder.encode(user.getPassword()));
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
	
//	@PostMapping("/login")
//	public ResponseEntity<UserResponse> login(@RequestBody userCredentials user, HttpServletResponse response) throws NoSuchAlgorithmException {
//		
//		int userId = userService.validateUser(user, response);
//		UserResponse userResponse = new UserResponse();
//		userResponse.setuser_id(userId);
//		
//		if(userId > 0)
//		{	
//			userResponse.setMessage("User Logged in succesfully");
//			
//			return new ResponseEntity<UserResponse>(userResponse, HttpStatus.OK);
//		}
//		else if(userId == 0)
//		{
//			userResponse.setMessage("Bad Credentials");
//			return new ResponseEntity<UserResponse>(userResponse, HttpStatus.BAD_REQUEST);
//		}
//		else
//		{
//			userResponse.setMessage("Couldn't find user");
//			return new ResponseEntity<UserResponse>(userResponse, HttpStatus.NOT_FOUND);
//		}
//			
//	}
	
	@PostMapping("/login")
	public ResponseEntity<?> generateAuthenticationToken(@RequestBody JwtRequest authenticationRequest)
			throws Exception {
		
		System.out.println(authenticationRequest.getUsername() + "username");
		System.out.println(authenticationRequest.getPassword() + "password");
		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
		final UserDetails userDetails = jwtInMemoryUserDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());
		System.out.println(userDetails);
		System.out.println("hi");
		final String token = jwtTokenUtil.generateToken(userDetails);
		
		return ResponseEntity.ok(new JwtResponse(token));
	}
	
	private void authenticate(String username, String password) throws Exception {
		Objects.requireNonNull(username);
		Objects.requireNonNull(password);
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}

	@GetMapping("/add-cookie")
	public String addCookie(@RequestParam String id, HttpServletRequest request , HttpServletResponse response) {
		Cookie cookie = new Cookie("user_id", id);
		cookie.setPath("/");
		response.addCookie(cookie);
		return  id;
	}
}
