package de.dhbw.studientag.model;

import java.io.Serializable;

public class Subject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String name;
	private Faculty faculty;
	
	public Subject(String name, Faculty faculty){
		this.name=name;
		this.faculty=faculty;
	}
	
	public Subject(long id, String name, Faculty faculty){
		this(name, faculty);
		this.id=id;
	}
	
	public String getName() {
		return name;
	}


	public Faculty getFaculty() {
		return faculty;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return this.getName();
	}


	
}
