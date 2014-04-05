package de.dhbw.studientag.dbHelpers;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import de.dhbw.studientag.model.Comment;
import de.dhbw.studientag.model.Company;

public class CommentHelper {

	private static final String COMMENT_TABLE_NAME = "Comment";
	private static final String COMMENT_COMPANY_ID = "companyId";
	public static final String COMMENT_MESSAGE = "message";

	private static final String[] COMMENT_ALL_COLUMNS = { MySQLiteHelper.ID,
			COMMENT_COMPANY_ID, COMMENT_MESSAGE };

	protected static final String COMMENT_TABLE_CREATE = "CREATE TABLE "
			+ COMMENT_TABLE_NAME + "( " + MySQLiteHelper.ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + COMMENT_COMPANY_ID
			+ " INTEGER REFERENCES " + CompanyHelper.COMPANY_TABLE_NAME + "("
			+ MySQLiteHelper.ID + ")," + COMMENT_MESSAGE + " TEXT " + ")";

	public static void insertComment(long companyId, String message, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put(COMMENT_COMPANY_ID, companyId);
		values.put(COMMENT_MESSAGE, message);
		if (CommentHelper.commentForCompanyExist(companyId, db))
			db.update(COMMENT_TABLE_NAME, values, COMMENT_COMPANY_ID + "=?",
					new String[] { Long.toString(companyId) });
		else
			db.insert(COMMENT_TABLE_NAME, null, values);
	}

	public static void deleteComment(long companyId, SQLiteDatabase db) {
		db.delete(COMMENT_TABLE_NAME,
				COMMENT_COMPANY_ID + "=" + Long.toString(companyId), null);
	}

	public static Comment getCommentByIdCompanyId(long companyId, SQLiteDatabase db) {
		Cursor cursor = db.query(COMMENT_TABLE_NAME, COMMENT_ALL_COLUMNS,
				COMMENT_COMPANY_ID + "=?", new String[] { Long.toString(companyId) },
				null, null, null);
		Comment comment = null;
		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			long commentId = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.ID));
			Company company = CompanyHelper.getCompanyById(db, companyId);
			String message = cursor.getString(cursor.getColumnIndex(COMMENT_MESSAGE));
			comment = new Comment(commentId,company,message);
					
		}
		cursor.close();
		return comment;
	}

	public static boolean commentForCompanyExist(long companyId, SQLiteDatabase db) {
		long comments = DatabaseUtils.queryNumEntries(db, COMMENT_TABLE_NAME,
				COMMENT_COMPANY_ID + "=" + companyId);
		if (comments > 0)
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @param db
	 * @return List of Map<String,Object>. Each map has two entries one key is
	 *         "company" the other "message"
	 */
	public static List<Comment> getAllComments(SQLiteDatabase db) {
		// SELECT message, companyId FROM Comment INNER JOIN Company ON
		// _id=companyID ;
		List<Comment> comments = new LinkedList<Comment>();
		// Cursor cursor =
		// db.rawQuery("SELECT "+COMMENT_MESSAGE+", "+COMMENT_COMPANY_ID+" FROM "+
		// COMMENT_TABLE_NAME +" INNER JOIN "+CompanyHelper.COMPANY_TABLE_NAME
		// +" com ON com."+
		// MySQLiteHelper.ID+"="+COMMENT_COMPANY_ID, null);
		Cursor cursor = db.query(COMMENT_TABLE_NAME, COMMENT_ALL_COLUMNS, null, null,
				null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			long companyId = cursor.getLong(cursor.getColumnIndex(COMMENT_COMPANY_ID));
			Company company = CompanyHelper.getCompanyById(db, companyId);
			String message = cursor.getString(cursor.getColumnIndex(COMMENT_MESSAGE));
			long commentId = cursor.getLong(0);
			Comment comment = new Comment(commentId, company, message);
			comments.add(comment);
			cursor.moveToNext();
		}
		cursor.close();
		return comments;

	}

}
