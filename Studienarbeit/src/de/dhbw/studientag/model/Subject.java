package de.dhbw.studientag.model;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Subject = degree course. Every Subject belongs to a faculty. Companies offer subjects.
 * 
 *
 */
public class Subject implements Parcelable {

	private long id;
	private String name;
	private Faculty faculty;
	private String webAddress;
	/**
	 * the color of a subject represents the color used in the DHBW flyer
	 */
	private Color color;

	public Subject(String name, Faculty faculty, String webAddress, Color color) {
		this.name = name;
		this.faculty = faculty;
		this.webAddress=webAddress;
		this.color=color;
	}

	public Subject(long id, String name, Faculty faculty, String webAddress, Color color) {
		this(name, faculty, webAddress, color);
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
	
	public Color getColor(){
		return color;
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
		dest.writeParcelable(color, flags);

	}

	private Subject(Parcel source) {
		this(source.readLong(), source.readString(), (Faculty) source
				.readSerializable(), source.readString(), (Color) source.readParcelable(Color.class.getClassLoader()));

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
