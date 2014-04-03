package de.dhbw.studientag.tours;

import java.util.List;

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
import de.dhbw.studientag.OnBinClicked;
import de.dhbw.studientag.R;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.dbHelpers.TourHelper;
import de.dhbw.studientag.model.Tour;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the
 * ListView with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class TourListFragment extends ListFragment implements OnBinClicked {

	private OnTourSelectedListener mTourListener;
	private NewTourListener mCreateNewTourListener;
	protected static final String TOUR_NAME = "tourName";
	protected static final String TOUR_STATIONS = "stations";
	protected static final String TOUR_ID = "tourId";

	/**
	 * The Adapter which will be used to populate the ListView/GridView with
	 * Views.
	 */
	private TourAdapter mTourAdapter;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public TourListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

	}

	private void initAdapter(List<Tour> tourList) {
		mTourAdapter = new TourAdapter(getActivity(), tourList);
		mTourAdapter.notifyDataSetChanged();
		mTourAdapter.setOnBinClickListener(this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_tourfragmentlist, container, false);

		// Set the adapter
		setListAdapter(mTourAdapter);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mTourListener = (OnTourSelectedListener) activity;
			mCreateNewTourListener= (NewTourListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnTourSelectedListener and NewTourListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mTourListener = null;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		// Log.i("studientag", "onListItemClick pos " +
		// Integer.toString(position) + " id "
		// + Long.toString(id));
		if (mTourListener != null) {
			Tour tour = (Tour) l.getItemAtPosition(position);
			mTourListener.onTourSelected(tour);
		}
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void onResume() {
		initAdapter(getTourList());
		setListAdapter(mTourAdapter);
		getActivity().setTitle(R.string.title_activity_tours);
		
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
		public void onTourSelected(Tour tour);
	}

	private List<Tour> getTourList() {
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
		List<Tour> tourList = TourHelper.getAllTours(dbHelper.getReadableDatabase());
		dbHelper.close();
		return tourList;

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (NfcAdapter.getDefaultAdapter(getActivity()) != null)
			inflater.inflate(R.menu.tour_list, menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch(itemId){
		case R.id.menu_item_import:
			Intent intent = new Intent(getActivity(), NfcActivity.class);
			startActivity(intent);
			return true;
		case R.id.menu_item_newTour:
			mCreateNewTourListener.createNewTour();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void binClicked(int position) {

		Tour tour = (Tour) mTourAdapter.getItem(position);
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
		TourHelper.deleteTourById(dbHelper.getWritableDatabase(), tour.getId());
		// Log.i("studientag", "on image button clickd " +
		// Long.toString(tourId));

		dbHelper.close();
		initAdapter(getTourList());
		setListAdapter(mTourAdapter);

	}
	
	public interface NewTourListener{
		public void createNewTour();
	}

}
