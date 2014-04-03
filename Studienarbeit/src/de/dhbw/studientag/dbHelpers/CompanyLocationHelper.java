package de.dhbw.studientag.dbHelpers;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.dhbw.studientag.TestData;
import de.dhbw.studientag.model.Building;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Floor;
import de.dhbw.studientag.model.Location;
import de.dhbw.studientag.model.Room;

public class CompanyLocationHelper {

	protected static final String COMPANYROOM_TABLE_NAME = "CompanyRoom";
	protected static final String COMPANYROOM_COMPANY_ID = "companyId";
	protected static final String COMPANYROOM_ROOM_ID = "roomId";

	public static final String[] COMPANYROOM_ALL_COLUMNS = { COMPANYROOM_COMPANY_ID,
			COMPANYROOM_ROOM_ID };

	protected static final String COMPANYRROOM_TABLE_CREATE = "CREATE TABLE "
			+ COMPANYROOM_TABLE_NAME + " (" + COMPANYROOM_COMPANY_ID
			+ " INTEGER REFERENCES " + CompanyHelper.COMPANY_TABLE_NAME + "("
			+ MySQLiteHelper.ID + "), " + COMPANYROOM_ROOM_ID + " INTEGER REFERENCES "
			+ RoomHelper.ROOM_TABLE_NAME + "(" + MySQLiteHelper.ID + "), UNIQUE("
			+ COMPANYROOM_COMPANY_ID + ")" + ")";

	protected static void init(SQLiteDatabase db) {
		List<Company> companyList = TestData.getCompanies();
		for (Company company : companyList) {
			ContentValues values = new ContentValues();
			values.put(COMPANYROOM_COMPANY_ID, company.getId());
			values.put(COMPANYROOM_ROOM_ID, company.getLocation().getRoom().getId());
			db.insert(COMPANYROOM_TABLE_NAME, null, values);
		}
	}

	public static Location getLocationByCompanyId(long companyId, SQLiteDatabase db) {
		Cursor cursor = db.query(COMPANYROOM_TABLE_NAME,
				new String[] { COMPANYROOM_ROOM_ID },
				COMPANYROOM_COMPANY_ID + "=" + Long.toString(companyId), null, null,
				null, null);

		cursor.moveToFirst();
		Room room = RoomHelper.getRoomById(
				cursor.getLong(cursor.getColumnIndex(COMPANYROOM_ROOM_ID)), db);
		Floor floor = FloorHelper.getFloorByRoomId(db, room.getId());
		Building building = BuildingHelper.getBuildingByFloorId(db, floor.getId());
		Location location = new Location(building, floor, room);
		cursor.close();

		return location;
	}

	public static List<Company> getCompaniesByRoomId(SQLiteDatabase db, long roomId) {
		List<Company> companyList = new ArrayList<Company>();
		Cursor cursor = db
				.query(COMPANYROOM_TABLE_NAME, new String[] { COMPANYROOM_COMPANY_ID },
						COMPANYROOM_ROOM_ID + "=" + Long.toString(roomId), null, null,
						null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			long companyId = cursor.getLong(0);
			Company company = CompanyHelper.getCompanyById(db, companyId);
			companyList.add(company);
			cursor.moveToNext();
		}
		cursor.close();
		return companyList;
	}

}
