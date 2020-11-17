package com.example.simplerestapis.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.example.simplerestapis.models.User;
import com.example.simplerestapis.models.userCredentials;
import com.example.simplerestapis.service.FileBasedDeployAndRetrieve;
import com.example.simplerestapis.service.GitStoreService;
import com.example.simplerestapis.service.SalesforceService;
import com.example.simplerestapis.service.UserService;

@RestController
public class WebController {
			
	@Autowired
	private UserService service;
	
	@Autowired
	private SalesforceService SFservice;
	
	@Autowired
	private GitStoreService gitStoreService;
	
	@Autowired
	private FileBasedDeployAndRetrieve fbd;
	
		
	@GetMapping("/")
	public ModelAndView welcome() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("index");
		return mv;
	}
	
	@PostMapping("/register")
	public User register(@RequestBody User user) {
		return service.addUser(user);
	}
	
	@PostMapping("/login")
	public User login(@RequestBody userCredentials user, HttpServletResponse response) {
		User user1= service.validateUser(user);
		if(user1 != null && user1.getPassword().equals(user.password)) {
			System.out.println(user1.toString());
			Cookie cookie = new Cookie("user_id", String.valueOf(user1.getId()));
			
			response.addCookie(cookie);
			return user1;
		}
		return null;
	}
	
	@GetMapping("/xyz")
	public String xyz(HttpServletRequest request , HttpServletResponse response) {
		Cookie cookie = new Cookie("user_id", "1");
		response.addCookie(cookie);
		Cookie cookies[] = request.getCookies();
		for(Cookie c : cookies) {
			if(c.getName().equals("user_id"))
				return c.getValue();
		}
		
		return "";
	}
	
	

	@GetMapping("/new-org")
	public String authorizeOrg(@RequestParam(required = false) String code, HttpServletRequest request) 
	{
		return SFservice.authorizeOrg(code, request);
	}

//	@GetMapping("/showRepos")
//	public ArrayList<String> showRepos() throws Exception {
//		URL url = new URL("https://api.github.com/user/repos");
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestProperty("Authorization","Bearer "+"3b32577b36090b71ef53cf0a4ea39856168291ec");
//        conn.setRequestProperty("Content-Type","application/json");
//        conn.setRequestMethod("GET");
//        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        String output;
//
//        StringBuffer response = new StringBuffer();
//        while ((output = in.readLine()) != null) {
//            response.append(output);
//        }
//        JSONArray jsonArr = new JSONArray(response.toString());
//        JSONArray result = new JSONArray();
//        ArrayList<String> array = new ArrayList<String>();
//        System.out.println(jsonArr);
//       for (int i = 0; i < jsonArr.length(); i++)
//        {
//            JSONObject jsonObj = jsonArr.getJSONObject(i);
//            array.add(jsonObj.getString("svn_url"));           
//            System.out.println(jsonObj.getString("svn_url"));
//       }
//        in.close();
//        
//        return array;
//		
//	}
	
//	@GetMapping("/new-org")
//	public ModelAndView authorized(@RequestParam String code) 
//	{
//		String organizationId = SFservice.authorizeOrg(code);
//		
//		ModelAndView mv = new ModelAndView();
//		mv.addObject("orgId",organizationId );
//		mv.setViewName("retrieve");
//		return organizationId;
//	
//	}

	
	@GetMapping("/new-repo")
	public String authorizeGitAcc(@RequestParam String code) {
		return gitStoreService.authorizeGitAcc(code);
	}
	
	@GetMapping("/list-repo")
	public String listRepos()
	{
		return "I am authorized";
	}
	
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


