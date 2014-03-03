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

import de.dhbw.studientag.model.Building;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the
 * ListView with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class LocationsFragment extends ListFragment {

	
	OnBuildingSelectedListener mCallback;
	
	//Conatiner Activty must implement this interface
	public interface OnBuildingSelectedListener{
		public void onBuildingClicked(Building building);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		List<Building> buildings = getActivity().getIntent()
				.getParcelableArrayListExtra("buildings");
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
			((LocationsActivity)getActivity()).setMarker(location);
		}
		getActivity().getIntent().putExtra("bulding", building);
		mCallback.onBuildingClicked(building);

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
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
