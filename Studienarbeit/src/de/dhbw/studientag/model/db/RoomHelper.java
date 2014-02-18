package de.dhbw.studientag.model.db;

import android.content.Context;

public class RoomHelper extends MySQLiteHelper {

	protected final static String ROOM_FLOOR = "floor";
	protected final static String ROOM_NUMBER = "number";
	protected final static String ROOM_BUILDING_ID = "buildingId";
	
	
	
	
	
	public RoomHelper(Context context) {
		super(context);
	}
	
	

}
