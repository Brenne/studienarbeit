package de.dhbw.studientag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import de.dhbw.studientag.dbHelpers.CompanyRoomHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.dbHelpers.TourHelper;
import de.dhbw.studientag.model.Building;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.CompanyLocation;
import de.dhbw.studientag.model.Room;
import de.dhbw.studientag.model.TourPoint;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link TourPointFragment.OnFloorSelectedListener} interface to handle
 * interaction events. Use the {@link TourPointFragment#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
public class TourPointFragment extends ListFragment implements
		MyAdapterWithBin.OnBinClicked {

	private static final String TAG ="TourPointFragment";
	private ShareActionProvider mShareActionProvider;
	private List<TourPoint> tourPoints = new ArrayList<TourPoint>();
	private List<HashMap<String, ?>> data = new ArrayList<HashMap<String, ?>>();
	private Company company;
	private MyAdapterWithBin adapter;
	private boolean newTour = false;
	private boolean tourNameChanged = false;
	private EditText tourName;
	private static final String COMPANY_NAME = "CompanyName";
	private static final String COMPANY_LOCATION = "Location";
	protected static final String TOUR_POINT_ID="tourPontId";


	public TourPointFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
//		company = getArguments().getParcelable(COMPANY);
		// tourPoints = getArguments().getParcelableArrayList("tourPoints");
		initAdapter();
		adapter.setOnBinClickListener(this);
		if (company != null) {
			
			if (newTour) {
				
				MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity().getBaseContext());
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				String tourName = TourHelper.getFreeTourName(db, getString(R.string.label_TourName));
				TourPoint tourPoint = new TourPoint(tourName, 0, company, 0);
				tourPoint.setTourId(TourHelper.insertTour(db, tourPoint.getName()));
				tourPoint.setId(TourHelper.insertTourPoint(db, tourPoint));
				db.close();
				dbHelper.close();
				tourPoints.add(0, tourPoint);
				addToAdapter(tourPoint);
			}

		} else if (tourPoints != null && !tourPoints.isEmpty()) {

			for (TourPoint tourPoint : tourPoints) {
				addToAdapter(tourPoint);
			}
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_tour_point, container, false);
		tourName = (EditText) v.findViewById(R.id.editText_TourName);
		if (!tourPoints.isEmpty()) {
			tourName.setText(tourPoints.get(0).getName());
		} else if (tourNameExists(tourName.getText().toString())) {
			tourName.setError(getString(R.string.warning_tourName_exists));
		}

		tourName.addTextChangedListener(new TextWatcher() {
			final String originalTourName = tourName.getText().toString();

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				tourNameChanged=true;
				if (s.toString().equals(originalTourName)) {
					tourName.setError(null);
					tourNameChanged=false;
				} else if (s.toString().isEmpty()) {
					tourName.setError(getString(R.string.warning_empty_TourName));
				} else if (tourNameExists(s.toString())) {
					tourName.setError(getString(R.string.warning_tourName_exists));
				} else {
					tourName.setError(null);
				}

			}
		});
		return v;
	}

	private boolean tourNameExists(String tourName) {
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
		boolean returnV = TourHelper.tourNameExists(dbHelper.getReadableDatabase(),
				tourName);
		dbHelper.close();
		return returnV;
	}

	private void initAdapter() {
		adapter = new MyAdapterWithBin(getActivity(), data, new String[] { COMPANY_NAME,
				COMPANY_LOCATION });
		adapter.setOnBinClickListener(this);
		setListAdapter(adapter);
	}


	private void addToAdapter(TourPoint tourPoint) {
		HashMap<String, Object> newItem = new HashMap<String, Object>();
		newItem.put(COMPANY_NAME, tourPoint.getCompany().getName());
		CompanyLocation companyLocation = getCompanyLocationByCompany(
				tourPoint.getCompany(), getActivity().getBaseContext());
		newItem.put(COMPANY_LOCATION, companyLocation.getBuilding().getFullName() + " "
				+ companyLocation.getRoom().getRoomNo());
		newItem.put(TOUR_POINT_ID, tourPoint.getId());
		data.add(newItem);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		TourPoint tourPoint = tourPoints.get(position);
		Intent intent = new Intent(getActivity().getBaseContext(), CompanyActivity.class);
		intent.putExtra(TourActivity.COMPANY, tourPoint.getCompany());
		startActivity(intent);

	}

	private static CompanyLocation getCompanyLocationByCompany(Company company,
			Context context) {
		MySQLiteHelper dbHelper = new MySQLiteHelper(context);
		CompanyLocation companyLocation = CompanyRoomHelper.getLocationByCompanyId(
				company.getId(), dbHelper.getReadableDatabase());
		dbHelper.close();
		return companyLocation;
	}

	public static TourPointFragment getInitializedFragement(List<TourPoint> tourPoints) {
		TourPointFragment fragment = new TourPointFragment();
		fragment.tourPoints = tourPoints;
		return fragment;
	}

	public static TourPointFragment newTour(Company company, boolean newTour) {
		TourPointFragment fragment = new TourPointFragment();
		fragment.company = company;
		fragment.newTour = newTour;
		return fragment;
	}

	@Override
	public void onPause() {
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity().getBaseContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String tourName = this.tourName.getText().toString();
		if (!tourName.isEmpty() && tourNameChanged) {
			long tourId = tourPoints.get(0).getTourId();
			TourHelper.updateTourName(db, tourId, tourName);
		}
		db.close();
		dbHelper.close();
		super.onPause();
	}

	@Override
	public void binClicked(int position) {
		
		if(tourPoints.isEmpty())
			return;
		long tourPointId= tourPoints.get(position).getId();
//		long tourId = tourPoints.get(0).getTourId();
		tourPoints.remove(position);
		
		Iterator<HashMap<String, ?>> iterator = data.iterator();
		while(iterator.hasNext()){
			HashMap<String, ? > tourPointInfo =  iterator.next();
			long tmpTourPointId = (Long) tourPointInfo.get(TOUR_POINT_ID);
			if(tmpTourPointId==tourPointId){
				iterator.remove();
				break;
			}	
		}
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
		TourHelper.deleteTourPointById(dbHelper.getWritableDatabase(), tourPointId);
		//if there are no more TourPoints in this tour also delete the tour
		if(data.isEmpty()){
//			TourHelper.deleteTourById(dbHelper.getWritableDatabase(), tourId);
			getActivity().onBackPressed();
		}else{
			initAdapter();
		}
		dbHelper.close();

	}
	
	private String getCompanyIdsOfTour(){
		StringBuilder companyIdsBuilder = new StringBuilder();
		if(tourPoints != null && !tourPoints.isEmpty())
		for(TourPoint tourPoint : tourPoints){
			long companyId = tourPoint.getCompany().getId();
			companyIdsBuilder.append(Long.toString(companyId)+",");
		}
		
		return companyIdsBuilder.toString().substring(0, companyIdsBuilder.length()-1);
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.tour, menu);
	
		// Locate MenuItem with ShareActionProvider
	    MenuItem item = menu.findItem(R.id.menu_item_share);

	    // Fetch and store ShareActionProvider
	    mShareActionProvider = (ShareActionProvider) item.getActionProvider();
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
//		sendIntent.putExtra(Intent.EXTRA_TEXT, tourPoints.get(0).getName()+":"+getCompanyIdsOfTour());
		sendIntent.putExtra(Intent.EXTRA_TEXT, getShareMessage());
		sendIntent.setType(ImportActivity.MIME_TEXT_PLAIN);
		setShareIntent(sendIntent);
		
		MenuItem exportItem = menu.findItem(R.id.menu_item_export);
		Intent exportIntent = new Intent();
		exportIntent.setAction(Intent.ACTION_SEND);
		exportIntent.putExtra(Intent.EXTRA_TEXT, tourPoints.get(0).getName()+":"+getCompanyIdsOfTour());
		exportIntent.setType(ImportActivity.MIME_TEXT_PLAIN);
		((ShareActionProvider)exportItem.getActionProvider()).setShareIntent(exportIntent);
//		super.onCreateOptionsMenu(menu, inflater);
	}
	
	private String getShareMessage(){
		StringBuilder shareMessage = new StringBuilder();
		shareMessage.append(getString(R.string.label_begin_share_message));
		shareMessage.append("\n");
		String tourName =tourPoints.get(0).getName();
		shareMessage.append(tourName+"\n");
		SQLiteDatabase db = new MySQLiteHelper(getActivity()).getReadableDatabase();
		for(TourPoint tourPoint : tourPoints){
			Company company = tourPoint.getCompany();
			CompanyLocation companyLocation = CompanyRoomHelper.getLocationByCompanyId(company.getId(), db);
			Building building =companyLocation.getBuilding();
			Room room = companyLocation.getRoom();
			shareMessage.append(company.getName());
			shareMessage.append(", ");
			shareMessage.append(building.getFullName()+ " "+getString(R.string.room)
					+ " "+room.getRoomNo()+" \n");
			
			
			
		}
		db.close();
		return shareMessage.toString();
	}
	
	// Call to update the share intent
	private void setShareIntent(Intent shareIntent) {
	    if (mShareActionProvider != null) {
	        mShareActionProvider.setShareIntent(shareIntent);
	    }
	}

}
