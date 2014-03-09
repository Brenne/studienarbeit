package de.dhbw.studientag;

import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import de.dhbw.studientag.model.Floor;
import de.dhbw.studientag.model.Room;

public class FloorFragment extends ListFragment {
	
	protected static final String FLOOR = "floor";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Floor floor = getArguments().getParcelable(FLOOR);
		List<Room> rooms = floor.getRoomList();
		setListAdapter(new ArrayAdapter<Room>(getActivity(),
				android.R.layout.simple_list_item_1, rooms));
	}
	
	public static FloorFragment newInstance(Floor floor) {
		FloorFragment f = new FloorFragment();
		Bundle args = new Bundle();
		args.putParcelable(FLOOR, floor);
		f.setArguments(args);
		return f;
	}
	
}
