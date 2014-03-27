package de.dhbw.studientag;

import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import de.dhbw.studientag.dbHelpers.CommentHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.model.Comment;
import de.dhbw.studientag.model.Company;

public class CommentsFragment extends ListFragment implements OnBinClicked {

	private final static String TAG = "CommentsFragment";
	private List<Comment> mComments;
	private OnCommentAddListener mCommentAddListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	private void setListAdapter() {
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
		mComments = CommentHelper.getAllComments(dbHelper.getReadableDatabase());
		CommentAdapter adapter = new CommentAdapter(getActivity(), mComments);
		setListAdapter(adapter);
		adapter.setOnBinClickListener(this);
		dbHelper.close();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_comments, container, false);

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.comments, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case R.id.menu_item_add_comment:
			mCommentAddListener.addComment();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Comment selectedComment = (Comment) getListAdapter().getItem(position);
		Company selectedCompany = (Company) selectedComment.getCompany();
		Intent intent = new Intent(getActivity(), CommentActivity.class);
		intent.putExtra(CompanyActivity.COMPANY, selectedCompany);
		startActivity(intent);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCommentAddListener = (OnCommentAddListener) activity;

		} catch (ClassCastException catExeption) {
			Log.e(TAG, "activity did not ipmlement OnCommentAddListener", catExeption);
		}
	}
	
	@Override
	public void onResume() {
		setListAdapter();
		super.onResume();
	}

	@Override
	public void binClicked(int position) {
		Comment comment = mComments.get(position);
		if (comment != null) {
			MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
			CommentHelper.deleteComment(comment.getCompany().getId(),
					dbHelper.getReadableDatabase());
			dbHelper.close();
			setListAdapter();
		}

	}

	public interface OnCommentAddListener {
		public void addComment();
	}

}
