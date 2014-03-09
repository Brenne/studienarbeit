package de.dhbw.studientag;

import java.util.List;
import java.util.Map;

import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.dbHelpers.TourHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MyDialogAdapter extends SimpleAdapter {

	private final Context context;
	private final List<? extends Map<String, ?>> data;

	public MyDialogAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from) {
		super(context, data, resource, from, new int[] { R.id.dialogListTourName });
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.dialog_list, parent, false);
		TextView tourName = (TextView) row.findViewById(R.id.dialogListTourName);
		tourName.setText((CharSequence) data.get(position).get(
				ToursListFragment.TOUR_NAME));
		ImageButton delete = (ImageButton) row.findViewById(R.id.dialogListDelete);
		if (data.get(position).get(SelectTourDialogFragment.COMPANY_IN_TOUR) != null
				&& (Boolean) data.get(position).get(
						SelectTourDialogFragment.COMPANY_IN_TOUR)) {
			delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					MySQLiteHelper dbHelper = new MySQLiteHelper(v.getContext());
					TourHelper.deleteTourPointById(dbHelper.getWritableDatabase(),
							(Long) data.get(position)
									.get(TourPointFragment.TOUR_POINT_ID));
					dbHelper.close();
					((ViewManager)v.getParent()).removeView(v);

				}
			});

		} else {
			((ViewManager) delete.getParent()).removeView(delete);
		}
		return row;
	}

}
