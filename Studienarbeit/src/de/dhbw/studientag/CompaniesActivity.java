package de.dhbw.studientag;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import de.dhbw.studientag.model.TestData;

public class CompaniesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_companies);
		TestData testData = new TestData();
		View listView = findViewById(R.id.listView);
		final ListView companiesList = (ListView) listView;
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 
				testData.getCompanyNames());
		companiesList.setAdapter(adapter);
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.searchCompanies);
		textView.setAdapter(adapter);
		
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.companies, menu);
		return true;
	}

}
