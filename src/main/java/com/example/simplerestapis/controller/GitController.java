package com.example.simplerestapis.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.example.simplerestapis.models.GitAccounts;
import com.example.simplerestapis.models.GitStore;
import com.example.simplerestapis.service.FileBasedDeployAndRetrieve;
import com.example.simplerestapis.service.GitAccountsService;
import com.example.simplerestapis.service.GitStoreService;
import com.example.simplerestapis.service.JGitService2;
import com.example.simplerestapis.service.UtilService;

@CrossOrigin(origins = "http://localhost:4200" , allowCredentials = "true")
@RestController
@RequestMapping("/git")
public class GitController {
	
	@Autowired
	private UtilService utilService;
	
	@Autowired
	private GitStoreService gitStoreService;
	
	@Autowired
	private GitAccountsService gitAccountsService;
	
	@Autowired
	private JGitService2 jgitService2;
	
	@Autowired
	private FileBasedDeployAndRetrieve fileBasedDeployAndRetrieve;
	
	@GetMapping("/oauth")
	public ModelAndView oauthGit() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("oauthGit");
		return mv;
	}
	
	@Autowired
	private Environment env;
	
	
	@GetMapping("/new-repo")
	public RedirectView authorizeGitAcc(@RequestParam String code, HttpServletRequest request) {
		String userId = utilService.readCookie(request, "user_id");
		gitAccountsService.authorizeGitAcc(code, userId);
		RedirectView redirectView = new RedirectView();
	    redirectView.setUrl(env.getProperty("app.angular.pages.settings"));
	    return redirectView;
	}


	@GetMapping("/list-repos/{accountId}")
	public ArrayList<GitStore> listRepos(@PathVariable int accountId, HttpServletRequest request)
	{	
		String userId = utilService.readCookie(request, "user_id");
		return gitStoreService.listRepos(accountId, Integer.parseInt(userId));
	}
	

	@GetMapping("list-accounts")
	public ArrayList<GitAccounts> listGitAccounts(HttpServletRequest request){
		String userId = utilService.readCookie(request, "user_id");
		return gitAccountsService.listGitAccounts(Integer.parseInt(userId));
	}

	
	@PostMapping(path = "/commit", headers = "Accept=application/json")
	public Boolean gitCommit(@RequestBody Map<String, String> data)
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
		
		
		if(jgitService2.gitClone(accessToken, repoUrl, path))
		{
			
			try {
				fileBasedDeployAndRetrieve.createMetadataConnection("retrieve",org_id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				fileBasedDeployAndRetrieve.unzip(env.getProperty("app.sf.metadata.dirpathZip"), env.getProperty("app.sf.metadata.dirpathUnZip"));
			} catch (Exception e1) {
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
			
			if(jgitService2.gitCommit(path, message, username))
			{
				return jgitService2.gitPush(accessToken, path);
			}
		}
		
		return false;
	}

	@GetMapping("/deploy/{orgId}")
	public ModelAndView deployData(@PathVariable String orgId) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("retrieve");
		try {
			fileBasedDeployAndRetrieve.createMetadataConnection("deploy",orgId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
}
