package de.dhbw.studientag;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.dbHelpers.TourHelper;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.TourPoint;

public class TourActivity extends Activity implements
		ToursListFragment.OnTourSelectedListener {
	
	protected static final String COMPANY = "company";
	protected static final String NEW_TOUR = "newTour";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tour);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		Company company = getIntent().getParcelableExtra(COMPANY);
		Fragment fragment;
		if (company != null) {
			boolean newTour = getIntent().getBooleanExtra(NEW_TOUR, false);
			fragment = TourPointFragment.newTour(company, newTour);
		} else {

			fragment = new ToursListFragment();

		}

		transaction.add(R.id.toursFragmentContainer, fragment);
		transaction.commit();
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		

		return true;
	}

	@Override
	public void onTourSelected(Map<String, Object> tourPoint) {
		MySQLiteHelper dbHelper = new MySQLiteHelper(getBaseContext());
		List<TourPoint> tourPoints = TourHelper.getTourPointListByTourId(
				dbHelper.getReadableDatabase(),
				(Long) tourPoint.get(ToursListFragment.TOUR_ID));
		dbHelper.close();
		TourPointFragment fragment = TourPointFragment
				.getInitializedFragement(tourPoints);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.toursFragmentContainer, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
		

	}

}
