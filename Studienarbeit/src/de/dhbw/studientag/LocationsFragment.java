package de.dhbw.studientag;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import de.dhbw.studientag.dbHelpers.BuildingHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.model.Building;

/**
 * A fragment containing a list of all {@link Building} objects.
 * <p />
 * Activities containing this fragment MUST implement the {@link OnBuildingSelectedListener}
 * interface.
 */
public class LocationsFragment extends ListFragment {

	
	OnBuildingSelectedListener mCallback;
	
	//Conatiner Activty must implement this interface
	public interface OnBuildingSelectedListener{
		public void onBuildingSelected(Building building);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
		List<Building> buildings = BuildingHelper
				.getAllBuildings(dbHelper.getReadableDatabase());
		dbHelper.close();
		setListAdapter(new ArrayAdapter<Building>(getActivity(),
				android.R.layout.simple_list_item_1, buildings));
		
		
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		Building building = (Building) getListView()
				.getItemAtPosition(position);
		Map<String,LatLng> locations = ((LocationsActivity)getActivity()).getLocations();
		LatLng location = locations.get(building.getShortName());
		if(location != null){
			((LocationsActivity)getActivity()).setMarker(location, building.getFullName());
		}
//		getActivity().getIntent().putExtra("bulding", building);
		mCallback.onBuildingSelected(building);

	}

	@Override
	public void onResume() {
		getActivity().setTitle(getString(R.string.title_activity_locations));
		super.onResume();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
	    // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnBuildingSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnBuildingSelectedListener");
        }
	}

}
