package de.dhbw.studientag;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.TestData;
import de.dhbw.studientag.model.db.CompanyHelper;
import de.dhbw.studientag.model.db.MySQLiteHelper;

public class CompaniesActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_companies);
//		MySQLiteHelper dbHelper = new MySQLiteHelper(getBaseContext());
		CompanyHelper companyDB = new CompanyHelper(getBaseContext());
		

		final ArrayAdapter<Company> adapter = new ArrayAdapter<Company>(this, android.R.layout.simple_list_item_1, 
				companyDB.getAllCompanies(companyDB.getReadableDatabase()));
		setListAdapter(adapter);
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.searchCompanies);
		textView.setAdapter(adapter);
		
		

	}

	@Override
	protected void onListItemClick(android.widget.ListView l, android.view.View v, int position, long id) {
	
		String selected = (String) getListAdapter().getItem(position);
		Log.d("Studientagapp", selected);
		
		
		if(selected.equals(getString(R.string.label_companies))){
			Intent intent = new Intent(this, CompaniesActivity.class);
			startActivity(intent);
		}else if(selected.equals(getString(R.string.label_faculties))){
			
		}
		
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.companies, menu);
		return true;
	}

}
