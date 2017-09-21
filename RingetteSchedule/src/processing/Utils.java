package processing;

import teamsnap.entities.EventType;

public class Utils {
	public static String parseLocationFromIceInfo(String iceInfo) {
		String location = iceInfo;
		location = location.trim();
		location = location.replaceAll("[0-9][0-9]{0,1}:[0-9]{2}", "");
		location = location.trim();
		return location;
	}


	public static EventType getEventTypeFromShareValue(String shareValue) {
		if (shareValue.equals("H") || shareValue.equals("V")) {
			return EventType.GAME;
		} else if (shareValue.equals("X")) {
			return EventType.OTHER;
		} else {
			return EventType.PRACTICE;
		}
	}
}
