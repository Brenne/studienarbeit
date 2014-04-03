package de.dhbw.studientag;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import de.dhbw.studientag.dbHelpers.CompanyHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Subject;

public class FacultyActivity extends ListActivity {
	
	protected static final String SUBJECTS = "subjects";
	protected static final String FACULTY  = "facluty";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_faculty);
		List<Subject> subjects = getIntent().getParcelableArrayListExtra(SUBJECTS);
		String title = (String) getIntent().getCharSequenceExtra(FACULTY);
		title = getString(R.string.label_faculty) + " " + getFirstLetterCapital(title);
		setTitle(title);
//		final ArrayAdapter<Subject> adapter = new ArrayAdapter<Subject>(this,
//				R.layout.subject_list_item, R.id.subjectName,subjects);
		final SubjectAdapter adapter = new SubjectAdapter(this, subjects);
		setListAdapter(adapter);

	}

	@Override
	protected void onListItemClick(ListView l, View v,
			int position, long id) {
		Subject selectedSubject = (Subject) getListAdapter().getItem(position);
		startActivity(getCompanyiesActivityIntentBySubject(selectedSubject, this));

	}
	
	public static String getFirstLetterCapital(String string){
		return string.charAt(0)
		+ string.substring(1).toLowerCase(Locale.getDefault());
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
