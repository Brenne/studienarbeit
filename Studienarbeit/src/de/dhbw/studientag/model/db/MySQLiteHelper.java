package de.dhbw.studientag.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.dhbw.studientag.TestData;

public class MySQLiteHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME ="studientag";
	public static final int DATABASE_VERSION = 1;
//	public SQLiteDatabase database;
	protected Context context;
	protected static final String ID = "_id";
	public static TestData testData = null;
	
	
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		if(testData == null)
			MySQLiteHelper.testData = new TestData(context.getAssets());
		
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CompanyHelper.COMPANY_TABLE_CREATE);
		db.execSQL(OfferedSubjectsHelper.OFFERED_SUBJECTS_TABLE_CREATE);
		db.execSQL(SubjectsHelper.SUBJECTS_TABLE_CREATE);
		SubjectsHelper subject = new SubjectsHelper(context);
		subject.initSubjects(db);
		CompanyHelper company = new CompanyHelper(context);
		company.initCompanies(db);
		

	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP DATABASE IF EXISTS "+ DATABASE_NAME );
		onCreate(db);
		
	}



}
