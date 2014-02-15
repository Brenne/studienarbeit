package de.dhbw.studientag.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SubjectsHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String SUBJECTS_TABLE_NAME ="Subjects";
	private static final String SUBJECT_NAME = "name";
	private static final String FACULTY_ID = "facultyId";
	private static final String SUBJECTS_TABLE_CREATE=
			"CREATE TABLE " + SUBJECTS_TABLE_NAME + " (" +
			SUBJECT_NAME + " TEXT" +
			FACULTY_ID + " INTEGER"  +
			")";
	
	public SubjectsHelper(Context context) {
		super(context, MySQLiteHelper.DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SUBJECTS_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}


}
