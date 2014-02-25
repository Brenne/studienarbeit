package de.dhbw.studientag;

import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.db.CommentHelper;
import de.dhbw.studientag.model.db.MySQLiteHelper;

public class CommentsActivity extends ListActivity {

	private final static String COMPANY_KEY = "company";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comments);
		// Show the Up button in the action bar.
		setupActionBar();
		
		setListAdapter();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Map<?, ?> selectedComment =  (Map<?, ?>) getListAdapter().getItem(position);
		if(selectedComment.containsKey(COMPANY_KEY)){
			Company selectedCompany = (Company) selectedComment.get(COMPANY_KEY);
			Intent intent = new Intent(this, CommentActivity.class);
			intent.putExtra(COMPANY_KEY, selectedCompany);
			startActivity(intent);
		}

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setListAdapter();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.comments, menu);
		return true;
	}
	
	private void setListAdapter(){
		MySQLiteHelper dbHelper = new MySQLiteHelper(this);
		List<Map<String, Object>> comments = CommentHelper.getAllComments(dbHelper.getReadableDatabase());
		
		SimpleAdapter adapter = new SimpleAdapter(this, comments, android.R.layout.simple_list_item_2, 
				new String[] {COMPANY_KEY, "message"}, 
				new int[]{android.R.id.text1, android.R.id.text2});
		setListAdapter(adapter);
		dbHelper.close();
	}



}
