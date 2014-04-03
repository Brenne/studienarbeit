package de.dhbw.studientag;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
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
		View rowView = LayoutInflater.from(mContext).inflate(R.layout.subject_list_item, parent, false);
		
		TextView subjectName = (TextView) rowView.findViewById(R.id.subjectName);
		subjectName.setText(getItem(position).getName());
		ImageButton infoButton = (ImageButton) rowView.findViewById(R.id.subjectInfoLink);
		final String webAddress = getItem(position).getWebAddress();
		if(webAddress != null && !webAddress.isEmpty()){
			
			infoButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webAddress));
					mContext.startActivity(browserIntent);
					
				}
			});
		}else{
			//no info icon if webaddress is empty or null
			((ViewManager) infoButton.getParent()).removeView(infoButton);
		}
		return rowView;
	}

}
