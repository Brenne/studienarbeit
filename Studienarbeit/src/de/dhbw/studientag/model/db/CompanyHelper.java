package de.dhbw.studientag.model.db;

import java.util.ArrayList;
import java.util.List;

import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.TestData;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public final class CompanyHelper extends MySQLiteHelper {
	
	protected static final String COMPANY_TABLE_NAME ="Company";
	protected static final String COMPANY_NAME = "name";
	protected static final String COMPANY_WEBSITE ="website";
	protected static final String COMPANY_CITY = "city";
	protected static final String COMPANY_STREET ="street";
	protected static final String COMPANY_PLZ= "plz";
	public static final String[] COMPANY_ALL_COLUMNS={
		ID, COMPANY_NAME, COMPANY_CITY, COMPANY_PLZ, COMPANY_STREET,
		COMPANY_WEBSITE
		
	};
	
	protected static final String COMPANY_TABLE_CREATE = 
			"CREATE TABLE " + COMPANY_TABLE_NAME + " (" +
			ID 	+ " integer primary key autoincrement," +
			COMPANY_NAME	 + " TEXT, " +
			COMPANY_WEBSITE	 + " TEXT, " +
			COMPANY_CITY	 + " TEXT, " +
			COMPANY_STREET	 + " TEXT, " +
			COMPANY_PLZ		 + " TEXT "  +
			")";
	

	private ContentValues values;
	
	
	public CompanyHelper(Context context) {
		super(context);


	}

	
	protected final void initCompanies(SQLiteDatabase db){
		ArrayList<Company> companies = testData.getCompanies();
		for(Company company : companies)
			initCompany(company,db);
	}
	
	private final void initCompany(Company company, SQLiteDatabase db){
		ContentValues values= new ContentValues();
		values.put(COMPANY_NAME, company.getName());
		values.put(COMPANY_CITY, company.getCity());
		values.put(COMPANY_PLZ, company.getPlz());
		values.put(COMPANY_STREET, company.getStreet());
		values.put(COMPANY_WEBSITE, company.getWebiste());
		long id = db.insert(COMPANY_TABLE_NAME, null, values);
		company.setId(id);
		OfferedSubjectsHelper.initOfferedSubjects(company, db);
	}
	public Cursor getCursor(SQLiteDatabase database, String[] columns){
	    Cursor cursor = database.query(COMPANY_TABLE_NAME,
	    		columns, null, null,
	            null, null, null);
	    return cursor;
	}
	
	public static Company getCompanyById(SQLiteDatabase database, long id){
	    Cursor cursor = database.query(COMPANY_TABLE_NAME,
	    		COMPANY_ALL_COLUMNS, ID + " = " + id, null,
	            null, null, null);
	    cursor.moveToFirst();
	    Company company = cursorToCompany(cursor);
	    cursor.close();
	    return company;
	}
	
	public List<Company> getAllCompanies(SQLiteDatabase database){
		List<Company> companies = new ArrayList<Company>();
	    Cursor cursor = database.query(COMPANY_TABLE_NAME,
		        COMPANY_ALL_COLUMNS, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Company company = cursorToCompany(cursor);
	      company.setSubjectList(OfferedSubjectsHelper.getOfferdSubjectsByCompanyId(company.getId(), database));
	      companies.add(company);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
		return companies;
	}
	
	public List<String> getAllCompanyNames(SQLiteDatabase database){
		List<String> companyNames = new ArrayList<String>();
		for(Company company : getAllCompanies(database)){
			companyNames.add(company.getName());
		}
		return companyNames;
	}
	
	private static Company cursorToCompany(Cursor cursor){
//		Log.d("id" ,cursor.getColumnNames().toString());
		Company company = new Company(
				cursor.getInt(0),
				cursor.getString(cursor.getColumnIndex(COMPANY_NAME)),
				cursor.getString(cursor.getColumnIndex(COMPANY_STREET)),
				cursor.getString(cursor.getColumnIndex(COMPANY_CITY)),
				cursor.getString(cursor.getColumnIndex(COMPANY_PLZ)),
				cursor.getString(cursor.getColumnIndex(COMPANY_WEBSITE)));
		return company;
				
	}

}
