package de.dhbw.studientag.model;


public class Company {
	
//	private int id;
	private String name;
	private String street;
	private String plz;
	private String city;
	private String website;

	
	
	public Company(String name, String street, String city, String plz){
		this.name=name;
		this.street=street;
		this.city=city;
		this.plz=plz;
	}
	
	public Company(String name, String street, String city, String plz, String website){
		this(name, street, city,plz);
		this.website=website;
	}


	public String getName() {
		return name;
	}
	
	public String getAdress(){
		return street+", "+city;
	}

	public String getCity(){
		return city;
	}

	public String getStreet(){
		return street;
	}
	
	public String getWebiste(){
		return website;
	}

	public String getPlz() {
		return plz;
	}

	@Override
	public String toString() {
		return this.getName();
	}

}
