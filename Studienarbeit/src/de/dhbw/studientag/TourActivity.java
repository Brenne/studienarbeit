package de.dhbw.studientag;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.dbHelpers.TourHelper;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.TourPoint;

public class TourActivity extends Activity  implements ToursListFragment.OnTourSelectedListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tour);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		Company company = getIntent().getParcelableExtra("company");
		Fragment fragment;
		if(company !=null){
			boolean newTour = getIntent().getBooleanExtra("newTour", false);
			 fragment = TourPointFragment.newTourPoint(company, newTour);
		}else{
			MySQLiteHelper dbHelper = new MySQLiteHelper(this);
			Map<Integer, List<TourPoint>> tourPoints = TourHelper.getAllTours(dbHelper.getReadableDatabase());
			
			dbHelper.close();
			 fragment = ToursListFragment.newInstance(tourPoints);
			
		}
		
		transaction.add(R.id.toursFragmentContainer,fragment);
		transaction.commit();
		
		
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tour, menu);
		return true;
	}





	@Override
	public void onTourSelected(Map<String, Object> tourPoint) {
		MySQLiteHelper dbHelper = new MySQLiteHelper(getBaseContext());
		List<TourPoint> tourPoints = TourHelper.getTourPointListByTourId(dbHelper.getReadableDatabase(), (Long) tourPoint.get(ToursListFragment.TOUR_ID));
		dbHelper.close();
		TourPointFragment fragment = TourPointFragment.getInitializedFragement(tourPoints);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.toursFragmentContainer,fragment);
		transaction.commit();
		
	}

}
