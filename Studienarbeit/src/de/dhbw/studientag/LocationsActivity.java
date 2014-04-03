package de.dhbw.studientag;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import de.dhbw.studientag.model.Building;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Floor;
import de.dhbw.studientag.model.Room;

public class LocationsActivity extends Activity implements
		LocationsFragment.OnBuildingSelectedListener,
		BuildingFragment.OnFloorSelectedListener,
		FloorFragment.OnRoomSelectedListener,  OnMyLocationButtonClickListener{

	private GoogleMap mMap;
	private boolean showMap = true;
	private int topFragmentContainerHeight;
	private LocationsFragment locationsFragment;
	private LatLng mMarkerPosition;
	private static final float CAMERA_ZOOM = 17;
	private static final String MARKER_POSITION = "MarkerPosition";
	private static final String LOCATIONS_FRAGMENT="locationsFragment";
	/**
	 * key is building shortname and value is LatLng position of the building
	 */
	public static final Map<String, LatLng> LOCATIONS;
	static{
		Map<String, LatLng> locationsMap = new HashMap<String, LatLng>();
		locationsMap.put("RB41", new LatLng(48.773536, 9.170902));
		locationsMap.put("J58", new LatLng(48.785111, 9.173414));
		locationsMap.put("J56", new LatLng(48.784607, 9.174141));
		locationsMap.put("P50", new LatLng(48.773298, 9.170553));
		LOCATIONS = Collections.unmodifiableMap(locationsMap);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			mMarkerPosition= savedInstanceState.getParcelable(MARKER_POSITION);
		}
		setContentView(R.layout.activity_locations);
		setUpMapIfNeeded();
		
		FrameLayout fragmentContainerTop = (FrameLayout) findViewById(R.id.fragmentContainer);
		topFragmentContainerHeight = ((LayoutParams) fragmentContainerTop
				.getLayoutParams()).height;
		FragmentManager fragmentManager = getFragmentManager();

		locationsFragment = (LocationsFragment) fragmentManager.findFragmentByTag(LOCATIONS_FRAGMENT);
		Company company = getIntent().getParcelableExtra(CompanyActivity.COMPANY);

		if(company != null){
			onRoomSelected(company.getLocation().getRoom(), company.getLocation().getFloor());
			CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(
					LOCATIONS.get(company.getLocation().getBuilding().getShortName()), CAMERA_ZOOM);
			mMap.moveCamera(cameraPosition);
			LatLng location = LOCATIONS.get(company.getLocation().getBuilding().getShortName());
			if(location != null){
				setMarker(location,company.getLocation().getBuilding().getFullName());
			}
			
		}else if(locationsFragment == null){
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			locationsFragment = new LocationsFragment();
			fragmentTransaction.add(R.id.fragmentContainer, locationsFragment, LOCATIONS_FRAGMENT);

			fragmentTransaction.commit();
		}

	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		 super.onSaveInstanceState(outState);

        //save MarkerPosition
         outState.putParcelable(MARKER_POSITION, mMarkerPosition);
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
					.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				// The Map is verified. It is now safe to manipulate the map.
				mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationButtonClickListener(this);
                if(mMarkerPosition != null){
                	setMarker(mMarkerPosition, "DHBW Stuttgart");
                }
			}
		}

	}

	public void setMarker(LatLng position, String title) {
		mMap.clear();
		mMap.addMarker(new MarkerOptions().position(position).title(title));
		mMarkerPosition = position;
	}



	public Map<String, LatLng> getLocations() {
		return LocationsActivity.LOCATIONS;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.location, menu);
		// since there is no equivalent to setColorFilter in XML do it here
		// programmatically
		menu.findItem(R.id.menu_map).getIcon()
				.setColorFilter(android.graphics.Color.GREEN, Mode.MULTIPLY);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId(); 
		switch(itemId){
		case R.id.menu_map:
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			Fragment mapFragment = getFragmentManager().findFragmentById(R.id.map);
			FrameLayout fragmentContainerTop = (FrameLayout) findViewById(R.id.fragmentContainer);
			if (showMap) {

				transaction.hide(mapFragment);
				transaction.commit();

				((LayoutParams) fragmentContainerTop.getLayoutParams()).height = LayoutParams.MATCH_PARENT;
				item.getIcon()
						.setColorFilter(android.graphics.Color.WHITE, Mode.MULTIPLY);
				showMap = false;
				return true;
			} else {
				((LayoutParams) fragmentContainerTop.getLayoutParams()).height = topFragmentContainerHeight;
				transaction.show(mapFragment);
				transaction.commit();
				item.getIcon()
						.setColorFilter(android.graphics.Color.GREEN, Mode.MULTIPLY);
				showMap = true;
				return true;	
			}
		case R.id.menu_goToLocation:
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}


		

	}
	
	

	@Override
	public void onBuildingClicked(Building building) {

		CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(
				LOCATIONS.get(building.getShortName()), CAMERA_ZOOM);

		BuildingFragment buildingFragement = BuildingFragment.newInstance(building);
		mMap.moveCamera(cameraPosition);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragmentContainer, buildingFragement);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onFloorSelected(Floor floor) {
		FloorFragment floorFragment = FloorFragment.newInstance(floor);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragmentContainer, floorFragment);
		transaction.addToBackStack(null);
		transaction.commit();

	}

	@Override
	public void onRoomSelected(Room room, Floor floor) {
		RoomFragment roomFragment = RoomFragment.newInstance(room, floor);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragmentContainer, roomFragment);
		if(locationsFragment!= null)
			transaction.addToBackStack(null);
		transaction.commit();
		
	}

	@Override
	public boolean onMyLocationButtonClick() {
		
		return false;
	}
	
	
}
