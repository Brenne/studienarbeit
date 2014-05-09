package de.dhbw.studientag.tours;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import de.dhbw.studientag.LocationsActivity;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.dbHelpers.TourHelper;
import de.dhbw.studientag.model.Building;
import de.dhbw.studientag.model.TourPoint;

public final class BestTourController {

	private Location mCurrentLocation;
	private final Context mContext;
	private final List<TourPoint> mTourPoints;
	private static final String TAG = "BestTourController";

	public BestTourController(Location currentLocation, Context context,
			List<TourPoint> tourPoints) {
		this.mCurrentLocation = currentLocation;
		this.mContext = context;
		this.mTourPoints = tourPoints;
	}
	
	public List<TourPoint> getBestTourPointList() {

		ArrayList<TourPoint> newTourPointList = new ArrayList<TourPoint>();
		Permutations<Building> buildingPermutations = new Permutations<Building>(
				getUniqueBuildings(mTourPoints));
		TreeMap<Float, List<Building>> tourLength = new TreeMap<Float, List<Building>>();
		while(buildingPermutations.hasNext()) {
			List<Building> buildings = buildingPermutations.next();
			tourLength.put(calcDistance(buildings), buildings);
		}
		Log.v(TAG, "shortest tourLength: "+Float.toString(tourLength.firstEntry().getKey()));
		for(Building building : tourLength.firstEntry().getValue()){
			ArrayList<TourPoint> tempList = new ArrayList<TourPoint>();
			for(TourPoint tourPoint: mTourPoints){
				if(tourPoint.getCompany().getLocation().getBuilding().getId()== building.getId()){
					tempList.add(tourPoint);
				}
			}
			//sort floors
			Collections.sort(tempList);
			newTourPointList.addAll(tempList);
		
		}
	
		// check if no tourPoint got Lost
		if (mTourPoints.size() == newTourPointList.size()) {
			makePositionsPersistent(newTourPointList);
			
			
			
			return newTourPointList;
		} else {
			Log.e(TAG, "error in order list check code");
			return mTourPoints;
		}
	}

	private Map<String, Float> getDistanceMap() {

		Map<String, Float> distance = new HashMap<String, Float>();

		if (mCurrentLocation == null) {
			Log.w(TAG, "curLocation is null");
			final String buildingShortName = "RB41";
			Location location = new Location(buildingShortName);
			LatLng coordinates = LocationsActivity.LOCATIONS.entrySet().iterator().next().getValue();
			location.setLatitude(coordinates.latitude);
			location.setLongitude(coordinates.longitude);
			mCurrentLocation= location;
		} 
		for (Entry<String, LatLng> entry : LocationsActivity.LOCATIONS.entrySet()) {
			Location location = new Location(entry.getKey());
			location.setLatitude(entry.getValue().latitude);
			location.setLongitude(entry.getValue().longitude);

			float fDistance = location.distanceTo(mCurrentLocation);
			distance.put(entry.getKey(), fDistance);
		}

		

		return distance;
	}

	/**
	 * 
	 * @param buildingA
	 *            the first building
	 * @param buildingB
	 *            the second building
	 * @return distance between buildingA and buildingB in meters
	 */
	private float distanceBetween(Building buildingA, Building buildingB) {
		float[] distance = { 0 };
		Location locA = new Location(buildingA.getShortName());
		locA.setLatitude(LocationsActivity.LOCATIONS.get(buildingA.getShortName()).latitude);
		locA.setLongitude(LocationsActivity.LOCATIONS.get(buildingA.getShortName()).longitude);
		Location locB = new Location(buildingB.getShortName());
		locB.setLatitude(LocationsActivity.LOCATIONS.get(buildingB.getShortName()).latitude);
		locB.setLongitude(LocationsActivity.LOCATIONS.get(buildingB.getShortName()).longitude);

		Location.distanceBetween(locA.getLatitude(), locA.getLongitude(),
				locB.getLatitude(), locB.getLongitude(), distance);
		return distance[0];

	}

	/**
	 * 
	 * @param buildings
	 *            list of buildings
	 * @return the sum of the distance between the buildings + the distance from
	 *         the current position to the first building
	 */
	private float calcDistance(List<Building> buildings) {
		float distance = 0;
		for (int i = 0; i < buildings.size(); i++) {
			if (i == 0) {
				Map<String, Float> distanceMap = getDistanceMap();
				distance += distanceMap.get(buildings.get(0).getShortName());
			} else {
				Building prevTourPoint = buildings.get(i - 1);
				Building thisTourPoint = buildings.get(i);
				if (prevTourPoint.getId() != thisTourPoint.getId()) {
					distance += distanceBetween(prevTourPoint, thisTourPoint);
				}
			}
		}
		Log.v(TAG, "Buildinglist distance : " + Float.toString(distance));
		return distance;
	}

	private void makePositionsPersistent(List<TourPoint> tourPoints) {
		int i = 1;
		SQLiteDatabase db = new MySQLiteHelper(mContext).getReadableDatabase();
		for (TourPoint tourPoint : tourPoints) {
			tourPoint.setPosition(i);
			TourHelper.updatePosition(db, tourPoint);
			i++;

		}
		db.close();
	}

	private List<Building> getUniqueBuildings(List<TourPoint> tourPoints) {
		HashMap<Long, Building> buildingMap = new HashMap<Long, Building>(4);
		List<Building> buildingList = new ArrayList<Building>();
		for (TourPoint tourPoint : tourPoints) {
			Building building = tourPoint.getCompany().getLocation().getBuilding();
			buildingMap.put(building.getId(), building);
		}
		for (Entry<Long, Building> building : buildingMap.entrySet()) {
			buildingList.add(building.getValue());
		}
		return buildingList;

	}
	
	public void setCurrentLocation(Location currentLocation){
		if(currentLocation != null){
			this.mCurrentLocation=currentLocation;
		}
	}

}
