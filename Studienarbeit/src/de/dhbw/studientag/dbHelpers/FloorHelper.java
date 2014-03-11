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
	
		}
	}
	
	public static Floor getFloorById(SQLiteDatabase database, long id){
	    Cursor cursor = database.query(FLOOR_TABLE_NAME,
	    		FLOOR_ALL_COLUMNS, MySQLiteHelper.ID + " = " + id, null,
	            null, null, null);
	    cursor.moveToFirst();
	    Floor floor = cursorToFloor(cursor);
	    floor.setRoomList(RoomHelper.getRoomsByFloorId(floor.getId(), database));
	    cursor.close();
	    return floor;
	}
	
	public static Floor getFloorByRoomId(SQLiteDatabase db, long roomId){
		String query = "SELECT * FROM "+FLOOR_TABLE_NAME+" f INNER JOIN "+RoomHelper.ROOM_TABLE_NAME+" r ON f."+MySQLiteHelper.ID+"=r."+
				RoomHelper.ROOM_FLOOR+" AND r."+MySQLiteHelper.ID+"=?";
		Cursor cursor = db.rawQuery(query, new String[]{Long.toString(roomId)});
		cursor.moveToFirst();
		Floor floor = cursorToFloor(cursor);
	    floor.setRoomList(RoomHelper.getRoomsByFloorId(floor.getId(), db));
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
	/**
	 * 
	 * @param db
	 * @param floorId to check
	 * @return true if the floor has at least one room which is occupied by at least one company, false otherwise
	 */
	public static boolean hasRoomsOccupiedRooms(SQLiteDatabase db, long floorId){
		//SELECT count(*) FROM CompanyRoom cr INNER JOIN Room r ON r._id=cr.roomId AND r.floorId=floorId
		boolean returnV=false;
		String query = "SELECT * FROM "+CompanyRoomHelper.COMPANYROOM_TABLE_NAME+ " cr INNER JOIN "+
				RoomHelper.ROOM_TABLE_NAME+" r ON r."+MySQLiteHelper.ID+"=cr."+CompanyRoomHelper.COMPANYROOM_ROOM_ID+ 
				" AND r."+RoomHelper.ROOM_FLOOR+"="+Long.toString(floorId);
		
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		int rowCount = cursor.getCount();
		cursor.close();
		if(rowCount >= 1)
			returnV = true;
		return returnV;
	}
	

	
	private static Floor cursorToFloor(Cursor cursor){
		Floor floor = new Floor(
				cursor.getLong(0),
				cursor.getInt(cursor.getColumnIndex(FLOOR_NUM)),
				cursor.getString(cursor.getColumnIndex(FLOOR_NAME)));
		return floor;
				
	}
	
	

}
