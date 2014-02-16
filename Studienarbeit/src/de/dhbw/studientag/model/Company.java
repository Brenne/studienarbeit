package de.dhbw.studientag.model;

import java.io.Serializable;
import java.util.ArrayList;


public class Company implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7389788887935934036L;
	private long id;
	private String name;
	private String street;
	private String plz;
	private String city;
	private String website;
	private ArrayList<Subject> subjectList;

	
	
	public Company(long id, String name, String street, String city, String plz){
		this.id=id;
		this.name=name;
		this.street=street;
		this.city=city;
		this.plz=plz;
	}
	
	public Company(long id, String name, String street, String city, String plz, String website){
		this(id, name, street, city,plz);
		this.website=website;
	}
	
	public long getId(){
		return id;
	}
	
	public void setId(long id){
		this.id=id;
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

	public ArrayList<Subject> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<Subject> subjectList) {
		this.subjectList = subjectList;
	}

	@Override
	public String toString() {
		return this.getName();
	}

}
