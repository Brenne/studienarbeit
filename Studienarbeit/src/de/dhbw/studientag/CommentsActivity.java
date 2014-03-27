package de.dhbw.studientag;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import de.dhbw.studientag.dbHelpers.CompanyHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.model.Company;

public class CommentsActivity extends Activity implements
		CompaniesFragment.OnCompanySelectedLitener, CommentsFragment.OnCommentAddListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comments);
		// Show the Up button in the action bar.
		setupActionBar();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		CommentsFragment commentsFragment = new CommentsFragment();
		transaction.add(R.id.comments_fragment_container, commentsFragment).commit();

	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public void onCompanySelected(Company company) {
		getFragmentManager().popBackStack();

		Intent intent = new Intent(this, CommentActivity.class);
		intent.putExtra(CompanyActivity.COMPANY, company);
		startActivity(intent);

	}

	@Override
	public void addComment() {

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		SQLiteDatabase db = new MySQLiteHelper(this).getReadableDatabase();
		ArrayList<Company> companyList = (ArrayList<Company>) CompanyHelper
				.getAllCompanies(db);
		db.close();
		CompaniesFragment companiesFragment = CompaniesFragment
				.newCompaniesFragmentInstance(companyList);
		transaction.addToBackStack(null);
		transaction.replace(R.id.comments_fragment_container, companiesFragment,
				CompanyActivity.COMPANY).commit();

	}

}
