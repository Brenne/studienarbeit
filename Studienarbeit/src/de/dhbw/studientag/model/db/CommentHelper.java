package de.dhbw.studientag.model.db;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

public class CommentHelper {

	private static final String COMMENT_TABLE_NAME = "Comment";
	private static final String COMMENT_COMPANY_ID = "companyId";
	private static final String COMMENT_MESSAGE = "message";

	private static final String[] COMMENT_ALL_COLUMNS = { MySQLiteHelper.ID,
			COMMENT_COMPANY_ID, COMMENT_MESSAGE };

	protected static final String COMMENT_TABLE_CREATE = "CREATE TABLE "
			+ COMMENT_TABLE_NAME + "( " + MySQLiteHelper.ID
			+ " integer primary key autoincrement," + COMMENT_COMPANY_ID
			+ " INTEGER unique," + COMMENT_MESSAGE + " TEXT " + ")";

	public static void insertComment(long companyId, String message,
			SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put(COMMENT_COMPANY_ID, companyId);
		values.put(COMMENT_MESSAGE, message);
		if (CommentHelper.commentsForCompanyExist(companyId, db))
			db.update(COMMENT_TABLE_NAME, values, COMMENT_COMPANY_ID + "=?",
					new String[] { Long.toString(companyId) });
		else
			db.insert(COMMENT_TABLE_NAME, null, values);
	}
	
	public static void deleteComment(long companyId, SQLiteDatabase db){
		db.delete(COMMENT_TABLE_NAME, COMMENT_COMPANY_ID+"="+Long.toString(companyId), null);
	}

	public static String getCommentByIdCompanyId(long companyId,
			SQLiteDatabase db) {
		Cursor cursor = db.query(COMMENT_TABLE_NAME,
				new String[] { COMMENT_MESSAGE }, COMMENT_COMPANY_ID + "=?",
				new String[] { Long.toString(companyId) }, null, null, null);
		String message = "";
		if (cursor.getCount() == 1) {
			cursor.moveToFirst();

			message = cursor.getString(0);
		}
		cursor.close();
		return message;
	}

	public static boolean commentsForCompanyExist(long companyId,
			SQLiteDatabase db) {
		long comments = DatabaseUtils.queryNumEntries(db, COMMENT_TABLE_NAME,
				COMMENT_COMPANY_ID + "=" + companyId);
		if (comments > 0)
			return true;
		else
			return false;
	}
	
	public static List<Map<String, Object>> getAllComments(SQLiteDatabase db){
		//SELECT message, companyId FROM Comment INNER JOIN Company  ON _id=companyID ;
		List<Map<String, Object>> comments = new LinkedList<Map<String, Object>>();
		Cursor cursor = db.rawQuery("SELECT "+COMMENT_MESSAGE+", "+COMMENT_COMPANY_ID+" FROM "+
				COMMENT_TABLE_NAME +" INNER JOIN "+CompanyHelper.COMPANY_TABLE_NAME +" com ON com."+
				MySQLiteHelper.ID+"="+COMMENT_COMPANY_ID, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Map<String, Object> comment = new HashMap<String, Object>();
			long companyId = cursor.getLong(cursor.getColumnIndex(COMMENT_COMPANY_ID));
			String message = cursor.getString(cursor.getColumnIndex(COMMENT_MESSAGE));
			message = shortenMessage(message);
			comment.put("company", CompanyHelper.getCompanyById(db, companyId));
			comment.put("message", message);
			comments.add(comment);
			cursor.moveToNext();
		}
		cursor.close();
		return comments;
		
	}
	
	private static String shortenMessage(String message){
		final int LIMIT = 100;
		if(message.length()<LIMIT){
			return message;
		}else{
			return message.substring(0,LIMIT).concat("...");
		}
	}

}
