package de.dhbw.studientag.dbHelpers;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.dhbw.studientag.ImportDataController;
import de.dhbw.studientag.model.Color;
import de.dhbw.studientag.model.Faculty;
import de.dhbw.studientag.model.Subject;

public final class SubjectsHelper  {


	protected static final String SUBJECTS_TABLE_NAME ="Subjects";
	protected static final String SUBJECT_NAME = "name";
	protected static final String FACULTY_ID = "facultyId";
	protected static final String SUBJECT_WEBADDRESS ="webAddress";
	protected static final String SUBJECT_COLOR="color";
	protected static final String SUBJECTS_TABLE_CREATE=
			"CREATE TABLE " + SUBJECTS_TABLE_NAME + " ( " +
			MySQLiteHelper.ID 	+ " integer primary key autoincrement, " +
			SUBJECT_NAME + " TEXT unique, " +
			SUBJECT_WEBADDRESS + " TEXT, "+
			FACULTY_ID + " INTEGER, "  +
			SUBJECT_COLOR+ " INTEGER"+
			")";
	protected static final String[] SUBJECT_ALL_COLUMNS ={
		MySQLiteHelper.ID, SUBJECT_NAME, FACULTY_ID, SUBJECT_WEBADDRESS, SUBJECT_COLOR
	};
	
	protected static final void initSubjects(SQLiteDatabase db){
		for(Subject subject : ImportDataController.getSubjects())
			initSubject(subject,db);
	}
	
	private static final void initSubject(Subject subject, SQLiteDatabase db){
		ContentValues values= new ContentValues();
		values.put(SUBJECT_NAME, subject.getName());
		values.put(FACULTY_ID, 
				subject.getFaculty().getId());
		values.put(SUBJECT_WEBADDRESS, subject.getWebAddress());
		values.put(SUBJECT_COLOR, subject.getColor().getColor());
		db.insert(SUBJECTS_TABLE_NAME, null, values);
				
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
	
//	public List<Subject> getAllSubjects(SQLiteDatabase database){
//	    Cursor cursor = database.query(SUBJECTS_TABLE_NAME,
//		        SUBJECT_ALL_COLUMNS, null, null, null, null, null);
//
//	    List<Subject> subjects = cursorToSubjectList(cursor);
//	    // make sure to close the cursor
//	    cursor.close();
//		return subjects;
//	}
	

	
	private static Subject cursorToSubject(Cursor cursor){
//		Log.d("id" ,cursor.getColumnNames().toString());
		Color color = new Color();
		color.setColor(cursor.getInt(cursor.getColumnIndex(SUBJECT_COLOR)));
		Subject subject = new Subject(
				cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.ID)),
				cursor.getString(cursor.getColumnIndex(SUBJECT_NAME)),
				Faculty.getById(cursor.getInt(cursor.getColumnIndex(FACULTY_ID))),
				cursor.getString(cursor.getColumnIndex(SUBJECT_WEBADDRESS)),
				color
				);
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
