package schedule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import lombok.extern.java.Log;

@Log
public class EventManager {
	private static EventManager instance = new EventManager();
	private static int eventCounter = 0;
	private HashSet<String> keys = new HashSet<String>();

	private EventManager() {
	}

	public static EventManager getInstance() {
		return instance;
	}

	public String makeEventKeyString(String team, Date calendar) {
		SimpleDateFormat dateformater = new SimpleDateFormat("yyyyMMdd");
		String dateString = dateformater.format(calendar.getTime());
		String key = team + "." + dateString + "." + calendar.getTime();
		if (keys.contains(key)) {
			key += "." + eventCounter++;
			log.warning("Event Conflict:" + team + ":" + calendar.getTime());
		}
		keys.add(key);

		return key;
	}

}
