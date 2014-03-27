package de.dhbw.studientag.tours;

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
import de.dhbw.studientag.model.TourPoint;

public class TourPointAdapter extends ArrayAdapter<TourPoint> {

	private OnBinClicked mBinClicked;
	private final Context context;
	private final Tour mTour;
	
	public TourPointAdapter(Context context, Tour tour) {
		super(context, R.layout.two_line_list_item_with_bin, tour.getTourPointList());
		this.context = context;
		this.mTour = tour;
		
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.two_line_list_item_with_bin, parent, false);
		TextView tvCompanyName = (TextView) rowView.findViewById(R.id.binItemFirstLine);
		TextView tvCompanLocation = (TextView) rowView
				.findViewById(R.id.bintemSecondLine);
		tvCompanyName.setText((CharSequence) mTour.getTourPointList().get(position).getCompany().getName());
		String tourPointBuilding =  mTour.getTourPointList().get(position).getCompany().getLocation().getBuilding().getFullName();
		String tourPointRoom     =  mTour.getTourPointList().get(position).getCompany().getLocation().getRoom().getRoomNo();
		tvCompanLocation.setText(tourPointBuilding+", "+getContext().getString(R.string.room)+" "+tourPointRoom);

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
