package leagueSched;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.htmlparser.util.ParserException;

import lombok.extern.java.Log;
import utils.Config;
import utils.DateTimeUtils;

@Log
public class NRLSchedule extends AbstractLeagueSchedule {

	public NRLSchedule(String team) {
		super(team);
		try {

			String url = Config.getInstance().GetConfig(team).getUrl();

			if (url != null) {
				URL ncrrl = new URL(url);
				BufferedReader in = new BufferedReader(new InputStreamReader(ncrrl.openStream()));

				String inputLine;
				String homeStr = null;
				String awayStr = null;
				String locStr = null;
				String dateStr = null;
				String timeStr = null;

				while ((inputLine = in.readLine()) != null) {
					// <td class="ls-schedule ls-date">
					if (inputLine.matches("\\s*<td class=\"ls-schedule ls-date\">")) {
						dateStr = cleanDate(in.readLine());
					}

					if (inputLine.matches("\\s*<td class=\"ls-schedule ls-time\">.*")) {
						timeStr = cleanTime(inputLine);
					}

					// <td class="ls-schedule ls-visiting"><a
					// href="roster.php?season_id=36&amp;team_id=5">Ottawa
					// Ice</a></td>
					if (inputLine.matches("\\s*<td class=\"ls-schedule ls-visiting\">.*")) {
						awayStr = cleanTeam(inputLine);
					}
					if (inputLine.matches("\\s*<td class=\"ls-schedule ls-home\">.*")) {
						homeStr = cleanTeam(inputLine);
					}

					if (inputLine.matches("\\s*<td class=\"ls-schedule ls-venue\">.*")) {
						locStr = cleanLoc(inputLine);
					}
					if (homeStr != null && awayStr != null && locStr != null && dateStr != null && timeStr != null) {
						// create record
						ScheduleRecord event = new ScheduleRecord();
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(0);
						int[] timeValues = DateTimeUtils.parseTimeAMPM(timeStr);
						int[] dateValues = DateTimeUtils.parseDateDDD_MMM_DD_YY(dateStr);
						calendar.set(Calendar.YEAR, dateValues[DateTimeUtils.PARSE_DATE_YEAR]);
						calendar.set(Calendar.MONTH, dateValues[DateTimeUtils.PARSE_DATE_MONTH]);
						calendar.set(Calendar.DAY_OF_MONTH, dateValues[DateTimeUtils.PARSE_DATE_DAY]);

						event.setHome(homeStr);
						event.setVisitor(awayStr);
						event.setGameDate(calendar.getTime());
						event.setGameTime(timeValues[0] + ":" + timeValues[1]);
						event.setLocation(locStr);
						schedule.add(event);

						// reset
						homeStr = null;
						awayStr = null;
						locStr = null;
						dateStr = null;
						timeStr = null;
					}
				}
				in.close();

			} else {
				log.warning("************ No League Schedule for: " + team);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String cleanTime(String time) {
		boolean pm = time.toLowerCase().contains("pm");
		String t = time.replaceAll("\\s*<td class=\"ls-schedule ls-time\">", "").replaceAll("</td>", "")
				.replaceAll(" pm.*", "").replaceAll(" am.*", "").trim();
		if (pm) {
			String hourStr = t.substring(0, t.indexOf(":"));
			String minuteStr = t.substring(t.indexOf(":") + 1);
			int hour = Integer.valueOf(hourStr);
			if (hour < 12) {
				hour = hour + 12;
			}
			t = hour + ":" + minuteStr;
		}

		return t;
	}

	private String cleanLoc(String loc) {
		String l = loc.replaceAll("\\s*<td class=\"ls-schedule ls-venue\">", "").replaceAll(".*\\s*<br />", "")
				.replaceAll("\\s*&nbsp;", " ").replaceAll("\\s*</td>", "").trim();
		return l;

	}

	private String cleanTeam(String inputLine) {
		String t = inputLine.replaceAll("\\s*.*?team_id=[0-9]{1,2}\">", "").replaceAll("</a></td>", "")
				.replaceAll("</a></b></td>", "").trim();
		return t;
	}

	private String cleanDate(String date) {
		String d = date.replaceAll("</b></td>", "").replaceAll("\\s*</td>", "").trim();

		return d;
	}

	@Override
	public String getTeam() {
		return super.getTeam();
	}

	public static void main(String[] args) throws ParserException {

		NRLSchedule loader = new NRLSchedule("NRL Open AA");
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		try {

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(formatter.parse("07/11/2015"));

			List<ScheduleRecord> records = loader.findEntriesForDay(formatter.parse("07/11/2015"));
			for (int i = 0; i < records.size(); i++) {
				NRLSchedule.log.info(records.get(i).getHome() + ":" + records.get(i).getVisitor() + ":"
						+ records.get(i).getLocation() + ":" + records.get(i).getGameDate() + ":" + records.get(i).getGameTime());
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
