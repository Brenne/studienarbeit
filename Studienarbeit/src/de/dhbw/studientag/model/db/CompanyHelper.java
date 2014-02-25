package de.dhbw.studientag.model.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.dhbw.studientag.TestData;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Subject;

public final class CompanyHelper {
	
	protected static final String COMPANY_TABLE_NAME ="Company";
	protected static final String COMPANY_NAME = "name";
	protected static final String COMPANY_WEBSITE ="website";
	protected static final String COMPANY_CITY = "city";
	protected static final String COMPANY_STREET ="street";
	protected static final String COMPANY_PLZ= "plz";
	public static final String[] COMPANY_ALL_COLUMNS={
		MySQLiteHelper.ID, COMPANY_NAME, COMPANY_CITY, COMPANY_PLZ, COMPANY_STREET,
		COMPANY_WEBSITE
		
	};
	
	protected static final String COMPANY_TABLE_CREATE = 
			"CREATE TABLE " + COMPANY_TABLE_NAME + " (" +
			MySQLiteHelper.ID 	+ " integer primary key autoincrement," +
			COMPANY_NAME	 + " TEXT, " +
			COMPANY_WEBSITE	 + " TEXT, " +
			COMPANY_CITY	 + " TEXT, " +
			COMPANY_STREET	 + " TEXT, " +
			COMPANY_PLZ		 + " TEXT "  +
			")";
	

	

	
	protected static final void initCompanies(SQLiteDatabase db){
		ArrayList<Company> companies = TestData.getCompanies();
		for(Company company : companies)
			initCompany(company,db);
	}
	
	private static final void initCompany(Company company, SQLiteDatabase db){
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
	            null, null, COMPANY_NAME + " ASC");
	    return cursor;
	}
	
	public static Company getCompanyById(SQLiteDatabase database, long id){
	    Cursor cursor = database.query(COMPANY_TABLE_NAME,
	    		COMPANY_ALL_COLUMNS, MySQLiteHelper.ID + " = " + id, null,
	            null, null, null);
	    cursor.moveToFirst();
	    Company company = cursorToCompany(cursor);
	    cursor.close();
	    return company;
	}
	
	public static List<Company> getAllCompanies(SQLiteDatabase database){
		List<Company> companies = new ArrayList<Company>();
	    Cursor cursor = database.query(COMPANY_TABLE_NAME,
		        COMPANY_ALL_COLUMNS, null, null, null, null, COMPANY_NAME + " ASC ");

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
	
	public static List<Company> getAllCompaniesBySubject(SQLiteDatabase database, Subject subject){
		List<Company> companies = new ArrayList<Company>();
		//Select name  from company c, subjects s WHERE s.companyId == c._id 
	    Cursor cursor = database.rawQuery("SELECT * " + 
		" FROM company c  INNER JOIN offeredsubjects os ON c._id=os.companyId AND os.subjectID=? ORDER BY c.name ASC", 
	    		new String[]{Long.toString(subject.getId())});

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
