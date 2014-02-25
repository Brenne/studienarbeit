package de.dhbw.studientag;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.widget.ArrayAdapter;
import de.dhbw.studientag.model.db.BuildingHelper;
import de.dhbw.studientag.model.db.CompanyHelper;
import de.dhbw.studientag.model.db.MySQLiteHelper;

public class MainActivity extends ListActivity {

	private MySQLiteHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ArrayList<String> list = new ArrayList<String>();
		list.add(getString(R.string.label_companies));
		list.add(getString(R.string.label_faculties));
		list.add(getString(R.string.title_activity_locations));
		list.add(getString(R.string.label_comments));
		
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		setListAdapter(adapter);
		dbHelper = new MySQLiteHelper(this);

	}

	@Override
	protected void onListItemClick(android.widget.ListView l,
			android.view.View v, int position, long id) {

		String selected = (String) getListAdapter().getItem(position);

		if (selected.equals(getString(R.string.label_companies))) {
			Intent intent = new Intent(this, CompaniesActivity.class);

			ArrayList<? extends Parcelable> companies = (ArrayList<? extends Parcelable>) CompanyHelper
					.getAllCompanies(dbHelper.getReadableDatabase());
			intent.putParcelableArrayListExtra("companies", companies);

			startActivity(intent);
		} else if (selected.equals(getString(R.string.label_faculties))) {
			DialogFragment newFragment = new SelectFacultyDialogFragment();
			newFragment.show(getFragmentManager(), "faculties");
		} else if (selected.equals(getString(R.string.title_activity_locations))) {
			Intent intent = new Intent(this, LocationsActivity.class);
			ArrayList<? extends Parcelable> buildings = (ArrayList<? extends Parcelable>) BuildingHelper
					.getAllBuildings(dbHelper.getReadableDatabase());
			intent.putParcelableArrayListExtra("buildings", buildings);
			startActivity(intent);
		} else if (selected.equals(getString(R.string.label_comments))){
			Intent intent = new Intent(this, CommentsActivity.class);
			startActivity(intent);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPause() {
		dbHelper.close();
		super.onPause();
	}

}
