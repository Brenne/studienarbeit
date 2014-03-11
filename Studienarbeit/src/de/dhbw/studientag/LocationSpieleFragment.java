package de.dhbw.studientag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.dhbw.studientag.dbHelpers.BuildingHelper;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.model.Building;

public class LocationSpieleFragment extends Fragment {

	private MyDistanceListener listener;
	private ArrayAdapter<Building> mAdapter;
	private ArrayList<Building> mBuildings;
	

	public LocationSpieleFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SQLiteDatabase db = new MySQLiteHelper(getActivity()).getReadableDatabase();
		mBuildings = (ArrayList<Building>) BuildingHelper
				.getAllBuildings(db);
		db.close();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_location_spiele, container,
				false);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ListView lvBuildings = (ListView) view.findViewById(R.id.listView_buildings);
		
		mAdapter = new ArrayAdapter<Building>(getActivity(),
				android.R.layout.simple_list_item_1, mBuildings);
		lvBuildings.setAdapter(mAdapter);
		lvBuildings.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				

			}
		});

	}
	
	//Button orderList
	
	public void orderList(View view) {
		ListView lv = (ListView) getView().findViewById(R.id.listView_buildings);
		if(lv!= null){
			
			Map<String, Float> distance = sortByValue(listener.getDistanceMap());
			ArrayList<Building> buldingList = new ArrayList<Building>();

			for (Entry<String, Float> entry : distance.entrySet()) {
				Log.v("distance", entry.getKey() + " " + entry.getValue());
				for(Building building : mBuildings){
					if(building.getShortName().equals(entry.getKey())){
						buldingList.add(building);
					}
				}
				
			}
			mAdapter = new ArrayAdapter<Building>(getActivity(),
					android.R.layout.simple_list_item_1, buldingList);
			lv.setAdapter(mAdapter);
		}
			
		
	}
	


	public static  Map<String, Float> sortByValue(Map<String, Float> map) {
		List<Map.Entry<String, Float>> list = new LinkedList<Map.Entry<String, Float>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
			public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<String, Float> result = new LinkedHashMap<String, Float>();
		for (Map.Entry<String, Float> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public interface MyDistanceListener {
		public Map<String, Float> getDistanceMap();
	}
	


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (MyDistanceListener) activity;
			
		} catch (ClassCastException catExeption) {
			Log.e("LocationSpiele", "activity did not ipmlement listener", catExeption);
		}
	}
}
