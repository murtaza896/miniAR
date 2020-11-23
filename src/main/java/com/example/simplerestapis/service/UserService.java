package com.example.simplerestapis.service;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.simplerestapis.models.User;
import com.example.simplerestapis.models.userCredentials;
import com.example.simplerestapis.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private UtilService utilService;
	
	public List<User> getAllUsers() {
		return repository.findAll();
	}
	
	public int addUser(User obj) {
//		obj.setPassword(utilService.hashPass(obj.getPassword()));
		if(this.checkExistence(obj.getEmail()) == -1)
		{
			User res = repository.save(obj);	
			return res.getId();
		}
		else
		{
			return -1;
		}
	}
	
	public User getUserById(int id) {
		return repository.findById(id).orElse(null);
	}
	
	public int checkExistence(String email) 
	{
		User userRecord = repository.findByEmail(email);
		
		if(userRecord == null)
		{
			return -1;
		}
		
		return userRecord.getId();
	}
	
 	public int validateUser(userCredentials user, HttpServletResponse response) {
		
 		User userRecord = repository.findByEmail(user.email);
		
		if(userRecord == null)
		{
			return -1;
		}
		
		else if(userRecord.getPassword().equals(user.password)) 
		{
			Cookie cookie = new Cookie("user_id", String.valueOf(userRecord.getId()));
			response.addCookie(cookie);
			return userRecord.getId();
		}
		
		return 0;
	}
	
	public String deleteUser(int id) {
		if(getUserById(id) != null)
			repository.deleteById(id);
		else
			return "Invalid id";
		return "User [ " + id + " ] Deleted Succesfully";
	}
	
	public User updateUser(User new_user) {
		User existing_user = repository.findById((int)new_user.getId()).orElse(null);
		
		existing_user.setFirst_name(new_user.getFirst_name());
		existing_user.setLast_name(new_user.getLast_name());
		existing_user.setEmail(new_user.getEmail());
		existing_user.setPassword(new_user.getPassword());
		
		return repository.save(existing_user);
	}
}
