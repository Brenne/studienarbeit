package de.dhbw.studientag.model.db;

import java.util.ArrayList;
import java.util.List;

import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Faculty;
import de.dhbw.studientag.model.Subject;
import de.dhbw.studientag.model.TestData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public final class SubjectsHelper extends MySQLiteHelper {


	protected static final String SUBJECTS_TABLE_NAME ="Subjects";
	protected static final String SUBJECT_NAME = "name";
	protected static final String FACULTY_ID = "facultyId";
	protected static final String SUBJECTS_TABLE_CREATE=
			"CREATE TABLE " + SUBJECTS_TABLE_NAME + " ( " +
			ID 	+ " integer primary key autoincrement, " +
			SUBJECT_NAME + " TEXT unique, " +
			FACULTY_ID + " INTEGER"  +
			")";
	protected static final String[] SUBJECT_ALL_COLUMNS ={
		ID, SUBJECT_NAME, FACULTY_ID
	};
	
	public SubjectsHelper(Context context) {
		super(context);
		
	}
	
	protected final void initSubjects(SQLiteDatabase db){
		for(Subject subject : testData.getSubjects())
			initSubject(subject,db);
	}
	
	private final void initSubject(Subject subject, SQLiteDatabase db){
		ContentValues values= new ContentValues();
		values.put(SUBJECT_NAME, subject.getName());
		values.put(FACULTY_ID, 
				subject.getFaculty().getId());

		db.insert(SUBJECTS_TABLE_NAME, null, values);
				
	}
	public Cursor getCursor(SQLiteDatabase database, String[] columns){
	    Cursor cursor = database.query(SUBJECTS_TABLE_NAME,
	    		columns, null, null,
	            null, null, null);
	    return cursor;
	}
	
	public static Subject getSubjectById(SQLiteDatabase database, long id){
	    Cursor cursor = database.query(SUBJECTS_TABLE_NAME,
	    		SUBJECT_ALL_COLUMNS, ID + " = " + id, null,
	            null, null, null);
	    cursor.moveToFirst();
	    Subject subject = cursorToSubject(cursor);
	    cursor.close();
	    return subject;
	}
	
	public static Subject getSubjectByName(SQLiteDatabase database, String name){
	    Cursor cursor = database.query(SUBJECTS_TABLE_NAME,
	    		SUBJECT_ALL_COLUMNS, SUBJECT_NAME + " = ?",new String[]{ name},
	            null, null, null);
	    cursor.moveToFirst();
	    Subject subject = cursorToSubject(cursor);
	    cursor.close();
	    return subject;
	}
	
	public List<Subject> getAllSubjects(SQLiteDatabase database){
		List<Subject> subjects = new ArrayList<Subject>();
	    Cursor cursor = database.query(SUBJECTS_TABLE_NAME,
		        SUBJECT_ALL_COLUMNS, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Subject subject = cursorToSubject(cursor);
	      subjects.add(subject);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
		return subjects;
	}
	
//	public List<String> getAllCompanyNames(SQLiteDatabase database){
//		List<String> companyNames = new ArrayList<String>();
//		for(Company company : getAllCompanies(database)){
//			companyNames.add(company.getName());
//		}
//		return companyNames;
//	}
	
	private static Subject cursorToSubject(Cursor cursor){
//		Log.d("id" ,cursor.getColumnNames().toString());
		int col = cursor.getColumnIndex(SUBJECT_NAME);
		Subject subject = new Subject(
				cursor.getLong(cursor.getColumnIndex(ID)),
				cursor.getString(col),
				Faculty.getById(cursor.getInt(cursor.getColumnIndex(FACULTY_ID))));
		return subject;
				
	}




}
