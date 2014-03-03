package de.dhbw.studientag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
		AbsListView.OnItemClickListener {

	private OnTourSelectedListener mTourListener;
	protected static final String TOUR_NAME ="tourName";
	protected static final String TOUR_STATIONS="stations";
	protected static final String TOUR_ID="tourId";

	/**
	 * The fragment's ListView/GridView.
	 */
	private AbsListView mListView;

	/**
	 * The Adapter which will be used to populate the ListView/GridView with
	 * Views.
	 */
	private ListAdapter mTours;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ToursListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		List<Map<String,Object>> tourPoints = (List<Map<String, Object>>) getArguments().getSerializable("tours");
		
	
		mTours = new SimpleAdapter(getActivity().getBaseContext(), tourPoints, android.R.layout.simple_list_item_2, 
				new String[] {TOUR_NAME, TOUR_STATIONS}, 
				new int[]{android.R.id.text1, android.R.id.text2});
		
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
					+ " must implement OnTourPointSelectedListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mTourListener = null;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (null != mTourListener) {
			Map<String,Object> tourPoint = (Map<String, Object>) getListView().getItemAtPosition(position);
			mTourListener.onTourSelected(tourPoint);
		}
		super.onListItemClick(l, v, position, id);
	}



	/**
	 * The default content for this Fragment has a TextView that is shown when
	 * the list is empty. If you would like to change the text, call this method
	 * to supply the text it should use.
	 */
	public void setEmptyText(CharSequence emptyText) {
		View emptyView = mListView.getEmptyView();

		if (emptyText instanceof TextView) {
			((TextView) emptyView).setText(emptyText);
		}
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
		public void onTourSelected(Map<String, Object> tourPoint);
	}
	
	public static ToursListFragment newInstance(Map<Integer, List<TourPoint>> allTourPoints){
		ToursListFragment f  = new ToursListFragment();
		ArrayList<Map<String,Object>> tourNameAndStationsList = (ArrayList<Map<String, Object>>) allTourPointsMapToAllToursList(allTourPoints);

		
		Bundle args = new Bundle();
		args.putSerializable("tours", tourNameAndStationsList);
		f.setArguments(args);
		return f;
	}
	
	public static List<Map<String, Object>> allTourPointsMapToAllToursList(Map<Integer, List<TourPoint>> allTourPoints){
		ArrayList<Map<String,Object>> tourNameAndStationsList = new ArrayList<Map<String, Object>>();
		
		for(Entry<Integer,List<TourPoint>> tour : allTourPoints.entrySet()){
			Map<String, Object> tourNameAndStations = new HashMap<String, Object>();
			List<TourPoint> tourPoints =  tour.getValue();
			if(!tourPoints.isEmpty()){
				tourNameAndStations.put(TOUR_NAME, tourPoints.get(0).getName());
				tourNameAndStations.put(TOUR_ID,tourPoints.get(0).getTourId());
				tourNameAndStations.put(TOUR_STATIONS,tourPoints.size());
				tourNameAndStationsList.add(tourNameAndStations);
			}
		}
		
		return tourNameAndStationsList;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		
	}

}
