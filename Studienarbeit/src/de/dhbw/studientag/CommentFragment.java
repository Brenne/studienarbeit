package de.dhbw.studientag;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import de.dhbw.studientag.dbHelpers.CommentHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.model.Comment;

public class CommentFragment extends Fragment {

	protected static final String COMMENT = "comment";
	private static final String TAG="CommentFragment";
	private Comment mComment;
	private EditText editTextComment;
	private OnRemoveFragmentListener mRemoveFragmentListener;

	public CommentFragment() {

	}

	public static CommentFragment getCommentFragment(Comment comment) {
		CommentFragment fragment = new CommentFragment();
		Bundle args = new Bundle();
		args.putParcelable(COMMENT, comment);
		fragment.setArguments(args);
		return fragment;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if(getArguments().containsKey(COMMENT)){
			mComment = getArguments().getParcelable(COMMENT);
		}else{
			Log.e("CommentFragment", "startComment Fragment without comment parcelable");
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mRemoveFragmentListener = (OnRemoveFragmentListener) activity;
		} catch (ClassCastException castExeption) {
			Log.e(TAG, "activity did not ipmlement OnRemoveFragmentListener", castExeption);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_comment, container, false);
		TextView companyName = (TextView) view
				.findViewById(R.id.textView_commentCompanyName);
		companyName.setText(mComment.getCompany().getName());
		editTextComment = (EditText) view.findViewById(R.id.editText_comment);
		editTextComment.setText(mComment.getFullMessage());
		InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		editTextComment.setSelection(editTextComment.getText().length());
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.comment, menu);
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case R.id.menu_save_comment:
			mRemoveFragmentListener.removeFragment();

			return true;
		case R.id.menu_delete_comment:
			editTextComment.setText("");
			mRemoveFragmentListener.removeFragment();

			return true;
		default:
			return super.onOptionsItemSelected(item);

		}

	}

	public void saveComment() {
		Editable message = editTextComment.getText();
		Log.d("Save Comment", message.toString());
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity());
		if (message.length() > 0)
			CommentHelper.insertComment(mComment.getCompany().getId(),
					message.toString(), dbHelper.getWritableDatabase());
		else
			CommentHelper.deleteComment(mComment.getCompany().getId(),
					dbHelper.getWritableDatabase());
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editTextComment.getWindowToken(), 0);
		dbHelper.close();
	}

	@Override
	public void onPause() {

		saveComment();
		super.onPause();
	}
	
	public interface OnRemoveFragmentListener{
		public void removeFragment();
	}

}
