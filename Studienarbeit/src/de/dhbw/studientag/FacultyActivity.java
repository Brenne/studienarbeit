package de.dhbw.studientag;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import de.dhbw.studientag.dbHelpers.CompanyHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Subject;

public class FacultyActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_faculty);
		List<Subject> subjects = getIntent().getParcelableArrayListExtra("subjects");
		String title = (String) getIntent().getCharSequenceExtra("faculty");
		title = getString(R.string.label_faculty) + " " + title.charAt(0)
				+ title.substring(1).toLowerCase(Locale.getDefault());
		setTitle(title);
		final ArrayAdapter<Subject> adapter = new ArrayAdapter<Subject>(this,
				android.R.layout.simple_list_item_1, subjects);
		setListAdapter(adapter);

	}

	@Override
	protected void onListItemClick(android.widget.ListView l, android.view.View v,
			int position, long id) {
		Subject selectedSubject = (Subject) getListAdapter().getItem(position);
		startActivity(getCompanyiesActivityIntentBySubject(selectedSubject, this));

	}

	public static Intent getCompanyiesActivityIntentBySubject(Subject subject,
			Context context) {
		Intent intent = new Intent(context, CompaniesActivity.class);

		MySQLiteHelper mysqlHelper = new MySQLiteHelper(context);
		ArrayList<Company> companies = (ArrayList<Company>) CompanyHelper
				.getAllCompaniesBySubject(mysqlHelper.getReadableDatabase(), subject);
		intent.putParcelableArrayListExtra(CompaniesActivity.COMPANIES, companies);

		intent.putExtra(CompaniesActivity.TITLE, subject.getName());
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mysqlHelper.close();
		return intent;

	}

}
