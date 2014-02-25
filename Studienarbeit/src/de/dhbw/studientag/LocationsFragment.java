package de.dhbw.studientag;

import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
		Log.i("building fullName ", building.getFullName());
		getActivity().getIntent().putExtra("bulding", building);
		BuildingFragment buildingFragement = BuildingFragment
				.newInstance(building);

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

}
