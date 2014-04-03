package de.dhbw.studientag.tours;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
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
import de.dhbw.studientag.CompanyActivity;
import de.dhbw.studientag.OnBinClicked;
import de.dhbw.studientag.R;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.dbHelpers.TourHelper;
import de.dhbw.studientag.model.Building;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Location;
import de.dhbw.studientag.model.Room;
import de.dhbw.studientag.model.Tour;
import de.dhbw.studientag.model.TourPoint;

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
				// TODO rework if branch

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
			// mTourName.setError(getString(R.string.warning_tourName_exists));
		}

		mTourName.addTextChangedListener(new TextWatcher() {
			String originalTourName = mTourName.getText().toString();

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
				if (mTour.getName().equals(s.toString())
						|| s.toString().equals(originalTourName)) {
					mTourName.setError(null);
					mTourNameChanged = false;
				} else if (s.toString().isEmpty()) {
					mTourName.setError(getString(R.string.warning_empty_TourName));
					mTourNameChanged = false;
				} else if (tourNameExists(s.toString())) {
					mTourName.setError(getString(R.string.warning_tourName_exists));
					mTourNameChanged = false;
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

		} catch (ClassCastException castExeption) {
			Log.e(TAG,
					"activity did not ipmlement MyDistanceListener or OnTourPointAddListener",
					castExeption);
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

	public static TourFragment newTour(Company company, boolean newTour) {
		// TODO maybe insert new tour here into db;
		TourFragment fragment = new TourFragment();
		fragment.mCompany = company;
		fragment.mNewTour = newTour;
		return fragment;
	}

	@Override
	public void onPause() {
		mListener.requestUpdates(false);
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
		mListener.requestUpdates(true);
		if (mTour != null && mTour.getId() != 0) {
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case R.id.menu_item_start_tour:
			mTour.setTourPointList(mListener.bestTourPointList(mTour.getTourPointList()));
			mAdapter.clear();
			initAdapter();
			return true;
		case R.id.menu_item_export:
			String exportString = mTour.getName() + ":" + getCompanyIdsOfTour();
			Intent exportIntent = new Intent(getActivity(), NfcActivity.class);
			exportIntent.putExtra(NfcActivity.EXPORT, exportString);
			startActivity(exportIntent);
			return true;
		case R.id.menu_item_add_tourPoint:
			if (mTourNameChanged)
				mTour.setName(mTourName.getText().toString());
			mCompanyAddListener.addCompanyToTour(mTour);
			return true;
		default:
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

	private void setShareIntent(Intent shareIntent) {
		if (mShareActionProvider != null) {
			mShareActionProvider.setShareIntent(shareIntent);
		}
	}

	public interface MyDistanceListener {
		public List<TourPoint> bestTourPointList(List<TourPoint> tourPoints);

		public void requestUpdates(boolean onOff);
	}

	public interface OnTourPointAddListener {
		public void addCompanyToTour(Tour tour);
	}
}
