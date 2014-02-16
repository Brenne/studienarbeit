package de.dhbw.studientag.model;

import java.io.Serializable;

public enum Faculty implements Serializable{
	
	TECHNIK(1), WIRTSCHAFT(2), SOZIALWESEN(3);
	
	private int id;
	
	Faculty(int id){
		this.id=id;
	}
	
	public static Faculty getById(int id) {
	    for(Faculty f : values()) {
	        if(f.getId()==id) return f;
	    }
	    return null;
	 }
	
	public int getId(){
		return this.id;
	}

}
