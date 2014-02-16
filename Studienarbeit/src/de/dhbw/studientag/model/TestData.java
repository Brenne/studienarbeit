package de.dhbw.studientag.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.res.AssetManager;
import android.util.Log;
import au.com.bytecode.opencsv.CSVReader;

public class TestData {
	
	private static  ArrayList<Company> companies= new ArrayList<Company>();
	private static  List<String> subjectNamesList = new ArrayList<String>();
	private static  ArrayList<Subject> subjects = new ArrayList<Subject>();
	
	private static final int COMPANY_NAME = 0;
	private static final int COMPANY_SHORT_NAME = 1;
	private static final int COMPANY_STREET = 2;
	private static final int COMPANY_PLZ = 3;
	private static final int COMPANY_CITY = 4;
	private static final int COMPANY_MAIL = 11;
	private static final int COMPANY_WWW = 12;
	private static final int COMPANY_OFFERED_SUBJECTS = 14;
	
	
	public  TestData(AssetManager assets){
		

		
		
		try {
			//Read faculty subject mapping
			CSVReader facultySubjectReader = new CSVReader( new InputStreamReader(assets.open("faculty_subject_mapping.csv")), ';');
			String[] nextLine;
			facultySubjectReader.readNext();
			while ((nextLine = facultySubjectReader.readNext()) != null) {
				Subject subject = new Subject(nextLine[0], 
						Faculty.valueOf(
									nextLine[1].toUpperCase()
								)
				);
				subjects.add(subject);
			}
			facultySubjectReader.close();
			CSVReader reader = new CSVReader( new InputStreamReader(assets.open("beispieldaten.csv")), ';');

		    //skip first line
		    reader.readNext();
		    while ((nextLine = reader.readNext()) != null) {
		        // nextLine[] is an array of values from the line
//		        System.out.println(nextLine[COMPANY_NAME]+" "+  nextLine[COMPANY_STREET] +" "+
//		        		nextLine[COMPANY_CITY] + " "+ nextLine[COMPANY_PLZ] +" "+ nextLine[COMPANY_WWW]);
		        ArrayList<Subject> companyOfferedSubjects = getSubjectList(nextLine[COMPANY_OFFERED_SUBJECTS]);
		
		        Company company = new Company(0,nextLine[COMPANY_NAME], nextLine[COMPANY_STREET], 
		        		nextLine[COMPANY_CITY], nextLine[COMPANY_PLZ], nextLine[COMPANY_WWW]);
		        company.setSubjectList(companyOfferedSubjects);
		        TestData.companies.add(company);
		       
		    }
		    reader.close();
		    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}
	
//	public void addCompany(){
//		Company schmiede = new Company("Musterschmiede", "hauptstrasse 2", "72159 Musterhausen");
//		Company holzhacker = new Company("Holzhacker GmbH", "Waldweg 6", "78547 Schwarzwald");
//		Company maler = new Company("Malergesellen", "Bunte Strasse 4", "41587 Farbdorf");
//		Company holzfaeller = new Company("Holzfaeller GmbH", "Forstweg 7", "73147 Waldheim");
//		
//		ArrayList<Company> sampleCompanyList = new ArrayList<Company>();
//		sampleCompanyList.add(schmiede);
//		sampleCompanyList.add(holzhacker);
//		sampleCompanyList.add(maler);
//		sampleCompanyList.add(holzfaeller);
//		
//		this.companies=sampleCompanyList;
//		
//		
//	}
	
	public ArrayList<Company> getCompanies(){
		return TestData.companies;
	}

	public ArrayList<Subject> getSubjects(){
		return TestData.subjects;
	}
	
	public ArrayList<String> getCompanyNames(){
		ArrayList<String> companyNamesList = new ArrayList<String>();
		for(int i=0; i<TestData.companies.size(); i++){
			companyNamesList.add(TestData.companies.get(i).getName());
		}
		return companyNamesList;
	}
	
	private ArrayList<Subject> getSubjectList(String subjects){
		String[] subjectNames = subjects.split(",");
		ArrayList<Subject> subjectList = new ArrayList<Subject>();
		for(String subjectName : subjectNames){
			/*TODO work with a copy of TestData.subjects
			 * remove found subjects of list -> improve performance because fewer loops 
			 */
			for(Subject subject : TestData.subjects){
				if(subject.getName().equalsIgnoreCase(subjectName.trim())){
					subjectList.add(subject);
					break;
				}
					
			}
		}
		if(subjectNames.length != subjectList.size())
			Log.i("studientag", "Not all subjects of company found in subject list ");
		
		return subjectList;
	}
	
	
	

	

}
