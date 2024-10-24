package com.intellisoft.ibmcallforcode.controller;

import com.intellisoft.ibmcallforcode.service.IamTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/ibm/watson", produces = "application/json")
public class LlmController {
	
	private final IamTokenService iamTokenService;
	
	public LlmController(IamTokenService iamTokenService) {
		this.iamTokenService = iamTokenService;
	}
	
	@PostMapping("/invoke")
	public ResponseEntity<String> invoke(@RequestBody String message) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(iamTokenService.makeAuthenticatedApiCall(message));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error invoking API: " + e.getMessage());
		}
	}
}
