package de.dhbw.studientag.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Tour implements Parcelable {
	
	private long id;
	private String name;
	private List<TourPoint> tourPointList = new ArrayList<TourPoint>();
	
	public Tour(long id, String name, List<TourPoint> tourPointList){
		this(id, name);
		this.tourPointList= tourPointList;
	}
	
	public Tour(long id, String name){
		this(name);
		this.id = id;
	}
	
	public Tour(String name){
		this.name = name;
	}
	
	public Tour(Tour tour){
		this(tour.id, tour.name, tour.tourPointList);
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id){
		this.id=id;
	}

	public String getName() {
		return name;
	}

	public List<TourPoint> getTourPointList() {
		return tourPointList;
	}
	
	public void setTourPointList(List<TourPoint> tourPointList){
		this.tourPointList= tourPointList;
	}
	
	public void addTourPoint(TourPoint tourPoint){
		this.tourPointList.add(tourPoint);
	}
	
	
	@Override
	public int describeContents() {
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeTypedList(tourPointList);
	

	}


	public static final Parcelable.Creator<Tour> CREATOR = new Parcelable.Creator<Tour>() {
		public Tour createFromParcel(Parcel in) {
			long id = in.readLong();
			String name = in.readString();
			List<TourPoint> tourPointList = new ArrayList<TourPoint>();
			in.readTypedList(tourPointList, TourPoint.CREATOR);
			return new Tour(id, name, tourPointList);
		}

		public Tour[] newArray(int size) {
			return new Tour[size];
		}
	};
	
	
	
	
	

}
