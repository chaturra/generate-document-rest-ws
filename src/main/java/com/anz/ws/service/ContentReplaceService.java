package com.anz.ws.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import com.anz.ws.model.CustomerDetails;
import com.anz.ws.response.model.ApiResponse;

@Service
public class ContentReplaceService {

	public ApiResponse FindReplaceContent(CustomerDetails cust) throws IOException, InvalidFormatException {
		Properties pro = new Properties();
		InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
		if (input != null) {
			pro.load(input);
		} else {
			throw new FileNotFoundException("Properties file does not exist in the classpath");
		}
		String templateName = cust.getTemplateName();
		String sourceTemplate = pro.getProperty("sourceTemplateLocation") + "\\" + templateName + "." + "docx";
		FileInputStream fis = new FileInputStream(sourceTemplate);
		XWPFDocument doc = new XWPFDocument(OPCPackage.open(fis));
		for (XWPFParagraph paragraph : doc.getParagraphs()) {

			System.out.println(paragraph.getText());
			System.out.println(paragraph.getRuns());
			List<XWPFRun> list1 = paragraph.getRuns();
			for (XWPFRun run : list1) {
				System.out.println(run.getText(0));
				String text = run.getText(0);
				if (text != null && text.contains("<<")) {
					text = text.replace("<<", "");
				}
				if (text != null && text.contains(">>")) {
					text = text.replace("<<", "");
				}
				if (text != null && text.contains("<")) {
					text = text.replace("<", "");
				}
				if (text != null && text.contains(">")) {
					text = text.replace(">", "");
				}
				if (text != null && text.contains("Title")) {
					text = text.replace("Title", cust.getCustTitle());
				}
				if (text != null && text.contains("Name")) {
					text = text.replace("Name", cust.getCustName());
				}
				if (text != null && text.contains("Facilty_Amount")) {
					text = text.replace("Facilty_Amount", cust.getFacilityAmount());
				}
				if (text != null && text.contains("Facility_Currency")) {
					text = text.replace("Facility_Currency", cust.getFacilityCurrency());
				}
				run.setText(text, 0);
				System.out.println("after change: " + run.getText(0));

			}

			System.out.println("********************************************************************");
		}
		String destTemplate = pro.getProperty("DestinationLocation") + "\\" + templateName + "_generated" + "."
				+ "docx";
		boolean status = saveDoc(doc, destTemplate);
		ApiResponse res = new ApiResponse();
		if(status==true)
		{
			res.setMessage("success");
			res.setResultCode("200");
		}
		else if(status==false)
		{
			res.setMessage("Error in genarting document");
			res.setResultCode("500");
		}
		
		return res;

	}

	private boolean saveDoc(XWPFDocument doc, String destLocation) {
		try {
			FileOutputStream fis = new FileOutputStream(destLocation);
			doc.write(fis);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
