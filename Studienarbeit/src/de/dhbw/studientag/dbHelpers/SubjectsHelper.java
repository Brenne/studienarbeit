package de.dhbw.studientag.dbHelpers;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.dhbw.studientag.TestData;
import de.dhbw.studientag.model.Faculty;
import de.dhbw.studientag.model.Subject;

public final class SubjectsHelper  {


	protected static final String SUBJECTS_TABLE_NAME ="Subjects";
	protected static final String SUBJECT_NAME = "name";
	protected static final String FACULTY_ID = "facultyId";
	protected static final String SUBJECTS_TABLE_CREATE=
			"CREATE TABLE " + SUBJECTS_TABLE_NAME + " ( " +
			MySQLiteHelper.ID 	+ " integer primary key autoincrement, " +
			SUBJECT_NAME + " TEXT unique, " +
			FACULTY_ID + " INTEGER"  +
			")";
	protected static final String[] SUBJECT_ALL_COLUMNS ={
		MySQLiteHelper.ID, SUBJECT_NAME, FACULTY_ID
	};
	
	
	
	protected static final void initSubjects(SQLiteDatabase db){
		for(Subject subject : TestData.getSubjects())
			initSubject(subject,db);
	}
	
	private static final void initSubject(Subject subject, SQLiteDatabase db){
		ContentValues values= new ContentValues();
		values.put(SUBJECT_NAME, subject.getName());
		values.put(FACULTY_ID, 
				subject.getFaculty().getId());

		db.insert(SUBJECTS_TABLE_NAME, null, values);
				
	}
	public Cursor getCursor(SQLiteDatabase database, String[] columns){
	    Cursor cursor = database.query(SUBJECTS_TABLE_NAME,
	    		columns, null, null,
	            null, null, SUBJECT_NAME + " ASC");
	    return cursor;
	}
	
	public static Subject getSubjectById(SQLiteDatabase database, long id){
	    Cursor cursor = database.query(SUBJECTS_TABLE_NAME,
	    		SUBJECT_ALL_COLUMNS, MySQLiteHelper.ID + " = " + id, null,
	            null, null, SUBJECT_NAME + " ASC");
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
	
	public static List<Subject> getSubjectsByFaculty(SQLiteDatabase database, Faculty faculty){
		
		int facultyId = faculty.getId();
		Cursor cursor = database.query(SUBJECTS_TABLE_NAME,
		        SUBJECT_ALL_COLUMNS, FACULTY_ID + " = "+ facultyId, null, null, null, SUBJECT_NAME + " ASC");


	    List<Subject> subjects = cursorToSubjectList(cursor);
	    // make sure to close the cursor
	    cursor.close();
		return subjects;
	}
	
	public List<Subject> getAllSubjects(SQLiteDatabase database){
	    Cursor cursor = database.query(SUBJECTS_TABLE_NAME,
		        SUBJECT_ALL_COLUMNS, null, null, null, null, null);

	    List<Subject> subjects = cursorToSubjectList(cursor);
	    // make sure to close the cursor
	    cursor.close();
		return subjects;
	}
	

	
	private static Subject cursorToSubject(Cursor cursor){
//		Log.d("id" ,cursor.getColumnNames().toString());
		int col = cursor.getColumnIndex(SUBJECT_NAME);
		Subject subject = new Subject(
				cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.ID)),
				cursor.getString(col),
				Faculty.getById(cursor.getInt(cursor.getColumnIndex(FACULTY_ID))));
		return subject;
				
	}
	
	private static List<Subject> cursorToSubjectList(Cursor cursor){
	    List<Subject> subjects = new ArrayList<Subject>();
		cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Subject subject = cursorToSubject(cursor);
	      subjects.add(subject);
	      cursor.moveToNext();
	    }
	    return subjects;
	}




}
