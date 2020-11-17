package com.example.simplerestapis.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.simplerestapis.models.User;
import com.example.simplerestapis.models.userCredentials;
import com.example.simplerestapis.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository repository;
	
	public List<User> getAllUsers() {
		return repository.findAll();
	}
	
	public User addUser(User obj) {
		return repository.save(obj);
	}
	
	public User getUserById(int id) {
		return repository.findById(id).orElse(null);
	}
	
	public User validateUser(userCredentials user) {
		return repository.findByEmail(user.email);
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
