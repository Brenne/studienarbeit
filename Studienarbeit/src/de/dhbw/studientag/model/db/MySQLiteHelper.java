package de.dhbw.studientag.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME ="studientag";
	public static final int DATABASE_VERSION = 1;
//	public SQLiteDatabase database;
	protected Context context;
	protected static final String ID = "_id";
	
	
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CompanyHelper.COMPANY_TABLE_CREATE);
		CompanyHelper company = new CompanyHelper(context);
		company.initCompanies(db, context);
		
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}



}
