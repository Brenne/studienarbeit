package de.dhbw.studientag.model;

import android.os.Parcel;
import android.os.Parcelable;


public class Room implements Parcelable{

	private String roomNo;
	private long id;
	
	public Room(String roomNo){
		this.roomNo=roomNo;
		
	}
	
	public Room(long id, String roomNo){
		this(roomNo);
		this.id=id;
		
	}
	
	public String getRoomNo() {
		return roomNo;
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
		
	}
	
	private Room(Parcel source){
		this(source.readLong(), source.readString());
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
