package de.dhbw.studientag;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.content.res.AssetManager;
import android.util.Log;
import au.com.bytecode.opencsv.CSVReader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.dhbw.studientag.model.Building;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Faculty;
import de.dhbw.studientag.model.Floor;
import de.dhbw.studientag.model.Location;
import de.dhbw.studientag.model.Room;
import de.dhbw.studientag.model.Subject;

public final class TestData {

	private static final String TAG ="TestData";
	private static ArrayList<Company> companies = new ArrayList<Company>();
	private static ArrayList<Subject> subjects = new ArrayList<Subject>();
	private static List<Building> buildings = new ArrayList<Building>();
	private static List<Location> locationList = new ArrayList<Location>();

	private static final short COMPANY_NAME = 0;
//	private static final short COMPANY_SHORT_NAME = 1;
	private static final short COMPANY_STREET = 2;
	private static final short COMPANY_PLZ = 3;
	private static final short COMPANY_CITY = 4;
//	private static final short COMPANY_MAIL = 11;
	private static final short COMPANY_WWW = 12;
	private static final short COMPANY_OFFERED_SUBJECTS = 14;
	private static final short COMPANY_BUILDING = 15;
	private static final short COMPANY_ROOM = 16;

	public TestData(AssetManager assets) {
		Log.v(TAG,"TestData constructor");
		initBuildingList(assets);
		try {
			// Read faculty subject mapping
			CSVReader facultySubjectReader = new CSVReader(new InputStreamReader(
					assets.open("faculty_subject_mapping.csv")), ';');
			String[] nextLine;
			facultySubjectReader.readNext();
			while ((nextLine = facultySubjectReader.readNext()) != null) {
				Subject subject = new Subject(nextLine[0], Faculty.valueOf(nextLine[1]
						.toUpperCase(Locale.getDefault())));
				subjects.add(subject);
			}
			facultySubjectReader.close();
			CSVReader reader = new CSVReader(new InputStreamReader(
					assets.open("beispieldaten.csv")), ';');

			// skip first line
			reader.readNext();
			while ((nextLine = reader.readNext()) != null) {

				ArrayList<Subject> companyOfferedSubjects = getSubjectList(nextLine[COMPANY_OFFERED_SUBJECTS]);

				Company company = new Company(0, nextLine[COMPANY_NAME],
						nextLine[COMPANY_STREET], nextLine[COMPANY_CITY],
						nextLine[COMPANY_PLZ], nextLine[COMPANY_WWW]);
				company.setSubjectList(companyOfferedSubjects);
				String companyRoomName = nextLine[COMPANY_ROOM];
				String companyBuildingName = nextLine[COMPANY_BUILDING];
				company.setLocation(getLocationBy(companyBuildingName, companyRoomName));

				TestData.companies.add(company);

			}
			reader.close();

		} catch (FileNotFoundException e) {
			Log.e(TAG,"FileNot Found, no space left on device?",e);
		} catch (IOException e) {
			Log.e(TAG, "IOException", e);
		}


	}
	
	private final void initBuildingList(AssetManager assets){
		// Building room.json TestData
		try {
			Type listType = new TypeToken<ArrayList<Building>>() {
				
			}.getType();

			StringBuilder buf = new StringBuilder();
			InputStream json = assets.open("room.json");
			BufferedReader in = new BufferedReader(new InputStreamReader(json));
			String line;

			while ((line = in.readLine()) != null) {
				buf.append(line);
			}

			final List<Building> buildingList = new Gson().fromJson(buf.toString(),
					listType);
			TestData.buildings = buildingList;
			setLocationList(buildingList);
		} catch (IOException e) {
			Log.e(TAG,"Error in initBuldings could not open assets",e);
		}
	}
	
	private void setLocationList(List<Building> buildingList){
		List<Location> locationList = new ArrayList<Location>();
		for(Building building : buildingList){
			for(Floor floor : building.getFloorList()){
				for(Room room : floor.getRoomList()){
					Location location = new Location(building, floor, room);
					locationList.add(location);
				}
				
			}
			
		}
		
		TestData.locationList=locationList;
	}
	
	private Location getLocationBy(String buildingShortName, String roomName){
		for(Location location : TestData.locationList){
			if(location.getBuilding().getShortName().equalsIgnoreCase(buildingShortName)){
				for(Floor floor : location.getBuilding().getFloorList()){
					for(Room room : floor.getRoomList()){
						if(room.getRoomNo().equalsIgnoreCase(roomName)){
							Location returnLocation = new Location(location.getBuilding(), floor, room);
							return returnLocation;
						}		
					}	
				}
				Log.e(TAG, "no "+roomName+" found in building "+buildingShortName);
				return null;
			}
		}
		Log.e(TAG, "no "+buildingShortName+" found");
		return null;
	}

	public static ArrayList<Company> getCompanies() {
		return TestData.companies;
	}

	public static ArrayList<Subject> getSubjects() {
		return TestData.subjects;
	}

	public static ArrayList<String> getCompanyNames() {
		ArrayList<String> companyNamesList = new ArrayList<String>();
		for (int i = 0; i < TestData.companies.size(); i++) {
			companyNamesList.add(TestData.companies.get(i).getName());
		}
		return companyNamesList;
	}

	/**
	 * 
	 * @param subjects
	 *            String of comma seperated subject Names
	 * @return
	 */
	private static ArrayList<Subject> getSubjectList(String subjects) {
		String[] subjectNames = subjects.split(",");
		ArrayList<Subject> subjectList = new ArrayList<Subject>();
		//use LinkedList because better to remove objects than ArrayList
		LinkedList<Subject> allSubjects = new LinkedList<Subject>(TestData.subjects);
		for (String subjectName : subjectNames) {
			Iterator<Subject> i = allSubjects.iterator();
			while (i.hasNext()) {
				Subject subject = (Subject) i.next();
				if (subject.getName().equalsIgnoreCase(subjectName.trim())) {
					subjectList.add(subject);
					allSubjects.remove(subject);
					break;
				}
			}

		}
		if (subjectNames.length != subjectList.size())
			Log.w("studientag", "Not all subjects of company found in subject list ");

		return subjectList;
	}

	public static List<Building> getBuildings() {
		return buildings;
	}

	
	

	


}
