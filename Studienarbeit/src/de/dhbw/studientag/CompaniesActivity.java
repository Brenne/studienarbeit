package de.dhbw.studientag;

import java.util.List;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import de.dhbw.studientag.model.Company;

public class CompaniesActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_companies);
		String title = (String) getIntent().getCharSequenceExtra("title");
		if (title == null)
			title = getString(R.string.label_companies);
		setTitle(title);

		List<Company> companies = getIntent().getParcelableArrayListExtra(
				"companies");
		// final SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
		// this,
		// android.R.layout.simple_list_item_1,
		// companyDB.getCursor(companyDB.getReadableDatabase(),
		// CompanyHelper.COMPANY_ALL_COLUMNS),
		// new String[]{"name","plz"},
		// new int[]{android.R.id.text1,
		// android.R.id.text2},
		// 0);
		final ArrayAdapter<Company> adapter = new ArrayAdapter<Company>(this,
				android.R.layout.simple_list_item_1, companies);

		setListAdapter(adapter);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Company selectedCompany = (Company) getListAdapter().getItem(position);
		Intent intent = new Intent(this, CompanyActivity.class);
		intent.putExtra("company", selectedCompany);
		startActivity(intent);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.companies, menu);

		// Get the SearchView and set the searchable configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search)
				.getActionView();
		// Assumes current activity is the searchable activity
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(false); // Do not iconify the widget;
													// expand it by default
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO maybe not LIKE search but full text search?
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {

				Filterable adapter = (Filterable) getListAdapter();
				adapter.getFilter().filter(newText);
				return false;
			}
		});

		return true;
	}

}
