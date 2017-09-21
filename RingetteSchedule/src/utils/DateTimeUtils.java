package utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

public final class DateTimeUtils {

	public final static int PARSE_TIME_HOUR = 0;
	public final static int PARSE_TIME_MINUTE = 1;

	public final static int PARSE_DATE_YEAR = 0;
	public final static int PARSE_DATE_MONTH = 1;
	public final static int PARSE_DATE_DAY = 2;

	private static Hashtable<String, Integer> monthMap = new Hashtable<String, Integer>();

	private DateTimeUtils() {
	}

	public static int[] parseTime(String timeString) {

		timeString.trim();

		int[] retVal = new int[2];

		int i = timeString.indexOf(":");
		if (i > -1) {
			if (i == 1) {
				retVal[PARSE_TIME_HOUR] = Integer.valueOf(timeString.substring(i - 1, i));
				retVal[PARSE_TIME_MINUTE] = Integer.valueOf(timeString.substring(i + 1, i + 3));

			} else {
				retVal[PARSE_TIME_HOUR] = Integer.valueOf(timeString.substring(i - 2, i));
				retVal[PARSE_TIME_MINUTE] = Integer.valueOf(timeString.substring(i + 1, i + 3));
			}
		}

		return retVal;
	}

	public static int[] parseFrenchTime(String timeString) {

		timeString.trim();

		int[] retVal = new int[2];

		int i = timeString.indexOf("h");
		if (i > -1) {
			if (i == 1) {
				retVal[PARSE_TIME_HOUR] = Integer.valueOf(timeString.substring(i - 1, i));
				retVal[PARSE_TIME_MINUTE] = Integer.valueOf(timeString.substring(i + 1, i + 3));

			} else {
				retVal[PARSE_TIME_HOUR] = Integer.valueOf(timeString.substring(i - 2, i));
				retVal[PARSE_TIME_MINUTE] = Integer.valueOf(timeString.substring(i + 1, i + 3));
			}
		}

		return retVal;
	}

	public static int[] parseTimeAMPM(String timeString) {

		timeString.trim();

		int[] retVal = new int[2];

		int i = timeString.indexOf(":");
		if (i > -1) {
			if (i == 1) {
				retVal[PARSE_TIME_HOUR] = Integer.valueOf(timeString.substring(i - 1, i));
				retVal[PARSE_TIME_MINUTE] = Integer.valueOf(timeString.substring(i + 1, i + 3));

			} else {
				retVal[PARSE_TIME_HOUR] = Integer.valueOf(timeString.substring(i - 2, i));
				retVal[PARSE_TIME_MINUTE] = Integer.valueOf(timeString.substring(i + 1, i + 3));
			}
		}

		i = timeString.indexOf(" ");
		String ampm = timeString.substring(i + 1, i + 3);
		if (ampm.equals("pm")) {
			retVal[PARSE_TIME_HOUR] += 12;
		}
		return retVal;
	}

	public static int[] parseDateYYYYMMDDT(String dateString) {

		dateString.trim();

		int[] retVal = new int[3];

		retVal[PARSE_DATE_YEAR] = Integer.valueOf(dateString.substring(0, 4));
		retVal[PARSE_DATE_MONTH] = Integer.valueOf(dateString.substring(5, 7)) - 1;
		retVal[PARSE_DATE_DAY] = Integer.valueOf(dateString.substring(8, 10));

		return retVal;
	}

	// Sat, Nov 7, 2015
	public static int[] parseDateDDD_MMM_DD_YY(String dateString) {

		dateString.trim();

		int[] retVal = new int[3];

		int i = dateString.indexOf(",");
		String monthStr = dateString.substring(i + 2, i + 5);
		retVal[PARSE_DATE_MONTH] = monthMap.get(monthStr).intValue();

		int j = dateString.lastIndexOf(",");
		String dayString = dateString.substring(i + 6, j);
		retVal[PARSE_DATE_DAY] = Integer.valueOf(dayString);

		retVal[PARSE_DATE_YEAR] = Integer.valueOf(dateString.substring(j + 2, j + 6));
		// String monthStr = dateString.substring(5, 8);

		return retVal;
	}

