package de.dhbw.studientag.dbHelpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.TourPoint;

public class TourHelper {
	
	
	protected final static String TOUR_TABLE_NAME= "Tour";
	protected final static String TOUR_POINT_TABLE_NAME="TourPoint";
	protected final static String TOUR_POINT_TOUR_ID ="tourId";
	protected final static String TOUR_NAME="name";
	protected final static String TOUR_POINT_COMPANY_ID="companyId";
	protected final static String TOUR_POINT_POSITION="position";
	protected final static String TOUR_TABLE_CREATE=
			"CREATE TABLE "+TOUR_TABLE_NAME+" ("+
			MySQLiteHelper.ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
			TOUR_NAME+" TEXT unique"+
			")";
	protected final static String TOUR_POINT_TABLE_CREATE =
			"CREATE TABLE "+TOUR_POINT_TABLE_NAME+" ( "+
			MySQLiteHelper.ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
			TOUR_POINT_TOUR_ID +" integer REFERENCES "+TOUR_TABLE_NAME+"("+MySQLiteHelper.ID+"), "+
			TOUR_POINT_COMPANY_ID + " integer REFERENCES "+CompanyHelper.COMPANY_TABLE_NAME+"("+
			MySQLiteHelper.ID+"), "+
			TOUR_POINT_POSITION + " integer not null, "+
			"UNIQUE("+TOUR_POINT_TOUR_ID+", "+TOUR_POINT_COMPANY_ID+ ") ON CONFLICT REPLACE, "+
			"UNIQUE("+TOUR_POINT_TOUR_ID+", "+TOUR_POINT_POSITION+ " ) ON CONFLICT REPLACE"+
			")";

	private final static String[] TOUR_POINT_ALL_COLUMNS={
		MySQLiteHelper.ID, TOUR_POINT_COMPANY_ID, TOUR_POINT_POSITION, TOUR_POINT_TOUR_ID
	};
	
	public static long insertTour(SQLiteDatabase db, String tourName){
		ContentValues values = new ContentValues();
		values.put(TOUR_NAME, tourName);
		long id = db.insert(TOUR_TABLE_NAME, null, values);
		return id;
	}
	
	public static long insertTourPoint(SQLiteDatabase db, TourPoint tourPoint){
		if(tourPoint.getCompany() == null || tourPoint.getTourId() == 0){
			Log.d("studientag", "cannot Insert TourPoint no valid companyId or no tourId");
		}
		
	
		ContentValues values = new ContentValues();
		int position = getNewTourPointPosition(db, tourPoint.getTourId());
		values.put(TOUR_POINT_COMPANY_ID, tourPoint.getCompany().getId());
		values.put(TOUR_POINT_TOUR_ID, tourPoint.getTourId());
		//TODO raw query mit position = SELECT max(position+1) WHERE tourid=tourId 
		values.put(TOUR_POINT_POSITION, position);
		long id = db.insert(TOUR_POINT_TABLE_NAME, null, values);

		
		return id;
		
		
	}
	
	public static String getTourNameByTourId(SQLiteDatabase db, int tourId){
		Cursor cursor = db.query(TOUR_TABLE_NAME, new String[]{"DISTINCT("+TOUR_NAME+")"}, MySQLiteHelper.ID+"="+tourId, null, null, null, null);
		cursor.moveToFirst();
		String tourName = cursor.getString(0);
		cursor.close();
		return tourName;
		
	}
	
	public static void updateTourName(SQLiteDatabase db, long tourId, String tourName){
		ContentValues values = new ContentValues();
		values.put(TOUR_NAME, tourName);
		db.update(TOUR_TABLE_NAME, values, MySQLiteHelper.ID+"="+tourId, null);
	}
	
