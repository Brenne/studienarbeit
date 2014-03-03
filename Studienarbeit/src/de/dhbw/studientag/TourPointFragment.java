package de.dhbw.studientag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListFragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import de.dhbw.studientag.dbHelpers.CompanyRoomHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.dbHelpers.TourHelper;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.CompanyLocation;
import de.dhbw.studientag.model.TourPoint;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link TourPointFragment.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link TourPointFragment#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
public class TourPointFragment extends ListFragment {
	

	private List<TourPoint> tourPoints;
	private List<HashMap<String, String>> data = new ArrayList<HashMap<String,String>>();
	private Company company;
	private SimpleAdapter adapter;
	private boolean newTour=false;
	private EditText tourName;
	private static final String COMPANY_NAME="CompanyName";
	private static final String COMPANY_LOCATION="Location";
	private static final String COMPANY ="Company";
	private static final String NEW_TOUR="newTour";
	

	public TourPointFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		company = getArguments().getParcelable(COMPANY);
		tourPoints = getArguments().getParcelableArrayList("tourPoints");
		initAdapter();
		
		if( company !=null){
			//TODO füge company der Liste hinzu
			newTour=getArguments().getBoolean(NEW_TOUR);
			
			if(newTour){
				
				addToAdapter(company);
			}
			
		}else if( tourPoints != null && !tourPoints.isEmpty()){
			
			for(TourPoint tourPoint : tourPoints){
				addToAdapter(tourPoint.getCompany());
			}
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v=  inflater.inflate(R.layout.fragment_tour_point, container, false);
		tourName = (EditText) v.findViewById(R.id.editText_TourName);
		if(!newTour){
			tourName.setText(tourPoints.get(0).getName());
		}
		return v;
	}
	


	private void initAdapter(){
		
		HashMap<String,String> listItem = new HashMap<String, String>();
		data.add(listItem);
		adapter = new SimpleAdapter(getActivity().getBaseContext(),
				data,
				android.R.layout.simple_list_item_2,
				new String[]{COMPANY_NAME,COMPANY_LOCATION},
				new int[]{android.R.id.text1,android.R.id.text2});
		setListAdapter(adapter);
	}

	private void addToAdapter(Company company){
		HashMap<String,String> newItem = new HashMap<String,String>();
		newItem.put(COMPANY_NAME, company.getName());
		CompanyLocation companyLocation = getCompanyLocationByCompany(company,getActivity().getBaseContext());
		newItem.put(COMPANY_LOCATION,companyLocation.getBuilding().getFullName()+" "+companyLocation.getRoom().getRoomNo());
		data.add(newItem);
		
		adapter.notifyDataSetChanged();

	}
	
	private  static CompanyLocation getCompanyLocationByCompany(Company company, Context context){
		MySQLiteHelper dbHelper = new MySQLiteHelper(context);
		CompanyLocation companyLocation = CompanyRoomHelper.getLocationByCompanyId(company.getId(), dbHelper.getReadableDatabase());
		dbHelper.close();
		return companyLocation;
	}
	
	public static TourPointFragment getInitializedFragement(List<TourPoint> tourPoints){
		TourPointFragment fragment = new TourPointFragment();
		Bundle args = new Bundle();
		args.putParcelableArrayList("tourPoints", (ArrayList<? extends Parcelable>) tourPoints);
		fragment.setArguments(args);
		return fragment;
	}
	
	public static TourPointFragment newTourPoint(Company company, boolean newTour){
		TourPointFragment fragment = new TourPointFragment();
		Bundle args = new Bundle();
		args.putParcelable(COMPANY, company);
		if(newTour){
			args.putBoolean(NEW_TOUR, newTour);
		}
		fragment.setArguments(args);
		
		return fragment;
		
	}
	

	
	@Override
	public void onPause() {
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity().getBaseContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String tourName = this.tourName.getText().toString();
		if(newTour && !tourName.isEmpty() && company !=null ){
			TourPoint tourPoint = new TourPoint(tourName, 0, company, 0);
			
			
			tourPoint.setTourId(TourHelper.insertTour(db, tourPoint.getName()));
			TourHelper.insertTourPoint(db, tourPoint);
			
		}else if(tourPoints != null && !tourPoints.isEmpty() && !tourName.isEmpty()){
			long tourId = tourPoints.get(0).getTourId();
			TourHelper.updateTourName(db, tourId, tourName);
		}
		db.close();
		dbHelper.close();
		super.onPause();
	}
	


}
