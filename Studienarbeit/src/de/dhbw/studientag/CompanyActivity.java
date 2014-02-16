package de.dhbw.studientag;

import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Subject;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CompanyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_company);
		Company company = (Company) getIntent().getParcelableExtra("company");
		TextView companyName = (TextView) findViewById(R.id.textView_companyName);
		TextView companyWWW  = (TextView) findViewById(R.id.textView_companyWWW);
		TextView companyCity = (TextView) findViewById(R.id.textView_companyCity);
		companyName.setText(company.getName());
		companyWWW.setText(company.getWebiste());
		companyCity.setText(company.getCity());
		final ListView offeredSubjects = (ListView) findViewById(R.id.listView_companyOfferedSubjects);
		final ArrayAdapter<Subject> adapter = new ArrayAdapter<Subject>(this,android.R.layout.simple_list_item_1, 
				company.getSubjectList());
		offeredSubjects.setAdapter(adapter);
		offeredSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Subject subject = (Subject) offeredSubjects.getAdapter().getItem(position);
				FacultyActivity.startCompaniesActivityWithSubject(subject, getApplicationContext());
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.company, menu);
		return true;
		
	}

}
