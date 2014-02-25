package de.dhbw.studientag.model.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Subject;

public final class OfferedSubjectsHelper  {
	
	protected static final String OFFERED_SUBJECTS_TABLE_NAME ="OfferedSubjects";
	protected static final String COMPANY_ID = "companyId";
	protected static final String SUBJECT_ID = "subjectId";
	protected static final String[] OFFERED_SUBJECTS_ALL_COLUMNS ={
		COMPANY_ID , SUBJECT_ID
	};
	
	protected static final String OFFERED_SUBJECTS_TABLE_CREATE=
			"CREATE TABLE " + OFFERED_SUBJECTS_TABLE_NAME + " ( " +
			COMPANY_ID + " INTEGER, " +
			SUBJECT_ID + " INTEGER"  +
			")";
	
	
	protected static final void initOfferedSubjects(Company company, SQLiteDatabase db){
		
		ArrayList<Subject> offeredSubjects = company.getSubjectList();
		for(Subject subject : offeredSubjects){
			ContentValues values= new ContentValues();
			values.put(COMPANY_ID, company.getId());
			Subject subjectbyname = SubjectsHelper.getSubjectByName(db, 
					subject.getName());
			values.put(SUBJECT_ID, subjectbyname.getId());
			db.insert(OFFERED_SUBJECTS_TABLE_NAME, null, values);			
		}
	
				
	}
	
	protected static ArrayList<Subject> getOfferdSubjectsByCompanyId(long companyId, SQLiteDatabase db){
		ArrayList<Subject> offeredSubjects = new ArrayList<Subject>();
		String query = "SELECT "+SUBJECT_ID + ", " + SubjectsHelper.SUBJECT_NAME +" FROM "+
					OFFERED_SUBJECTS_TABLE_NAME + " os INNER JOIN " + SubjectsHelper.SUBJECTS_TABLE_NAME + " s ON "
					+"s."+MySQLiteHelper.ID+"=os."+SUBJECT_ID + " WHERE os."+
				COMPANY_ID +"=? ORDER BY s."+SubjectsHelper.SUBJECT_NAME+" ASC";

		Cursor cursor = db.rawQuery(query, new String[]{Long.toString(companyId)});

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	int subjectId = cursor.getInt(cursor.getColumnIndex(SUBJECT_ID));
	    	Subject subject = SubjectsHelper.getSubjectById(db, subjectId);
	    	offeredSubjects.add(subject);
	    	cursor.moveToNext();
	    }
	    cursor.close();
		return offeredSubjects;
		
	}
	

}
