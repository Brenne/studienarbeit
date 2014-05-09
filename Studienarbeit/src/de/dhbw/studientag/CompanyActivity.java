package de.dhbw.studientag;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import de.dhbw.studientag.dbHelpers.CommentHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.model.Comment;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Subject;
import de.dhbw.studientag.tours.SelectTourDialogFragment;

public class CompanyActivity extends Activity {

	private Company company;
	private Comment mComment = null;
	public static final String COMPANY = "company";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_company);
		setTitle(getString(R.string.label_companyProfile));
		company = (Company) getIntent().getParcelableExtra(COMPANY);

		TextView companyName = (TextView) findViewById(R.id.textView_companyName);
		TextView companyWWW = (TextView) findViewById(R.id.textView_companyWWW);
		TextView companyCity = (TextView) findViewById(R.id.textView_companyCity);
		TextView companyRoom = (TextView) findViewById(R.id.textView_companyRoom);
		TextView companyBld = (TextView) findViewById(R.id.textView_companyBuilding);

		ImageButton map = (ImageButton) findViewById(R.id.company_imageMap);
		map.setColorFilter(android.graphics.Color.LTGRAY, Mode.MULTIPLY);

		companyName.setText(company.getName());
		companyWWW.setText(company.getWebiste());
		companyWWW.setMovementMethod(LinkMovementMethod.getInstance());
		companyCity.setText(company.getPlz() + " " + company.getCity());
		companyRoom.setText(company.getLocation().getRoom().getRoomNo());
		companyBld.setText(company.getLocation().getBuilding().getFullName());

		final ListView offeredSubjects = (ListView) findViewById(R.id.listView_companyOfferedSubjects);
		final SubjectAdapter adapter = new SubjectAdapter(this, company.getSubjectList());
		offeredSubjects.setAdapter(adapter);
		offeredSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final Subject subject = (Subject) offeredSubjects.getAdapter().getItem(position);
				startActivity(FacultyActivity.getCompanyiesActivityIntentBySubject(subject,
						getApplicationContext()));
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
		MySQLiteHelper dbHelper = new MySQLiteHelper(this);
		if (CommentHelper.commentForCompanyExist(company.getId(), dbHelper.getReadableDatabase())) {
			mComment = CommentHelper.getCommentByIdCompanyId(company.getId(),
					dbHelper.getReadableDatabase());
			comment.setIcon(android.R.drawable.ic_input_get);
		} else {
			mComment = new Comment(company);
		}
		dbHelper.close();
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case R.id.action_comment:
			Intent intent = new Intent(this, CommentsActivity.class);
			intent.putExtra(CommentFragment.COMMENT, mComment);
			startActivity(intent);
			return true;
		case R.id.action_addToTour:
			DialogFragment toursDialog = SelectTourDialogFragment.newInstance(company);
			toursDialog.show(getFragmentManager(), "tours");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// image button next to location information about exhibition venue
	public void goToDhbwLocation(View v) {
		Intent intent = new Intent(this, LocationsActivity.class);
		intent.putExtra(COMPANY, company);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		invalidateOptionsMenu();
		super.onResume();
	}

}
