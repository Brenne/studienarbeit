package de.dhbw.studientag;

import java.util.List;

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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import de.dhbw.studientag.model.TourPoint;
import de.dhbw.studientag.tours.BestTourController;

/**
 * provides methods for locating the user via
 * GooglePlay Location Service
 * 
 */
public abstract class LocationServiceActivity extends Activity implements

GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, LocationListener {

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
	public static final String KEY_UPDATES_ON = "KEY_UPDATES_ON";
	private static final String TAG = "LocationServiceActivity";
	private static final int NUM_UPDATES = 5;

	protected GoogleApiClient mGoogleApiClient;
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
				
		mLocationRequest.setNumUpdates(NUM_UPDATES);
		// Open the shared preferences
		mPrefs = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		// Get a SharedPreferences editor
		mEditor = mPrefs.edit();
		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks.
		 */

		mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addApi(LocationServices.API)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .build();
		mUpdatesRequested = true;
	}

	
	
	public void requestUpdates(boolean on_off){
		
		if(on_off){
			mEditor.putBoolean(KEY_UPDATES_ON, true).apply();
			if(mGoogleApiClient.isConnected()){
				 LocationServices.FusedLocationApi.requestLocationUpdates(
			                mGoogleApiClient, mLocationRequest, this);
				
			}
		}else{
			mEditor.putBoolean(KEY_UPDATES_ON, false).apply();
			LocationServices.FusedLocationApi.removeLocationUpdates(
							mGoogleApiClient, this);
		}
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
		mGoogleApiClient.connect();

	}



	@Override
	protected void onStop() {
		// If the client is connected
		if (mGoogleApiClient.isConnected()) {
			/*
			 * Remove location updates for a listener. The current Activity is
			 * the listener, so the argument is "this".
			 */
			LocationServices.FusedLocationApi.removeLocationUpdates(
					mGoogleApiClient, this);
		}
		/*
		 * After disconnect() is called, the client is considered "dead".
		 */
		mGoogleApiClient.disconnect();
		super.onStop();
	}

	@Override
	public void onLocationChanged(Location location) {
		// Report to the UI that the location was updated
		String msg = "Updated Location: " + Double.toString(location.getLatitude()) + ","
				+ Double.toString(location.getLongitude());
		
		Log.d(TAG, msg);
		mCurrentLocation = location;

	}
	
	public List<TourPoint> bestTourPointList(List<TourPoint> tourPoints) {
		BestTourController bestTour = new BestTourController(mCurrentLocation, this, tourPoints);
		return bestTour.getBestTourPointList();
		
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
			default:
					Log.d(TAG,"Connection Failure");
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
		Log.d(TAG,"Google Play Connection Failed");
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
			Log.d(TAG,"Goolge Play Connection failed, no resolution available");
			showErrorDialog(result.getErrorCode());
		}

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mCurrentLocation =LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (mUpdatesRequested || mCurrentLocation == null) {
			Log.v(TAG, "updatesRequestet mLocationClient requestLocationUpdates");
			LocationServices.FusedLocationApi.requestLocationUpdates(
	                mGoogleApiClient, mLocationRequest, this);
			
		}

		// TODO color menu icon blue

	}

	

}
