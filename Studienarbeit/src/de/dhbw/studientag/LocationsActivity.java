package de.dhbw.studientag;


import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import de.dhbw.studientag.model.Building;

public class LocationsActivity extends Activity implements LocationsFragment.OnBuildingSelectedListener{

	private GoogleMap mMap;
	
	private final Map<String,LatLng> LOCATIONS = new HashMap<String,LatLng>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		LocationsFragment locationsFragment = new LocationsFragment();
		fragmentTransaction.add(R.id.fragmentContainer, locationsFragment);
//		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
		setContentView(R.layout.activity_locations);
		setUpMapIfNeeded();
		initLocations();
		

	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.locations, menu);
		return true;
	}
	
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                                .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                // The Map is verified. It is now safe to manipulate the map.

            }
        }
        
    }

    public void setMarker(LatLng position){
    	  mMap.addMarker(new MarkerOptions().position(position).title("Marker"));
    }
 
    private final void initLocations(){
    	if(LOCATIONS.isEmpty()){
	    	this.LOCATIONS.put("RB41", new LatLng(48.773536, 9.170902));
	    	this.LOCATIONS.put("J58", new LatLng(48.785111, 9.173414));
	    	this.LOCATIONS.put("J56", new LatLng(48.784607, 9.174141));
	    	this.LOCATIONS.put("P50", new LatLng(48.773298, 9.170553));
    	}
    	
    }
    
    public Map<String,LatLng> getLocations(){
    	return this.LOCATIONS;
    }

	@Override
	public void onBuildingClicked(Building building) {
		
		BuildingFragment buildingFragement = BuildingFragment.newInstance(building);
		
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragmentContainer, buildingFragement);
		transaction.addToBackStack(null);
		transaction.commit();
	}

}
