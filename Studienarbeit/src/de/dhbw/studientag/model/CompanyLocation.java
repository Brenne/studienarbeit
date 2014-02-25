package de.dhbw.studientag.model;

public class CompanyLocation {
	private String companyName;
	private String buildingShortName;
	private String buildingFullName;
	private String roomNo;

	public CompanyLocation(String companyName, String buildingShortName, String roomNo) {
		this.companyName = companyName;
		this.buildingShortName = buildingShortName;
		this.roomNo = roomNo;
	}

	public CompanyLocation(String companyName, String buildingShortName, String buildingFullName, String roomNo) {
		this(companyName, buildingShortName, roomNo);
		this.buildingFullName=buildingFullName;
	}
	
	public String getCompanyName() {
		return companyName;
	}

	public String getBuildingShortName() {
		return buildingShortName;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public String getBuildingFullName() {
		return buildingFullName;
	}



}