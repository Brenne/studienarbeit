package de.dhbw.studientag;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.dhbw.studientag.dbHelpers.CommentHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Subject;

public class CompanyActivity extends Activity {

	private Company company;
	private MySQLiteHelper dbHelper = new MySQLiteHelper(this);
	protected static final String COMPANY ="company";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_company);
		setTitle(getString(R.string.label_companyProfile));
		company = (Company) getIntent().getParcelableExtra(COMPANY);

		TextView companyName = (TextView) findViewById(R.id.textView_companyName);
		TextView companyWWW  = (TextView) findViewById(R.id.textView_companyWWW);
		TextView companyCity = (TextView) findViewById(R.id.textView_companyCity);
		TextView companyRoom = (TextView) findViewById(R.id.textView_companyRoom);
		TextView companyBld  = (TextView) findViewById(R.id.textView_companyBuilding);
		
		companyName.setText(company.getName());
		companyWWW.setText(company.getWebiste());
		companyWWW.setMovementMethod(LinkMovementMethod.getInstance());
		companyCity.setText(company.getPlz() + " " +company.getCity());
		dbHelper.close();
		companyRoom.setText(company.getLocation().getRoom().getRoomNo());
		companyBld.setText(company.getLocation().getBuilding().getFullName());
		final ListView offeredSubjects = (ListView) findViewById(R.id.listView_companyOfferedSubjects);
		final ArrayAdapter<Subject> adapter = new ArrayAdapter<Subject>(this,android.R.layout.simple_list_item_1, 
				company.getSubjectList());
		offeredSubjects.setAdapter(adapter);
		offeredSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Subject subject = (Subject) offeredSubjects.getAdapter().getItem(position);
				startActivity(FacultyActivity.getCompanyiesActivityIntentBySubject(subject, getApplicationContext()));
			}
			
		});
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.company, menu);
		return true;
		
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem comment = menu.findItem(R.id.action_comment);
		
		if(CommentHelper.commentsForCompanyExist(company.getId(), dbHelper.getReadableDatabase() ))
			comment.setIcon(android.R.drawable.ic_input_get);
		dbHelper.close();
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    int itemId = item.getItemId();
		if (itemId == R.id.action_comment) {
			Intent intent = new Intent(this, CommentActivity.class);
			intent.putExtra("company", company);
			startActivity(intent);
			return true;
		} else if (itemId == R.id.action_addToTour) {
			DialogFragment toursDialog = SelectTourDialogFragment.newInstance(company);
			toursDialog.show(getFragmentManager(),"tours");
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onResume() {
		invalidateOptionsMenu();
		super.onResume();
	}
	


}
