package de.dhbw.studientag;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import de.dhbw.studientag.model.Company;

public class CommentsActivity extends Activity implements
		CompaniesFragment.OnCompanySelectedLitener, CommentsFragment.OnCommentAddListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comments);
		Fragment commentsFragment = new CommentsFragment();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.comments_fragment_container, commentsFragment).commit();
		// Show the Up button in the action bar.
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
		CompaniesFragment companiesFragment =new CompaniesFragment();
		transaction.addToBackStack(null);
		transaction.replace(R.id.comments_fragment_container, companiesFragment,
				CompanyActivity.COMPANY).commit();
		setTitle(R.string.title_new_comment);
		

	}

}
