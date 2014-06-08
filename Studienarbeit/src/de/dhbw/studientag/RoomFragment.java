package de.dhbw.studientag;

import java.util.List;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.dhbw.studientag.dbHelpers.BuildingHelper;
import de.dhbw.studientag.dbHelpers.CompanyLocationHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.model.Building;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Floor;
import de.dhbw.studientag.model.Room;

/**
 * A fragment representing a {@link Floor} and containing a list of {@link Room}
 * objects.
 * 
 */
public class RoomFragment extends ListFragment {

	private static final String ROOM = "room";
	private Room room;
	private Floor floor;
	private Building building;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		room = getArguments().getParcelable(ROOM);
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
		List<Company> companies = CompanyLocationHelper.getCompaniesByRoomId(
				dbHelper.getReadableDatabase(), room.getId());
		floor = getArguments().getParcelable(FloorFragment.FLOOR);
		building = BuildingHelper.getBuildingByFloorId(dbHelper.getReadableDatabase(),
				floor.getId());
		dbHelper.close();
		setListAdapter(new ArrayAdapter<Company>(getActivity(),
				android.R.layout.simple_list_item_1, companies));
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Company company = (Company) getListView().getItemAtPosition(position);
		Intent intent = new Intent(getActivity(), CompanyActivity.class);
		intent.putExtra(CompanyActivity.COMPANY, company);
		startActivity(intent);
	}

	@Override
	public void onResume() {

		if (room != null && floor != null && building != null) {
			getActivity().setTitle(
					building.getFullName() + ", " + floor.getName() + ", " + room.getRoomNo());
		}
		super.onResume();
	}

	public static RoomFragment newInstance(Room room, Floor floor) {
		RoomFragment f = new RoomFragment();
		Bundle args = new Bundle();
		args.putParcelable(ROOM, room);
		args.putParcelable(FloorFragment.FLOOR, floor);
		f.setArguments(args);
		return f;
	}

}
