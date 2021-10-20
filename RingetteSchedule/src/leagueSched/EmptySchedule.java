package leagueSched;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import lombok.extern.java.Log;

@Log
public class EmptySchedule extends AbstractLeagueSchedule {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			EmptySchedule empty = new EmptySchedule("U16AA Wippel");
			SimpleDateFormat formatter = new SimpleDateFormat("d-MMM-yy");
			List<ScheduleRecord> records = empty.findEntriesForDay(formatter.parse("28-Sep-19"));
			for (int i = 0; i < records.size(); i++) {
				EmptySchedule.log.info(records.get(i).getHome() + ":" + records.get(i).getVisitor() + ":"
						+ records.get(i).getLocation() + ":" + records.get(i).getGameDate() + ":"
						+ records.get(i).getGameTime() + ":" + records.get(i).getGameNumber());
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public EmptySchedule(String team) {
		super(team);
	}

}
