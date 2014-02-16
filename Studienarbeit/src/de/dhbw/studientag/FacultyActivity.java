package de.dhbw.studientag;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.widget.ArrayAdapter;
import de.dhbw.studientag.model.Subject;
import de.dhbw.studientag.model.db.CompanyHelper;
import de.dhbw.studientag.model.db.MySQLiteHelper;

public class FacultyActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_faculty);
		List<Subject> subjects = getIntent().getParcelableArrayListExtra("subjects");
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.faculty, menu);
		return true;
	}

}
