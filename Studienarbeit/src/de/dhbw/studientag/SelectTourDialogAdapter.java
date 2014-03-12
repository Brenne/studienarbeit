package de.dhbw.studientag;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import de.dhbw.studientag.model.Tour;

public class SelectTourDialogAdapter extends ArrayAdapter<Tour> {

	private final Context context;
	private final List<Pair<Boolean,Tour>> tourList;
	private OnBinClicked mBinClicked;

	public SelectTourDialogAdapter(Context context,List<Pair<Boolean,Tour>> tourList) {
		super(context,  R.layout.dialog_list, extractTour(tourList) );
		this.context = context;
		this.tourList = tourList;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.dialog_list, parent, false);
		TextView tourName = (TextView) row.findViewById(R.id.dialogListTourName);
		tourName.setText((CharSequence) tourList.get(position).second.getName());
		ImageButton delete = (ImageButton) row.findViewById(R.id.dialogListDelete);
		if (tourList.get(position).first) {
			delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mBinClicked.binClicked(position);
					((ViewManager)v.getParent()).removeView(v);

				}
			});

		} else {
			((ViewManager) delete.getParent()).removeView(delete);
		}
		return row;
	}
	
	public void setOnBinClickListener(OnBinClicked binClicked) {
		this.mBinClicked = binClicked;
	}
	
	private static List<Tour> extractTour(List<Pair<Boolean,Tour>> pTourList){
		List<Tour> tourList = new ArrayList<Tour>();
		for(Pair<Boolean,Tour> pair : pTourList){
			tourList.add(pair.second);
		}
		return tourList;
	}

}
