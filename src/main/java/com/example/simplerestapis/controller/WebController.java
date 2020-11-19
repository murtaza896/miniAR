package com.example.simplerestapis.controller;

import java.io.File;

import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.example.simplerestapis.models.GitAccounts;
import com.example.simplerestapis.models.GitStore;
import com.example.simplerestapis.models.User;
import com.example.simplerestapis.models.userCredentials;
import com.example.simplerestapis.service.FileBasedDeployAndRetrieve;
import com.example.simplerestapis.service.GitAccountsService;
import com.example.simplerestapis.service.GitStoreService;
import com.example.simplerestapis.service.JGitService2;
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
	private FileBasedDeployAndRetrieve fileBasedDeployAndRetrieve;
	
	@Autowired
	private GitAccountsService gitAccountsService;
	
	@Autowired
	private JGitService2 jgitService2;
	
	@Autowired
	private Environment env;
	
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
	public RedirectView authorizeOrg(@RequestParam(required = false) String code, HttpServletRequest request) 
	{
		SFservice.authorizeOrg(code, request);
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl(env.getProperty("app.angular.pages.settings"));
	    return redirectView;
	}
	@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
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
	public RedirectView authorizeGitAcc(@RequestParam String code, HttpServletRequest request) {
		String userId = SFservice.readCookie(request, "user_id");
		gitAccountsService.authorizeGitAcc(code, userId);
		RedirectView redirectView = new RedirectView();
	    redirectView.setUrl(env.getProperty("app.angular.pages.settings"));
	    return redirectView;
	}
	

	
	@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")

	@GetMapping("/list-repos/{accountId}")
	public ArrayList<GitStore> listRepos(@PathVariable int accountId, HttpServletRequest request)
	{	
		String userId = SFservice.readCookie(request, "user_id");
		return gitStoreService.listRepos(accountId, Integer.parseInt(userId));
	}
	@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
	@GetMapping("list-git-accounts")
	public ArrayList<GitAccounts> listGitAccounts(HttpServletRequest request){
		String userId = SFservice.readCookie(request, "user_id");
		return gitAccountsService.listGitAccounts(Integer.parseInt(userId));
	}
	
//	@GetMapping("/retrieve/{orgId}")
//	public ModelAndView retrieveData(@PathVariable String orgId) {
//		ModelAndView mv = new ModelAndView();
//		mv.setViewName("retrieve");
//		try {
//			fbd.createMetadataConnection("retrieve",orgId);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return mv;
//	}
	
//	@GetMapping("/deploy/{orgId}")
//	public ModelAndView deployData(@PathVariable String orgId) {
//		ModelAndView mv = new ModelAndView();
//		mv.setViewName("retrieve");
//		try {
//			fileBasedDeployAndRetrieve.createMetadataConnection("deploy",orgId);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return mv;
//	}
	
	@PostMapping(path = "/git-commit", headers = "Accept=application/json")
	public Boolean gitClone(@RequestBody Map<String, String> data)
	{
		String org_id = data.get("org_id");
		String repoUrl = data.get("repo_url");
		String accId = data.get("acc_id");
		String message = data.get("commit_msg");
		String path = env.getProperty("app.git.clone.dirpath");
		System.out.println(data);
		GitAccounts gitAccount = gitAccountsService.getUserById(Integer.parseInt(accId));
		
		String accessToken = gitAccount.getAccess_token();
		String username = gitAccount.getUsername();
		
		
		//Cloning the git repo
		if(jgitService2.gitClone(accessToken, repoUrl, path))
		{
			
			//Retrieving  the Salesforce metadata
			try {
				fileBasedDeployAndRetrieve.createMetadataConnection("retrieve",org_id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				fileBasedDeployAndRetrieve.unzip(env.getProperty("app.sf.metadata.dirpathZip"), env.getProperty("app.sf.metadata.dirpathUnZip"));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			File targetDir = new File(env.getProperty("app.git.clone.dirpath"));
			File sourceDirUnZip = new File(env.getProperty("app.sf.metadata.dirpathUnZip"));
			try 
			{
				jgitService2.copyDirectory(sourceDirUnZip, targetDir);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
			
			
			//Commiting and pushing to Git
			if(jgitService2.gitCommit(path, message, username))
			{
				return jgitService2.gitPush(accessToken, path);
			}
		}
		
		return false;
	}
}


