package leagueSched;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.htmlparser.util.ParserException;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;
import utils.Config;
import utils.DateTimeUtils;

public class NCRRLSchedule extends AbstractLeagueSchedule {

	private static final Logger LOGGER = Logger.getLogger(NCRRLSchedule.class.getName());

	public NCRRLSchedule(String team) {
		super(team);

		try {
			String urlList = Config.getInstance().GetConfig(team).getUrl();
			String[] urls = urlList.split("\\|");
			for (String urlString : urls) {
				URL url = new URL(urlString);

				URLConnection conn = url.openConnection();

				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuffer sb = new StringBuffer();
				String line;
				while ((line = rd.readLine()) != null) {
					sb.append(line);
					sb.append("\n");
				}
				rd.close();

				StringReader sin = new StringReader(sb.toString());

				CalendarBuilder builder = new CalendarBuilder();

				Calendar calendar = builder.build(sin);
				System.err.println(calendar);
				ComponentList<CalendarComponent> componentList = calendar.getComponents();
				for (Object component : componentList) {
					if (component instanceof VEvent) {
						VEvent vEvent = (VEvent) component;
						DtStart start = vEvent.getStartDate();
						Date startDate = start.getDate();
						Summary summary = vEvent.getSummary();
						if (summary.getValue().contains("[Game - NCRRL]")) {
							Location location = vEvent.getLocation();
							String gameNumberStr = vEvent.getUid().getValue().replaceFirst("EID", "");
							String[] parts = gameNumberStr.split("-");
							String gameNumber = parts[1];
							String teamName = Config.getInstance().GetConfig(team).getMap();
							String homeStr = parseHomeFromSummary(summary.getValue());
							String visitorStr = parseVisitorFromSummary(summary.getValue());
							if (homeStr.equals(teamName) || visitorStr.equals(teamName)) {
								ScheduleRecord event = new ScheduleRecord();
								event.setGameDate(DateTimeUtils.makeTruncatedDate(startDate));
								event.setGameTime(DateTimeUtils.makeTruncatedTime(startDate));
								event.setLocation(location.getValue());
								event.setHome(homeStr);
								event.setVisitor(visitorStr);
								event.setGameNumber(gameNumber);
								schedule.add(event);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String parseVisitorFromSummary(String value) {
		String[] values = value.split("]");
		String game = values[1].trim();
		String[] teams = game.split("vs");
		return teams[0].trim();
	}

	private String parseHomeFromSummary(String value) {
		String[] values = value.split("]");
		String game = values[1].trim();
		String[] teams = game.split("vs");
		return teams[1].trim();
	}

	public static void main(String[] args) throws ParserException {

		NCRRLSchedule loader = new NCRRLSchedule("U8 Lannon");
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		try {

			List<ScheduleRecord> records = loader.findEntriesForDay(formatter.parse("03/12/2017"));
			for (int i = 0; i < records.size(); i++) {
				NCRRLSchedule.LOGGER.info(records.get(i).getHome() + ":" + records.get(i).getVisitor() + ":"
						+ records.get(i).getGameDate() + ":" + records.get(i).getGameTime() + ":"
						+ records.get(i).getGameNumber() + ":" + records.get(i).getLocation());
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
