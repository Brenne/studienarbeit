package de.dhbw.studientag.model;


import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Floor implements Parcelable {
	

	private long id;
	private int number;
	private String name;
	private ArrayList<Room> roomList = new ArrayList<Room>();
	
	public Floor(int number, String name){
		this.number=number;
		this.name=name;
	}
	
	public Floor(long id, int number, String name){
		this(number,name);
		this.id=id;
	}

	public long getId(){
		return this.id;
	}
	
	public void setId(long id){
		this.id=id;
	}
	
	public int getNumber() {
		return number;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Room> getRoomList() {
		return roomList;
	}

	public void addRoom(Room room) {
		for(Room roomInList : roomList){
			if(roomInList.getRoomNo().equals(room.getRoomNo())){
				Log.d("studientag", "Room already listed in floor "+ getName());
				return;
			}
		}
		this.roomList.add(room);
	}
	
	public void setRoomList(List<Room> roomList){
		for(Room room: roomList)
			addRoom(room);
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
		dest.writeInt(number);
		dest.writeString(name);
		dest.writeTypedList(roomList);
		
	}
	
	private Floor(Parcel source){
		this(source.readLong(), source.readInt(), source.readString());
		source.readTypedList(roomList, Room.CREATOR);
	}
	
   public static final Parcelable.Creator<Floor> CREATOR = 
            new Parcelable.Creator<Floor>() {
        public Floor createFromParcel(Parcel in) {
            return new Floor(in);
        }

        public Floor[] newArray(int size) {
            return new Floor[size];
        }
    };
	

}
