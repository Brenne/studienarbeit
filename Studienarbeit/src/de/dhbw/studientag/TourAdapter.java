package de.dhbw.studientag;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import de.dhbw.studientag.model.Tour;

public class TourAdapter extends ArrayAdapter<Tour> {

	private Context context;
	private List<Tour> tourList = new ArrayList<Tour>();
	private OnBinClicked mBinClicked;

	public TourAdapter(Context context, List<Tour> tourList) {
		super(context, R.layout.tour_list_item, tourList);
		this.context = context;
		this.tourList=tourList;
		
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.tour_list_item, parent, false);
		TextView tourName = (TextView) rowView.findViewById(R.id.tourItemFirstLine);
		int tourStations = tourList.get(position).getTourPointList().size();
		TextView tourCountStations = (TextView) rowView
				.findViewById(R.id.tourItemSecondLine);
		tourName.setText(tourList.get(position).getName());
		tourCountStations.setText(Integer.toString(tourStations));

		ImageButton imageButton = (ImageButton) rowView.findViewById(R.id.tourItemDelete);
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
