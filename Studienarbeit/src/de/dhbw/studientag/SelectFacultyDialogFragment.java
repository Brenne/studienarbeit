package de.dhbw.studientag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.dbHelpers.SubjectsHelper;
import de.dhbw.studientag.model.Faculty;

public class SelectFacultyDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		Faculty[] faculties = Faculty.class.getEnumConstants();
		List<Faculty> faclutyList = new ArrayList<Faculty>(Arrays.asList(faculties));
		FacultyAdapter facultyAdapter = new FacultyAdapter(getActivity(), faclutyList);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle(
				R.string.label_select_faculty).setAdapter(facultyAdapter, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				MySQLiteHelper mySQLiteHelper = new MySQLiteHelper(getActivity());
				Intent intent = new Intent(getActivity().getBaseContext(), FacultyActivity.class);
				Faculty faculty = Faculty.getById(++which);
				intent.putExtra(FacultyActivity.FACULTY, faculty.name());
				intent.putParcelableArrayListExtra(FacultyActivity.SUBJECTS,
						(ArrayList<? extends Parcelable>) SubjectsHelper.getSubjectsByFaculty(
								mySQLiteHelper.getReadableDatabase(), faculty));
				mySQLiteHelper.close();
				startActivity(intent);

			}
		});

		return builder.create();

	}

}
