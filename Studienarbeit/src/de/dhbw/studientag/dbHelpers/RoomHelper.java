package de.dhbw.studientag.dbHelpers;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.dhbw.studientag.model.Floor;
import de.dhbw.studientag.model.Room;

public class RoomHelper {

	protected final static String ROOM_FLOOR = "floorId";
	protected final static String ROOM_NUMBER = "number";
	protected final static String ROOM_SUBJECT_ID = "subjectId";
	protected final static String ROOM_TABLE_NAME = "Room";
	protected final static String ROOM_TABLE_CREATE =
			"CREATE TABLE " + ROOM_TABLE_NAME + " (" +
			MySQLiteHelper.ID 	+ " integer primary key autoincrement, " +
			ROOM_NUMBER	 + " TEXT, " +
			ROOM_FLOOR	 + " INTEGER REFERENCES " + 
				FloorHelper.FLOOR_TABLE_NAME+"("+MySQLiteHelper.ID+"), "+
			ROOM_SUBJECT_ID +" INTEGER REFERENCES "+
				SubjectsHelper.SUBJECTS_TABLE_NAME+"("+MySQLiteHelper.ID+")"+
			")";
	protected final static String[] ROOM_ALL_COLUMNS={
		MySQLiteHelper.ID,ROOM_NUMBER,ROOM_FLOOR, ROOM_SUBJECT_ID
	};
	
	protected static final void initRooms(Floor floor, SQLiteDatabase db){
		for(Room room : floor.getRoomList()){
			ContentValues values= new ContentValues();
			values.put(ROOM_NUMBER, room.getRoomNo());
			values.put(ROOM_FLOOR, floor.getId());
			values.put(ROOM_SUBJECT_ID, room.getSubjectId());
			long roomId=db.insert(ROOM_TABLE_NAME, null, values);
			room.setId(roomId);
			
		}
	}
	
	public static Room getRoomById(long roomId, SQLiteDatabase db){
		Cursor cursor = db.query(ROOM_TABLE_NAME, ROOM_ALL_COLUMNS, MySQLiteHelper.ID+"="+Long.toString(roomId), null, null, null, null);
		cursor.moveToFirst();
		Room room = cursorToRoom(cursor);
		cursor.close();
		return room;
	}
	
	public static List<Room> getRoomsByFloorId(long floorId, SQLiteDatabase database){
		List<Room> roomList = new ArrayList<Room>();
		
		 Cursor cursor = database.rawQuery("SELECT * " + 
					" FROM "+RoomHelper.ROOM_TABLE_NAME+" r  INNER JOIN "+FloorHelper.FLOOR_TABLE_NAME+" f ON r."
					+ROOM_FLOOR+"=f."+MySQLiteHelper.ID+" AND f."+MySQLiteHelper.ID+"=? ", 
				    		new String[]{Long.toString(floorId)});
	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Room room = cursorToRoom(cursor);
	      
	      roomList.add(room);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
		return roomList;
	}


	private static Room cursorToRoom(Cursor cursor) {
		Room room = new Room(cursor.getLong(0), 
				cursor.getString(cursor.getColumnIndex(ROOM_NUMBER)),
				cursor.getLong(cursor.getColumnIndex(ROOM_SUBJECT_ID)));
		return room;
	}
	

}
