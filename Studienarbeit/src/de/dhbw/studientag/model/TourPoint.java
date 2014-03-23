package de.dhbw.studientag.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TourPoint implements Parcelable, Comparable<TourPoint>{

	
	private long id;
	private Company company;
	private int position;
	
	public TourPoint(Company company, int position){
		this(company);
		this.position=position;
	}
	
	public TourPoint( Company company){
		this.company=company;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public Company getCompany() {
		return company;
	}

	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position){
		this.position=position;
	}
	
	@Override
	public String toString() {
		return this.company.toString();
	}

	@Override
	public int describeContents() {
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(company, 0);
		dest.writeInt(position);
	

	}

	private TourPoint(Parcel source) {
		this( (Company) source.readParcelable(Company.class.getClassLoader()), source.readInt());

	}

	public static final Parcelable.Creator<TourPoint> CREATOR = new Parcelable.Creator<TourPoint>() {
		public TourPoint createFromParcel(Parcel in) {
			return new TourPoint(in);
		}

		public TourPoint[] newArray(int size) {
			return new TourPoint[size];
		}
	};

	@Override
	public int compareTo(TourPoint another)  {
		if(another.getCompany().getLocation().getBuilding().getId()!= this.getCompany().getLocation().getBuilding().getId()){
			System.err.println("Can not compare TourPoints of different buildings");
			throw new ClassCastException();
		}else{
			long anotherFloorNumber = another.getCompany().getLocation().getFloor().getNumber();
			long thisFloorNumber = this.getCompany().getLocation().getFloor().getNumber();
			if(anotherFloorNumber<thisFloorNumber){
				return 1;
			}else if(anotherFloorNumber==thisFloorNumber){
				return 0;
			}else{
				return -1;
			}
		}
		
	}


	
}
