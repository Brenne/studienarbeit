package de.dhbw.studientag;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.dhbw.studientag.model.Faculty;

public class FacultyAdapter extends ArrayAdapter<Faculty> {

	private final Context context;
	private final List<Faculty> facultyList;

	public FacultyAdapter(Context context, List<Faculty> faculties) {
		super(context, R.layout.image_list_item, faculties);
		this.context = context;
		this.facultyList = faculties;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.image_list_item, parent, false);
		TextView facultyName = (TextView) row.findViewById(R.id.faculty_name);
		int drawable = context.getResources().getIdentifier(
				"ic_dialog_"
						+ facultyList.get(position).name()
								.toLowerCase(Locale.getDefault()), "drawable",
				context.getPackageName());
		ImageView facultyIcon = (ImageView) row.findViewById(R.id.faculty_icon);
		facultyIcon.setImageResource(drawable);
		
		facultyName.setText(FacultyActivity.getFirstLetterCapital(facultyList.get(
				position).name()));
		

		return row;
	}

}
