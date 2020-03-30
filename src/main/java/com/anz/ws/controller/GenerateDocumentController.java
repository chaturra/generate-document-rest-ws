package com.anz.ws.controller;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anz.ws.model.CustomerDetails;
import com.anz.ws.model.CustomerInfo;
import com.anz.ws.response.model.ApiResponse;
import com.anz.ws.service.ContentReplaceService;


@RestController
@RequestMapping("/api")
public class GenerateDocumentController {
	@Autowired
	ContentReplaceService serv;
	
	@GetMapping("/")
	public String health() {
		return "{healthy:true}";
	}
	
	@PostMapping("/generatedoc")
	public ApiResponse generateDocument(@RequestBody CustomerDetails cust) throws InvalidFormatException, IOException
	{
		ApiResponse res= serv.FindReplaceContent(cust);
//		returnedResponse.setResultCode("200");
		return res;
		
	}
	
	@PostMapping("/flatfile")
	public ApiResponse generateDocumentWithFlatFile(@RequestBody CustomerInfo custInfo) throws InvalidFormatException, IOException
	{
		System.out.println("inside flat file"); 
		ApiResponse res= serv.readContentFromFlatFile(custInfo);
//		returnedResponse.setResultCode("200");
		return res;
		
	}

}
