package model;

import java.util.Date;
import java.util.logging.Logger;

import utils.DateTimeUtils;

public class Event implements Comparable<Event> {

	private static final Logger LOGGER = Logger.getLogger(Event.class.getName());


	private final String team;
	private final Date date;
	private final String time;

	private String shareTeam;
	private String gameNumber;
	private String location;
	private ShareValue shareValue;

	public Event(String team, String location, ShareValue shareValue, String shareTeam, Date date, String time,
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

	public ShareValue getShareValue() {
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

	public void setShareValue(ShareValue shareValue) {
		this.shareValue = shareValue;
	}

	public String getTime() {
		return time;
	}

	public boolean isGame() {
		return shareValue != null && (shareValue == ShareValue.HOME || shareValue == ShareValue.VISITOR);
	}

	public boolean isPractice() {
		return shareValue != null && (shareValue==ShareValue.HALF || shareValue == ShareValue.FULL);
	}

	public boolean isFullIce() {
		return shareValue != null && shareValue == ShareValue.FULL;
	}

	public Date getFullDateTime() {
		return date != null && time != null ? DateTimeUtils.makeFullDateFromDateAndTime(date, time) : new Date();
	}

	public String getSummary() {
		if (isGame()) {
			return makeGameSummary();
		} else if (isPractice()) {
			return makePracticeSummary();
		} else {
			return team + "Other event";
		}
	}

	private String makePracticeSummary() {
		if (isFullIce()) {
			return team + " - Full Ice";
		} else {
			return team + " shared with " + shareTeam;
		}
	}

	private String makeGameSummary() {
		if (shareTeam == null) {
			LOGGER.info("No game info found for: " + team + ":" + getDate());
			return "Unknown Time: See league schedule for details";
		} else {
			return gameNumber + ": " + team + " (" + shareValue.getShortString() + ") vs. " + shareTeam;
		}
	}

	@Override
	public int compareTo(Event otherEvent) {
		return getFullDateTime().compareTo(otherEvent.getFullDateTime());
	}

}
