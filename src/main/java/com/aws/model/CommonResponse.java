package com.aws.model;


import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class CommonResponse{
	
	private HttpStatus status;
    private Response response;
    private String message;


}
