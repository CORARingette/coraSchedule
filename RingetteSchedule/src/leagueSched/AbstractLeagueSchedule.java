package leagueSched;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.java.Log;
import utils.DateTimeUtils;

@Log
public abstract class AbstractLeagueSchedule {

	protected String team;

	public AbstractLeagueSchedule(String team) {
		this.team = team;
	}

	protected List<ScheduleRecord> schedule = new ArrayList<ScheduleRecord>();

	public List<ScheduleRecord> findEntriesForDay(Date gameDate) {
		List<ScheduleRecord> results = schedule.stream()
				.filter(s -> DateTimeUtils.makeTruncatedDate(s.getGameDate()).equals(gameDate))
				.collect(Collectors.toList());
		Collections.sort(results);
		return results;
	}

	public ScheduleRecord findEntryForDateAndTime(Date gameDate, String time) {
		Stream<ScheduleRecord> results = schedule.stream()
				.filter(s -> s.getGameDate().equals(gameDate) && s.getGameTime().equals(time));
		if (results.count() > 1) {
			log.warning("Duplicate game record for team: " + team + " Date:" + gameDate + " Time: " + time);
		}
		return results.findFirst().orElse(null);
	}

	public String getTeam() {
		return team;
	}

	protected String stripTag(String input) {
		return input.replaceFirst("\\s*<td class=.+?>", "").replaceFirst("</td>", "");
	}

	public List<ScheduleRecord> getAllEntries() {
		return schedule;
	}

}