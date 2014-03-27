package de.dhbw.studientag.model;

public class Comment {

	private long id;
	private Company company;
	private String fullMessage;

	public Comment(long id, Company company, String message) {
		this.id = id;
		this.company = company;
		this.fullMessage = message;
	}

	public long getId() {
		return id;
	}

	public Company getCompany() {
		return company;
	}

	public String getFullMessage() {
		return fullMessage;
	}

	public String getShortMessage() {
		final int LIMIT = 100;
		if (fullMessage != null && !fullMessage.isEmpty()) {
			// remove line breaks:
			String shortMessage = this.fullMessage.replace("\n", " ").replace("\r", " ");
			if (shortMessage.length() < LIMIT) {
				return shortMessage;
			} else {
				return shortMessage.substring(0, LIMIT).concat("...");
			}
		} else {
			return "";
		}

	}

}
