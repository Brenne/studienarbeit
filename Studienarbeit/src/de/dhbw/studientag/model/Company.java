package de.dhbw.studientag.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;


public class Company implements Parcelable{
	
	/**
	 * 
	 */
	private long id;
	private String fullName;
	private String street;
	private String plz;
	private String city;
	private String website;
	private ArrayList<Subject> subjectList = new ArrayList<Subject>();

	
	
	public Company(long id, String fullName, String street, String city, String plz){
		this.id=id;
		this.fullName=fullName;
		this.street=street;
		this.city=city;
		this.plz=plz;
	}
	
	public Company(long id, String fullName, String street, String city, String plz, String website){
		this(id, fullName, street, city,plz);
		this.website=website;
	}
		
	public long getId(){
		return id;
	}
	
	public void setId(long id){
		this.id=id;
	}

	public String getName() {
		return fullName;
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

	@Override
	public int describeContents() {
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(fullName);
		dest.writeString(street);
		dest.writeString(city);
		dest.writeString(plz);
		dest.writeString(website);
		dest.writeTypedList(subjectList);
		
	}
	
	private Company(Parcel source){
		this(source.readLong(), source.readString(), source.readString(), 
				source.readString(), source.readString(), source.readString());
		source.readTypedList(subjectList, Subject.CREATOR);
		
	}
	
   public static final Parcelable.Creator<Company> CREATOR = 
            new Parcelable.Creator<Company>() {
        public Company createFromParcel(Parcel in) {
            return new Company(in);
        }

        public Company[] newArray(int size) {
            return new Company[size];
        }
    };

}
