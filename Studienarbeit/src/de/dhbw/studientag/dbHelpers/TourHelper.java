package de.dhbw.studientag.dbHelpers;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Tour;
import de.dhbw.studientag.model.TourPoint;

public class TourHelper {

	private final static String TAG = "TourHelper";
	protected final static String TOUR_TABLE_NAME = "Tour";
	protected final static String TOUR_POINT_TABLE_NAME = "TourPoint";
	protected final static String TOUR_POINT_TOUR_ID = "tourId";
	protected final static String TOUR_NAME = "name";
	protected final static String TOUR_POINT_COMPANY_ID = "companyId";
	protected final static String TOUR_POINT_POSITION = "position";
	private final static String TOUR_POINT_ID = "tourPointId";
	protected final static String TOUR_TABLE_CREATE = "CREATE TABLE " + TOUR_TABLE_NAME
			+ " (" + MySQLiteHelper.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TOUR_NAME + " TEXT unique" + ")";
	protected final static String TOUR_POINT_TABLE_CREATE = "CREATE TABLE "
			+ TOUR_POINT_TABLE_NAME + " ( " + MySQLiteHelper.ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + TOUR_POINT_TOUR_ID
			+ " INTEGER REFERENCES " + TOUR_TABLE_NAME + "(" + MySQLiteHelper.ID + "), "
			+ TOUR_POINT_COMPANY_ID + " integer REFERENCES "
			+ CompanyHelper.COMPANY_TABLE_NAME + "(" + MySQLiteHelper.ID + "), "
			+ TOUR_POINT_POSITION + " integer not null, " + "UNIQUE("
			+ TOUR_POINT_TOUR_ID + ", " + TOUR_POINT_COMPANY_ID
			+ ") ON CONFLICT ROLLBACK, " + "UNIQUE(" + TOUR_POINT_TOUR_ID + ", "
			+ TOUR_POINT_POSITION + " ) ON CONFLICT REPLACE" + ")";

	private final static String[] TOUR_ALL_COLUMNS = { MySQLiteHelper.ID, TOUR_NAME };
	private final static String[] TOUR_POINT_ALL_COLUMNS = { MySQLiteHelper.ID,
			TOUR_POINT_COMPANY_ID, TOUR_POINT_POSITION, TOUR_POINT_TOUR_ID };

	public static long insertTour(SQLiteDatabase db, String tourName) {
		ContentValues values = new ContentValues();
		values.put(TOUR_NAME, tourName);
		long id = db.insert(TOUR_TABLE_NAME, null, values);
		return id;
	}

	public static long insertTourPoint(SQLiteDatabase db, TourPoint tourPoint, long tourId) {
		if (tourPoint.getCompany() == null || tourId == 0) {
			Log.d("studientag", "cannot Insert TourPoint no valid companyId or no tourId");
		}

		ContentValues values = new ContentValues();
		// TODO raw query mit position = SELECT max(position+1) WHERE
		int position = getNewTourPointPosition(db, tourId);
		values.put(TOUR_POINT_COMPANY_ID, tourPoint.getCompany().getId());
		values.put(TOUR_POINT_TOUR_ID, tourId);

		values.put(TOUR_POINT_POSITION, position);
		long id;
		try {
			id = db.insert(TOUR_POINT_TABLE_NAME, null, values);
		} catch (SQLiteConstraintException constraintEx) {
			Log.w(TAG, "Tourpoint already associated with company", constraintEx);
			Cursor cursor = db.query(TOUR_POINT_TABLE_NAME,
					new String[] { MySQLiteHelper.ID },
					TOUR_POINT_TOUR_ID + "=" + Long.toString(tourId), null, null, null,
					null);
			cursor.moveToFirst();
			id = cursor.getLong(0);
			cursor.close();
		}
		tourPoint.setId(id);
		return id;

	}

