package de.dhbw.studientag;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.db.CommentHelper;
import de.dhbw.studientag.model.db.MySQLiteHelper;

public class CommentActivity extends Activity {

	private Company company;
	private EditText comment;
	MySQLiteHelper dbHelper = new MySQLiteHelper(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		comment = (EditText) findViewById(R.id.editText_comment);
		company =(Company) getIntent().getParcelableExtra("company");
		
		TextView companyName = (TextView) findViewById(R.id.textView_commentCompanyName);
		companyName.setText(company.getName());
		comment.setText(CommentHelper.getCommentByIdCompanyId(company.getId(), dbHelper.getReadableDatabase()));
		comment.setSelection(comment.getText().length());
		dbHelper.close();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
		
		
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.comment, menu);
		return true;
	}
	

	public void saveComment(View view){
		
		saveComment();
		finish();
	}
	
	public void saveComment(){
		Editable message = comment.getText();
//		Log.i("Save Comment",message.toString());
		if(message.length()>0)
			CommentHelper.insertComment(company.getId(), message.toString(), dbHelper.getWritableDatabase());
		else
			CommentHelper.deleteComment(company.getId(), dbHelper.getWritableDatabase());
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);
		dbHelper.close();
	}
	@Override
	protected void onPause() {
		dbHelper.close();
		
		saveComment();
		super.onPause();
	}
	

	


}
