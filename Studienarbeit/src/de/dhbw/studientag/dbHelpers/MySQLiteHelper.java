package de.dhbw.studientag.dbHelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.dhbw.studientag.TestData;

public final class MySQLiteHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME ="studientag";
	public static final int DATABASE_VERSION = 1;
//	public SQLiteDatabase database;
	protected Context context;
	protected static final String ID = "_id";
	public static boolean init = false;
	
	
	
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		if(!init && context != null){
			new TestData(context.getAssets());
			init=true;
		}
		
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(CompanyHelper.COMPANY_TABLE_CREATE);
		db.execSQL(OfferedSubjectsHelper.OFFERED_SUBJECTS_TABLE_CREATE);
		db.execSQL(SubjectsHelper.SUBJECTS_TABLE_CREATE);
		db.execSQL(FloorHelper.FLOOR_TABLE_CREATE);
		db.execSQL(BuildingHelper.BUILDING_TABLE_CREATE);
		db.execSQL(RoomHelper.ROOM_TABLE_CREATE);
		db.execSQL(CompanyLocationHelper.COMPANYRROOM_TABLE_CREATE);
		db.execSQL(CommentHelper.COMMENT_TABLE_CREATE);
		db.execSQL(TourHelper.TOUR_TABLE_CREATE);
		db.execSQL(TourHelper.TOUR_POINT_TABLE_CREATE);
		
		
		
		SubjectsHelper.initSubjects(db);
		CompanyHelper.initCompanies(db);
		BuildingHelper.initBuildings(db);
		CompanyLocationHelper.init(db);
		

	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		context.deleteDatabase(DATABASE_NAME);
		
		
		onCreate(db);
		
	}



}
