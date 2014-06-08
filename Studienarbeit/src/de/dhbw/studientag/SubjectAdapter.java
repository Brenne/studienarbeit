package de.dhbw.studientag;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import de.dhbw.studientag.model.Subject;

public class SubjectAdapter extends BaseAdapter {
	
	private final List<Subject> mSubjects;
	private final Context mContext;
	
	public SubjectAdapter(Context context, List<Subject> subjects){
		this.mContext = context;
		this.mSubjects= subjects;
	}

	@Override
	public int getCount() {
		return mSubjects.size();
	}

	@Override
	public Subject getItem(int position) {
		return mSubjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		// getItemId is not used
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView subjectName;
		ImageButton infoButton;
		ImageView subjectColor;
		//getView is called for every list item and only null the first time
		if(convertView ==null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.subject_list_item, parent, false);
			subjectName = (TextView) convertView.findViewById(R.id.subjectName);
			infoButton = (ImageButton) convertView.findViewById(R.id.subjectInfoLink);
			subjectColor = (ImageView) convertView.findViewById(R.id.subjectColorCircle);
			/* setTags at the first time getView is called (first list item),
			 * because findViewById costs much and therefore
			 * can be avoided on future iterations
			 */
			convertView.setTag(R.id.subjectName, subjectName);
			convertView.setTag(R.id.subjectInfoLink, infoButton);
			convertView.setTag(R.id.subjectColorCircle, subjectColor);
		}else{
			subjectName = (TextView) convertView.getTag(R.id.subjectName);
			infoButton = (ImageButton) convertView.getTag(R.id.subjectInfoLink);
			subjectColor = (ImageView) convertView.getTag(R.id.subjectColorCircle);
		}
		Subject currentSubject = getItem(position);
		subjectName.setText(currentSubject.getName());
		int color = currentSubject.getColor().getColor();
		if(color != android.graphics.Color.TRANSPARENT){
			subjectColor.setColorFilter(color,Mode.MULTIPLY);
		}else{
			//if subjectColor is transparent hide color circle
			 subjectColor.setVisibility(View.INVISIBLE);
		}
			
		final String webAddress = currentSubject.getWebAddress();
		if(webAddress != null && !webAddress.isEmpty()){
			infoButton.setOnClickListener(new OnClickListener() {		
				@Override
				public void onClick(View v) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webAddress));
					mContext.startActivity(browserIntent);		
				}
			});
		}else{
			//no info icon if web address (of subject) is empty or null
			infoButton.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

}
