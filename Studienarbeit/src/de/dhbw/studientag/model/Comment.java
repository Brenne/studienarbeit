package de.dhbw.studientag.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {

	private long id;
	private Company company;
	private String fullMessage;

	public Comment(long id, Company company, String message) {
		this(company);
		this.id = id;
		this.fullMessage = message;
	}
	
	public Comment(Company company){
		this.company = company;
	}

	public long getId() {
		return id;
	}

	public Company getCompany() {
		return company;
	}

	public String getFullMessage() {
		return fullMessage;
	}

	public String getShortMessage() {
		final int LIMIT = 100;
		if (fullMessage != null && !fullMessage.isEmpty()) {
			// remove line breaks:
			String shortMessage = this.fullMessage.replace("\n", " ").replace("\r", " ");
			if (shortMessage.length() < LIMIT) {
				return shortMessage;
			} else {
				return shortMessage.substring(0, LIMIT).concat("...");
			}
		} else {
			return "";
		}

	}
	
	@Override
	public int describeContents() {
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeParcelable(company, flags);
		dest.writeString(fullMessage);
		
	}
	
	private Comment(Parcel source){
		this(source.readLong(), (Company) source.readParcelable(Company.class.getClassLoader()), source.readString());
		
	}
	
   public static final Parcelable.Creator<Comment> CREATOR = 
            new Parcelable.Creator<Comment>() {
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };


}