	public static String getTourNameByTourId(SQLiteDatabase db, int tourId) {
		Cursor cursor = db.query(TOUR_TABLE_NAME, new String[] { "DISTINCT(" + TOUR_NAME
				+ ")" }, MySQLiteHelper.ID + "=" + tourId, null, null, null, null);
		cursor.moveToFirst();
		String tourName = cursor.getString(0);
		cursor.close();
		return tourName;

	}

	public static void updateTourName(SQLiteDatabase db, long tourId, String tourName) {
		ContentValues values = new ContentValues();
		values.put(TOUR_NAME, tourName);
		try {
			db.update(TOUR_TABLE_NAME, values, MySQLiteHelper.ID + "=" + tourId, null);
		} catch (SQLiteConstraintException ex) {
			Log.e(TAG, "SQLiteConstraintException: Coud not update tourName. ", ex);
		}
	}

	public static List<Tour> getAllTours(SQLiteDatabase db) {
		List<Tour> tourList = new ArrayList<Tour>();

		Cursor cursor = db.query(TOUR_TABLE_NAME, TOUR_ALL_COLUMNS, null, null, null,
				null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Tour tour = cursorToTour(db, cursor);
			tourList.add(tour);
			cursor.moveToNext();
		}
		cursor.close();

		return tourList;
	}

	public static Tour getTourById(SQLiteDatabase db, long tourId) {
		Cursor cursor = db.query(TOUR_TABLE_NAME, TOUR_ALL_COLUMNS, MySQLiteHelper.ID
				+ "=" + Long.toString(tourId), null, null, null, null);
		cursor.moveToFirst();
		Tour tour = cursorToTour(db, cursor);
		cursor.close();
		return tour;
	}

	public static List<String> getTourNames(SQLiteDatabase db) {
		List<String> tourNames = new ArrayList<String>();
		Cursor cursor = db.query(true, TOUR_TABLE_NAME, new String[] { TOUR_NAME }, null,
				null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			tourNames.add(cursor.getString(0));
		}
		cursor.close();
		return tourNames;
	}

	private static TourPoint cursorToTourPoint(Cursor cursor, SQLiteDatabase db) {
		int companyId = cursor.getInt(cursor.getColumnIndex(TOUR_POINT_COMPANY_ID));
		Company company = CompanyHelper.getCompanyById(db, companyId);
		int position = cursor.getInt(cursor.getColumnIndex(TOUR_POINT_POSITION));
		long tourPointId = cursor.getLong(cursor.getColumnIndex(TOUR_POINT_ID));
		TourPoint tourPoint = new TourPoint(company, position);
		tourPoint.setId(tourPointId);
		return tourPoint;
	}

