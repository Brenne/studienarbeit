package de.dhbw.studientag;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

public abstract class LocationServiceActivity extends Activity implements

GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
			* FASTEST_INTERVAL_IN_SECONDS;
	private static final String KEY_UPDATES_ON = "KEY_UPDATES_ON";
	private static final String TAG = "LocationServiceActivity";

	protected LocationClient mLocationClient;
	protected Location mCurrentLocation;
	boolean mUpdatesRequested;
	// Define an object that holds accuracy and frequency parameters
	protected LocationRequest mLocationRequest;
	private SharedPreferences mPrefs;
	private Editor mEditor;

	public void initLocationService() {
		// Create the LocationRequest object
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 5 seconds
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

		// mLocationRequest.setNumUpdates(NUM_UPDATES);
		// Open the shared preferences
		mPrefs = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		// Get a SharedPreferences editor
		mEditor = mPrefs.edit();
		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks.
		 */

		mLocationClient = new LocationClient(this, this, this);
		mUpdatesRequested = false;
	}

	public Map<String, Float> getDistanceMap() {

		Map<String, Float> distance = new HashMap<String, Float>();

		if (mCurrentLocation == null) {
			Log.v(TAG, "curLocation is null");
		} else {
			for (Entry<String, LatLng> entry : LocationsActivity.LOCATIONS.entrySet()) {
				Location location = new Location(entry.getKey());
				location.setLatitude(entry.getValue().latitude);
				location.setLongitude(entry.getValue().longitude);

				float fDistance = location.distanceTo(mCurrentLocation);
				distance.put(entry.getKey(), fDistance);
			}

		}

		return distance;
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*
		 * Get any previous setting for location updates Gets "false" if an
		 * error occurs
		 */

		if (mPrefs.contains(KEY_UPDATES_ON)) {
			mUpdatesRequested = mPrefs.getBoolean(KEY_UPDATES_ON, false);

			// Otherwise, turn off location updates
		} else {
			mEditor.putBoolean(KEY_UPDATES_ON, false);
			mEditor.commit();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient.connect();

	}

	@Override
	protected void onPause() {
		super.onPause();
		mEditor.putBoolean(KEY_UPDATES_ON, mUpdatesRequested);
		mEditor.commit();
	}

	@Override
	protected void onStop() {
		// If the client is connected
		if (mLocationClient.isConnected()) {
			/*
			 * Remove location updates for a listener. The current Activity is
			 * the listener, so the argument is "this".
			 */
			mLocationClient.removeLocationUpdates(this);
		}
		/*
		 * After disconnect() is called, the client is considered "dead".
		 */
		mLocationClient.disconnect();
		super.onStop();
	}

	@Override
	public void onLocationChanged(Location location) {
		// Report to the UI that the location was updated
		String msg = "Updated Location: " + Double.toString(location.getLatitude()) + ","
				+ Double.toString(location.getLongitude());
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		Log.v(TAG, msg);
		mCurrentLocation = location;

	}

	// Location Google Play services from
	// http://developer.android.com/training/location/retrieve-current.html

	/*
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;

		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	/*
	 * Handle results returned to the FragmentActivity by Google Play services
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {

		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK:
				/*
				 * Try the request again
				 */

				break;
			}

		}
	}

	// ...
	private boolean showErrorDialog(int errorCode) {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d(TAG, "Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Get the error code

			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this,
					CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(getFragmentManager(), "Location Updates");
			}
		}

		return false;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (result.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				result.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */

			showErrorDialog(result.getErrorCode());
		}

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mCurrentLocation = mLocationClient.getLastLocation();
		if (mUpdatesRequested || mCurrentLocation == null) {
			Log.v(TAG, "updatesRequestet mLocationClient requestLocationUpdates");
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}

		// TODO color menu icon blue

	}

	@Override
	public void onDisconnected() {
		// Display the connection status
		// TODO color menu icon red

	}

}
