package de.dhbw.studientag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.dbHelpers.TourHelper;
import de.dhbw.studientag.model.Building;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Location;
import de.dhbw.studientag.model.Room;
import de.dhbw.studientag.model.Tour;
import de.dhbw.studientag.model.TourPoint;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link TourFragment.OnFloorSelectedListener} interface to handle interaction
 * events. Use the {@link TourFragment#newInstance} factory method to create an
 * instance of this fragment.
 * 
 */
public class TourFragment extends ListFragment implements OnBinClicked {

	private ShareActionProvider mShareActionProvider;
	private MyDistanceListener mListener;
	private OnTourPointAddListener mCompanyAddListener;
	private Tour mTour;
	private Company mCompany;
	private TourPointAdapter mAdapter;
	private boolean mNewTour = false;
	private boolean mTourNameChanged = false;
	private EditText mTourName;
	private static final String TAG = "TourPointFragment";
	protected static final String TOUR = "tour";

	public TourFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		if (mCompany != null) {
			MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity().getBaseContext());
			if (mNewTour) {

				SQLiteDatabase db = dbHelper.getWritableDatabase();

				String tourName = TourHelper.getFreeTourName(db,
						getString(R.string.label_TourName));
				TourPoint tourPoint = new TourPoint(mCompany);
				long tourId = TourHelper.insertTour(db, tourName);
				mTour = new Tour(tourId, tourName);
				tourPoint.setId(TourHelper.insertTourPoint(db, tourPoint, tourId));
				db.close();

				mTour.addTourPoint(tourPoint);

			} else if (mTour != null) {
				// not a new tour but add company to exiting tourPoint
				long tourId = mTour.getId();
				TourPoint tourPoint = new TourPoint(mCompany);
				tourPoint.setId(TourHelper.insertTourPoint(
						dbHelper.getWritableDatabase(), tourPoint, tourId));
				mTour.addTourPoint(tourPoint);

			}
			dbHelper.close();
		} else if (mTour != null && !mTour.getTourPointList().isEmpty()) {

		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_tour_point, container, false);
		mTourName = (EditText) v.findViewById(R.id.editText_TourName);
		if (!mTour.getName().isEmpty()) {
			mTourName.setText(mTour.getName());
		} else if (tourNameExists(mTourName.getText().toString())) {
			mTourName.setError(getString(R.string.warning_tourName_exists));
		}

		mTourName.addTextChangedListener(new TextWatcher() {
			final String originalTourName = mTourName.getText().toString();

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// do nothing wait until afterTextChanged
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// do nothing wait until afterTextChanged
			}

			@Override
			public void afterTextChanged(Editable s) {
				mTourNameChanged = true;
				if (s.toString().equals(originalTourName)) {
					mTourName.setError(null);
					mTourNameChanged = false;
				} else if (s.toString().isEmpty()) {
					mTourName.setError(getString(R.string.warning_empty_TourName));
				} else if (tourNameExists(s.toString())) {
					mTourName.setError(getString(R.string.warning_tourName_exists));
				} else {
					mTourName.setError(null);
				}

			}
		});
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (MyDistanceListener) activity;
			mCompanyAddListener = (OnTourPointAddListener) activity;

		} catch (ClassCastException catExeption) {
			Log.e(TAG,
					"activity did not ipmlement MyDistanceListener or OnTourPointAddListener",
					catExeption);
		}
	}

	private boolean tourNameExists(String tourName) {
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
		boolean returnV = TourHelper.tourNameExists(dbHelper.getReadableDatabase(),
				tourName);
		dbHelper.close();
		return returnV;
	}

	private void initAdapter() {
		mAdapter = new TourPointAdapter(getActivity(), mTour);
		mAdapter.setOnBinClickListener(this);
		setListAdapter(mAdapter);
		updateShareIntent();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		TourPoint tourPoint = mTour.getTourPointList().get(position);
		Intent intent = new Intent(getActivity().getBaseContext(), CompanyActivity.class);
		intent.putExtra(TourActivity.COMPANY, tourPoint.getCompany());
		startActivity(intent);

	}

	public static TourFragment getInitializedFragement(Tour tour) {
		TourFragment fragment = new TourFragment();
		fragment.mTour = tour;
		return fragment;
	}

	public static TourFragment getInitializedFragement(Tour tour, Company company) {
		TourFragment fragment = getInitializedFragement(tour);
		fragment.mCompany = company;
		return fragment;
	}

	public static TourFragment newTour(Company company, boolean newTour) {
		TourFragment fragment = new TourFragment();
		fragment.mCompany = company;
		fragment.mNewTour = newTour;
		return fragment;
	}

	@Override
	public void onPause() {
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity().getBaseContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String tourName = this.mTourName.getText().toString();
		if (!tourName.isEmpty() && mTourNameChanged) {
			long tourId = mTour.getId();
			TourHelper.updateTourName(db, tourId, tourName);
		}
		db.close();
		dbHelper.close();
		super.onPause();
	}

	@Override
	public void onResume() {
		getActivity().setTitle(R.string.title_activity_tour);
		if (mTour != null && mTour.getId() != 0) {
			// MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
			// mTour = TourHelper.getTourById(dbHelper.getReadableDatabase(),
			// mTour.getId());
			// dbHelper.close();
			initAdapter();
		}
		super.onResume();
	}

	@Override
	public void binClicked(int position) {

		if (mTour.getTourPointList().isEmpty())
			return;
		long tourPointId = mTour.getTourPointList().get(position).getId();
		// long tourId = tourPoints.get(0).getTourId();
		mTour.getTourPointList().remove(position);

		Iterator<TourPoint> iterator = mTour.getTourPointList().iterator();
		while (iterator.hasNext()) {
			TourPoint tourPoint = iterator.next();
			long tmpTourPointId = tourPoint.getId();
			if (tmpTourPointId == tourPointId) {
				iterator.remove();
				break;
			}
		}
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
		TourHelper.deleteTourPointById(dbHelper.getWritableDatabase(), tourPointId);
		// if there are no tourPoints in tourList return to TourListFragment
		if (mTour.getTourPointList().isEmpty()) {
			getActivity().onBackPressed();
		} else {
			initAdapter();
		}
		dbHelper.close();

	}

	// needed for export companyids
	private String getCompanyIdsOfTour() {
		StringBuilder companyIdsBuilder = new StringBuilder();
		if (!mTour.getTourPointList().isEmpty())
			for (TourPoint tourPoint : mTour.getTourPointList()) {
				long companyId = tourPoint.getCompany().getId();
				companyIdsBuilder.append(Long.toString(companyId) + ",");
			}

		return companyIdsBuilder.toString().substring(0, companyIdsBuilder.length() - 1);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.tour, menu);

		// Locate MenuItem with ShareActionProvider
		MenuItem item = menu.findItem(R.id.menu_item_share);

		// Fetch and store ShareActionProvider
		mShareActionProvider = (ShareActionProvider) item.getActionProvider();
		updateShareIntent();
		// super.onCreateOptionsMenu(menu, inflater);
	}

	private void orderList() {
		ListView lv = getListView();
		if (lv != null) {
			Map<String, Float> distance = sortByValue(mListener.getDistanceMap());
			ArrayList<TourPoint> newTourPointList = new ArrayList<TourPoint>();

			initAdapter();
			List<TourPoint> tourPointList = new LinkedList<TourPoint>(
					mTour.getTourPointList());

			for (Entry<String, Float> entry : distance.entrySet()) {
				Iterator<TourPoint> iterator = tourPointList.iterator();
				Log.v("distance", entry.getKey() + " " + entry.getValue());
				while (iterator.hasNext()) {
					TourPoint tourPoint = iterator.next();
					if (tourPoint.getCompany().getLocation().getBuilding().getShortName()
							.equals(entry.getKey())) {
						newTourPointList.add(tourPoint);
						iterator.remove();

					}
				}

			}
			newTourPointList = orderFloors(newTourPointList);

			// check if no tourPoint got Lost
			if (mTour.getTourPointList().size() == newTourPointList.size()) {
				mTour.setTourPointList(newTourPointList);
				mAdapter.clear();
				initAdapter();
				makePositionsPersistent();

			} else {
				Log.w(TAG, "error in order list check code");
			}

		}

	}

	/**
	 * 
	 * @param tourPointList
	 *            the tourPoints should be ordered by buildings i.e. all
	 *            tourPoints with the same building should be next to each other
	 * @return ArrayList<TourPoint> where the tourPoints are orderd in respect
	 *         of the building and with a increasing floor number within one
	 *         building
	 */
	private ArrayList<TourPoint> orderFloors(List<TourPoint> tourPointList) {
		Building building = tourPointList.get(0).getCompany().getLocation().getBuilding();
		ArrayList<TourPoint> returnList = new ArrayList<TourPoint>();
		int i = 0;
		for (TourPoint tourPoint : tourPointList) {
			if (tourPoint.getCompany().getLocation().getBuilding().getId() != building
					.getId()) {
				i = returnList.size();
				building = tourPoint.getCompany().getLocation().getBuilding();
			}
			if (returnList.size() > i
					&& returnList.get(i) != null
					&& returnList.get(i).getCompany().getLocation().getFloor()
							.getNumber() < tourPoint.getCompany().getLocation()
							.getFloor().getNumber()) {
				returnList.add(i + 1, tourPoint);
			} else {
				returnList.add(i, tourPoint);
			}
		}
		return returnList;
	}

	private void makePositionsPersistent() {

		int i = 1;
		SQLiteDatabase db = new MySQLiteHelper(getActivity()).getReadableDatabase();
		for (TourPoint tourPoint : mTour.getTourPointList()) {
			tourPoint.setPosition(i);
			TourHelper.updatePositionByTourPointId(db, tourPoint.getId(), i);
			i++;

		}
		db.close();
	}

	public static Map<String, Float> sortByValue(Map<String, Float> map) {
		List<Map.Entry<String, Float>> list = new LinkedList<Map.Entry<String, Float>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
			public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<String, Float> result = new LinkedHashMap<String, Float>();
		for (Map.Entry<String, Float> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.menu_item_start_tour) {
			orderList();
			return true;
		} else if (itemId == R.id.menu_item_export) {

			String exportString = mTour.getName() + ":" + getCompanyIdsOfTour();
			Intent exportIntent = new Intent(getActivity(), NfcActivity.class);
			exportIntent.putExtra(NfcActivity.EXPORT, exportString);
			startActivity(exportIntent);
			return true;
		} else if (itemId == R.id.menu_item_add_tourPoint) {
			mCompanyAddListener.addCompanyToTour(mTour);
			return true;
		} else {

			return super.onOptionsItemSelected(item);
		}
	}

	private String getShareMessage() {
		StringBuilder shareMessage = new StringBuilder();
		shareMessage.append(getString(R.string.label_begin_share_message));
		shareMessage.append("\n");
		shareMessage.append(mTour.getName() + "\n");
		for (TourPoint tourPoint : mTour.getTourPointList()) {
			Company company = tourPoint.getCompany();
			Location companyLocation = company.getLocation();
			Building building = companyLocation.getBuilding();
			Room room = companyLocation.getRoom();
			shareMessage.append(company.getName());
			shareMessage.append(", ");
			shareMessage.append(building.getFullName() + " " + getString(R.string.room)
					+ " " + room.getRoomNo() + " \n");

		}

		return shareMessage.toString();
	}

	private void updateShareIntent() {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, getShareMessage());
		sendIntent.setType(NfcActivity.MIME_TEXT_PLAIN);
		setShareIntent(sendIntent);

	}

	// Call to update the share intent
	private void setShareIntent(Intent shareIntent) {
		if (mShareActionProvider != null) {
			mShareActionProvider.setShareIntent(shareIntent);
		}
	}

	public interface MyDistanceListener {
		public Map<String, Float> getDistanceMap();
	}

	public interface OnTourPointAddListener {
		public void addCompanyToTour(Tour tour);
	}
}
