package leagueSched;

import java.util.Date;

import utils.DateTimeUtils;

public class ScheduleRecord implements Comparable<ScheduleRecord> {

	private Date gameDate;
	private String gameTime;
	private String home;
	private String visitor;
	private String location;
	private String gameNumber;

	public Date getGameDate() {
		return gameDate;
	}

	public void setGameDate(Date gameDate) {
		this.gameDate = gameDate;
	}

	public String getGameTime() {
		return gameTime;
	}

	public void setGameTime(String gameTime) {
		this.gameTime = gameTime;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public String getVisitor() {
		return visitor;
	}

	public void setVisitor(String visitor) {
		this.visitor = visitor;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getGameNumber() {
		return gameNumber;
	}

	public void setGameNumber(String gameNumber) {
		this.gameNumber = gameNumber;
	}

	public Date getGameDateAndTime() {
		return DateTimeUtils.makeFullDateFromDateAndTime(gameDate, gameTime);
	}

	public String getKey() {
		return gameDate + "#" + gameTime + "#" + home + "#" + visitor + "#" + location;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof ScheduleRecord)) {
			return false;
		}
		return this.getKey().equals(((ScheduleRecord) obj).getKey());
	}

	@Override
	public int hashCode() {
		return getKey().hashCode();
	}

	@Override
	public int compareTo(ScheduleRecord obj) {
		return this.getGameDateAndTime().compareTo(obj.getGameDateAndTime());
	}

}
