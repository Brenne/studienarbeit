package de.dhbw.studientag.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import au.com.bytecode.opencsv.CSVReader;

public class TestData {
	
	private ArrayList<Company> companies= new ArrayList<Company>();
	
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

			CSVReader reader = new CSVReader( new InputStreamReader(assets.open("beispieldaten.csv")), ';');
		    String [] nextLine;
		    //skip first line
		    reader.readNext();
		    while ((nextLine = reader.readNext()) != null) {
		        // nextLine[] is an array of values from the line
//		        System.out.println(nextLine[COMPANY_NAME]+" "+  nextLine[COMPANY_STREET] +" "+
//		        		nextLine[COMPANY_CITY] + " "+ nextLine[COMPANY_PLZ] +" "+ nextLine[COMPANY_WWW]);
		        
		        Company company = new Company(nextLine[COMPANY_NAME], nextLine[COMPANY_STREET], 
		        		nextLine[COMPANY_CITY], nextLine[COMPANY_PLZ], nextLine[COMPANY_WWW]);
		        this.companies.add(company);
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
