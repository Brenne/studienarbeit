package de.dhbw.studientag;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import de.dhbw.studientag.model.Building;
import de.dhbw.studientag.model.Floor;

public class LocationsActivity extends Activity implements
		LocationsFragment.OnBuildingSelectedListener,
		BuildingFragment.OnFloorSelectedListener {

	private GoogleMap mMap;
	private boolean showMap = true;
	private int topFragmentContainerHeight;
	private LocationsFragment locationsFragment;
	private static final String LOCATIONS_FRAGMENT="locationsFragment";

	private final Map<String, LatLng> LOCATIONS = new HashMap<String, LatLng>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentManager fragmentManager = getFragmentManager();
		locationsFragment = (LocationsFragment) fragmentManager.findFragmentByTag(LOCATIONS_FRAGMENT);
		if(locationsFragment == null){
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			locationsFragment = new LocationsFragment();
			fragmentTransaction.add(R.id.fragmentContainer, locationsFragment, LOCATIONS_FRAGMENT);

			fragmentTransaction.commit();
		}

		setContentView(R.layout.activity_locations);
		setUpMapIfNeeded();
		initLocations();

		FrameLayout fragmentContainerTop = (FrameLayout) findViewById(R.id.fragmentContainer);
		topFragmentContainerHeight = ((LayoutParams) fragmentContainerTop
				.getLayoutParams()).height;

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

			}
		}

	}

	public void setMarker(LatLng position) {
		mMap.clear();
		mMap.addMarker(new MarkerOptions().position(position).title("Marker"));
	}

	private final void initLocations() {
		if (LOCATIONS.isEmpty()) {
			this.LOCATIONS.put("RB41", new LatLng(48.773536, 9.170902));
			this.LOCATIONS.put("J58", new LatLng(48.785111, 9.173414));
			this.LOCATIONS.put("J56", new LatLng(48.784607, 9.174141));
			this.LOCATIONS.put("P50", new LatLng(48.773298, 9.170553));
		}

	}

	public Map<String, LatLng> getLocations() {
		return this.LOCATIONS;
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
		switch (item.getItemId()) {
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
		default:
			return super.onOptionsItemSelected(item);

		}

	}
	
	

	@Override
	public void onBuildingClicked(Building building) {

		CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(
				LOCATIONS.get(building.getShortName()), 17);

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

}
