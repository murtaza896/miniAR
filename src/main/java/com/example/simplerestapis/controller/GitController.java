package com.example.simplerestapis.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.example.simplerestapis.config.JwtTokenUtil;
import com.example.simplerestapis.models.CommitHistory;
import com.example.simplerestapis.models.CommitHistoryResponse;
import com.example.simplerestapis.models.GitAccounts;
import com.example.simplerestapis.models.GitStore;
import com.example.simplerestapis.service.CommitHistoryService;
import com.example.simplerestapis.service.FileBasedDeployAndRetrieve;
import com.example.simplerestapis.service.GitAccountsService;
import com.example.simplerestapis.service.GitStoreService;
import com.example.simplerestapis.service.JGitService2;
import com.example.simplerestapis.service.UserService;
import com.example.simplerestapis.service.UtilService;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
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
	private UserService userService;

	@Autowired
	private JGitService2 jgitService2;

	@Autowired
	private FileBasedDeployAndRetrieve fileBasedDeployAndRetrieve;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private CommitHistoryService commitHistoryService;
	
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
//		String userId = utilService.readCookie(request, "user_id");
		
		final String requestTokenHeader = utilService.readCookie(request, "token");
		System.out.println(requestTokenHeader);
		String username = null;
		String jwtToken = null;
		// JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
		if (requestTokenHeader != null) {
			jwtToken = requestTokenHeader;
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			} catch (IllegalArgumentException e) {
				System.out.println("Unable to get JWT Token");
			} catch (Exception e) {
				System.out.println("JWT Token has expired");
			}
		} else {
			System.out.println("JWT Token does not begin with Bearer String");
		}
		
		String userId = userService.getIdByEmail(username) + "";
		System.out.println("userId: " + userId);
		gitAccountsService.authorizeGitAcc(code, userId);
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl(env.getProperty("app.angular.pages.settings"));
		return redirectView;
	}

	@GetMapping("/list-repos/{accountId}")
	public ArrayList<GitStore> listRepos(@PathVariable int accountId, HttpServletRequest request) {
//		String userId = utilService.readCookie(request, "user_id");
		String userId = userService.getIdByEmail(request.getAttribute("email").toString()) + "";
		return gitStoreService.listRepos(accountId, Integer.parseInt(userId));
	}

	@GetMapping("list-accounts")
	public ArrayList<GitAccounts> listGitAccounts(HttpServletRequest request) {
//		String userId = utilService.readCookie(request, "user_id");
		String userId = userService.getIdByEmail(request.getAttribute("email").toString()) + "";
		return gitAccountsService.listGitAccounts(Integer.parseInt(userId));
	}

	@PostMapping(value = "/addFile")
	public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @RequestParam(name="org_id") String orgId, HttpServletRequest request) {
		int userId = utilService.readIdFromToken(request);
		 
		
		String path = env.getProperty("app.data.dirPath") + File.separator + userId + File.separator + orgId + File.separator + "package.xml" ;
//		path = context.getRealPath(path);
		
		
		System.out.println("uploading package.xml to: " + path);
//		String path = "D:\\Integration\\miniAR\\files\\1\\" + file.getOriginalFilename();
		
		File directory = new File(path);
		if (!directory.exists()) {
			directory.mkdirs();
			System.out.println("directory not exists");
		}
		System.out.println("path:" + path);

		try {
			file.transferTo(directory);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<>( HttpStatus.OK);
	}

	@PostMapping(path = "/commit", headers = "Accept=application/json")
	public Boolean gitCommit(@RequestBody Map<String, String> data, HttpServletRequest request) {
		String org_id = data.get("org_id");
		String repoUrl = data.get("repo_url");
		String accId = data.get("acc_id");
		String message = data.get("commit_msg");
		String repoId = data.get("repo_id");
		String repoName = data.get("repo_name");
		String gitUsername = data.get("git_username");
		int userId = utilService.readIdFromToken(request);
		
//		String path = env.getProperty("app.sf.files.uri");
		String path = env.getProperty("app.data.dirPath") + File.separator + userId  + File.separator + org_id;
		
		GitAccounts gitAccount = gitAccountsService.getUserById(Integer.parseInt(accId));

		String accessToken = gitAccount.getAccess_token();
		String username = gitAccount.getUsername();

		if (jgitService2.gitClone(accessToken, repoUrl, path + File.separator + repoId)) {
			try {
				fileBasedDeployAndRetrieve.createMetadataConnection("retrieve", org_id, userId, repoId, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String src = path + File.separator + "SFData.zip";
			String dest = path + File.separator + "SFData";
			try {
				fileBasedDeployAndRetrieve.unzip(src , dest);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			File targetDir = new File(path + File.separator + repoId); 

			File sourceDirUnZip = new File(dest);

			try {
				jgitService2.copyDirectory(sourceDirUnZip, targetDir);

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
			
			if (jgitService2.gitCommit(path + File.separator + repoId, message, username, repoUrl, userId, org_id, repoName, gitUsername, accId, repoId)) {
				return jgitService2.gitPush(accessToken, path + File.separator + repoId);
			}
			
			return true;
		}

		return false;
	}

//	@GetMapping("/deploy/{orgId}")
//	public ModelAndView deployData(@PathVariable String orgId, HttpServletRequest request) {
//		ModelAndView mv = new ModelAndView();
//		int userId = utilService.readIdFromToken(request);
//		mv.setViewName("retrieve");
//		try {
//			fileBasedDeployAndRetrieve.createMetadataConnection("deploy", orgId, userId);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return mv;
//	}
	
	@GetMapping("/commit-history")
	public ArrayList<CommitHistoryResponse> listCommitHistory(HttpServletRequest request){
		String userId = userService.getIdByEmail(request.getAttribute("email").toString())+"";
		System.out.println(userId);
		ArrayList<CommitHistory> commitHistories = commitHistoryService.listCommitHistory(Integer.parseInt(userId));
		ArrayList<CommitHistoryResponse> chResponse = new ArrayList<CommitHistoryResponse>();
		System.out.println("this is size of history array:::" + commitHistories.size());
		for(int i = 0; i<commitHistories.size(); i++) {
			
			CommitHistory commitHistory = commitHistories.get(i);
			System.out.println("my id is ......" + commitHistory.toString());
			CommitHistoryResponse res = new CommitHistoryResponse(commitHistory.getGit_username(), commitHistory.getTimestamp(), commitHistory.getRepo_name(), commitHistory.getRepo_url(), commitHistory.getCommit_hash(), commitHistory.getCommit_msg(), commitHistory.getSforg().getNickName(), commitHistory.getGitAccount().getId() + "", commitHistory.getSforg().getId(), commitHistory.getRepo_id());
			chResponse.add(res);
		}
		
		return chResponse;
	}
	
	@PostMapping("/test-deploy")
	public ResponseEntity<?> testDeployment(@RequestBody String obj1, HttpServletRequest request) throws InvalidRemoteException, TransportException, GitAPIException, JSONException, IOException {
		JSONObject obj = new JSONObject(obj1);
		try {
			//obj = new JSONObject(obj1);
			
			System.out.println(obj.toString());
			
			String targetOrgId = obj.getString("target_org_id");
			String orgId = obj.getString("org_id");
			String repoId = obj.getString("repo_id");
			String userId = userService.getIdByEmail(request.getAttribute("email").toString())+"";
			//System.out.println(obj.get("access_token") + " " + obj.get("repo_url") + " " + obj.get("commit_hash")  + " " +  obj.get("path"));
			
			String path = env.getProperty("app.data.dirPath") + File.separator + userId  + File.separator + obj.getString("org_id") + File.separator +obj.getString("repo_id");
			
			int account_id = Integer.parseInt(obj.getString("account_id")); 
			jgitService2.testDeploy(gitAccountsService.getUserById(account_id).getAccess_token(), obj.getString("repo_url"), obj.getString("commit_hash"), path, targetOrgId, repoId, Integer.parseInt(userId), orgId);
			System.out.println(obj);
			return new ResponseEntity<>(obj.toMap(), HttpStatus.OK);
		}
		catch(Exception e) {
			return new ResponseEntity<>(obj.toMap(), HttpStatus.BAD_REQUEST);
		}
		
	}
}
