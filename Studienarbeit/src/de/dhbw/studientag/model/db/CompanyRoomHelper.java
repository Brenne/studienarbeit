package de.dhbw.studientag.model.db;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.dhbw.studientag.TestData;
import de.dhbw.studientag.model.CompanyLocation;

public class CompanyRoomHelper {

	protected static final String COMPANYROOM_TABLE_NAME = "CompanyRoom";
	protected static final String COMPANYROOM_COMPANY_ID = "companyId";
	protected static final String COMPANYROOM_ROOM_ID = "roomId";

	public static final String[] COMPANYROOM_ALL_COLUMNS = {
			COMPANYROOM_COMPANY_ID, COMPANYROOM_ROOM_ID };

	protected static final String COMPANYRROOM_TABLE_CREATE = "CREATE TABLE "
			+ COMPANYROOM_TABLE_NAME + " (" + COMPANYROOM_COMPANY_ID
			+ " INTEGER, " + COMPANYROOM_ROOM_ID + " INTEGER " + ")";

	protected static void init(SQLiteDatabase db) {
		List<CompanyLocation> companyLocation = TestData.getCompanyLocation();
		for (int i = 0; i < companyLocation.size(); i++) {
			CompanyLocation curCompanyLocation = companyLocation.get(i);
			String companyName = curCompanyLocation.getCompanyName();
			String companyBld = curCompanyLocation.getBuildingShortName();
			String companyRoom = curCompanyLocation.getRoomNo();
//			 SELECT c._id, r._id FROM Company c, Room r, Floor f, Building b
//			 WHERE
//			 c.name=? AND r.number=? AND r.floorId=f._id AND f.buildingId =
//			 b._id AND b.shortName=?
			String query = "SELECT c._id AS " + COMPANYROOM_COMPANY_ID
					+ ", r._id AS " + COMPANYROOM_ROOM_ID + " FROM "
					+ CompanyHelper.COMPANY_TABLE_NAME + " c, "
					+ RoomHelper.ROOM_TABLE_NAME + " r, "
					+ FloorHelper.FLOOR_TABLE_NAME + " f, "
					+ BuildingHelper.BUILDING_TABLE_NAME + " b WHERE " + "c."
					+ CompanyHelper.COMPANY_NAME + "=? AND r."
					+ RoomHelper.ROOM_NUMBER + "=? AND " + "r."
					+ RoomHelper.ROOM_FLOOR + "=f." + MySQLiteHelper.ID
					+ " AND f." + FloorHelper.FLOOR_BUILDING_ID + "= b."
					+ MySQLiteHelper.ID + " AND " + "b."
					+ BuildingHelper.BUILDING_SHORTNAME + "=?";
//			Log.d("studientag", query);
			Cursor cursor = db.rawQuery(query, new String[] { companyName,
					companyRoom, companyBld });
			if (cursor.getCount() == 1) {
				cursor.moveToFirst();
				ContentValues values = new ContentValues();

				values.put(COMPANYROOM_COMPANY_ID, cursor.getInt(cursor
						.getColumnIndex(COMPANYROOM_COMPANY_ID)));
				values.put(COMPANYROOM_ROOM_ID, cursor.getInt(cursor
						.getColumnIndex(COMPANYROOM_ROOM_ID)));
				db.insert(COMPANYROOM_TABLE_NAME, null, values);
			}
			cursor.close();

		}

	}

	public static CompanyLocation getLocationByCompanyId(long id,
			SQLiteDatabase db) {
		final String BUILDING_FULLNAME = "BuildingFullName";
		final String BUILDING_SHORTNAME = "BuildingShortName";
		final String COMPANY_NAME = "CompanyName";
		final String ROOM_NO = "RoomNo";

		String query = "SELECT  b." + BuildingHelper.BUILDING_FULLNAME + " AS "
				+ BUILDING_FULLNAME + ", b."
				+ BuildingHelper.BUILDING_SHORTNAME + "" + " AS "
				+ BUILDING_SHORTNAME + ", r." + RoomHelper.ROOM_NUMBER + " AS "
				+ ROOM_NO + ", c." + CompanyHelper.COMPANY_NAME + " AS "
				+ COMPANY_NAME + " FROM " + CompanyHelper.COMPANY_TABLE_NAME
				+ " c, " + BuildingHelper.BUILDING_TABLE_NAME + " b, "
				+ RoomHelper.ROOM_TABLE_NAME + " r, " + COMPANYROOM_TABLE_NAME
				+ " cr, " + FloorHelper.FLOOR_TABLE_NAME + " f " + "WHERE c."
				+ MySQLiteHelper.ID + "=cr." + COMPANYROOM_COMPANY_ID + " AND "
				+ "cr." + COMPANYROOM_ROOM_ID + "=r." + MySQLiteHelper.ID
				+ " AND " + "r." + RoomHelper.ROOM_FLOOR + "=f."
				+ MySQLiteHelper.ID + " AND " + "f."
				+ FloorHelper.FLOOR_BUILDING_ID + "=b." + MySQLiteHelper.ID
				+ " AND " + "c." + MySQLiteHelper.ID + "=?";

		Cursor cursor = db.rawQuery(query, new String[] { Long.toString(id) });

		if (cursor.getCount() >= 1) {
			cursor.moveToFirst();
			CompanyLocation companyLocation = new CompanyLocation(
					cursor.getString(cursor.getColumnIndex(COMPANY_NAME)),
					cursor.getString(cursor.getColumnIndex(BUILDING_SHORTNAME)),
					cursor.getString(cursor.getColumnIndex(BUILDING_FULLNAME)),
					cursor.getString(cursor.getColumnIndex(ROOM_NO)));

			cursor.close();
			return companyLocation;
		}
		return null;

	}

}
