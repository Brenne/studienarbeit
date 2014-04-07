package de.dhbw.studientag;

import android.app.Activity;
import android.os.Bundle;

public class SoftwareLicensesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_software_licenses);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		SoftwareLicenseFragment fragment = (SoftwareLicenseFragment) getFragmentManager()
				.findFragmentById(R.id.fragment_software_licences);
		fragment.printLicenses(this);
	}

}
