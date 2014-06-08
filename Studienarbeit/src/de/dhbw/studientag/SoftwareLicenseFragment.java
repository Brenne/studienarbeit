package de.dhbw.studientag;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;

public class SoftwareLicenseFragment extends Fragment {

	private TextView mTextViewGooglePlay;
	private TextView mTextViewApacheFullLicence;
	private TextView mTextViewWait;

	public SoftwareLicenseFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_software_licenses, container, false);

		mTextViewApacheFullLicence = (TextView) view
				.findViewById(R.id.textView_apache_full_licence);
		mTextViewGooglePlay = (TextView) view.findViewById(R.id.textView_google_play_licence);
		mTextViewWait = (TextView) view.findViewById(R.id.textView_wait);
		mTextViewWait.setVisibility(View.VISIBLE);
		return view;
	}

	public void printLicenses(Context context) {
		new ShowPlayLicence().execute(getActivity());
		new ShowApacheLicence().execute();
	}

	private class ShowApacheLicence extends AsyncTask<Void, Void, Spanned> {

		protected void onPostExecute(Spanned result) {
			mTextViewApacheFullLicence.setText(result);
		}

		protected Spanned doInBackground(Void... params) {
			return Html.fromHtml(getString(R.string.label_apache_full_licence));

		}
	}

	private class ShowPlayLicence extends AsyncTask<Context, Void, String> {

		protected void onPostExecute(String result) {
			mTextViewGooglePlay.setText(result);
			mTextViewWait.setVisibility(View.GONE);
		}

		protected String doInBackground(Context... params) {
			return GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(params[0]);

		}
	}

}
