package de.dhbw.studientag.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OfferedSubjectsHelper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
	private static final String OFFERED_SUBJECTS_TABLE_NAME ="OfferedSubjects";
	private static final String COMPANY_ID = "companyId";
	private static final String SUBJECT_ID = "subjectId";
	private static final String OFFERED_SUBJECTS_TABLE_CREATE=
			"CREATE TABLE " + OFFERED_SUBJECTS_TABLE_NAME + " (" +
			COMPANY_ID + " INTEGER," +
			SUBJECT_ID + " INTEGER"  +
			")";
	
	public OfferedSubjectsHelper(Context context) {
		super(context, MySQLiteHelper.DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(OFFERED_SUBJECTS_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
