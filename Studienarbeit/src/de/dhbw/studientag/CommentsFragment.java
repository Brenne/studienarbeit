package de.dhbw.studientag;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import de.dhbw.studientag.dbHelpers.CommentHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.model.Comment;
import de.timroes.android.listview.EnhancedListView;
import de.timroes.android.listview.EnhancedListView.Undoable;

public class CommentsFragment extends Fragment implements OnBinClicked {

	private final static String TAG = "CommentsFragment";
	private EnhancedListView mEnhancedListView;
	private CommentsFragmentListeners mCommentsListener;
	private boolean mShowCommentsInfo;
	private TextView mCommentInfoText;
	private CommentAdapter mCommentAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		SharedPreferences prefs = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);	
		mShowCommentsInfo = prefs.getBoolean(CommentsActivity.COMMENT_INFO, true);
		
		super.onCreate(savedInstanceState);
	}

	private void setListAdapter() {	
		final CommentAdapter adapter =  new CommentAdapter(getActivity(), getCommentListFromDB());
		mCommentAdapter = adapter;
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
	                    
	                    @Override
	                    public void discard() {
	                		
	                		if (comment != null) {
	                			MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
	                			CommentHelper.deleteComment(comment.getCompany().getId(),
	                					dbHelper.getReadableDatabase());
	                			dbHelper.close();
	                				
	                		}
	                    }
	                    
	                    @Override
	                    public String getTitle() {	
	                    	return getString(R.string.label_comment_removed);
	                    }
	                } ;   
			}
		});
		mEnhancedListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Comment selectedComment = (Comment) adapter.getItem(position);
				mCommentsListener.commentSelected(selectedComment);

			}
	
		});
		adapter.setOnBinClickListener(this);
		
	}
	
	private List<Comment> getCommentListFromDB(){
		final MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
		List<Comment> comments = CommentHelper.getAllComments(dbHelper.getReadableDatabase());
		dbHelper.close();
		return comments;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_comments, container, false);
		mEnhancedListView = (EnhancedListView) view.findViewById(R.id.list);
		mCommentInfoText = (TextView) view.findViewById(R.id.comments_info);
		setListAdapter();		
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.comments, menu);
		if(mShowCommentsInfo || mCommentAdapter.isEmpty()){
			menu.findItem(R.id.menu_item_info_comments).setVisible(false);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case R.id.menu_item_add_comment:
			mCommentsListener.addComment(mCommentAdapter.getCommentList());
			return true;
		case R.id.menu_item_info_comments:

			AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ; 
			mCommentInfoText.setVisibility(View.VISIBLE);
			mCommentInfoText.startAnimation(fadeIn);	
			fadeIn.setDuration(1200);
			fadeIn.setFillAfter(true);
			item.setVisible(false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}



	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCommentsListener = (CommentsFragmentListeners) activity;
		} catch (ClassCastException castExeption) {
			Log.e(TAG, "activity did not ipmlement OnCommentAddListener", castExeption);
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		getActivity().setTitle(R.string.title_activity_comments);
		if(!mShowCommentsInfo && !mCommentAdapter.isEmpty()){
			mCommentInfoText.setVisibility(View.GONE);
		}else{
			mCommentInfoText.setVisibility(View.VISIBLE);
		}
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onStop() {
		mEnhancedListView.discardUndo();
		super.onStop();
	}

	@Override
	public void binClicked(int position) {
		mEnhancedListView.delete(position);
	}

	public interface CommentsFragmentListeners {
		public void addComment(List<Comment> existingComments);
		public void commentSelected(Comment comment);
	}

}
