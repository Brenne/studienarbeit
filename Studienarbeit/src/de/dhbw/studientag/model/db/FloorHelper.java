package de.dhbw.studientag.model.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.dhbw.studientag.model.Floor;

public final class FloorHelper extends MySQLiteHelper {
	
	protected static final String FLOOR_TABLE_NAME ="Floor";
	protected static final String FLOOR_NUM = "num";
	protected static final String FLOOR_NAME ="name";
	protected static final String FLOOR_BUILDING_ID ="buildingId";

	public static final String[] FLOOR_ALL_COLUMNS={
		ID, FLOOR_NUM, FLOOR_NAME, FLOOR_BUILDING_ID
		
	};
	
	protected static final String FLOOR_TABLE_CREATE = 
			"CREATE TABLE " + FLOOR_TABLE_NAME + " (" +
			ID 	+ " integer primary key autoincrement," +
			FLOOR_NUM	 + " INTEGER, " +
			FLOOR_NAME	 + " TEXT, " +
			FLOOR_BUILDING_ID + " INTEGER" +
			")";
	

	
	public FloorHelper(Context context) {
		super(context);


	}


	
	private final void initFloor(Floor floor, SQLiteDatabase db){
		ContentValues values= new ContentValues();
		values.put(FLOOR_NUM, floor.getNumber());
		values.put(FLOOR_NAME, floor.getName());
		long id = db.insert(FLOOR_TABLE_NAME, null, values);
//		floor.setId(id);
		//.initOfferedSubjects(building, db);
	}
	public Cursor getCursor(SQLiteDatabase database, String[] columns){
	    Cursor cursor = database.query(FLOOR_TABLE_NAME,
	    		columns, null, null,
	            null, null, FLOOR_NUM + " ASC");
	    return cursor;
	}
	
	public static Floor getFloorById(SQLiteDatabase database, long id){
	    Cursor cursor = database.query(FLOOR_TABLE_NAME,
	    		FLOOR_ALL_COLUMNS, ID + " = " + id, null,
	            null, null, null);
	    cursor.moveToFirst();
	    Floor floor = cursorToFloor(cursor);
	    cursor.close();
	    return floor;
	}
	
	public static List<Floor> getFloorsByBuildingId(long buildingId, SQLiteDatabase database){
		List<Floor> floorList = new ArrayList<Floor>();
		//SELECT * FROM Floor f INNER JOIN Building b ON f.buildingId=b._id AND b._id = ?
		 Cursor cursor = database.rawQuery("SELECT * " + 
					" FROM Floor f  INNER JOIN Building b ON f.buildingId=b._id AND b._id? ORDER BY c.name ASC", 
				    		new String[]{Long.toString(buildingId)});
	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Floor floor = cursorToFloor(cursor);
	      floorList.add(floor);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
		return floorList;
	}
	
//	public static List<Company> getAllCompaniesBySubject(SQLiteDatabase database, Subject subject){
//		List<Company> companies = new ArrayList<Company>();
//		//Select name  from company c, subjects s WHERE s.companyId == c._id 
//	    Cursor cursor = database.rawQuery("SELECT * " + 
//		" FROM company c  INNER JOIN offeredsubjects os ON c._id=os.companyId AND os.subjectID=? ORDER BY c.name ASC", 
//	    		new String[]{Long.toString(subject.getId())});
//
//	    cursor.moveToFirst();
//	    while (!cursor.isAfterLast()) {
//	      Company company = cursorToCompany(cursor);
//	      company.setSubjectList(//.getOfferdSubjectsByCompanyId(company.getId(), database));
//	      companies.add(company);
//	      cursor.moveToNext();
//	    }
//	    // make sure to close the cursor
//	    cursor.close();
//		return companies;
//		
//	}
	

	
	private static Floor cursorToFloor(Cursor cursor){
//		Log.d("id" ,cursor.getColumnNames().toString());
		Floor floor = new Floor(
				cursor.getLong(0),
				cursor.getInt(cursor.getColumnIndex(FLOOR_NUM)),
				cursor.getString(cursor.getColumnIndex(FLOOR_NAME)));
		return floor;
				
	}
	
	

}
