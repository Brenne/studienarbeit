package de.dhbw.studientag.model;


import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Building implements Parcelable{
	
	private final String fullName;
	private final String shortName;
	private final ArrayList<Floor> floorList = new ArrayList<Floor>();
	private long id;
			
	public Building(String shortName, String fullName){
		this.fullName=fullName;
		this.shortName=shortName;
	}
	
	public Building(long id, String shortName, String fullName){
		this(shortName,fullName);
		this.setId(id);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
	
	public void setFloorList(List<Floor> floorList){
		for(Floor floor: floorList)
			addFloor(floor);
	}
	
	private boolean floorNumberUsed(int i){
		for(Floor floor : this.floorList){
			if(floor.getNumber()==i)
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return this.getFullName();
	}

	@Override
	public int describeContents() {
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(shortName);
		dest.writeString(fullName);
		dest.writeTypedList(floorList);
		
	}
	
	private Building(Parcel source){
		this(source.readLong(), source.readString(), source.readString());
		source.readTypedList(floorList, Floor.CREATOR);
	}
	
   public static final Parcelable.Creator<Building> CREATOR = 
            new Parcelable.Creator<Building>() {
        public Building createFromParcel(Parcel in) {
            return new Building(in);
        }

        public Building[] newArray(int size) {
            return new Building[size];
        }
    };


	


}
