package de.dhbw.studientag.dbHelpers;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.dhbw.studientag.TestData;
import de.dhbw.studientag.model.Building;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.CompanyLocation;
import de.dhbw.studientag.model.Room;

public class CompanyRoomHelper {

	protected static final String COMPANYROOM_TABLE_NAME = "CompanyRoom";
	protected static final String COMPANYROOM_COMPANY_ID = "companyId";
	protected static final String COMPANYROOM_ROOM_ID = "roomId";

	public static final String[] COMPANYROOM_ALL_COLUMNS = { COMPANYROOM_COMPANY_ID,
			COMPANYROOM_ROOM_ID };

	protected static final String COMPANYRROOM_TABLE_CREATE = "CREATE TABLE "
			+ COMPANYROOM_TABLE_NAME + " (" + COMPANYROOM_COMPANY_ID
			+ " INTEGER REFERENCES " + CompanyHelper.COMPANY_TABLE_NAME + "("
			+ MySQLiteHelper.ID + "), " + COMPANYROOM_ROOM_ID + " INTEGER REFERENCES "
			+ RoomHelper.ROOM_TABLE_NAME + "(" + MySQLiteHelper.ID + ")" + ")";

	protected static void init(SQLiteDatabase db) {
		List<CompanyLocation> companyLocations = TestData.getCompanyLocation();
		for (CompanyLocation companyLocation : companyLocations) {

			String companyName = companyLocation.getCompany().getName();
			String companyBld = companyLocation.getBuilding().getShortName();
			String companyRoom = companyLocation.getRoom().getRoomNo();
			// SELECT c._id, r._id FROM Company c, Room r, Floor f, Building b
			// WHERE
			// c.name=? AND r.number=? AND r.floorId=f._id AND f.buildingId =
			// b._id AND b.shortName=?
			String query = "SELECT c." + MySQLiteHelper.ID + " AS "
					+ COMPANYROOM_COMPANY_ID + ", r." + MySQLiteHelper.ID + " AS "
					+ COMPANYROOM_ROOM_ID + " FROM " + CompanyHelper.COMPANY_TABLE_NAME
					+ " c, " + RoomHelper.ROOM_TABLE_NAME + " r, "
					+ FloorHelper.FLOOR_TABLE_NAME + " f, "
					+ BuildingHelper.BUILDING_TABLE_NAME + " b WHERE " + "c."
					+ CompanyHelper.COMPANY_NAME + "=? AND r." + RoomHelper.ROOM_NUMBER
					+ "=? AND " + "r." + RoomHelper.ROOM_FLOOR + "=f."
					+ MySQLiteHelper.ID + " AND f." + FloorHelper.FLOOR_BUILDING_ID
					+ "= b." + MySQLiteHelper.ID + " AND " + "b."
					+ BuildingHelper.BUILDING_SHORTNAME + "=?";
			// Log.d("studientag", query);
			Cursor cursor = db.rawQuery(query, new String[] { companyName, companyRoom,
					companyBld });
			if (cursor.getCount() == 1) {
				cursor.moveToFirst();
				ContentValues values = new ContentValues();

				values.put(COMPANYROOM_COMPANY_ID,
						cursor.getInt(cursor.getColumnIndex(COMPANYROOM_COMPANY_ID)));
				values.put(COMPANYROOM_ROOM_ID,
						cursor.getInt(cursor.getColumnIndex(COMPANYROOM_ROOM_ID)));
				db.insert(COMPANYROOM_TABLE_NAME, null, values);
			}
			cursor.close();

		}

	}

	public static CompanyLocation getLocationByCompanyId(long id, SQLiteDatabase db) {
		final String BUILDING_ID = "BuildingId";
		final String COMPANY_ID = "CompanyId";
		final String ROOM_ID = "RoomId";

		String query = "SELECT  b." + MySQLiteHelper.ID + "" + " AS " + BUILDING_ID
				+ ", r." + MySQLiteHelper.ID + " AS " + ROOM_ID + ", c."
				+ MySQLiteHelper.ID + " AS " + COMPANY_ID + " FROM "
				+ CompanyHelper.COMPANY_TABLE_NAME + " c, "
				+ BuildingHelper.BUILDING_TABLE_NAME + " b, "
				+ RoomHelper.ROOM_TABLE_NAME + " r, " + COMPANYROOM_TABLE_NAME + " cr, "
				+ FloorHelper.FLOOR_TABLE_NAME + " f " + "WHERE c." + MySQLiteHelper.ID
				+ "=cr." + COMPANYROOM_COMPANY_ID + " AND " + "cr." + COMPANYROOM_ROOM_ID
				+ "=r." + MySQLiteHelper.ID + " AND " + "r." + RoomHelper.ROOM_FLOOR
				+ "=f." + MySQLiteHelper.ID + " AND " + "f."
				+ FloorHelper.FLOOR_BUILDING_ID + "=b." + MySQLiteHelper.ID + " AND "
				+ "c." + MySQLiteHelper.ID + "=?";

		Cursor cursor = db.rawQuery(query, new String[] { Long.toString(id) });

		if (cursor.getCount() >= 1) {
			cursor.moveToFirst();
			Company company = CompanyHelper.getCompanyById(db,
					cursor.getLong(cursor.getColumnIndex(COMPANY_ID)));
			Building building = BuildingHelper.getBuildingById(db,
					cursor.getLong(cursor.getColumnIndex(BUILDING_ID)));
			Room room = RoomHelper.getRoomById(
					cursor.getLong(cursor.getColumnIndex(ROOM_ID)), db);
			CompanyLocation companyLocation = new CompanyLocation(company, building, room);

			cursor.close();
			// map<Company,
			return companyLocation;

		} else {
			// if this happens there is more than one company with the same id
			// in database
			Log.e("studientag",
					"Fatal Error in getLocationByCompanyId more than one company with the same ID");
			return null;

		}

	}

	public static List<Company> getCompaniesByRoomId(SQLiteDatabase db, long roomId) {
		List<Company> companyList = new ArrayList<Company>();
		Cursor cursor = db
				.query(COMPANYROOM_TABLE_NAME, new String[] { COMPANYROOM_COMPANY_ID },
						COMPANYROOM_ROOM_ID + "=" + Long.toString(roomId), null, null,
						null, null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			long companyId = cursor.getLong(0);
			Company company = CompanyHelper.getCompanyById(db,companyId);
			companyList.add(company);
			cursor.moveToNext();
		}
		cursor.close();
		return companyList;
	}

}
