package de.dhbw.studientag;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import de.dhbw.studientag.dbHelpers.CompanyHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.tours.TourActivity;

public class MainActivity extends ListActivity {

	private MySQLiteHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		final ArrayList<String> list = new ArrayList<String>();
		list.add(getString(R.string.label_companies));
		list.add(getString(R.string.label_faculties));
		list.add(getString(R.string.title_activity_locations));
		list.add(getString(R.string.label_comments));
		list.add(getString(R.string.title_activity_tours));
		
	
		
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

			ArrayList<Company> companies = (ArrayList<Company>) CompanyHelper
					.getAllCompanies(dbHelper.getReadableDatabase());
			intent.putParcelableArrayListExtra(CompaniesActivity.COMPANIES, companies);

			startActivity(intent);
		} else if (selected.equals(getString(R.string.label_faculties))) {
			DialogFragment newFragment = new SelectFacultyDialogFragment();
			newFragment.show(getFragmentManager(), "faculties");
		} else if (selected.equals(getString(R.string.title_activity_locations))) {
			Intent intent = new Intent(this, LocationsActivity.class);
			startActivity(intent);
		} else if (selected.equals(getString(R.string.label_comments))){
			Intent intent = new Intent(this, CommentsActivity.class);
			startActivity(intent);
		} else if(selected.equals(getString(R.string.title_activity_tours))){
			Intent intent = new Intent(this, TourActivity.class);
			startActivity(intent);
		}

	}



	@Override
	protected void onPause() {
		dbHelper.close();
		super.onPause();
	}

}
