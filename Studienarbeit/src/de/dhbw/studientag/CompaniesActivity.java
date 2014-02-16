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
import android.widget.SimpleCursorAdapter;
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
		
//		final SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
//				this, 
//				android.R.layout.simple_list_item_1, 
//				companyDB.getCursor(companyDB.getReadableDatabase(), CompanyHelper.COMPANY_ALL_COLUMNS), 
//				new String[]{"name","plz"}, 
//				new int[]{android.R.id.text1, 
//					android.R.id.text2},
//				0);
		final ArrayAdapter<Company> adapter = new ArrayAdapter<Company>(this, android.R.layout.simple_list_item_1, 
				companyDB.getAllCompanies(companyDB.getReadableDatabase()));
		setListAdapter(adapter);
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.searchCompanies);
		textView.setAdapter(adapter);
		
		

	}

	@Override
	protected void onListItemClick(android.widget.ListView l, android.view.View v, int position, long id) {
	
		Company selectedCompany = (Company) getListAdapter().getItem(position);
		Intent intent = new Intent(this,CompanyActivity.class);
		intent.putExtra("company", selectedCompany);
		startActivity(intent);
		

		
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.companies, menu);
		return true;
	}

}
