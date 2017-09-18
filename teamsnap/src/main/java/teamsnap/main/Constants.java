package teamsnap.main;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Constants {
	public static final String ME = "2419173";
	public static final String MY_LEAGUE = "232815";
	public static final String ADMIN_DIVISION = "234174";
	public static final String TEST_TEAM_ID = "3613778";
	public static final String BREWER_ID = "29250308";
	public static final String TEAMSNAP_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(TEAMSNAP_DATE_FORMAT);

	static {
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
}
