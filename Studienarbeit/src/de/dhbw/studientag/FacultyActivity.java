package de.dhbw.studientag;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ArrayAdapter;
import de.dhbw.studientag.dbHelpers.CompanyHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.model.Subject;

public class FacultyActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_faculty);
		List<Subject> subjects = getIntent().getParcelableArrayListExtra("subjects");
		String title =(String) getIntent().getCharSequenceExtra("faculty");
		title = getString(R.string.label_faculty)+ " "+ title.charAt(0) + title.substring(1).toLowerCase(Locale.getDefault());
		setTitle(title);
		final ArrayAdapter<Subject> adapter = new ArrayAdapter<Subject>(this, android.R.layout.simple_list_item_1, 
				subjects);
		setListAdapter(adapter);
		
	}
	
	@Override
	protected void onListItemClick(android.widget.ListView l, android.view.View v, int position, long id) {
		Subject selectedSubject = (Subject) getListAdapter().getItem(position);
		startCompaniesActivityWithSubject(selectedSubject, this);

	}
	
	public static void startCompaniesActivityWithSubject(Subject subject, Context context){
		Intent intent = new Intent(context, CompaniesActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		MySQLiteHelper mysqlHelper = new MySQLiteHelper(context);
		ArrayList<? extends Parcelable> companies = (ArrayList<? extends Parcelable>) 
				CompanyHelper.getAllCompaniesBySubject(mysqlHelper.getReadableDatabase(), subject);
		intent.putParcelableArrayListExtra("companies", companies);
		
		intent.putExtra("title", subject.getName());
		mysqlHelper.close();
		context.startActivity(intent);
	}



}
