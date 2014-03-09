package de.dhbw.studientag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.dbHelpers.TourHelper;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.TourPoint;

public class SelectTourDialogFragment extends DialogFragment {

	private Company company;
	private MyDialogAdapter adapter;
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
							long tourId = (Long) ((Map<String, Object>) adapter
									.getItem(which)).get(ToursListFragment.TOUR_ID);
							TourPoint tourPoint = new TourPoint(tourId, company);
							TourHelper.insertTourPoint(dbHelper.getWritableDatabase(),
									tourPoint);
							dbHelper.close();
						}

					}
				});
		return builder.create();
	}

	@Override
	public void onAttach(Activity activity) {
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity().getBaseContext());
		List<Map<String, Object>> allToursList = ToursListFragment
				.allTourPointsMapToAllToursList(TourHelper.getAllTours(dbHelper
						.getReadableDatabase()));
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		for (Map<String, Object> tour : allToursList) {
			long tourId = (Long) tour.get(ToursListFragment.TOUR_ID);

			if (TourHelper.isCompanyInTour(db, company.getId(), tourId)) {
				List<TourPoint> tourPoints = TourHelper.getTourPointListByTourId(db,
						tourId);
				for (TourPoint tourPoint : tourPoints) {
					if (tourPoint.getCompany().getId() == company.getId()) {
						tour.put(TourPointFragment.TOUR_POINT_ID, tourPoint.getId());
						break;
					}
				}
				tour.put(COMPANY_IN_TOUR, true);
			} else {
				tour.put(COMPANY_IN_TOUR, false);
			}
		}
		HashMap<String, Object> firstEntry = new HashMap<String, Object>();
		firstEntry.put(ToursListFragment.TOUR_NAME,
				getString(R.string.label_create_new_Tour));
		allToursList.add(0, firstEntry);
		adapter = new MyDialogAdapter(activity, allToursList, R.layout.dialog_list,
				new String[] { ToursListFragment.TOUR_NAME, COMPANY_IN_TOUR });
		db.close();
		dbHelper.close();
		super.onAttach(activity);
	}

	public static SelectTourDialogFragment newInstance(Company company) {
		SelectTourDialogFragment frag = new SelectTourDialogFragment();
		frag.company = company;
		return frag;
	}

}
