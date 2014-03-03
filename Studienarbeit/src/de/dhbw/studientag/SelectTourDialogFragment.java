package de.dhbw.studientag;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import de.dhbw.studientag.dbHelpers.MySQLiteHelper;
import de.dhbw.studientag.dbHelpers.TourHelper;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.TourPoint;

public class SelectTourDialogFragment extends DialogFragment {
	
	private List<Map<String, Object>> allToursList ;
	private Company company;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    builder.setTitle(R.string.label_Select_Tour)
		           .setItems(getTourNames(), new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		            	 if(which==0){
		            		 Intent intent = new Intent(getActivity().getBaseContext(), TourActivity.class);
		            		 intent.putExtra("company", company);
		            		 intent.putExtra("newTour", true); 
		            		 startActivity(intent);
		            	 }else{
		            		 MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity().getBaseContext());
		            		 long tourId=getTourId(--which);
		            		 TourPoint tourPoint = new TourPoint(tourId, company);
		            		 TourHelper.insertTourPoint(dbHelper.getWritableDatabase(), tourPoint);
		            		 dbHelper.close();
		            	 }
		            	   	            	   
		            	   
		            	   
		           }
		    });
		    return builder.create();
		
		
		
		
	}
	@Override
	public void onAttach(Activity activity) {
		MySQLiteHelper dbHelper = new MySQLiteHelper(getActivity().getBaseContext());
		allToursList = ToursListFragment.allTourPointsMapToAllToursList(TourHelper.getAllTours(dbHelper.getReadableDatabase()));
  	  	
		dbHelper.close();
		super.onAttach(activity);
	}
	

	
	private String[] getTourNames(){
		Set<String> tourNames = new LinkedHashSet<String>();
		tourNames.add(getString(R.string.label_create_new_Tour));
		for(Map<String,Object> tour : allToursList){
			
			String tourName =(String)tour.get(ToursListFragment.TOUR_NAME);
			if(tourName != null){
				tourNames.add(tourName);
			}
		}
		return tourNames.toArray(new String[tourNames.size()] );
	}
	
	private long getTourId(int position){
		long tourId = (Long) allToursList.get(position).get(ToursListFragment.TOUR_ID);
		return tourId;
	}
	
	public static SelectTourDialogFragment newInstance(Company company){
		SelectTourDialogFragment frag = new SelectTourDialogFragment();
		frag.company=company;
		return frag;
	}
	
}
