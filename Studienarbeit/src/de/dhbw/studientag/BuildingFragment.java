package de.dhbw.studientag;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.dhbw.studientag.dbHelpers.FloorHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
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
	
	private static final String BUILDING = "building";
	OnFloorSelectedListener mCallback;
	private static final String TAG ="BuildingFragment";


	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public BuildingFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Building building = getArguments().getParcelable(BUILDING);
		List<Floor> floors = building.getFloorList();
		Iterator<Floor> iterator = floors.iterator();
		SQLiteDatabase db = new MySQLiteHelper(getActivity()).getReadableDatabase();
		while(iterator.hasNext()){
			Floor floor =iterator.next();
			if(!FloorHelper.hasRoomsOccupiedRooms(db, floor.getId())){
				
				iterator.remove();
			}
		}
		db.close();
		setListAdapter(new ArrayAdapter<Floor>(getActivity(),
				android.R.layout.simple_list_item_1, floors));
		
		
	}
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	    // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFloorSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFloorSelectedListener");
        }
	}
	
	@Override
	public void onResume() {
		Building building = getArguments().getParcelable(BUILDING);
		if(building != null)
			getActivity().setTitle(building.getFullName());
		else
			Log.d(TAG, "onResume building = null");
		super.onResume();
	}
	


	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Floor floor = (Floor) getListView()
				.getItemAtPosition(position);
	

		mCallback.onFloorSelected(floor);
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
	public interface OnFloorSelectedListener {
		public void onFloorSelected(Floor floor);
	}

	public static BuildingFragment newInstance(Building building) {
		BuildingFragment f = new BuildingFragment();
		Bundle args = new Bundle();
		args.putParcelable(BUILDING, building);
		f.setArguments(args);
		return f;
	}

}
