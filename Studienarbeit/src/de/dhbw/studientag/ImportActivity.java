package de.dhbw.studientag;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import de.dhbw.studientag.dbHelpers.CompanyHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.dbHelpers.TourHelper;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.TourPoint;

public class ImportActivity extends Activity {

	private NfcAdapter myNfcAdapter;
	public static final String MIME_TEXT_PLAIN = "text/plain";

	public static ImportActivity newInstance(String param1, String param2) {
		ImportActivity fragment = new ImportActivity();

		return fragment;
	}

	public ImportActivity() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import);
		myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		TextView mTextView = (TextView) findViewById(R.id.textView_importActivity_large);
		if (myNfcAdapter == null) {
			// Stop here, we definitely need NFC
			Toast.makeText(this, getString(R.string.label_no_nfc_support), Toast.LENGTH_LONG)
					.show();
			finish();
			return;

		}
		TextView waitForTag = (TextView) findViewById(R.id.textView_waitForTag);
		if (!myNfcAdapter.isEnabled()) {
			mTextView.setText(getString(R.string.label_nfc_disabled));
			ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar_importActivity);
			waitForTag.setText(getString(R.string.label_activate_nfc));
			progressBar.setVisibility(View.INVISIBLE);
		} else {
			mTextView.setText(getString(R.string.label_nfc_enabled));
			
			waitForTag.setText(getString(R.string.label_wait_for_Tag));
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		/**
		 * This method gets called, when a new Intent gets associated with the
		 * current activity instance. Instead of creating a new activity,
		 * onNewIntent will be called. For more information have a look at the
		 * documentation.
		 * 
		 * In our case this method gets called, when the user attaches a Tag to
		 * the device.
		 */
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

			String type = intent.getType();
			if (MIME_TEXT_PLAIN.equals(type)) {

				Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				new NdefReaderTask().execute(tag);

			} else {
				Log.d("ImportActivity", "Wrong mime type: " + type);
			}
		} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

			// In case we would still use the Tech Discovered Intent
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			String[] techList = tag.getTechList();
			String searchedTech = Ndef.class.getName();

			for (String tech : techList) {
				if (searchedTech.equals(tech)) {
					new NdefReaderTask().execute(tag);
					break;
				}
			}
		}
	}

	@Override
	protected void onPause() {
		/**
		 * Call this before onPause, otherwise an IllegalArgumentException is
		 * thrown as well.
		 */
		stopForegroundDispatch(this, myNfcAdapter);

		super.onPause();
	}

	@Override
	protected void onResume() {
		setupForegroundDispatch(this, myNfcAdapter);
		super.onResume();
	}

	public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
		final Intent intent = new Intent(activity.getApplicationContext(),
				activity.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		final PendingIntent pendingIntent = PendingIntent.getActivity(
				activity.getApplicationContext(), 0, intent, 0);

		IntentFilter[] filters = new IntentFilter[1];
		String[][] techList = new String[][] {};

		// Notice that this is the same filter as in our manifest.
		filters[0] = new IntentFilter();
		filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
		filters[0].addCategory(Intent.CATEGORY_DEFAULT);
		try {
			filters[0].addDataType(MIME_TEXT_PLAIN);
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("Check your mime type.");
		}

		adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
	}

	public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
		adapter.disableForegroundDispatch(activity);
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		return super.onCreateView(name, context, attrs);
	}

	private boolean processCompanyTourList(String rawData) {
		MySQLiteHelper dbHelper = new MySQLiteHelper(this);

		String[] tourDetails = rawData.trim().split(":");
		try {
			String tourName = tourDetails[0].trim();
			String[] companyIds = tourDetails[1].trim().split(",");
			tourName = TourHelper.getFreeTourName(dbHelper.getReadableDatabase(),
					tourName);
			long tourId = TourHelper.insertTour(dbHelper.getWritableDatabase(), tourName);
			try {
				for (String companyId : companyIds) {

					long companyIdL = Long.valueOf(companyId.trim());
					Company company = CompanyHelper.getCompanyById(
							dbHelper.getReadableDatabase(), companyIdL);
					if (company != null) {
						TourPoint tourPoint = new TourPoint(company);
						TourHelper.insertTourPoint(dbHelper.getWritableDatabase(),
								tourPoint, tourId);
					}else{
						Log.e("ImportActivity", "no company found by valid company Id");
						return false;
					}
					
				}
			} catch (NumberFormatException nfEx) {
				Log.e("ImportActivity", "companyId could not parsed to Long", nfEx);
				TourHelper.deleteTourById(dbHelper.getWritableDatabase(), tourId);
				return false;
			} finally {
				dbHelper.close();
			}
			return true;
		} catch (ArrayIndexOutOfBoundsException ex) {
			Log.e("ImportActivity", "wrong seperator", ex);
			return false;
		}
		
	}

	private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

		@Override
		protected String doInBackground(Tag... params) {
			Tag tag = params[0];

			Ndef ndef = Ndef.get(tag);
			if (ndef == null) {
				// NDEF is not supported by this Tag.
				return null;
			}

			NdefMessage ndefMessage = ndef.getCachedNdefMessage();

			NdefRecord[] records = ndefMessage.getRecords();
			for (NdefRecord ndefRecord : records) {
				if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN
						&& Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
					try {
						return readText(ndefRecord);
					} catch (UnsupportedEncodingException e) {
						Log.e("importActivty", "Unsupported Encoding", e);
					}
				}
			}

			return null;
		}

		private String readText(NdefRecord record) throws UnsupportedEncodingException {
			/*
			 * See NFC forum specification for "Text Record Type Definition" at
			 * 3.2.1
			 * 
			 * http://www.nfc-forum.org/specs/
			 * 
			 * bit_7 defines encoding bit_6 reserved for future use, must be 0
			 * bit_5..0 length of IANA language code
			 */

			byte[] payload = record.getPayload();

			// Get the Text Encoding
			String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

			// Get the Language Code
			int languageCodeLength = payload[0] & 0063;

			// String languageCode = new String(payload, 1, languageCodeLength,
			// "US-ASCII");
			// e.g. "en"

			// Get the Text
			return new String(payload, languageCodeLength + 1, payload.length
					- languageCodeLength - 1, textEncoding);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				Log.v("ImportActivity", "Read content: " + result);
			}
			if(processCompanyTourList(result)){
				Toast.makeText(getApplicationContext(), "Tour erfolgreich importiert", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(getApplicationContext(), TourActivity.class);
				startActivity(intent);
			}
		}
	}

}
