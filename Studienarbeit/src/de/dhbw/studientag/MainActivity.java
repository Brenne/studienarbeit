package de.dhbw.studientag;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.widget.ArrayAdapter;
import de.dhbw.studientag.model.db.CompanyHelper;
import de.dhbw.studientag.model.db.MySQLiteHelper;

public class MainActivity extends ListActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final ArrayList<String> list = new ArrayList<String>();
		list.add(getString(R.string.label_companies));
		list.add(getString(R.string.label_faculties));
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		setListAdapter(adapter);
		

		
	}
	

	
	@Override
	protected void onListItemClick(android.widget.ListView l, android.view.View v, int position, long id) {
	
		String selected = (String) getListAdapter().getItem(position);	
		
		if(selected.equals(getString(R.string.label_companies))){
			Intent intent = new Intent(this, CompaniesActivity.class);
			MySQLiteHelper mysqlHelper = new MySQLiteHelper(this);
			ArrayList<? extends Parcelable> companies = (ArrayList<? extends Parcelable>) CompanyHelper.getAllCompanies(mysqlHelper.getReadableDatabase());
			intent.putParcelableArrayListExtra("companies", companies);
			mysqlHelper.close();
			startActivity(intent);
		}else if(selected.equals(getString(R.string.label_faculties))){
		    DialogFragment newFragment = new SelectFacultyDialogFragment();
		    newFragment.show(getFragmentManager(), "faculties");
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	


}
