package de.dhbw.studientag;

import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import de.dhbw.studientag.model.Building;
import de.dhbw.studientag.model.Floor;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class BuildingFragment extends ListFragment {



	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public BuildingFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Building building = getArguments().getParcelable("building");
		List<Floor> floors = building.getFloorList();
		setListAdapter(new ArrayAdapter<Floor>(getActivity(),
				android.R.layout.simple_list_item_1, floors));
		
	}





	

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(String id);
	}

	public static BuildingFragment newInstance(Building building) {
		BuildingFragment f = new BuildingFragment();
		
		Bundle args = new Bundle();
		args.putParcelable("building", building);
		f.setArguments(args);
		return f;
	}

}
