package com.example.simplerestapis.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.example.simplerestapis.models.GitRepo;
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
	
	@CrossOrigin("*")
	@GetMapping("/new-org")
	public String authorizeOrg(@RequestParam(required = false) String code) 
	{
		if(code.isEmpty())
		{
			return "I am authorized";
		}
		
		return SFservice.authorizeOrg(code);
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
	
	@GetMapping("/list-repo/{accessToken}")
	public ArrayList<GitRepo> listRepos(@PathVariable String accessToken)
	{	
		return gitStoreService.listRepos(accessToken);
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


