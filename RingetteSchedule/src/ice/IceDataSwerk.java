/**
 * 
 */
package ice;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;

import lombok.extern.java.Log;
import model.Event;
import model.ShareValue;
import utils.Config;

/**
 * @author andrewmcgregor
 *
 */
@Log
public class IceDataSwerk implements IceData {

	private final static Set<String> teamsToDiscard = new HashSet<>(Arrays.asList(
			"BURN", // Ice being discarded
			"Sort-U12A",
			"Sort-U12B",
			"Sort-U12C",
			"Sort-U14B",
			"Sort-U14C",
			"Sort-U16B",
			"Sort-U16C",
			"Sort-U19B",
			"Sort-FUN2",
			"Sort-FUN3",
			"U19AA",
			"Erika",
			"WarmUps",
			"Coaches",
			"U14-U16 Co",
			"Avalanche",
			"Blizzard",
			"Moms",
			"Carl",
			"Goalie"));
	
	private final static String DATA_FILENAME = "swerkdata.csv";
	private Set<String> teamsLookup = new HashSet<String>();
	private List<Event> iceEvents = new ArrayList<Event>();
	// Map from date/time/arena+sheet to list of Events - this allows a quick lookup of shared events
	Map<String, List<Event>> eventShareList = new HashMap<String, List<Event>> ();

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

			Map<String, String> swerkTeamNamesToTeamNamesMap = Config.getInstance().getSwerkTeamNamesToTeamNamesMap();
			
			// Skip first line
			nextLine = reader.readNext();
			if (!nextLine[CSV_OFFSET_DATE].equals("Date")) {
				throw new Exception("'Date' must be first token of CSV file");
			}
			
			
			// Process lines
			while ((nextLine = reader.readNext()) != null) {
				log.log((Level.FINER), "Tokens: {0}, {1}, {4}, {5}, {7}, {9}", nextLine);
				Date date = formatter.parse(nextLine[CSV_OFFSET_DATE]);
				String iceTime = nextLine[CSV_OFFSET_START_TIME];
				String swerkTeamName = nextLine[CSV_OFFSET_TEAM];
				if (swerkTeamName.length() == 0)
					continue;
				
				// Used to discard certain ICE times for eaxample
				if (teamsToDiscard.contains(swerkTeamName)) {
					continue;
				}
				
				String team = swerkTeamNamesToTeamNamesMap.getOrDefault(swerkTeamName, null);
				if (team == null) {
					throw new Exception("No swerkTeamName found for '" + swerkTeamName + "'");
				}
				
				if (!teamsLookup.contains(team)) {
					teamsLookup.add(team);
				}
					
				String normalizedLocation = parseLocationFromIceInfo(nextLine[CSV_OFFSET_LOCATION]);
				if (normalizedLocation == null || normalizedLocation.isEmpty()) {
					log.warning("Unknown location: " + nextLine[CSV_OFFSET_LOCATION]);
					ArenaMapper.getInstance().addError(normalizedLocation);
					break;
				}
				String sheet = parseSheetFromIceInfo(nextLine[CSV_OFFSET_LOCATION]);
				
				// We can't set the "share value" yet because we don't know all the events so we set it to Other.
				Event event = new Event(team,
						normalizedLocation, sheet,
						ShareValue.OTHER, null, date, iceTime, null);
				iceEvents.add(event);
				
				String eventShareListKey = getShareKey(event.getDate(), event.getTime(), event.getLocation(), event.getSheet());
				List<Event> eventShareListItem = eventShareList.get (eventShareListKey);
				if (eventShareListItem == null) {
					List<Event> l = new ArrayList<Event>();
					l.add(event);
					eventShareList.put(eventShareListKey, l);
				} else {
					eventShareListItem.add(event);
				}
				
					
			} // While
			
			// Set the "share value"
			for (Event event: iceEvents) {
				String eventShareListKey = getShareKey(event.getDate(), event.getTime(), event.getLocation(), event.getSheet());
				List<Event> eventList = eventShareList.get(eventShareListKey);
				if (eventList.size() == 1) {
					event.setShareValue(ShareValue.FULL);
				}
				else if (eventList.size() == 2) {
					event.setShareValue(ShareValue.HALF);
				}
				else
				{
					throw new Error("Bad lookup for share team.  Expected 1 or 2 entries found " + eventList.size());
				}
			}	
		} catch (Exception e) {
			System.out.println("Working directory is " + System.getProperty("user.dir"));
			e.printStackTrace();
			throw new Error(e);
		}
	}

	private String getShareKey(Date date, String time, String location, String sheet) {
		return date.toString() + ":" + time + ":" + location + ":" + sheet;
	}

	private String parseLocationFromIceInfo(String locationWithSheet) {
		// Delete everything after the ":" which indicates the sheet within the location
		String location = locationWithSheet.replaceAll(":.*", "");
		return ArenaMapper.getInstance().getProperty(location);
	}

	private String parseSheetFromIceInfo(String locationWithSheet) {
		// Delete everything before the ":" which indicates the location, leaving the sheet
		return locationWithSheet.replaceAll(".*:", "");
	}

	@Override
	public String getWeekComment(int week) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAllTeams() {
		return new ArrayList<String>(teamsLookup);
	}

	@Override
	public boolean isValidTeam(String team) {
		return teamsLookup.contains(team);
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
	public String getShareTeam(Date date, String time, String location, String sheet,
			String team) {
		if (date == null || time == null || location == null || team == null) {
			// no point running expensive lookup
			return null;
		}

		String key = getShareKey(date, time, location, sheet);
		List<Event> eventList = eventShareList.get(key);
		if (eventList.size() == 1) {
			return null;
		}
		else if (eventList.size() == 2) {
			for (Event e: eventList)
				if (!e.getTeam().equals(team))
					return e.getTeam();
			throw new Error("Bad lookup for share team for:" + team);
		}
		else
		{
			throw new Error("Bad lookup for share team.  Expected 1 or 2 entries found " + eventList.size());
		}
	}
	@Override
	public void dump() {
		for (String team : teamsLookup) {
			log.fine(team);
		}
		for (Event event : iceEvents) {
			log.fine(event.dump());
		}
		ArenaMapper.getInstance().dumpErrors();

	}

}
