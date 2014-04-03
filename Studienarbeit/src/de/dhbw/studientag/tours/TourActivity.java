package de.dhbw.studientag.tours;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import de.dhbw.studientag.CompaniesFragment;
import de.dhbw.studientag.LocationServiceActivity;
import de.dhbw.studientag.R;
import de.dhbw.studientag.dbHelpers.CompanyHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.dbHelpers.TourHelper;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Tour;
import de.dhbw.studientag.model.TourPoint;

public class TourActivity extends LocationServiceActivity implements
		TourListFragment.OnTourSelectedListener, TourListFragment.NewTourListener,
		TourFragment.OnTourPointAddListener, TourFragment.MyDistanceListener,
		CompaniesFragment.OnCompanySelectedLitener {

	protected static final String COMPANY = "company";
	protected static final String NEW_TOUR = "newTour";
	protected static final String TAG_TOUR_FRAGMENT = "tourFragment";
	protected static final String TAG_TOUR_LIST_FRAGMENT = "tourListFragment";
	protected static final String TAG_COMPANIES_FRAGMENT = "companiesFragment";
	private static final String TAG = "TourActivity";
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
			transaction.replace(R.id.toursFragmentContainer, fragment,
					TAG_TOUR_LIST_FRAGMENT);

		}

		transaction.commit();
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public void onTourSelected(Tour tour) {

		TourFragment fragment = TourFragment.getInitializedFragement(tour);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.toursFragmentContainer, fragment, TAG_TOUR_FRAGMENT);
		transaction.addToBackStack(TAG_TOUR_FRAGMENT);
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
		transaction.addToBackStack(null);
		transaction.replace(R.id.toursFragmentContainer, companiesFragment,
				TAG_COMPANIES_FRAGMENT).commit();
		setTitle(getString(R.string.label_add_company) + " " + mTour.getName());

	}

	@Override
	public void onCompanySelected(Company company) {
		if (mTour != null) {
			//addTourPoint to tour
			long tourId = mTour.getId();
			MySQLiteHelper dbHelper = new MySQLiteHelper(this);
			TourPoint tourPoint = new TourPoint(company);
			tourPoint.setId(TourHelper.insertTourPoint(dbHelper.getWritableDatabase(),
					tourPoint, tourId));
			mTour.addTourPoint(tourPoint);
			dbHelper.close();
			getFragmentManager().popBackStack();
			getFragmentManager().popBackStack(TAG_TOUR_FRAGMENT, 0);
			mTour = null;
		}else{
			getFragmentManager().popBackStack();
			Log.d(TAG, "onCompanySelectedListener but mTour is null");
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			Fragment fragment = TourFragment.newTour(company, true);
			transaction.addToBackStack(null);
			transaction.replace(R.id.toursFragmentContainer, fragment, TAG_TOUR_FRAGMENT).commit();
			
		}
		

	}

	@Override
	public void createNewTour() {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		SQLiteDatabase db = new MySQLiteHelper(this).getReadableDatabase();
		ArrayList<Company> companyList = (ArrayList<Company>) CompanyHelper
				.getAllCompanies(db);
		db.close();
		CompaniesFragment companiesFragment = CompaniesFragment
				.newCompaniesFragmentInstance(companyList);
		transaction.addToBackStack(null);
		transaction.replace(R.id.toursFragmentContainer, companiesFragment, TAG_COMPANIES_FRAGMENT).commit();
		setTitle("Erstes Unternehmen der Tour: ");
		
	}

}
