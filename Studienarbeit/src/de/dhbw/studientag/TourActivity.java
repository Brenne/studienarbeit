package de.dhbw.studientag;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Tour;

public class TourActivity extends LocationServiceActivity implements
		TourListFragment.OnTourSelectedListener ,
		TourFragment.MyDistanceListener{
	
	protected static final String COMPANY = "company";
	protected static final String NEW_TOUR = "newTour";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//init LocationService
		initLocationService();
		setContentView(R.layout.activity_tour);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		Company company = getIntent().getParcelableExtra(COMPANY);
		Fragment fragment;
		if (company != null) {
			boolean newTour = getIntent().getBooleanExtra(NEW_TOUR, false);
			fragment = TourFragment.newTour(company, newTour);
		} else {

			fragment = new TourListFragment();

		}

		transaction.add(R.id.toursFragmentContainer, fragment);
		transaction.commit();
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}


	@Override
	public void onTourSelected(Tour tour) {

		TourFragment fragment = TourFragment
				.getInitializedFragement(tour);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.toursFragmentContainer, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
		

	}
	


}
