package com.anz.ws.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import com.anz.ws.model.CustomerDetails;
import com.anz.ws.model.CustomerInfo;
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
		String destTemplate = pro.getProperty("DestinationLocation") + "\\" + templateName + "_" + cust.getCustName()
				+ "-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "-"
				+ new SimpleDateFormat("HHmmss").format(new Date()) + "." + "docx";
		boolean status = saveDoc(doc, destTemplate);
		ApiResponse res = new ApiResponse();
		if (status == true) {
			res.setMessage("success");
			res.setResultCode("200");
		} else if (status == false) {
			res.setMessage("Error in genarting document");
			res.setResultCode("500");
		}

		return res;

	}

	public ApiResponse readContentFromFlatFile(CustomerInfo custInfo) throws IOException, InvalidFormatException {

		System.out.println("inside readContentFromFlatFile");

		Properties pro = new Properties();
		InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
		if (input != null) {
			pro.load(input);
		} else {
			throw new FileNotFoundException("Properties file does not exist in the classpath");
		}
		String templateName = custInfo.getTemplateName();
		System.out.println("template:" + templateName);
		String custName = custInfo.getDocKey();
		System.out.println("custName: " + custName);
		String docType = custInfo.getDocType();
		System.out.println("docType: " +docType);
		String country = custInfo.getCountry();
		System.out.println("country: " +country);
		String baseTemplate = pro.getProperty("sourceTemplateLocation") + "\\" + docType + "\\" + country + "\\"
				+ templateName + "_" + custName + "." + "docx";
		System.out.println("sourceTemplate: " + baseTemplate);
		String custDataFlatFile = pro.getProperty("CustomerFlatFileDataLocation") + "\\" + templateName + "_" + custName
				+ "." + "txt";
		System.out.println("custDataFlatFile: " + custDataFlatFile);
		BufferedReader br = new BufferedReader(new FileReader(custDataFlatFile));
		String det = br.readLine();
		System.out.println("reading text doc: " + det);
		Map<String, String> map1 = new ConcurrentHashMap<String, String>();
		while (det != null) {
			System.out.println("inside while loop");
			String[] arr = det.split("[:]");
			System.out.println(arr);
			if(arr.length>1)
			map1.put(arr[0], arr[1]);
			det = br.readLine();
		}
		Set<String> set1 = map1.keySet();
		List<String> listKey = new ArrayList<String>(set1);
		System.out.println(map1);
		FileInputStream fis = new FileInputStream(baseTemplate);
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
				for (int i = 0; i < set1.size(); i++) {
					if (text != null && text.contains(listKey.get(i))) {
						text = text.replace(listKey.get(i), map1.get(listKey.get(i)));
					}

				}
				run.setText(text, 0);
				System.out.println("after change: " + run.getText(0));

			}

			System.out.println("********************************************************************");
		}
		
		int maxVersion=1;
		int minVersion=0;
		String destTemplate = pro.getProperty("DestinationLocation") + "\\" + templateName + "_" + custName + "_"
				+ "1.0" + "." + "docx";
		if(isCustomerBasedTemplateExist(destTemplate))
		{
			System.out.println("Inside CustomerBased template exist");
			String dirFilePath=pro.getProperty("DestinationLocation");
			File f1=getLatestDoc(dirFilePath);
			String s1=f1.getAbsolutePath();
			String version=existingVersion(s1);
			System.out.println("subString: "+version);
			String [] ver=version.split("[.]");
			System.out.println("array: "+ver[0]);
			int max=Integer.parseInt(ver[0]);
			int min=Integer.parseInt(ver[1]);
			if(min>=9)
			{
				min=0;
				max++;
			}
			else if(min<9)
			{
				min++;
			}
			destTemplate=pro.getProperty("DestinationLocation") + "\\" + templateName + "_" + custName + "_"+max+"."+min+"."+ "docx";
			System.out.println("destTemplate: "+destTemplate);	
		}
		else
		{
			System.out.println("Inside CustomerBased template not exist");
			destTemplate = pro.getProperty("DestinationLocation") + "\\" + templateName + "_" + custName + "_"+ "1.0" + "." + "docx";		
		}
		boolean status = saveDoc(doc, destTemplate);
		ApiResponse res = new ApiResponse();
		if (status == true) {
			res.setMessage("success");
			res.setResultCode("200");
		} else if (status == false) {
			res.setMessage("Error in genarting document from Flat File");
			res.setResultCode("500");
		}

		return res;

	}

	public static boolean isCustomerBasedTemplateExist(String customerBasedTemplate) {
		File f1 = new File(customerBasedTemplate);
		if (f1.exists())
			return true;
		else
			return false;

	}
	
	public static String existingVersion(String s1)
	{
		String [] arr=s1.split("_");
		String s2=arr[arr.length-1];
		System.out.println(s2);
		
		return s2.substring(0, 3);
	}
	
	public static File getLatestDoc(String directoryFilePath)
	{
	    File directory = new File(directoryFilePath);
	    File[] files = directory.listFiles(File::isFile);
	    long lastModifiedTime = Long.MIN_VALUE;
	    File latestFile = null;

	    if (files != null)
	    {
	        for (File file : files)
	        {
	            if (file.lastModified() > lastModifiedTime)
	            {
	            	latestFile = file;
	                lastModifiedTime = file.lastModified();
	            }
	        }
	    }

	    return latestFile;
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
