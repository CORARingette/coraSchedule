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
							log.fine("found dateStr: " + dateStr + ",timeStr:" + timeStr + ",arena:" + arena
									+ ",away:" + away + ",home:" + home);

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
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			LERQSchedule lerq = new LERQSchedule("U16AA Wippel");
			SimpleDateFormat formatter = new SimpleDateFormat("d-MMM-yy");
			List<ScheduleRecord> records = lerq.findEntriesForDay(formatter.parse("28-Sep-19"));
			for (int i = 0; i < records.size(); i++) {
				LERQSchedule.log.info(records.get(i).getHome() + ":" + records.get(i).getVisitor() + ":"
						+ records.get(i).getLocation() + ":" + records.get(i).getGameDate() + ":"
						+ records.get(i).getGameTime() + ":" + records.get(i).getGameNumber());
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
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");


			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
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
