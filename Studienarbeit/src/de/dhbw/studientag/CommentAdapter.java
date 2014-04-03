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
		ImageButton imageButton;
		TextView tvCompanyName;
		TextView tvCommentMessage;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.two_line_list_item_with_bin, parent, false);
			tvCompanyName = (TextView) convertView.findViewById(R.id.binItemFirstLine);
			tvCommentMessage = (TextView) convertView
					.findViewById(R.id.bintemSecondLine);
			imageButton = (ImageButton) convertView.findViewById(R.id.binItemDelete);
			//setTags on the first list item, because findViewById costs much
			convertView.setTag(R.id.binItemFirstLine, tvCompanyName);
			convertView.setTag(R.id.bintemSecondLine, tvCommentMessage);
			convertView.setTag(R.id.binItemDelete, imageButton);
			
		}else{
			tvCompanyName = (TextView) convertView.getTag(R.id.binItemFirstLine);
			tvCommentMessage = (TextView) convertView.getTag(R.id.bintemSecondLine);
			imageButton = (ImageButton) convertView.getTag(R.id.binItemDelete);
		}
		tvCompanyName.setText(mComments.get(position).getCompany().getName());	
		tvCommentMessage.setText(mComments.get(position).getShortMessage());
		imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
					mBinClicked.binClicked(position);
			}
		});
		return convertView;
	}
	
	 public void remove(int position) {
         mComments.remove(position);
         notifyDataSetChanged();
     }
	 
     public void insert(int position, Comment comment) {
         mComments.add(position, comment);
         notifyDataSetChanged();
     }

	public void setOnBinClickListener(OnBinClicked binClicked) {
		this.mBinClicked = binClicked;
	}

}
