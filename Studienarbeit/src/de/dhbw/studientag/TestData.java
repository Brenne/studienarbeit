package de.dhbw.studientag;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.res.AssetManager;
import android.util.Log;
import au.com.bytecode.opencsv.CSVReader;
import de.dhbw.studientag.model.Company;
import de.dhbw.studientag.model.Faculty;
import de.dhbw.studientag.model.Subject;

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
		
		try {
			InputStream is = assets.open("room.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
		
		
//			final LinkedHashMap map = new LinkedHashMap<String,String[]>();
//			
//			map.put("buildings", null);
//			map.put("building", new String[]{"shortname", "fullname"});
//			map.put("floor", new String[]{"num","name"});
//			map.put("room", null);
			System.out.println("root of xml file "+ doc.getDocumentElement().getNodeName());
			if(doc.getDocumentElement().getNodeName().equalsIgnoreCase("buildings")){
				NodeList nodeListBuildings = doc.getElementsByTagName("building");
				for(int i =0; i< nodeListBuildings.getLength(); i++){
					Node nBuilding = nodeListBuildings.item(i);
					if(nBuilding.getNodeType() == Node.ELEMENT_NODE){
						Element eBuilding = (Element) nBuilding;
						
						System.out.println("Building shortname "+ eBuilding.getAttribute("shortname"));
						System.out.println("Building fullname "+ eBuilding.getAttribute("fullname"));
						
						NodeList nodeListFloors = eBuilding.getElementsByTagName("floor");
						processNodeList(nodeListFloors, new String[]{"num","name"});
						System.out.println("abort");
						
//						for(int j=0; j< nodeListFloors.getLength(); j++){
//							Node nFloor = nodeListFloors.item(j);
//							if(nFloor.getNodeType()== Node.ELEMENT_NODE){
//								Element eFloor = (Element) nFloor;
//								System.out.println("Floor num "+eFloor.getAttribute("num"));
//								System.out.println("Floor name "+eFloor.getAttribute("name"));
//								
//								NodeList nodeListRooms = eFloor.getElementsByTagName("room");
//								processNodeList(nodeListRooms, null);
////								for(int k=0; k<nodeListRooms.getLength(); k++){
////									Node nRoom= nodeListRooms.item(k);
////									if(nRoom.getNodeType() == Node.ELEMENT_NODE){
////										Element eRoom = (Element) nRoom;
////										System.out.println("room "+eRoom.getTextContent());
////									}
////	
////								}
//								
//							}
//						}
						
					}
					
					
				}
			}
			


			
			

		
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

      
		
				
		
	}
	
	
	private void ausgabe(LinkedHashMap<String,String[]> docStructure, Document doc){
		Set<Entry<String, String[]>> entries = docStructure.entrySet();
		for(Entry<String, String[]> entry : entries){
			NodeList nodeList = doc.getElementsByTagName(entry.getKey());
			processNodeList(nodeList, entry.getValue());
			
		}

	}
	
	private void processNodeList(NodeList nodeList, String[] attributeNames){
		
		
		for(int k=0; k<nodeList.getLength(); k++){
			Node node= nodeList.item(k);
			if(node.hasChildNodes()){
				processNodeList(node.getChildNodes(), attributeNames);
			}
			if(node.getNodeType() == Node.ELEMENT_NODE){
				
				Element element = (Element) node;
				System.out.println("Element node Name "+ element.getNodeName());
				if(element.hasAttributes()){
					for(String attributeName : attributeNames){
						if(element.hasAttribute(attributeName)){
							System.out.println(attributeName + " "+ element.getAttribute(attributeName));
						}
					}
				}else if(element.getNodeName().equalsIgnoreCase("room"))				
					System.out.println("room "+ element.getTextContent());
				
			}
		}
	}
	




	
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
