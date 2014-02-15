package de.dhbw.studientag.model.db;

import java.util.ArrayList;

import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.TestData;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class InitDB  {
	
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	
	public InitDB(Context context){
		dbHelper = new MySQLiteHelper(context);
		
	}
	
	public SQLiteDatabase open() throws SQLException {
		database = dbHelper.getReadableDatabase();
		return database;
	}
	
	public void close() {
		dbHelper.close();
	}
	

}
