package de.dhbw.studientag.dbHelpers;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.dhbw.studientag.ImportDataController;
import de.dhbw.studientag.model.Building;

public final class BuildingHelper {

	protected static final String BUILDING_TABLE_NAME = "Building";
	protected static final String BUILDING_SHORTNAME = "shortname";
	private static final String BUILDING_FULLNAME = "fullname";

	private static final String[] BUILDING_ALL_COLUMNS = { MySQLiteHelper.ID,
			BUILDING_SHORTNAME, BUILDING_FULLNAME

	};

	protected static final String BUILDING_TABLE_CREATE = "CREATE TABLE "
			+ BUILDING_TABLE_NAME + " (" + MySQLiteHelper.ID
			+ " integer primary key autoincrement," + BUILDING_SHORTNAME
			+ " TEXT unique, " + BUILDING_FULLNAME + " TEXT " + ")";

	protected final static void initBuildings(SQLiteDatabase db) {
		List<Building> builidings = ImportDataController.getBuildings();
		for (Building building : builidings)
			initBuilding(building, db);
	}

	private final static void initBuilding(Building building, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put(BUILDING_SHORTNAME, building.getShortName());
		values.put(BUILDING_FULLNAME, building.getFullName());
		long id = db.insert(BUILDING_TABLE_NAME, null, values);
		building.setId(id);
		FloorHelper.initFloors(building, db);
	}

	public Cursor getCursor(SQLiteDatabase database, String[] columns) {
		Cursor cursor = database.query(BUILDING_TABLE_NAME, columns, null,
				null, null, null, BUILDING_SHORTNAME + " ASC");
		return cursor;
	}

	public static Building getBuildingById(SQLiteDatabase database, long id) {
		Cursor cursor = database.query(BUILDING_TABLE_NAME,
				BUILDING_ALL_COLUMNS, MySQLiteHelper.ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
		Building building = cursorToBuilding(cursor);
		building.setFloorList(FloorHelper.getFloorsByBuildingId(
				building.getId(), database));
		cursor.close();
		return building;
	}

	public static List<Building> getAllBuildings(SQLiteDatabase database) {
		List<Building> buildings = new ArrayList<Building>();
		Cursor cursor = database.query(BUILDING_TABLE_NAME,
				BUILDING_ALL_COLUMNS, null, null, null, null,
				BUILDING_SHORTNAME + " ASC ");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Building building = cursorToBuilding(cursor);
			building.setFloorList(FloorHelper.getFloorsByBuildingId(
					building.getId(), database));
			buildings.add(building);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return buildings;
	}

	public static Building getBuildingByFloorId(SQLiteDatabase db, long floorId){
		//SELECT * FROM Building b INNER JOIN Floor f ON b_id=f.buildingId
		String query= "SELECT * FROM "+BUILDING_TABLE_NAME+ " b INNER JOIN "+
				FloorHelper.FLOOR_TABLE_NAME+" f ON "+ " b."+MySQLiteHelper.ID+"=f."+FloorHelper.FLOOR_BUILDING_ID+ 
				" AND f."+MySQLiteHelper.ID+"=?";
		Cursor cursor = db.rawQuery(query, new String[]{Long.toString(floorId)});
		cursor.moveToFirst();
		Building building = cursorToBuilding(cursor);
		building.setFloorList(FloorHelper.getFloorsByBuildingId(
				building.getId(), db));
		cursor.close();
		return building;
		
	}

	private static Building cursorToBuilding(Cursor cursor) {
		// Log.d("id" ,cursor.getColumnNames().toString());
		Building building = new Building(cursor.getLong(0),
				cursor.getString(cursor.getColumnIndex(BUILDING_SHORTNAME)),
				cursor.getString(cursor.getColumnIndex(BUILDING_FULLNAME)));
		return building;

	}

}
