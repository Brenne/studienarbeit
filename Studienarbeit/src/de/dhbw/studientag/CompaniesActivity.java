package de.dhbw.studientag;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import de.dhbw.studientag.model.Company;

public class CompaniesActivity extends Activity implements CompaniesFragment.OnCompanySelectedLitener {
	
	protected static final String TITLE = "title";
	protected static final String COMPANIES = "companies";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_companies);
		String title = (String) getIntent().getCharSequenceExtra(TITLE);
		if (title == null)
			title = getString(R.string.label_companies);
		setTitle(title);
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		List<Company> companyList = getIntent().getParcelableArrayListExtra(COMPANIES);
		if(companyList.isEmpty()){
			//no Companies activity without companies to display
			finish();
		}
		Fragment companiesFragment = CompaniesFragment.newCompaniesFragmentInstance((ArrayList<Company>) companyList);

		transaction.add(R.id.companies_fragment, companiesFragment); 
		transaction.commit();
		
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
	}

	@Override
	public void onCompanySelected(Company selectedCompany) {
		Intent intent = new Intent(this, CompanyActivity.class);
		intent.putExtra(CompanyActivity.COMPANY, selectedCompany);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		
	}

}
