package de.dhbw.studientag.model;

public class CompanyLocation {

	private Room room;
	private Building building;
	private Company company;
	
	public CompanyLocation(Company company, Building buildng, Room room){
		this.company=company;
		this.building=buildng;
		this.room=room;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {

		this.company = company;
	}

	public Room getRoom() {
		return room;
	}

	public Building getBuilding() {
		return building;
	}

}