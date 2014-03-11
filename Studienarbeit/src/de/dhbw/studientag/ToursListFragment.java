package de.dhbw.studientag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.dbHelpers.TourHelper;
import de.dhbw.studientag.model.TourPoint;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the
 * ListView with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ToursListFragment extends ListFragment implements
		MyAdapterWithBin.OnBinClicked {

	private OnTourSelectedListener mTourListener;
	protected static final String TOUR_NAME = "tourName";
	protected static final String TOUR_STATIONS = "stations";
	protected static final String TOUR_ID = "tourId";

	/**
	 * The Adapter which will be used to populate the ListView/GridView with
	 * Views.
	 */
	private MyAdapterWithBin mTours;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ToursListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

	}

	private void initAdapter(List<Map<String, Object>> tourPoints) {
		mTours = new MyAdapterWithBin(getActivity().getBaseContext(), tourPoints,
				new String[] { TOUR_NAME, TOUR_STATIONS });
		mTours.notifyDataSetChanged();
		mTours.setOnBinClickListener(this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_tourfragmentlist, container, false);

		// Set the adapter
		setListAdapter(mTours);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mTourListener = (OnTourSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnTourSelectedListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mTourListener = null;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

//		Log.i("studientag", "onListItemClick pos " + Integer.toString(position) + " id "
//				+ Long.toString(id));
		if (mTourListener != null) {
			Map<?, ?> tourPoint = (Map<?, ?>) l
					.getItemAtPosition(position);
			mTourListener.onTourSelected(tourPoint);
		}

		super.onListItemClick(l, v, position, id);

	}

	@Override
	public void onResume() {

		initAdapter(getTourPoints());
		setListAdapter(mTours);

		super.onResume();
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
	public interface OnTourSelectedListener {
		public void onTourSelected(Map<?, ?> tourPoint);
	}

	private ArrayList<Map<String, Object>> getTourPoints() {
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity().getBaseContext());
		Map<Integer, List<TourPoint>> tourPoints = TourHelper.getAllTours(dbHelper
				.getReadableDatabase());
		dbHelper.close();
		ArrayList<Map<String, Object>> tourNameAndStationsList = (ArrayList<Map<String, Object>>) allTourPointsMapToAllToursList(tourPoints);
		return tourNameAndStationsList;

	}

	public static List<Map<String, Object>> allTourPointsMapToAllToursList(
			Map<Integer, List<TourPoint>> allTourPoints) {
		ArrayList<Map<String, Object>> tourNameAndStationsList = new ArrayList<Map<String, Object>>();

		for (Entry<Integer, List<TourPoint>> tour : allTourPoints.entrySet()) {
			Map<String, Object> tourNameAndStations = new HashMap<String, Object>();
			List<TourPoint> tourPoints = tour.getValue();
			if (!tourPoints.isEmpty()) {
				tourNameAndStations.put(TOUR_NAME, tourPoints.get(0).getName());
				tourNameAndStations.put(TOUR_ID, tourPoints.get(0).getTourId());
				tourNameAndStations.put(TOUR_STATIONS,
						Integer.toString(tourPoints.size()));
				tourNameAndStationsList.add(tourNameAndStations);
			}
		}

		return tourNameAndStationsList;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if(NfcAdapter.getDefaultAdapter(getActivity()) != null)
			inflater.inflate(R.menu.tour_list, menu);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_item_import:
			Intent intent = new Intent(getActivity(), ImportActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);	
			
		}
		
	}

	@Override
	public void binClicked(int position) {

		Map<?, ?> tour = (Map<?, ?>) mTours.getItem(position);
		long tourId = (Long) tour.get(TOUR_ID);
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
		TourHelper.deleteTourById(dbHelper.getWritableDatabase(), tourId);
//		Log.i("studientag", "on image button clickd " + Long.toString(tourId));

		dbHelper.close();
		initAdapter(getTourPoints());
		setListAdapter(mTours);

	}



}
