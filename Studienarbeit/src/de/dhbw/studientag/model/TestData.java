package de.dhbw.studientag.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;

public class TestData {
	
	private ArrayList<Company> companies= new ArrayList<Company>();
	
	private static final int companyName = 0;
	private static final int comanyShortName = 1;
	private static final int companyStreet = 2;
	private static final int companyPLZ = 3;
	private static final int companyCity = 4;
	private static final int companyMail = 11;
	private static final int compaayWWW = 12;
	private static final int companyOfferedSubjects = 14;
	
	public TestData(){
		
		try {
			CSVReader reader = new CSVReader(new FileReader("data/beispieldaten.csv"), ';');
		    String [] nextLine;
		    while ((nextLine = reader.readNext()) != null) {
		        // nextLine[] is an array of values from the line
		        System.out.println(nextLine[0] + nextLine[1] + "etc...");
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
	
	public void createSampleCompaniesList(){
		Company schmiede = new Company("Musterschmiede", "hauptstrasse 2", "72159 Musterhausen");
		Company holzhacker = new Company("Holzhacker GmbH", "Waldweg 6", "78547 Schwarzwald");
		Company maler = new Company("Malergesellen", "Bunte Strasse 4", "41587 Farbdorf");
		Company holzfaeller = new Company("Holzfaeller GmbH", "Forstweg 7", "73147 Waldheim");
		
		ArrayList<Company> sampleCompanyList = new ArrayList<Company>();
		sampleCompanyList.add(schmiede);
		sampleCompanyList.add(holzhacker);
		sampleCompanyList.add(maler);
		sampleCompanyList.add(holzfaeller);
		
		this.companies=sampleCompanyList;
		
		
	}
	
	public ArrayList<Company> getCompanies(){
		return this.companies;
	}
	
	public ArrayList<String> getCompanyNames(){
		ArrayList<String> companyNamesList = new ArrayList<String>();
		for(int i=0; i<this.companies.size(); i++){
			companyNamesList.add(this.companies.get(i).getName());
		}
		return companyNamesList;
	}
	

}
