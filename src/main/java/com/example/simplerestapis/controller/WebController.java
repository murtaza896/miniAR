package com.example.simplerestapis.controller;

import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.example.simplerestapis.models.GitAccounts;
import com.example.simplerestapis.models.GitStore;
import com.example.simplerestapis.models.User;
import com.example.simplerestapis.models.userCredentials;
import com.example.simplerestapis.service.FileBasedDeployAndRetrieve;
import com.example.simplerestapis.service.GitAccountsService;
import com.example.simplerestapis.service.GitStoreService;
import com.example.simplerestapis.service.SalesforceService;
import com.example.simplerestapis.service.UserService;

@RestController
public class WebController {
			
	@Autowired
	private UserService userService;
	
	@Autowired
	private SalesforceService SFservice;
	
	@Autowired
	private GitStoreService gitStoreService;
	
	@Autowired
	private FileBasedDeployAndRetrieve fbd;
	
	@Autowired
	private GitAccountsService gitAccountsService;
	
	
	@GetMapping("/check-existence")
	public int checkExistence(@RequestParam(name="email") String email)
	{
		return userService.checkExistence(email);
		// -1: not exists, 1: exists
	}
	
	@PostMapping("/sign-up")
	public User signUp(@RequestBody User user) {
		return userService.addUser(user);
	}
	
	@PostMapping("/login")
	public int login(@RequestBody userCredentials user, HttpServletResponse response) {
		return userService.validateUser(user, response);
	}

	@GetMapping("/add-cookie")
	public String addCookie(@RequestParam String id, HttpServletRequest request , HttpServletResponse response) {
		Cookie cookie = new Cookie("user_id", id);
		response.addCookie(cookie);
		return  id;
	}
	
	@GetMapping("/new-org")
	public String authorizeOrg(@RequestParam(required = false) String code, HttpServletRequest request) 
	{
		return SFservice.authorizeOrg(code, request);
	}
	
	@GetMapping("/list-orgs")
	public ArrayList<Map<String, String>> getOrgList(HttpServletRequest request){
		String user_id = SFservice.readCookie(request, "user_id");
		return SFservice.getOrgList(user_id);
	}

	@GetMapping("/oauth-git")
	public ModelAndView oauthGit() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("oauthGit");
		return mv;
	}
	
	
	@GetMapping("/new-repo")
	public int authorizeGitAcc(@RequestParam String code, HttpServletRequest request) {
		String userId = SFservice.readCookie(request, "user_id");
		return gitAccountsService.authorizeGitAcc(code, userId);
	}
	
	
	@CrossOrigin("https://localhost:4200")
	@GetMapping("/list-repos/{accountId}")
	public ArrayList<GitStore> listRepos(@PathVariable int accountId, HttpServletRequest request)
	{	
		String userId = SFservice.readCookie(request, "user_id");
		return gitStoreService.listRepos(accountId, Integer.parseInt(userId));
	}
	
	@GetMapping("list-git-accounts")
	public ArrayList<GitAccounts> listGitAccounts(HttpServletRequest request){
		String userId = SFservice.readCookie(request, "user_id");
		return gitAccountsService.listGitAccounts(Integer.parseInt(userId));
	}
	
//	@GetMapping("/list-mapped-repos/{org_id}")
//	public ArrayList<Map<String, String>> listMappedRepos(@PathVariable String org_id)
//	{	
//		return gitStoreService.listMappedRepos(org_id);
//	}
	
	@GetMapping("/retrieve/{orgId}")
	public ModelAndView retrieveData(@PathVariable String orgId) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("retrieve");
		try {
			fbd.createMetadataConnection("retrieve",orgId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	
	@GetMapping("/deploy/{orgId}")
	public ModelAndView deployData(@PathVariable String orgId) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("retrieve");
		try {
			fbd.createMetadataConnection("deploy",orgId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	
}


