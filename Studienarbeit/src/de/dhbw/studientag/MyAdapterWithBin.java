package de.dhbw.studientag;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MyAdapterWithBin extends SimpleAdapter {

	private OnBinClicked mBinClicked;
	private final Context context;
	private final List<? extends Map<String, ?>> data;
	private final String[] from;

	public MyAdapterWithBin(Context context, List<? extends Map<String, ?>> data,
			String[] from) {
		super(context, data, R.layout.tour_list_item, from, new int[] {
				R.id.tourItemFirstLine, R.id.tourItemSecondLine });
		this.context = context;
		this.data = data;
		this.from = from;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.tour_list_item, parent, false);
		TextView tvCompanyName = (TextView) rowView.findViewById(R.id.tourItemFirstLine);
		TextView tvCompanLocation = (TextView) rowView
				.findViewById(R.id.tourItemSecondLine);
		tvCompanyName.setText((CharSequence) data.get(position).get(from[0]));
		tvCompanLocation.setText((CharSequence) data.get(position).get(from[1]));

		ImageButton imageButton = (ImageButton) rowView.findViewById(R.id.tourItemDelete);
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			
					mBinClicked.binClicked(position);
			}
		});
		return rowView;
	}

	public interface OnBinClicked {
		public void binClicked(int tourId);
	}

	public void setOnBinClickListener(OnBinClicked binClicked) {
		this.mBinClicked = binClicked;
	}

}
