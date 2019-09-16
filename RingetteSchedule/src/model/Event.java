package model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import utils.DateTimeUtils;

@Log
public class Event implements Comparable<Event> {

	@Getter
	private final String team;
	@Getter
	private final Date date;
	@Getter
	private final String time;

	@Getter
	@Setter
	private String shareTeam;
	@Getter
	@Setter
	private String gameNumber;
	@Getter
	@Setter
	private String location;
	@Getter
	@Setter
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

	public boolean isGame() {
		return shareValue != null && (shareValue == ShareValue.HOME || shareValue == ShareValue.VISITOR);
	}

	public boolean isPractice() {
		return shareValue != null && (shareValue == ShareValue.HALF || shareValue == ShareValue.FULL);
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
			if (shareTeam != null) {
				return team + " shared with " + shareTeam;
			} else {
				return team + " - Full Ice (tentative)";
			}
		}
	}

	private String makeGameSummary() {
		if (shareTeam == null) {
			log.warning("No game info found for: " + team + ":" + getDate());
			return "Unknown Time: See league schedule for details";
		} else {
			return gameNumber + ": " + team + " (" + shareValue.getShortString() + ") vs. " + shareTeam;
		}
	}

	@Override
	public int compareTo(Event otherEvent) {
		return getFullDateTime().compareTo(otherEvent.getFullDateTime());
	}

	public String dump() {
		StringBuffer sb = new StringBuffer();
		sb.append(team).append(":");
		sb.append(date).append(":");
		sb.append(time).append(":");
		sb.append(location).append(":");
		sb.append(shareValue).append(":");
		sb.append(shareTeam).append(":");
		sb.append(gameNumber).append(":");
		return sb.toString();
	}
}
