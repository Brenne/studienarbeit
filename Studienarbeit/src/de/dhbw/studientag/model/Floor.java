package de.dhbw.studientag.model;


import java.util.ArrayList;

import android.util.Log;

public class Floor {
	
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
	

	
	

}
