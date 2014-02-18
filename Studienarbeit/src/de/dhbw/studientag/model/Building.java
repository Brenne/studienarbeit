package de.dhbw.studientag.model;


import java.util.ArrayList;

import android.util.Log;

public  class Building {
	
	private final String fullName;
	private final String shortName;
	private final ArrayList<Floor> floorList = new ArrayList<Floor>();
			
	public Building(String shortName, String fullName){
		this.fullName=fullName;
		this.shortName=shortName;
	}

	public String getFullName() {
		return fullName;
	}
	public String getShortName() {
		return shortName;
	}

	public ArrayList<Floor> getFloorList() {
		return floorList;
	}

	public void addFloor(Floor floor) {
		if(!floorNumberUsed(floor.getNumber()))
				this.floorList.add(floor);
		else
			Log.d("studientag", "Floor Number already assosiated with building "
					+getShortName() + ". Floor "+ floor.getName() + "not added.");
	}
	
	private boolean floorNumberUsed(int i){
		for(Floor floor : this.floorList){
			if(floor.getNumber()==i)
				return true;
		}
		return false;
	}


	


}
