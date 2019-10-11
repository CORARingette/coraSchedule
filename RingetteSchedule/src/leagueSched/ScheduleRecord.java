package leagueSched;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import utils.DateTimeUtils;

public class ScheduleRecord implements Comparable<ScheduleRecord> {

	@Getter @Setter private Date gameDate;
	@Getter @Setter private String gameTime;
	@Getter @Setter private String home;
	@Getter @Setter private String visitor;
	@Getter @Setter private String location;
	@Getter @Setter private String gameNumber;

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
