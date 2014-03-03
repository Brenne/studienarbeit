package de.dhbw.studientag.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TourPoint implements Parcelable{

	private long tourId;
	private String tourName;
	private Company company;
	private int position;
	
	public TourPoint(String tourName, long tourId, Company company, int position){
		this(tourId, company);
		this.tourName=tourName;
		this.position=position;
	}
	
	public TourPoint(long tourId, Company company){
		this.tourId=tourId;
		this.company=company;
	}

	public long getTourId() {
		return tourId;
	}
	
	public void setTourId(long tourId){
		this.tourId=tourId;
	}

	public String getName() {
		return tourName;
	}
	
	public void setName(String name){
		this.tourName=name;
	}

	public Company getCompany() {
		return company;
	}

	public int getPosition() {
		return position;
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
		dest.writeString(tourName);
		dest.writeLong(tourId);
		dest.writeParcelable(company, 0);
		dest.writeInt(position);
	

	}

	private TourPoint(Parcel source) {
		this( source.readString(), source.readLong(), (Company) source.readParcelable(Company.class.getClassLoader()), source.readInt());

	}

	public static final Parcelable.Creator<TourPoint> CREATOR = new Parcelable.Creator<TourPoint>() {
		public TourPoint createFromParcel(Parcel in) {
			return new TourPoint(in);
		}

		public TourPoint[] newArray(int size) {
			return new TourPoint[size];
		}
	};
	
}
