package de.dhbw.studientag;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import de.dhbw.studientag.model.Comment;

public class CommentAdapter extends ArrayAdapter<Comment> {

	private OnBinClicked mBinClicked;
	private final Context context;
	private final List<Comment> mComments;
	
	public CommentAdapter(Context context, List<Comment> comments) {
		super(context, R.layout.two_line_list_item_with_bin, comments);
		this.context = context;
		this.mComments = comments;
		
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.two_line_list_item_with_bin, parent, false);
		TextView tvCompanyName = (TextView) rowView.findViewById(R.id.binItemFirstLine);
		TextView tvCommentMessage = (TextView) rowView
				.findViewById(R.id.bintemSecondLine);
		tvCompanyName.setText(mComments.get(position).getCompany().getName());	
		tvCommentMessage.setText(mComments.get(position).getShortMessage());
		
		ImageButton imageButton = (ImageButton) rowView.findViewById(R.id.binItemDelete);
		imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
					mBinClicked.binClicked(position);
			}
		});
		return rowView;
	}

	public void setOnBinClickListener(OnBinClicked binClicked) {
		this.mBinClicked = binClicked;
	}

}
