package com.anz.ws.model;

public class CustomerDetails {
	
	private String custName;
	private String custTitle;
	private String facilityAmount;
	private String facilityCurrency;
	private String templateName;
	
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getCustTitle() {
		return custTitle;
	}
	public void setCustTitle(String custTitle) {
		this.custTitle = custTitle;
	}
	public String getFacilityAmount() {
		return facilityAmount;
	}
	public void setFacilityAmount(String facilityAmount) {
		this.facilityAmount = facilityAmount;
	}
	public String getFacilityCurrency() {
		return facilityCurrency;
	}
	public void setFacilityCurrency(String facilityCurrency) {
		this.facilityCurrency = facilityCurrency;
	}
	
}
