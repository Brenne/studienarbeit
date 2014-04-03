package de.dhbw.studientag;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import de.dhbw.studientag.dbHelpers.CommentHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.model.Comment;
import de.dhbw.studientag.model.Company;
import de.timroes.android.listview.EnhancedListView;
import de.timroes.android.listview.EnhancedListView.Undoable;

public class CommentsFragment extends Fragment implements OnBinClicked {

	private final static String TAG = "CommentsFragment";
	private List<Comment> mComments;
	private EnhancedListView mEnhancedListView;
	private OnCommentAddListener mCommentAddListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	private void setListAdapter() {
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
		mComments = CommentHelper.getAllComments(dbHelper.getReadableDatabase());
		final CommentAdapter adapter = new CommentAdapter(getActivity(), mComments);
		mEnhancedListView.setAdapter(adapter);
		mEnhancedListView.setDismissCallback(new EnhancedListView.OnDismissCallback() {
			
			
			
			@Override
			public Undoable onDismiss(EnhancedListView listView, final int position) {
			     final Comment comment = (Comment) adapter.getItem(position);
	                adapter.remove(position);
	                return new EnhancedListView.Undoable() {
	                    @Override
	                    public void undo() {
	                        adapter.insert(position, comment);
	                    }
	                } ;   
			}
		});
		mEnhancedListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Comment selectedComment = (Comment) adapter.getItem(position);
				Company selectedCompany = (Company) selectedComment.getCompany();
				Intent intent = new Intent(getActivity(), CommentActivity.class);
				intent.putExtra(CompanyActivity.COMPANY, selectedCompany);
				startActivity(intent);
				
			}
			
		
			
		});
		adapter.setOnBinClickListener(this);
		dbHelper.close();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_comments, container, false);
		view.findViewById(R.id.comments_info).setOnTouchListener(new OnSwipeTouchListener(getActivity()){
			  public void onSwipeTop() {
			        Toast.makeText(getActivity(), "top", Toast.LENGTH_SHORT).show();
			    }
			    public void onSwipeRight() {
			        Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();
			    }
			    public void onSwipeLeft() {
			        Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();
			    }
			    public void onSwipeBottom() {
			        Toast.makeText(getActivity(), "bottom", Toast.LENGTH_SHORT).show();
			    }

		});
		
		mEnhancedListView = (EnhancedListView) view.findViewById(R.id.list);
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
		getActivity().setTitle(R.string.title_activity_comments);
		super.onResume();
	}

	@Override
	public void binClicked(int position) {
		Comment comment = mComments.get(position);
		mEnhancedListView.delete(position);

//		if (comment != null) {
//			MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
//			CommentHelper.deleteComment(comment.getCompany().getId(),
//					dbHelper.getReadableDatabase());
//			dbHelper.close();
//			setListAdapter();
//		}
	
	}

	public interface OnCommentAddListener {
		public void addComment();
	}

}
