/**
 * 
 */
package ice;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;

import lombok.extern.java.Log;
import model.Event;
import model.ShareValue;

/**
 * @author andrewmcgregor
 *
 */
@Log
public class IceDataSwerk implements IceData {

	private final static String DATA_FILENAME = "swerkdata.csv";
	private Hashtable<String, List<Integer>> teamsLookup = new Hashtable<String, List<Integer>>();
	private List<Event> iceEvents = new ArrayList<Event>();

	private final static int CSV_OFFSET_DATE = 0;
	private final static int CSV_OFFSET_START_TIME = 1;
	private final static int CSV_OFFSET_SHORT_LABEL = 4;
	private final static int CSV_OFFSET_EVENT_TYPE = 5;
	private final static int CSV_OFFSET_TEAM = 7;
	private final static int CSV_OFFSET_LOCATION = 9;
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

	IceDataSwerk() {
		try {
			CSVReader reader = null;
			// parsing a CSV file into CSVReader class constructor
			reader = new CSVReader(new FileReader(DATA_FILENAME));
			String[] nextLine;
			
			// Skip first line
			nextLine = reader.readNext();
			if (!nextLine[CSV_OFFSET_DATE].equals("Date")) {
				throw new Exception("'Date' must be first token of CSV file");
			}
			
			// Process lines
			while ((nextLine = reader.readNext()) != null) {
				log.log((Level.SEVERE), "Tokens: {0}, {1}, {4}, {5}, {7}, {9}", nextLine);
				Date date = formatter.parse(nextLine[CSV_OFFSET_DATE]);
				String share = nextLine.toString().trim();
				String iceTime = nextLine[CSV_OFFSET_START_TIME];
				String team = nextLine[CSV_OFFSET_TEAM];
				String location = parseLocationFromIceInfo(nextLine[CSV_OFFSET_LOCATION]);
				if (location == null || location.isEmpty()) {
					log.warning("Unknown location: " + nextLine[CSV_OFFSET_LOCATION]);
				}
				String normalizedLocation = ArenaMapper.getInstance().getProperty(location);
				if (normalizedLocation == null) {
					ArenaMapper.getInstance().addError(location);
				}
				Event event = new Event(team,
						normalizedLocation != null ? normalizedLocation : location,
						ShareValue.fromShortString(share), null, date, iceTime, null);
				iceEvents.add(event);
				
			}
		} catch (Exception e) {
			System.out.println("Working directory is " + System.getProperty("user.dir"));
			e.printStackTrace();
			throw new Error(e);
		}
	}

	private String parseLocationFromIceInfo(String location) {
		return ArenaMapper.getInstance().getProperty(location);
	}

	@Override
	public String getWeekComment(int week) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllTeams() {
		return Collections.list(teamsLookup.keys());
	}

	@Override
	public boolean isValidTeam(String team) {
		return teamsLookup.containsKey(team);
	}

	@Override
	public List<Event> getIceEvents(String team) {
		List<Event> teamEvents = iceEvents.stream().filter(i -> i.getTeam().equals(team)).collect(Collectors.toList());
		Collections.sort(teamEvents);
		return teamEvents;
	}

	@Override
	public List<Event> getIceEvents() {
		return iceEvents;
	}

	@Override
	public String getShareTeam(Date date, String time, String location, String team) {
		if (date == null || time == null || location == null || team == null) {
			// no point running expensive lookup
			return null;
		}

		Event matchingEvent = null;
		try {
			String normalizedLocation = ArenaMapper.getInstance().getProperty(location);

			if (normalizedLocation != null) {
				matchingEvent = iceEvents.stream().filter(e -> normalizedLocation.equals(e.getLocation())
						&& date.equals(e.getDate()) && time.equals(e.getTime()) && !team.equals(e.getTeam())).findAny()
						.orElse(null);
			} else {
				log.warning("******************* Loader Error: No normalized location found for " + location
						+ ".  Add rink name to ArenaMapper.xml");
			}
		} catch (Exception e) {
			dump();
		}
		return matchingEvent != null ? matchingEvent.getTeam() : null;
	}

	@Override
	public void dump() {
		for (String team : teamsLookup.keySet()) {
			log.fine(team);
		}
		for (Event event : iceEvents) {
			log.fine(event.dump());
		}
		ArenaMapper.getInstance().dumpErrors();

	}

}
