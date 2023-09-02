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
public class LERQSchedule extends AbstractLeagueSchedule {

	private class TableNodeVisitor extends NodeVisitor {

		@Override
		public void visitEndTag(Tag tag) {
			super.visitEndTag(tag);
		}

		@Override
		public void visitStringNode(Text string) {
			super.visitStringNode(string);
		}

		@Override
		public void visitTag(Tag tag) {
			String className = tag.getAttribute("class");
			boolean started = false;
			if (className != null && className.equals("DataTable")) {

				NodeList rows = tag.getChildren().extractAllNodesThatMatch(new TagNameFilter("tr"));
				for (int r = 0; r < rows.size(); r++) {
					NodeList cells = rows.elementAt(r).getChildren().extractAllNodesThatMatch(new TagNameFilter("td"));
					if (cells.elementAt(2).toPlainTextString().equals("Heure")) {
						started = true;
						continue;
					}
					if (started) {
						String gameNumber = cells.elementAt(0).toPlainTextString().trim();
						String dateStr = cells.elementAt(1).toPlainTextString().trim();
						String timeStr = cells.elementAt(2).toPlainTextString().trim();
						String arena = cells.elementAt(3).toPlainTextString().trim();
						String home = cells.elementAt(4).toPlainTextString().trim();
						String away = cells.elementAt(5).toPlainTextString().trim();

						String mappedTeamName = Config.getInstance().GetConfig(team).getMap();
						if (home.equals(mappedTeamName) || away.equals(mappedTeamName)) {
							log.fine("found dateStr: " + dateStr + ",timeStr:" + timeStr + ",arena:" + arena + ",away:"
									+ away + ",home:" + home);

							ScheduleRecord event = new ScheduleRecord();

							Calendar calendar = Calendar.getInstance();
							calendar.setTimeInMillis(0);
							int[] dateValues = DateTimeUtils.parseDateDDMMYY(dateStr);
							calendar.set(Calendar.YEAR, dateValues[DateTimeUtils.PARSE_DATE_YEAR]);
							calendar.set(Calendar.MONTH, dateValues[DateTimeUtils.PARSE_DATE_MONTH]);
							calendar.set(Calendar.DAY_OF_MONTH, dateValues[DateTimeUtils.PARSE_DATE_DAY]);

							event.setGameDate(calendar.getTime());
							event.setGameTime(timeStr);
							event.setHome(home);
							event.setVisitor(away);
							event.setGameNumber(gameNumber);
							event.setLocation(arena);
							schedule.add(event);
						}

					}
				}

			}
		}
	}

	/**
	 * This is a test function to test the parsing of the LRQ website which is used
	 * for
	 * 
	 * To use this test, set a known team name and a date on which there is an event
	 * as shown in the LRP "Horaire" page.
	 * 
	 * Then run this function in you id and see what is printed.
	 * 
	 * Note that there are several steps which must be tweaked from year to year.
	 * 
	 * Each team playing in the LRQ (typically the U16 and U19 AA teams) must set
	 * the correct URL and correct "map" in the team config file. The URL is
	 * something like <code>
	 * https://membres.ringuette-quebec.qc.ca/cedules_liste_web.asp?ligues_id=87&amp;NbrAfficher=1000
	 * </code>
	 * 
	 * where the ligues_id changes from year to year. To see the ligues_id check the
	 * page source for the schedule page.
	 * 
	 * Note that the "map" tag in the team config file must be set to the team name
	 * in the LRQ website (typically "Ottawa").
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String team = "U16AA Wippel (22-23)";
			String date = "27-Nov-22";
			LERQSchedule lerq = new LERQSchedule(team);
			SimpleDateFormat formatter = new SimpleDateFormat("d-MMM-yy");
			List<ScheduleRecord> records = lerq.findEntriesForDay(formatter.parse(date));
			if (records.size() == 0) {
				LERQSchedule.log.info("No records found for team " + team + " on " + date);
			} else {
				for (int i = 0; i < records.size(); i++) {
					LERQSchedule.log.info(records.get(i).getHome() + ":" + records.get(i).getVisitor() + ":"
							+ records.get(i).getLocation() + ":" + records.get(i).getGameDate() + ":"
							+ records.get(i).getGameTime() + ":" + records.get(i).getGameNumber());
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public LERQSchedule(String team) {
		super(team);

		try {
			URL url = new URL((String) Config.getInstance().GetConfig(team).getUrl());

			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

			// We need to use the ISO-8859-1 encoding to have the accented arena names processed correctly
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "ISO-8859-1"));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			Parser parser = new Parser(sb.toString());
			parser.visitAllNodesWith(new TableNodeVisitor());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
