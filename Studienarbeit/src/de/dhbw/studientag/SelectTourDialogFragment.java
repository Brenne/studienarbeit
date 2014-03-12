package de.dhbw.studientag;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.dbHelpers.TourHelper;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Tour;
import de.dhbw.studientag.model.TourPoint;

public class SelectTourDialogFragment extends DialogFragment implements OnBinClicked{

	private Company company;
	private SelectTourDialogAdapter adapter;
	private List<Tour> mTourList  = new ArrayList<Tour>();
	protected static final String COMPANY_IN_TOUR = "companyInTour";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.label_Select_Tour).setAdapter(adapter,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Log.v("SelectTourDialogFragment", "onClick");
						if (which == 0) {
							Intent intent = new Intent(getActivity().getBaseContext(),
									TourActivity.class);
							intent.putExtra(TourActivity.COMPANY, company);
							intent.putExtra(TourActivity.NEW_TOUR, true);
							startActivity(intent);
						} else {
							MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity()
									.getBaseContext());
							long tourId = (Long) ((Tour)adapter.getItem(which)).getId();
							TourPoint tourPoint = new TourPoint(company);
							TourHelper.insertTourPoint(dbHelper.getWritableDatabase(),
									tourPoint, tourId);
							dbHelper.close();
						}

					}
				});
		return builder.create();
	}

	@Override
	public void onAttach(Activity activity) {
		//the boolean is true if the company is in this tour
		List<Pair<Boolean,Tour>> pTourList = new ArrayList<Pair<Boolean,Tour>>();
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity().getBaseContext());
		mTourList = (TourHelper.getAllTours(dbHelper
						.getReadableDatabase()));
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		for (Tour tour : mTourList) {
			long tourId = tour.getId();
		
			boolean isCompanyInTour= TourHelper.isCompanyInTour(db, company.getId(), tourId);
			Pair<Boolean,Tour>	pair = new Pair<Boolean, Tour>(isCompanyInTour, tour);
			pTourList.add(pair);
		}
		db.close();
		dbHelper.close();
		Tour dummyTour = new Tour(getString(R.string.label_create_new_Tour));
		Pair<Boolean,Tour> firstEntry = new Pair<Boolean, Tour>(false,dummyTour);
		pTourList.add(0, firstEntry);
		adapter = new SelectTourDialogAdapter(activity, pTourList);
		adapter.setOnBinClickListener(this);
		super.onAttach(activity);
	}

	public static SelectTourDialogFragment newInstance(Company company) {
		SelectTourDialogFragment frag = new SelectTourDialogFragment();
		frag.company = company;
		return frag;
	}

	@Override
	public void binClicked(int position) {
		Tour tour = mTourList.get(--position);
		TourPoint tourPoint = getTourPointBy(tour, company);
		if(tourPoint != null){
			MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
			TourHelper.deleteTourPointById(dbHelper.getWritableDatabase(), tourPoint.getId());
			dbHelper.close();
		}else{
			Log.w("SelectTourDialogFragment", "delete Company from tour but company not in tour");
		}
		
	}
	
	//each company is only one time in a tour get this tourPoint
	private TourPoint getTourPointBy(Tour tour, Company company){
		
		for(TourPoint tourPoint: tour.getTourPointList()){
			if(tourPoint.getCompany().getId()== company.getId())
				return tourPoint;
		}
		return null;
	}

}
