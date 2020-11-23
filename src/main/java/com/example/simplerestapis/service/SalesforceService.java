package com.example.simplerestapis.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.simplerestapis.models.SalesforceOrg;
import com.example.simplerestapis.models.User;
import com.example.simplerestapis.repository.SalesforceOrgRepository;

@Service
public class SalesforceService {

	@Autowired
	private SalesforceOrgRepository repository;

	@Autowired
	private UserService service;

	@Autowired
	private Environment env;

	@Autowired
	private UtilService utilService;
//	String authToken;

	public SalesforceOrg addOrg(SalesforceOrg obj) {
		return repository.save(obj);
	}

	public SalesforceOrg getOrg(String orgId) {
		return repository.findById(orgId).orElse(null);
	}

	public String renewAccess(String orgId) {
		RestTemplate restTemplate = new RestTemplate();
		SalesforceOrg sfOrg = getOrg(orgId);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

//		String url = "https://login.salesforce.com/services/oauth2/token";
		String url = env.getProperty("app.sf.access_token.uri");

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("client_id", env.getProperty("app.sf.client_id"));
		map.add("redirect_uri", env.getProperty("app.sf.redirect.uri2"));
		map.add("client_secret", env.getProperty("app.sf.client_secret"));
		map.add("refresh_token", sfOrg.getRefreshToken());
		map.add("grant_type", "refresh_token");
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
		JSONObject obj1 = new JSONObject(response.getBody());
		System.out.println("response obj: " + response);
		String token = obj1.getString("access_token");
		System.out.println("response obj: " + token);
		sfOrg.setAccessToken(token);
		repository.save(sfOrg);
		return token;
	}

	public String authorizeOrg(String code, HttpServletRequest rqst) {

		String accessToken = "";
		String refreshToken = "";
		String clientId = env.getProperty("app.sf.client_id");
		String clientSecret = env.getProperty("app.sf.client_secret");
		String organizationId = "";
		String issuedAt = "";
		String identityUrl = "";
		String instanceUrl = "";
		String username = "";
		String nickName = "";
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

//		String url = "https://login.salesforce.com/services/oauth2/token";
		String url = env.getProperty("app.sf.access_token.uri");

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("client_id", clientId);

		map.add("redirect_uri", env.getProperty("app.sf.redirect.uri"));

		map.add("client_secret", clientSecret);
		map.add("code", code);
		map.add("grant_type", "authorization_code");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
			JSONObject obj = new JSONObject(response.getBody());

			accessToken = obj.getString("access_token");
			refreshToken = obj.getString("refresh_token");
			instanceUrl = obj.getString("instance_url");
			identityUrl = obj.getString("id");
			issuedAt = obj.getString("issued_at");
		} catch (Exception e) {
			System.out.println("Failed to get Access Token: \n" + e);
		}

		String url2 = identityUrl;
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url2);

		MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
		vars.add("oauth_token", accessToken);
		vars.add("format", "json");
		builder.queryParams(vars);

		try {
			String response2 = restTemplate.getForObject(builder.build().encode().toUriString(), String.class, vars);
			JSONObject obj2 = new JSONObject(response2);
			organizationId = obj2.getString("organization_id");
			username = obj2.getString("username");
			nickName = obj2.getString("nick_name");
		} catch (Exception e) {
			System.out.println("Failed to fetch org details: \n" + e);
		}

		String user_id = null;
		user_id = utilService.readCookie(rqst, "user_id");
		if (user_id.equals(null))
			return null;

		User user = service.getUserById(Integer.parseInt(user_id));
		SalesforceOrg org = new SalesforceOrg(organizationId, accessToken, refreshToken, identityUrl, instanceUrl,
				issuedAt, username, nickName, user);
		this.addOrg(org);

		return organizationId;

	}


	public ArrayList<Map<String, String>> getOrgList(String user_id) {
		ArrayList<Map<String, String>> res = new ArrayList<Map<String, String>>();
		ArrayList<SalesforceOrg> sfOrgs = repository.findByuser_id(Integer.parseInt(user_id));
		for (SalesforceOrg sfOrg : sfOrgs) {
			Map<String, String> mp = new HashMap<String, String>();
			mp.put("org_id", sfOrg.getId());
			mp.put("org_label", sfOrg.getInstanceUrl().substring(8));
			mp.put("username", sfOrg.getUsername());
			System.out.println(sfOrg.getUsername());
			mp.put("nick_name", sfOrg.getNickName());
			res.add(mp);
		}
		return res;
	}
}
