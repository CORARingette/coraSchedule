package model;

import java.util.Date;

import utils.DateTimeUtils;

public class Event implements Comparable<Event> {

	private final String team;

	private final String shareValue;
	private final Date date;
	private final String time;

	private String shareTeam;
	private String gameNumber;
	private String location;

	public Event(String team, String location, String shareValue, String shareTeam, Date date, String time,
			String gameNumber) {
		super();
		this.team = team;
		this.shareValue = shareValue;
		this.shareTeam = shareTeam;
		this.date = date;
		this.time = time;
		this.gameNumber = gameNumber;
		this.location = location;
	}

	public String getTeam() {
		return team;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getShareValue() {
		return shareValue;
	}

	public String getShareTeam() {
		return shareTeam;
	}

	public Date getDate() {
		return date;
	}

	public String getGameNumber() {
		return gameNumber;
	}

	public void setGameNumber(String gameNumber) {
		this.gameNumber = gameNumber;
	}

	public void setShareTeam(String shareTeam) {
		this.shareTeam = shareTeam;
	}

	public String getTime() {
		return time;
	}

	public boolean isGame() {
		return shareValue != null && (shareValue.equals("H") || shareValue.equals("V"));
	}

	public Date getFullDateTime() {
		return date != null && time != null ? DateTimeUtils.makeFullDateFromDateAndTime(date, time) : new Date();
	}

	public String getSummary() {
		if (isGame()) {
			if (shareTeam == null) {
				System.err.println("No game info found for: " + team + ":" + getDate());
				return "Unknown Time: See league schedule for details";
			} else {
				return gameNumber + ": " + team + " (" + shareValue + ") vs. " + shareTeam;
			}
		} else if (shareValue.equals("X")) {
			return team;
		} else {
			return team + (shareTeam != null ? " with " + shareTeam : " Full Ice");
		}

	}

	@Override
	public int compareTo(Event otherEvent) {
		return getFullDateTime().compareTo(otherEvent.getFullDateTime());
	}

}
