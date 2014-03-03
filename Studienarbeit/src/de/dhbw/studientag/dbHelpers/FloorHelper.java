package de.dhbw.studientag.dbHelpers;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.dhbw.studientag.model.Building;
import de.dhbw.studientag.model.Floor;

public final class FloorHelper  {
	
	protected static final String FLOOR_TABLE_NAME ="Floor";
	protected static final String FLOOR_NUM = "num";
	protected static final String FLOOR_NAME ="name";
	protected static final String FLOOR_BUILDING_ID ="buildingId";

	public static final String[] FLOOR_ALL_COLUMNS={
		MySQLiteHelper.ID, FLOOR_NUM, FLOOR_NAME, FLOOR_BUILDING_ID
		
	};
	
	protected static final String FLOOR_TABLE_CREATE = 
			"CREATE TABLE " + FLOOR_TABLE_NAME + " (" +
			MySQLiteHelper.ID 	+ " integer primary key autoincrement," +
			FLOOR_NUM	 + " INTEGER, " +
			FLOOR_NAME	 + " TEXT, " +
			FLOOR_BUILDING_ID + " INTEGER REFERENCES " +
			BuildingHelper.BUILDING_TABLE_NAME+"("+MySQLiteHelper.ID+")"+
			")";
	

	
	
	


	
	protected static final void initFloors(Building building, SQLiteDatabase db){
		for(Floor floor : building.getFloorList()){
			ContentValues values= new ContentValues();
			values.put(FLOOR_NUM, floor.getNumber());
			values.put(FLOOR_NAME, floor.getName());
			values.put(FLOOR_BUILDING_ID, building.getId());
			long id = db.insert(FLOOR_TABLE_NAME, null, values);
			floor.setId(id);
			RoomHelper.initRooms(floor, db);
	//		floor.setId(id);
			//.initOfferedSubjects(building, db);
		}
	}
	public Cursor getCursor(SQLiteDatabase database, String[] columns){
	    Cursor cursor = database.query(FLOOR_TABLE_NAME,
	    		columns, null, null,
	            null, null, FLOOR_NUM + " ASC");
	    return cursor;
	}
	
	public static Floor getFloorById(SQLiteDatabase database, long id){
	    Cursor cursor = database.query(FLOOR_TABLE_NAME,
	    		FLOOR_ALL_COLUMNS, MySQLiteHelper.ID + " = " + id, null,
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
					" FROM Floor f  INNER JOIN Building b ON f.buildingId=b._id AND b._id=? ", 
				    		new String[]{Long.toString(buildingId)});
	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Floor floor = cursorToFloor(cursor);
	      floor.setRoomList(RoomHelper.getRoomsByFloorId(floor.getId(), database));
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
