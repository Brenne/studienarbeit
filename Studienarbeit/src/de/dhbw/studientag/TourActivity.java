package de.dhbw.studientag;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import de.dhbw.studientag.dbHelpers.CompanyHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Tour;
import de.dhbw.studientag.model.TourPoint;

public class TourActivity extends LocationServiceActivity implements
		TourListFragment.OnTourSelectedListener, TourFragment.OnTourPointAddListener,
		TourFragment.MyDistanceListener, CompaniesFragment.OnCompanySelectedLitener {

	protected static final String COMPANY = "company";
	protected static final String NEW_TOUR = "newTour";
	protected static final String TAG_TOUR_FRAGMENT = "tourFragment";
	protected static final String TAG_TOUR_LIST_FRAGMENT="tourListFragment";
	protected static final String TAG_COMPANIES_FRAGMENT = "companiesFragment";
	private Tour mTour;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// init LocationService
		initLocationService();
		setContentView(R.layout.activity_tour);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		Company company = getIntent().getParcelableExtra(COMPANY);
		Fragment fragment;
		if (company != null) {
			boolean newTour = getIntent().getBooleanExtra(NEW_TOUR, false);
			fragment = TourFragment.newTour(company, newTour);
			transaction.replace(R.id.toursFragmentContainer, fragment, TAG_TOUR_FRAGMENT);
		} else {

			fragment = new TourListFragment();
			transaction.replace(R.id.toursFragmentContainer, fragment, TAG_TOUR_LIST_FRAGMENT);

		}

		
		transaction.commit();
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public void onTourSelected(Tour tour) {

		TourFragment fragment = TourFragment.getInitializedFragement(tour);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.toursFragmentContainer, fragment, TAG_TOUR_FRAGMENT);
		transaction.addToBackStack(null);
		transaction.commit();

	}

	@Override
	public void addCompanyToTour(Tour tour) {
		MySQLiteHelper dbHelper = new MySQLiteHelper(this);
		ArrayList<Company> allCompanies = (ArrayList<Company>) CompanyHelper
				.getAllCompanies(dbHelper.getReadableDatabase());
		dbHelper.close();
		Iterator<Company> iterator = allCompanies.iterator();
		mTour = tour;

		while (iterator.hasNext()) {
			Company company = iterator.next();
			for (TourPoint tourPoint : tour.getTourPointList()) {
				if (tourPoint.getCompany().getId() == company.getId()) {
					iterator.remove();
				}
			}
		}

		ListFragment companiesFragment = CompaniesFragment
				.newCompaniesFragmentInstance(allCompanies);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.toursFragmentContainer, companiesFragment,
				TAG_COMPANIES_FRAGMENT).commit();
		setTitle(getString(R.string.label_add_company) + " " + mTour.getName());

	}

	@Override
	public void onCompanySelected(Company company) {
		if (mTour != null) {
			TourFragment fragment = TourFragment.getInitializedFragement(mTour, company);

			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			Fragment companiesFragment = getFragmentManager().findFragmentByTag(
					TAG_COMPANIES_FRAGMENT);

			transaction.remove(companiesFragment);
			transaction.replace(R.id.toursFragmentContainer, fragment, TAG_TOUR_FRAGMENT);

			transaction.commit();
		}

	}

	@Override
	public void onBackPressed() {
		final TourFragment tourFragment = (TourFragment) getFragmentManager()
				.findFragmentByTag(TAG_TOUR_FRAGMENT);
		final CompaniesFragment companyFragment = (CompaniesFragment) getFragmentManager()
				.findFragmentByTag(TAG_COMPANIES_FRAGMENT);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if (tourFragment != null)
			transaction.remove(tourFragment);
		if (companyFragment != null)
			transaction.remove(companyFragment);
		transaction.commit();
		super.onBackPressed();
	}
	
	
}