	@SuppressLint("UseSparseArrays") //SparseArrays can not parameterized with <Integer,List<TourPoint>>
	public static Map<Integer, List<TourPoint>> getAllTours(SQLiteDatabase db){
		Map<Integer, List<TourPoint>> allTourPoints = new HashMap<Integer, List<TourPoint>>();
		final String TP_PREFIX="tp";
		final String T_PREFIX="t";
		String query = "SELECT "+TP_PREFIX+"."+TOUR_POINT_COMPANY_ID+", "
		+TP_PREFIX+"."+TOUR_POINT_POSITION+", "
		+TP_PREFIX+"."+TOUR_POINT_TOUR_ID+", "
		+T_PREFIX+"."+TOUR_NAME+
		" FROM "+TOUR_POINT_TABLE_NAME+" "+TP_PREFIX
		+"  INNER JOIN "+TOUR_TABLE_NAME+" "+T_PREFIX+" ON "
		+T_PREFIX+"."+MySQLiteHelper.ID+"="+TP_PREFIX+"."+TOUR_POINT_TOUR_ID+ " ORDER BY "+TOUR_POINT_TOUR_ID+" ASC";
//		Log.v("studientag", query);
		Cursor cursor = db.rawQuery(query, null);
		int tourId=0;
		int newTourId=0;
		List<TourPoint> tourPoints = new ArrayList<TourPoint>();
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			TourPoint tourPoint = cursorToTourPoint(cursor, db);
			try{
				newTourId = (int) tourPoint.getTourId();
			}catch(ClassCastException e){
				Log.d("studientag","Class Cast Exception tourId no Integer");
			}
			if(newTourId != tourId){
				tourPoints = new ArrayList<TourPoint>();
			}
			tourPoints.add(tourPoint);
			if(newTourId != tourId)
				allTourPoints.put(newTourId, tourPoints);
			tourId=newTourId;
			cursor.moveToNext();
		}
		cursor.close();

		return allTourPoints;
	}
	public static List<String> getTourNames(SQLiteDatabase db){
		List<String> tourNames = new ArrayList<String>();
		Cursor cursor = db.query(TOUR_TABLE_NAME, new String[]{"distinct("+TOUR_NAME+")"}, null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			tourNames.add(cursor.getString(0));
		}
		cursor.close();
		return tourNames;
	}
	private static TourPoint cursorToTourPoint(Cursor cursor, SQLiteDatabase db) {
		int companyId = cursor.getInt(cursor.getColumnIndex(TOUR_POINT_COMPANY_ID));
		Company company = CompanyHelper.getCompanyById(db, companyId);
		int position = cursor.getInt(cursor.getColumnIndex(TOUR_POINT_POSITION));
		int colIndex = cursor.getColumnIndex(TOUR_POINT_TOUR_ID);
		if(colIndex == -1){
			colIndex = cursor.getColumnIndex(MySQLiteHelper.ID);
		}
		int tourId = cursor.getInt(colIndex);
		String tourName = cursor.getString(cursor.getColumnIndex(TOUR_NAME));
		TourPoint tourPoint = new TourPoint(tourName, tourId, company, position);
		return tourPoint;
	}


	
	private static int getNewTourPointPosition(SQLiteDatabase db, long tourId){
		Cursor cursor = db.query(TOUR_POINT_TABLE_NAME, new String[]{"max("+TOUR_POINT_POSITION+")"}, TOUR_POINT_TOUR_ID+"="+tourId, null, null, null, null);
		cursor.moveToFirst();
		int position = cursor.getInt(0);
		cursor.close();
		return ++position;
	}
	
	public static List<TourPoint> getTourPointListByTourId(SQLiteDatabase db, long tourId){
		List<TourPoint> tourPointList = new ArrayList<TourPoint>();
		//TODO select tourName from tour
		String where = TOUR_POINT_TOUR_ID+"="+Long.toString(tourId);
		String orderBy =  TOUR_POINT_POSITION+ " ASC";
		String query = SQLiteQueryBuilder.buildQueryString(true, 
				TOUR_POINT_TABLE_NAME+" INNER JOIN "+TOUR_TABLE_NAME+" ON "+TOUR_TABLE_NAME+"."+MySQLiteHelper.ID+"="+TOUR_POINT_TABLE_NAME+"."+TOUR_POINT_TOUR_ID, 
				new String[]{TOUR_NAME,TOUR_POINT_COMPANY_ID,TOUR_POINT_POSITION,TOUR_POINT_TOUR_ID},
				where, null, null, orderBy, null);
		
		
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			tourPointList.add(cursorToTourPoint(cursor, db));
			cursor.moveToNext();
		}
		cursor.close();
		return tourPointList;
	}
	

}
