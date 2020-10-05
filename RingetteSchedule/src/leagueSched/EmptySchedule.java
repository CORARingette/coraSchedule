package leagueSched;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.NodeVisitor;

import lombok.extern.java.Log;
import utils.Config;
import utils.DateTimeUtils;

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