	private static Tour cursorToTour(SQLiteDatabase db, Cursor cursor) {
		long tourId = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.ID));
		String tourName = cursor.getString(cursor.getColumnIndex(TOUR_NAME));
		Tour tour = new Tour(tourId, tourName);
		tour.setTourPointList(getTourPointListByTourId(db, tour.getId()));
		return tour;

	}

	private static int getNewTourPointPosition(SQLiteDatabase db, long tourId) {
		Cursor cursor = db.query(TOUR_POINT_TABLE_NAME, new String[] { "max("
				+ TOUR_POINT_POSITION + ")" }, TOUR_POINT_TOUR_ID + "=" + tourId, null,
				null, null, null);
		cursor.moveToFirst();
		int position = cursor.getInt(0);
		cursor.close();
		return ++position;
	}

	public static List<TourPoint> getTourPointListByTourId(SQLiteDatabase db, long tourId) {
		List<TourPoint> tourPointList = new ArrayList<TourPoint>();
		String where = TOUR_POINT_TOUR_ID + "=" + Long.toString(tourId);
		String orderBy = TOUR_POINT_POSITION + " ASC";

		String query = SQLiteQueryBuilder.buildQueryString(true, TOUR_POINT_TABLE_NAME
				+ " INNER JOIN " + TOUR_TABLE_NAME + " ON " + TOUR_TABLE_NAME + "."
				+ MySQLiteHelper.ID + "=" + TOUR_POINT_TABLE_NAME + "."
				+ TOUR_POINT_TOUR_ID,
				new String[] {
						TOUR_POINT_COMPANY_ID,
						TOUR_POINT_POSITION,
						TOUR_POINT_TABLE_NAME + "." + MySQLiteHelper.ID + " AS "
								+ TOUR_POINT_ID }, where, null, null, orderBy, null);
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			tourPointList.add(cursorToTourPoint(cursor, db));
			cursor.moveToNext();
		}
		cursor.close();
		return tourPointList;
	}

	public static boolean tourNameExists(SQLiteDatabase db, String tourName) {
		Cursor cursor = db.query(TOUR_TABLE_NAME, new String[] { TOUR_NAME }, TOUR_NAME
				+ "=?", new String[] { tourName }, null, null, null);
		cursor.moveToFirst();
		if (cursor.getCount() == 0)
			return false;
		else
			return true;
	}

	public static void deleteTourById(SQLiteDatabase db, long tourId) {
		db.delete(TOUR_TABLE_NAME, MySQLiteHelper.ID + "=" + Long.toString(tourId), null);
	}

	public static int deleteTourPointById(SQLiteDatabase db, long tourPointId) {
		// TODO Inner Join
		String query = "SELECT " + TOUR_POINT_TOUR_ID + " FROM " + TOUR_POINT_TABLE_NAME
				+ " WHERE " + TOUR_POINT_TOUR_ID + "=" + "(SELECT " + TOUR_POINT_TOUR_ID
				+ " FROM " + TOUR_POINT_TABLE_NAME + " WHERE " + MySQLiteHelper.ID
				+ "=?)";
		Log.v(TAG, query);
		Cursor cursor = db.rawQuery(query, new String[] { Long.toString(tourPointId) });
		cursor.moveToFirst();
		// if tourPoint is the last of the tour also delete the Tour because no
		// tour without tourPoints
		if (cursor.getCount() == 1) {
			long tourId = cursor.getLong(0);
			deleteTourById(db, tourId);
		}
		int count_rows = db.delete(TOUR_POINT_TABLE_NAME,
				MySQLiteHelper.ID + "=" + Long.toString(tourPointId), null);

		return count_rows;
	}

	public static boolean hasTourPoints(SQLiteDatabase db, long tourId) {

		return false;

	}

	public static String getFreeTourName(SQLiteDatabase db, String tourName) {
		if (!tourNameExists(db, tourName)) {
			return tourName;
		}
		for (int i = 1; i < Integer.MAX_VALUE; i++) {
			String checkTourName = tourName + " " + Integer.toString(i);
			if (!tourNameExists(db, checkTourName))
				return checkTourName;
		}
		// more than Integer.MAY_VALUE entries in table
		return tourName + " changeme";
	}

	public static void updatePosition(SQLiteDatabase db, TourPoint tourPoint) {
		ContentValues values = new ContentValues();
		values.put(TOUR_POINT_POSITION, tourPoint.getPosition());
		db.update(TOUR_POINT_TABLE_NAME, values,
				MySQLiteHelper.ID + "=" + Long.toString(tourPoint.getId()), null);
	}

	public static boolean isCompanyInTour(SQLiteDatabase db, long companyId, long tourId) {

		Cursor cursor = db.query(true, TOUR_POINT_TABLE_NAME, new String[] {
				TOUR_POINT_TOUR_ID, TOUR_POINT_COMPANY_ID }, TOUR_POINT_TOUR_ID + "=? "
				+ " AND " + TOUR_POINT_COMPANY_ID + "=?",
				new String[] { Long.toString(tourId), Long.toString(companyId) }, null,
				null, null, null);
		cursor.moveToFirst();
		if (cursor.getCount() >= 1) {
			return true;
		} else {
			return false;
		}

	}
}
