package de.dhbw.studientag.model;

import java.util.ArrayList;

public class TestData {
	
	private ArrayList<Company> companies= new ArrayList<Company>();
	
	public TestData(){
		this.createSampleCompaniesList();
	}
	
	public void createSampleCompaniesList(){
		Company schmiede = new Company("Musterschmiede", "hauptstraﬂe 2", "72159 Musterhausen");
		Company holzhacker = new Company("Holzhacker GmbH", "Waldweg 6", "78547 Schwarzwald");
		Company maler = new Company("Malergesellen", "Bunte Straﬂe 4", "41587 Farbdorf");
		
		ArrayList<Company> sampleCompanyList = new ArrayList<Company>();
		sampleCompanyList.add(schmiede);
		sampleCompanyList.add(holzhacker);
		sampleCompanyList.add(maler);
		
		this.companies=sampleCompanyList;
		
		
	}
	
	public ArrayList<Company> getCompanies(){
		return this.companies;
	}
	

}
