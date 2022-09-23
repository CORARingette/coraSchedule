package schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Context {

	public static final boolean loadNCRRL = true;
	public static final boolean loadLERQ = true;

	// This is the date of the first Saturday of the schedule which should be
	// week 1
	private String scheduleStartDateString = "2020/08/15";
	private String scheduleStartDateFormat = "yyyy/MM/dd";

	private int processingEndWeek;
	private Date scheduleStartDate = null;

	private static Context instance = new Context();

	private Context() {
		try {
			SimpleDateFormat formater = new SimpleDateFormat(scheduleStartDateFormat);
			scheduleStartDate = formater.parse(scheduleStartDateString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Context getInstance() {
		return instance;
	}

	public int getProcessingEndWeek() {
		return processingEndWeek;
	}

	public void setProcessingEndWeek(int processingEndWeek) {
		this.processingEndWeek = processingEndWeek;
	}

	public Date getScheduleStartDate() {
		return scheduleStartDate;
	}

	public Date getWeekStartDate(int week) {
		Date d = null;
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(scheduleStartDateFormat);
		try {
			Date date = format.parse(scheduleStartDateString);
			calendar.setTime(date);
			calendar.add(Calendar.WEEK_OF_YEAR, week - 1);
			d = calendar.getTime();

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d;
	}

	public int getWeekFromDate(Date date) {
		int scheduleWeek = 0;

		Calendar start = Calendar.getInstance();
		start.setTime(Context.getInstance().getScheduleStartDate());
		start.setFirstDayOfWeek(Calendar.SATURDAY);
		start.setMinimalDaysInFirstWeek(1);

		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.setFirstDayOfWeek(start.get(Calendar.DAY_OF_WEEK));
		now.setMinimalDaysInFirstWeek(1);
		now.setFirstDayOfWeek(Calendar.SATURDAY);

		int currentWeek = now.get(Calendar.WEEK_OF_YEAR);
		int scheduleStartWeek = start.get(Calendar.WEEK_OF_YEAR);
		scheduleWeek = currentWeek - scheduleStartWeek + 1;
		if (scheduleWeek <= 0)
			scheduleWeek += start.getActualMaximum(Calendar.WEEK_OF_YEAR);
		return scheduleWeek;
	}

}
