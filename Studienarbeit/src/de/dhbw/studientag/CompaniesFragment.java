package de.dhbw.studientag;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import de.dhbw.studientag.model.Company;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class CompaniesFragment extends ListFragment {

	private static final String COMPANIES = "companies";
	private List<Company> mCompanyList;
	private OnCompanySelectedLitener mListener;

	public static CompaniesFragment newCompaniesFragmentInstance(
			ArrayList<Company> companyList) {
		CompaniesFragment fragment = new CompaniesFragment();
		Bundle args = new Bundle();
		args.putParcelableArrayList(COMPANIES, companyList);
		fragment.setArguments(args);
	
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public CompaniesFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			mCompanyList = getArguments().getParcelableArrayList(COMPANIES);

		}

		setListAdapter(new ArrayAdapter<Company>(getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1, mCompanyList));
		setHasOptionsMenu(true);
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.companies, menu);

		// Get the SearchView and set the searchable configuration
		SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search)
				.getActionView();
		// Assumes current activity is the searchable activity
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getActivity().getComponentName()));
		searchView.setIconifiedByDefault(false); // Do not iconify the widget;
													// expand it by default
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO maybe not LIKE search but full text search?
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {

				Filterable adapter = (Filterable) getListAdapter();
				adapter.getFilter().filter(newText);
				return false;
			}
		});

		
	}
	


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnCompanySelectedLitener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnCompanySelectedListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (null != mListener) {
			Company company = (Company) getListAdapter().getItem(position);
			mListener.onCompanySelected(company);
		}
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnCompanySelectedLitener {

		public void onCompanySelected(Company company);
	}

}
