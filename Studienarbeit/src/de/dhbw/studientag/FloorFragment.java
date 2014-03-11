package de.dhbw.studientag;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.dhbw.studientag.dbHelpers.BuildingHelper;
import de.dhbw.studientag.dbHelpers.CompanyRoomHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.model.Building;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Floor;
import de.dhbw.studientag.model.Room;

public class FloorFragment extends ListFragment {
	
	protected static final String FLOOR = "floor";
	OnRoomSelectedListener mCallback;
	private Floor floor;
	private Building building;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		floor = getArguments().getParcelable(FLOOR);
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
		building = BuildingHelper.getBuildingByFloorId(dbHelper.getReadableDatabase(), floor.getId());
		
		List<Room> rooms = floor.getRoomList();
		Iterator<Room> iterator = rooms.iterator();
		while(iterator.hasNext()){
			Room room = iterator.next();
			List<Company> companies = CompanyRoomHelper.getCompaniesByRoomId(
					dbHelper.getReadableDatabase(), room.getId());
			if(companies.isEmpty()){
				iterator.remove();
			}
			
		}
		dbHelper.close();
		setListAdapter(new ArrayAdapter<Room>(getActivity(),
				android.R.layout.simple_list_item_1, rooms));
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Room room = (Room) getListView()
				.getItemAtPosition(position);
	

		mCallback.onRoomSelected(room, floor);
	}
	
	@Override
	public void onResume() {
		
		
		if(floor != null && building != null){
			
			getActivity().setTitle(building.getFullName()+", "+floor.getName());
		}
		super.onResume();
	}
	
	@Override
	public void onAttach(Activity activity) {
		
		super.onAttach(activity);
		// This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnRoomSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRoomSelectedListener");
        }
	}
	
	public static FloorFragment newInstance(Floor floor) {
		FloorFragment f = new FloorFragment();
		Bundle args = new Bundle();
		args.putParcelable(FLOOR, floor);
		f.setArguments(args);
		return f;
	}
	
	
	public interface OnRoomSelectedListener{
		public void onRoomSelected(Room room, Floor floor);
	}
	
}
