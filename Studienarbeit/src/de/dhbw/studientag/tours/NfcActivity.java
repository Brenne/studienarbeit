package de.dhbw.studientag.tours;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import de.dhbw.studientag.LocationServiceActivity;
import de.dhbw.studientag.R;
import de.dhbw.studientag.dbHelpers.CompanyHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.dbHelpers.TourHelper;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Tour;
import de.dhbw.studientag.model.TourPoint;

public class NfcActivity extends LocationServiceActivity {

	private NfcAdapter myNfcAdapter;
	public static final String MIME_TEXT_PLAIN = "text/plain";
	private static final String TAG = "NFC Im-/Export";
	protected static final String EXPORT = "ExportTour";
	
	public NfcActivity() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		initLocationService();
		super.onCreate(savedInstanceState);
		if (getIntent().hasExtra(EXPORT)) {
			Log.v(TAG, "intent has Export");
			setTitle(R.string.title_export_tour);
		}else{
			requestUpdates(true);
			
		}
		
		setContentView(R.layout.activity_import);
		myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		TextView mTextView = (TextView) findViewById(R.id.textView_importActivity_large);
		if (myNfcAdapter == null) {
			// Stop here, we definitely need NFC
			Toast.makeText(this, getString(R.string.label_no_nfc_support),
					Toast.LENGTH_LONG).show();
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
		if(getIntent().hasExtra(EXPORT)){
			intent.putExtra(EXPORT, getIntent().getStringExtra(EXPORT));
		}else{
			requestUpdates(true);
		}
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		
		String type = intent.getType();
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if (intent.hasExtra(EXPORT)) {
			String exportString = getIntent().getStringExtra(EXPORT);
			if (writeTextToNfcTag(tag, exportString)) {
				Toast.makeText(this,
						getString(R.string.label_tour_export_successfull),
						Toast.LENGTH_LONG).show();
				Intent tourActivity = new Intent(this,
						TourActivity.class);
				startActivity(tourActivity);
			}else{
				Toast.makeText(this,
						getString(R.string.label_write_to_nfc_fail),
						Toast.LENGTH_LONG).show();
			}

		} else if (MIME_TEXT_PLAIN.equals(type)
				&& NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

			new NdefReaderTask().execute(tag);

		} else {
			Log.d(TAG, "Wrong mime type: " + type);
		}

	}
	
	//	Export
	private boolean writeTextToNfcTag(Tag tag, String message) {
		Ndef ndef = Ndef.get(tag);
		NdefMessage newNdefMessage = new NdefMessage(createTextRecord(message,
				Locale.GERMAN, true));
		if (ndef == null) {
			return false;
		}
		try {
			ndef.connect();
			if(ndef.isWritable()){
				ndef.writeNdefMessage(newNdefMessage);
				return true;
			}else{
				Log.e(TAG, "tag is not writable");
				return false;
			}
			

		} catch (TagLostException tagLostEx) {
			Log.i(TAG, "tag removed while writing");
			return false;

		} catch (IOException | FormatException e) {
			Log.e(TAG, "could not write to tag", e);
			return false;
		}

	}

	@Override
	protected void onPause() {
		/**
		 * Call this before onPause, otherwise an IllegalArgumentException is
		 * thrown as well.
		 */
		stopForegroundDispatch(this, myNfcAdapter);
		requestUpdates(false);

		super.onPause();
	}

	@Override
	protected void onResume() {
		setupForegroundDispatch(this, myNfcAdapter);
		super.onResume();
	}

	public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
		final Intent intent = new Intent(activity,
				activity.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		final PendingIntent pendingIntent = PendingIntent.getActivity(
				activity.getApplicationContext(), 0, intent, 0);

		IntentFilter[] filters = new IntentFilter[1];
		filters[0] = new IntentFilter();
	
		
		String[][] techList = new String[][]{};

		
		//if we import from a tag it should meet some requirements
		if (!activity.getIntent().hasExtra(EXPORT)) {
			Log.v(TAG,"setup filters for import");
			filters[0].addCategory(Intent.CATEGORY_DEFAULT);
			filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
			try {
				filters[0].addDataType(MIME_TEXT_PLAIN);
			} catch (MalformedMimeTypeException e) {
				throw new RuntimeException("Check your mime type.");
			}
		}else{
			Log.v(TAG,"setup export tech filter");
			techList = new String[][] { new String[] { Ndef.class.getName() } };
		}

		adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
	}

	public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
		adapter.disableForegroundDispatch(activity);
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
			Tour importTour = new Tour(tourId, tourName);
			try {
				for (String companyId : companyIds) {

					long companyIdL = Long.valueOf(companyId.trim());
					Company company = CompanyHelper.getCompanyById(
							dbHelper.getReadableDatabase(), companyIdL);
					if (company != null) {
						TourPoint tourPoint = new TourPoint(company);
						long tourPointId = TourHelper.insertTourPoint(dbHelper.getWritableDatabase(),
								tourPoint, tourId);
						tourPoint.setId(tourPointId);
						importTour.addTourPoint(tourPoint);
					} else {
						Log.e(TAG, "no company found by valid company Id");
						return false;
					}

				}
				BestTourController bestTour = new BestTourController(mCurrentLocation, this, importTour.getTourPointList());
				bestTour.getBestTourPointList();
			} catch (NumberFormatException nfEx) {
				Log.e(TAG, "companyId could not parsed to Long", nfEx);
				TourHelper.deleteTourById(dbHelper.getWritableDatabase(), tourId);
				return false;
			
			} finally {
				dbHelper.close();
			}
			return true;
		} catch (ArrayIndexOutOfBoundsException ex) {
			Log.e(TAG, "wrong seperator", ex);
			return false;
		}

	}

	private NdefRecord createTextRecord(String payload, Locale locale,
			boolean encodeInUtf8) {
		byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
		Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset
				.forName("UTF-16");
		byte[] textBytes = payload.getBytes(utfEncoding);
		int utfBit = encodeInUtf8 ? 0 : (1 << 7);
		char status = (char) (utfBit + langBytes.length);
		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		data[0] = (byte) status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
		NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
				NdefRecord.RTD_TEXT, new byte[0], data);
		return record;
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
						Log.e(TAG, "Unsupported Encoding", e);
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
				Log.v(TAG, "Read content: " + result);
			}
			if (processCompanyTourList(result)) {
				Toast.makeText(getApplicationContext(),
						getString(R.string.label_tour_import_successfull),
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(getApplicationContext(), TourActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(getApplicationContext(),
						R.string.label_tour_import_fail, Toast.LENGTH_LONG)
						.show();
			}
		}

	}

}
