package com.intellisoft.ibmcallforcode.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class IamTokenService {
	
	private static final String IAM_URL = "https://iam.cloud.ibm.com/identity/token";
	
	private static final String API_KEY = "MqqSm87AIX_jWOtaxjTcbu4habepEEFKeZ0hJWAXRNBI";
	
	private static final String GRANT_TYPE = "urn:ibm:params:oauth:grant-type:apikey";
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	private String accessToken;
	
	private int expiresIn;
	
	public String refreshIamToken() {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			
			MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
			requestBody.add("grant_type", GRANT_TYPE);
			requestBody.add("apikey", API_KEY);
			
			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);
			ResponseEntity<Map> response = restTemplate.exchange(IAM_URL, HttpMethod.POST, entity, Map.class);
			
			if (response.getStatusCode().is2xxSuccessful()) {
				Map<String, Object> responseBody = response.getBody();
				accessToken = (String) responseBody.get("access_token");
				expiresIn = (Integer) responseBody.get("expires_in");
				log.info("IAM Token refreshed successfully: " + accessToken);
				return accessToken;
			} else {
				log.error("Failed to refresh IAM token. Status code: " + response.getStatusCode());
			}
		}
		catch (Exception e) {
			log.error("Error refreshing IAM token: ", e);
		}
		return null;
	}
	
	/**
	 * Returns the access token
	 */
	public String getAccessToken() {
		if (accessToken == null || isTokenExpired()) {
			refreshIamToken();
		}
		return accessToken;
	}
	
	/**
	 * Checks if the token is expired based on its expiry time.
	 */
	private boolean isTokenExpired() {
		return expiresIn <= 0;
	}
	
	/**
	 * Prepare a HttpEntity with the Bearer token added to the Authorization header.
	 */
	public HttpEntity<?> createAuthorizedRequest(HttpHeaders headers) {
		headers.set("Authorization", "Bearer " + getAccessToken());
		return new HttpEntity<>(headers);
	}
	
	public int getTokenExpiry() {
		return expiresIn;
	}
	
}