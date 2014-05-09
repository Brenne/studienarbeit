package de.dhbw.studientag.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * represents the Location of a company information point
 *
 */
public class Location implements Parcelable{

	private Building building;
	private Floor floor;
	private Room room;
	
	public Location(Building building, Floor floor, Room room){
		this.building = building;
		this.floor = floor;
		this.room = room;
	}
	
	public Building getBuilding() {
		return building;
	}
	
	public Floor getFloor() {
		return floor;
	}
	
	public Room getRoom() {
		return room;
	}

	@Override
	public int describeContents() {
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(building, flags);
		dest.writeParcelable(floor, flags);
		dest.writeParcelable(room, flags);	
	}
	
	private Location(Parcel source){
		this((Building)source.readParcelable(Building.class.getClassLoader()), (Floor)source.readParcelable(Floor.class.getClassLoader()), 
				(Room) source.readParcelable(Room.class.getClassLoader()));	
	}
	
   public static final Parcelable.Creator<Location> CREATOR = 
            new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
	
}
