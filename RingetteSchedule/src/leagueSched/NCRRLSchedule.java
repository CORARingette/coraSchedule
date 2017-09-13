package leagueSched;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import org.htmlparser.util.ParserException;

import utils.Config;
import utils.DateTimeUtils;

public class NCRRLSchedule extends AbstractLeagueSchedule {

	private static final Logger LOGGER = Logger.getLogger(NCRRLSchedule.class.getName());

	public NCRRLSchedule(String team) {
		super(team);
		try {
			String urlList = Config.instance.GetConfig(team).getUrl();
			String[] urls = urlList.split("\\|");
			for (String url : urls) {
				if (url != null && !url.isEmpty()) {
					URL ncrrl = new URL(url);
					BufferedReader in = new BufferedReader(new InputStreamReader(ncrrl.openStream()));

					String inputLine;
					String previous = null;

					while ((inputLine = in.readLine()) != null) {
						if (inputLine.contains("<strike>")) {
							continue;
						}
						// if (inputLine.indexOf("15/12/2012") > 0) {
						// System.err.println(inputLine);
						// }
						String temp = null;
						try {

							if (inputLine.matches("^<td><nobr>.*\\&nbsp;[0-9]{1,2}-[A-z][a-z]{2}-[0-9]{2}.*$")) {
								String gameNumber = stripTag(previous);
								String dateStr = stripTag(inputLine);
								temp = in.readLine();// time
								String timeStr = stripTag(temp);
								temp = in.readLine();// ice
								String iceStr = stripTag(temp);
								temp = in.readLine();// away
								String awayStr = stripTag(temp);
								temp = in.readLine();// away score
								temp = in.readLine();// home
								String homeStr = stripTag(temp);
								temp = in.readLine();// home score
								temp = in.readLine(); // cancelled?

								String teamName = Config.instance.GetConfig(team).getMap();
								if (homeStr.equals(teamName) || awayStr.equals(teamName)) {
									ScheduleRecord event = new ScheduleRecord();
									Calendar calendar = Calendar.getInstance();
									calendar.setTimeInMillis(0);
									int[] dateValues = DateTimeUtils.parseDateDorDDMMMYY(dateStr);
									calendar.set(Calendar.YEAR, dateValues[DateTimeUtils.PARSE_DATE_YEAR]);
									calendar.set(Calendar.MONTH, dateValues[DateTimeUtils.PARSE_DATE_MONTH]);
									calendar.set(Calendar.DAY_OF_MONTH, dateValues[DateTimeUtils.PARSE_DATE_DAY]);

									event.setGameDate(calendar.getTime());
									event.setGameTime(timeStr);
									event.setHome(homeStr);
									event.setVisitor(awayStr);
									event.setLocation(iceStr);
									event.setGameNumber(gameNumber);
									schedule.add(event);
								}

							}
						} catch (Exception e) {
							System.err.println("Last Line Read:" + temp);
							e.printStackTrace();
						}
						previous = inputLine;
					}
					in.close();

				} else {
					LOGGER.warning("************ No League Schedule for: " + team);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected String stripTag(String input) {
		String result = input.replaceAll("<.*?>", "");
		result = result.replaceAll("&nbsp;", "");
		return result;
	}

	public static void main(String[] args) throws ParserException {

		NCRRLSchedule loader = new NCRRLSchedule("U14 Tween A Wippel");
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		try {

			List<ScheduleRecord> records = loader.findEntryForDay(formatter.parse("02/01/2016"));
			for (int i = 0; i < records.size(); i++) {
				NCRRLSchedule.LOGGER.info(records.get(i).getHome() + ":" + records.get(i).getVisitor() + ":"
						+ records.get(i).getGameDate() + ":" + records.get(i).getGameTime() + ":"
						+ records.get(i).getGameNumber());
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
