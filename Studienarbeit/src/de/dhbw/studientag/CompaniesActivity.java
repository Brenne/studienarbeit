package de.dhbw.studientag;

import java.util.List;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.Toast;
import de.dhbw.studientag.model.Company;

public class CompaniesActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_companies);
		String title = (String) getIntent().getCharSequenceExtra("title");
		if(title==null)
			title=getString(R.string.label_companies);
		setTitle(title);

		List<Company> companies = getIntent().getParcelableArrayListExtra("companies");
//		final SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
//				this, 
//				android.R.layout.simple_list_item_1, 
//				companyDB.getCursor(companyDB.getReadableDatabase(), CompanyHelper.COMPANY_ALL_COLUMNS), 
//				new String[]{"name","plz"}, 
//				new int[]{android.R.id.text1, 
//					android.R.id.text2},
//				0);
		final ArrayAdapter<Company> adapter = new ArrayAdapter<Company>(this, android.R.layout.simple_list_item_1, 
				companies);
		setListAdapter(adapter);
		
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.searchCompanies);
		textView.setAdapter(adapter);
		
		

		
		
		
//		Intent intent = getIntent();
//	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//	        String query = intent.getStringExtra(SearchManager.QUERY);
//	        Log.i("suche",query);
//	        Toast toast = Toast.makeText(this, query, query.length());
//	        toast.show();
//	      }

	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
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
		getMenuInflater().inflate(R.menu.options_menu, menu);
		
		 // Get the SearchView and set the searchable configuration
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
	    // Assumes current activity is the searchable activity
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

	    return true;
	}
	
	
	private void handleIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            Log.i("studeintag", query);
            
            
        }
        
	}

}
