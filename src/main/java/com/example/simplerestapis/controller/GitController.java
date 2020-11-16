package com.example.simplerestapis.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import com.example.simplerestapis.JGitDemoApplication;
import com.example.simplerestapis.models.Git;
import com.example.simplerestapis.service.IGitService;

@Controller
public class GitController {

	@Autowired
	private IGitService gitService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}

	@RequestMapping(value = "/list-commits", method = RequestMethod.GET)
	public String showGits(ModelMap model) {
		String name = getLoggedInUserName(model);
		model.put("commits", gitService.getGitsByUser(name));
		// model.put("todos", service.retrieveTodos(name));
		return "list-commits";
	}

	private String getLoggedInUserName(ModelMap model) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetails) {
			return ((UserDetails) principal).getUsername();
		}

		return principal.toString();
	}

	@RequestMapping(value = "/add-commit", method = RequestMethod.GET)
	public String showAddGitPage(ModelMap model) {
		model.addAttribute("git", new Git());
		return "commit";
	}

//	@RequestMapping(value = "/delete-todo", method = RequestMethod.GET)
//	public String deleteTodo(@RequestParam long id) {
//		todoService.deleteTodo(id);
//		// service.deleteTodo(id);
//		return "redirect:/list-todos";
//	}
//
//	@RequestMapping(value = "/update-todo", method = RequestMethod.GET)
//	public String showUpdateTodoPage(@RequestParam long id, ModelMap model) {
//		Git todo = todoService.getTodoById(id).get();
//		model.put("todo", todo);
//		return "todo";
//	}
//
//	@RequestMapping(value = "/update-todo", method = RequestMethod.POST)
//	public String updateTodo(ModelMap model, @Valid Git todo, BindingResult result) {
//
//		if (result.hasErrors()) {
//			return "todo";
//		}
//
//		todo.setUsername(getLoggedInUserName(model));
//		todoService.updateTodo(todo);
//		return "redirect:/list-todos";
//	}
	
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String showSuccessPage(ModelMap model) {
		model.addAttribute("success", new Git());
		return "success";
	}

	@RequestMapping(value = "/add-commit", method = RequestMethod.POST)
	public String addCommit(ModelMap model, Git git, BindingResult result) throws IOException {

		if (result.hasErrors()) {
			return "commit";
		}	
		JGitDemoApplication jda = new JGitDemoApplication(git.getRepoURL(),git.getUsername(),git.getPassword(),git.getMessage());
		File path = new File ("D:\\DemoPresentation\\Testing");
		File source = new File("D:\\DemoPresentation\\sample");
		jda.repoClone();
		JGitDemoApplication.copyDirectory(source, path);
		jda.makeAChange();
		jda.gitCommit();
		jda.gitPush();
		
		
		return "redirect:/success";
	}
}
