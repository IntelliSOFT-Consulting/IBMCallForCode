package com.intellisoft.ibmcallforcode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Data
public class RequestDto {
	private String model_id;
	private String project_id;
	private List<Message> messages;
	private int max_tokens;
	private double temperature;
	private int time_limit;
	
	@Getter
	@Setter
	@AllArgsConstructor
	@ToString
	@Data
	public static class Message {
		private String role;
		private Object content;
	}
}
