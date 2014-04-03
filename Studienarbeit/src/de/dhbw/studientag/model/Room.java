package de.dhbw.studientag.model;

import android.os.Parcel;
import android.os.Parcelable;


public class Room implements Parcelable{

	private String roomNo;
	private long id;
	/**
	 * represents the favored subject of the companies which are presenting themselves in this room
	 */
	private long subjectId;
	
	public Room(String roomNo){
		this.roomNo=roomNo;
		
	}
	
	public Room(long id, String roomNo, long subjectId){
		this(roomNo);
		this.id=id;
		this.subjectId=subjectId;
		
	}
	
	public String getRoomNo() {
		return roomNo;
	}

	public long getSubjectId() {
		return subjectId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return this.roomNo;
	}

	@Override
	public int describeContents() {
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(roomNo);
		dest.writeLong(subjectId);
	}
	
	private Room(Parcel source){
		this(source.readLong(), source.readString(),source.readLong());
	}
	
   public static final Parcelable.Creator<Room> CREATOR = 
            new Parcelable.Creator<Room>() {
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

}
