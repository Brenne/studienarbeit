package de.dhbw.studientag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import de.dhbw.studientag.dbHelpers.CompanyHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.model.Comment;
import de.dhbw.studientag.model.Company;

public class CommentsActivity extends Activity implements
		CompaniesFragment.OnCompanySelectedLitener, CommentsFragment.CommentsFragmentListeners,
		CommentFragment.OnRemoveFragmentListener {

	protected static final String COMMENT_INFO = "showCommentInfo";
	private static final String TAG_COMMENT_FRAGMENT = "CommentFragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comments);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		Fragment commentsFragment;
		if (getIntent().hasExtra(CommentFragment.COMMENT)) {
			Comment comment = getIntent().getParcelableExtra(CommentFragment.COMMENT);
			commentsFragment = CommentFragment.getCommentFragment(comment);
		} else {
			commentsFragment = new CommentsFragment();
		}
		transaction.replace(R.id.comments_fragment_container, commentsFragment).commit();

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public void onCompanySelected(Company company) {
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.popBackStack();
		Comment comment = new Comment(company);
		CommentFragment commentFragment = CommentFragment.getCommentFragment(comment);
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.addToBackStack(null);
		transaction
				.replace(R.id.comments_fragment_container, commentFragment, TAG_COMMENT_FRAGMENT)
				.commit();

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		SharedPreferences prefs = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		// do not show info text in future
		editor.putBoolean(COMMENT_INFO, false);
		editor.apply();

	}

	@Override
	public void addComment(List<Comment> existingComments) {
		MySQLiteHelper dbHelper = new MySQLiteHelper(this);
		ArrayList<Company> allCompanies = (ArrayList<Company>) CompanyHelper
				.getAllCompanies(dbHelper.getReadableDatabase());
		dbHelper.close();
		Iterator<Company> iterator = allCompanies.iterator();
		/*
		 * a new comment can only by added for companies which are not linked
		 * with a comment yet. Therefore remove all companies with existing
		 * comments
		 */
		while (iterator.hasNext()) {
			Company company = iterator.next();
			for (Comment comment : existingComments) {
				if (comment.getCompany().getId() == company.getId()) {
					iterator.remove();
				}
			}
		}
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		CompaniesFragment companiesFragment = CompaniesFragment
				.newCompaniesFragmentInstance(allCompanies);
		transaction.addToBackStack(null);
		transaction.replace(R.id.comments_fragment_container, companiesFragment,
				CompanyActivity.COMPANY).commit();
		setTitle(R.string.title_new_comment);

	}

	@Override
	public void removeFragment() {
		Fragment commentFragment = getFragmentManager().findFragmentByTag(TAG_COMMENT_FRAGMENT);
		if (commentFragment != null) {
			getFragmentManager().beginTransaction().remove(commentFragment).commit();
		} else {
			finish();
		}
		getFragmentManager().popBackStack();

	}

	@Override
	public void commentSelected(Comment comment) {

		CommentFragment commentFragment = CommentFragment.getCommentFragment(comment);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.addToBackStack(null);
		transaction
				.replace(R.id.comments_fragment_container, commentFragment, TAG_COMMENT_FRAGMENT)
				.commit();

	}

}
