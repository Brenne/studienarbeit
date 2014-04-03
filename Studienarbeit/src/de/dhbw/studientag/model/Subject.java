package de.dhbw.studientag.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Subject implements Parcelable {

	private long id;
	private String name;
	private Faculty faculty;
	private String webAddress;

	public Subject(String name, Faculty faculty, String webAddress) {
		this.name = name;
		this.faculty = faculty;
		this.webAddress=webAddress;
	}

	public Subject(long id, String name, Faculty faculty, String webAddress) {
		this(name, faculty, webAddress);
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public Faculty getFaculty() {
		return faculty;
	}
	
	public String getWebAddress() {
		return webAddress;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
		dest.writeString(name);
		dest.writeSerializable(faculty);
		dest.writeString(webAddress);

	}

	private Subject(Parcel source) {
		this(source.readLong(), source.readString(), (Faculty) source
				.readSerializable(), source.readString());

	}

	public static final Parcelable.Creator<Subject> CREATOR = new Parcelable.Creator<Subject>() {
		public Subject createFromParcel(Parcel in) {
			return new Subject(in);
		}

		public Subject[] newArray(int size) {
			return new Subject[size];
		}
	};

}
