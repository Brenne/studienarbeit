package de.dhbw.studientag.model;


public class Company {
	
//	private int id;
	private String name;
	private String street;
	private String place;
	private String website;

	
	
	public Company(String name, String street, String place){
		this.name=name;
		this.street=street;
		this.place=place;
	}
	
	public Company(String name, String street, String place, String website){
		this(name, street, place);
		this.website=website;
	}


	public String getName() {
		return name;
	}
	
	public String getAdress(){
		return street+", "+place;
	}

	public String getWebiste(){
		return website;
	}
}
