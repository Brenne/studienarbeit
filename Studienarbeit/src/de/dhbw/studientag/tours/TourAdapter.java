package de.dhbw.studientag.tours;

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
import de.dhbw.studientag.OnBinClicked;
import de.dhbw.studientag.R;
import de.dhbw.studientag.model.Tour;

public class TourAdapter extends ArrayAdapter<Tour> {

	private Context context;
	private List<Tour> tourList = new ArrayList<Tour>();
	private OnBinClicked mBinClicked;

	public TourAdapter(Context context, List<Tour> tourList) {
		super(context, R.layout.two_line_list_item_with_bin, tourList);
		this.context = context;
		this.tourList=tourList;
		
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.two_line_list_item_with_bin, parent, false);
		TextView tourName = (TextView) rowView.findViewById(R.id.binItemFirstLine);
		int tourStations = tourList.get(position).getTourPointList().size();
		TextView tourCountStations = (TextView) rowView
				.findViewById(R.id.bintemSecondLine);
		tourName.setText(tourList.get(position).getName());
		tourCountStations.setText(getContext().getString(R.string.label_stations)+" "+Integer.toString(tourStations));

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

	public void remove(int position) {
		tourList.remove(position);
		notifyDataSetChanged();		
	}

	public void insert(int position, Tour tour) {
		tourList.add(position, tour);
		notifyDataSetChanged();
		
	}
}
