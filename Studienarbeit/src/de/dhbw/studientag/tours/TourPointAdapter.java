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
		super(context, R.layout.two_line_list_item_with_bin, tour
				.getTourPointList());
		this.context = context;
		this.mTour = tour;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		TextView tvCompanyName;
		TextView tvCompanyLocation;
		ImageButton binButton;
		//getView is called for every list item and only null the first time
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.two_line_list_item_with_bin, parent, false);
			tvCompanyName = (TextView) convertView
					.findViewById(R.id.binItemFirstLine);
			tvCompanyLocation = (TextView) convertView
					.findViewById(R.id.bintemSecondLine);
			binButton = (ImageButton) convertView
					.findViewById(R.id.binItemDelete);
			/* setTags at the first time getView is called (first list item),
			 * because findViewById costs much and therefore
			 * can be avoided on future iterations
			 */
			convertView.setTag(R.id.binItemFirstLine, tvCompanyName);
			convertView.setTag(R.id.bintemSecondLine, tvCompanyLocation);
			convertView.setTag(R.id.binItemDelete, binButton);

		} else {
			//from the second time on when getView is called restore the saved objects by getTag(key)
			tvCompanyName = (TextView) convertView
					.getTag(R.id.binItemFirstLine);
			tvCompanyLocation = (TextView) convertView
					.getTag(R.id.bintemSecondLine);
			binButton = (ImageButton) convertView.getTag(R.id.binItemDelete);
		}
		TourPoint tourPoint = mTour.getTourPointList().get(position);

		tvCompanyName.setText((CharSequence) tourPoint.getCompany().getName());
		String tourPointBuilding = tourPoint.getCompany().getLocation()
				.getBuilding().getFullName();
		String tourPointRoom = tourPoint.getCompany().getLocation().getRoom()
				.getRoomNo();
		tvCompanyLocation.setText(tourPointBuilding + ", "
				+ getContext().getString(R.string.room) + " " + tourPointRoom);

		binButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mBinClicked.binClicked(position);
			}
		});
		return convertView;
	}

	public void setOnBinClickListener(OnBinClicked binClicked) {
		this.mBinClicked = binClicked;
	}

}