	public static int[] parseDateDDMMMYY(String dateString) {

		dateString.trim();

		int[] retVal = new int[3];

		retVal[PARSE_DATE_YEAR] = Integer.valueOf(dateString.substring(7, 9)) + 2000;
		String monthStr = dateString.substring(3, 6);
		retVal[PARSE_DATE_MONTH] = monthMap.get(monthStr).intValue();
		retVal[PARSE_DATE_DAY] = Integer.valueOf(dateString.substring(0, 2));

		return retVal;
	}

	public static int[] parseDateDorDDMMMYY(String dateString) {

		dateString.trim();

		int[] retVal = new int[3];

		String monthStr;
		String yearStr;
		String dayStr;
		if (dateString.indexOf("-") == 1) {
			yearStr = dateString.substring(6, 8);
			monthStr = dateString.substring(2, 5);
			dayStr = dateString.substring(0, 1);
		} else {
			yearStr = dateString.substring(7, 9);
			monthStr = dateString.substring(3, 6);
			dayStr = dateString.substring(0, 2);
		}
		retVal[PARSE_DATE_YEAR] = Integer.valueOf(yearStr) + 2000;
		retVal[PARSE_DATE_MONTH] = monthMap.get(monthStr).intValue();
		retVal[PARSE_DATE_DAY] = Integer.valueOf(dayStr);

		return retVal;
	}

	public static int[] parseDateDDMMYYYY(String dateString) {
		// DD/MM/YYYY

		dateString.trim();

		int[] retVal = new int[3];

		retVal[PARSE_DATE_YEAR] = Integer.valueOf(dateString.substring(6, 10));
		retVal[PARSE_DATE_MONTH] = Integer.valueOf(dateString.substring(3, 5)) - 1;
		retVal[PARSE_DATE_DAY] = Integer.valueOf(dateString.substring(0, 2));

		return retVal;
	}

	public static int[] parseDateDDMMYY(String dateString) {

		dateString.trim();

		int[] retVal = new int[3];
		int firstDash = dateString.indexOf("-");
		int secondDash = dateString.indexOf("-", firstDash + 1);

		retVal[PARSE_DATE_DAY] = Integer.valueOf(dateString.substring(0, firstDash));
		retVal[PARSE_DATE_MONTH] = Integer.valueOf(dateString.substring(firstDash + 1, secondDash)) - 1;
		retVal[PARSE_DATE_YEAR] = Integer.valueOf(dateString.substring(secondDash + 3, secondDash + 5)) + 2000;

		return retVal;
	}

	public static Date makeFullDateFromDateAndTime(Date date, String time) {
		Calendar eventCalendar = Calendar.getInstance();
		eventCalendar.setTimeInMillis(date.getTime());

		// Add the time portion
		if (time != null) {
			int[] timeValues = DateTimeUtils.parseTime(time.toString());
			eventCalendar.set(Calendar.HOUR_OF_DAY, timeValues[DateTimeUtils.PARSE_TIME_HOUR]);
			eventCalendar.set(Calendar.MINUTE, timeValues[DateTimeUtils.PARSE_TIME_MINUTE]);
		} else {
			eventCalendar.set(Calendar.HOUR_OF_DAY, 0);
			eventCalendar.set(Calendar.MINUTE, 0);
		}
		return eventCalendar.getTime();
	}

	public static Date makeTruncatedDate(Date startDate) {
		java.util.Calendar cal = java.util.Calendar.getInstance(); // locale-specific
		cal.setTime(startDate);
		cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
		cal.set(java.util.Calendar.MINUTE, 0);
		cal.set(java.util.Calendar.SECOND, 0);
		cal.set(java.util.Calendar.MILLISECOND, 0);
		long time = cal.getTimeInMillis();
		return new Date(time);
	}

	public static String makeTruncatedTime(Date startDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(startDate);
	}

	static {
		monthMap.put("Jan", Integer.valueOf(0));
		monthMap.put("Feb", Integer.valueOf(1));
		monthMap.put("Mar", Integer.valueOf(2));
		monthMap.put("Apr", Integer.valueOf(3));
		monthMap.put("May", Integer.valueOf(4));
		monthMap.put("Jun", Integer.valueOf(5));
		monthMap.put("Jul", Integer.valueOf(6));
		monthMap.put("Aug", Integer.valueOf(7));
		monthMap.put("Sep", Integer.valueOf(8));
		monthMap.put("Oct", Integer.valueOf(9));
		monthMap.put("Nov", Integer.valueOf(10));
		monthMap.put("Dec", Integer.valueOf(11));
	}

}
