package processing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import leagueSched.AbstractLeagueSchedule;
import leagueSched.LERQSchedule;
import leagueSched.LeagueScheduleFactory;
import leagueSched.NCRRLSchedule;
import leagueSched.NRLSchedule;
import leagueSched.ScheduleRecord;
import model.Event;
import schedule.ArenaMapper;
import schedule.Context;
import schedule.IceSpreadsheet;
import utils.Config;

public abstract class AbstractTeamEventProcessor {

	private static final Logger LOGGER = Logger.getLogger(AbstractTeamEventProcessor.class.getName());

	protected abstract void preProcess(String team);

	protected abstract void postProcess(String team);

	protected abstract void process(Event iceEvent);

	public void doProcessingAll() {
		for (String team : IceSpreadsheet.getInstance().getAllTeams()) {
			doProcessing(team);
		}

	}

	public void doProcessing(String team) {

		AbstractLeagueSchedule schedule = LeagueScheduleFactory.getInstance().getLeagueSchedule(team);
		if (schedule == null) {
			LOGGER.severe("Cannot process team: " + team + ", no league schedule found");
			return;
		}

		preProcess(team);

		int eventDayCounter = 0;
		// keep a list of events that match spreadsheet, will load rest of
		// events from league schedule later
		List<ScheduleRecord> usedCalendarEvents = new ArrayList<ScheduleRecord>();
		Date currentDate = new Date(0);
		List<ScheduleRecord> records = null;

		for (Event iceEvent : IceSpreadsheet.getInstance().getIceEvents(team)) {

			iceEvent.setShareTeam(
					IceSpreadsheet.getInstance().getShareTeam(iceEvent.getDate(), iceEvent.getLocation(), team));

			// reset on new date
			if (!iceEvent.getDate().equals(currentDate)) {
				eventDayCounter = 0;
				currentDate = iceEvent.getDate();
				records = schedule.findEntryForDay(iceEvent.getDate());
			}

			if (iceEvent.isGame()) {
				// look up records that match
				if (records.size() > 0) {
					// for each daily event, we need a schedule record
					// match
					if (eventDayCounter <= (records.size() - 1)) {
						ScheduleRecord scheduleRecord = records.get(eventDayCounter);
						if (iceEvent.getShareTeam() == null) {
							iceEvent.setShareTeam(scheduleRecord.getHome().equals(team) ? scheduleRecord.getVisitor()
									: scheduleRecord.getHome());
						}
						if (iceEvent.getLocation() == null) {
							iceEvent.setLocation(scheduleRecord.getLocation());
						}
						iceEvent.setGameNumber(scheduleRecord.getGameNumber());
						usedCalendarEvents.add(scheduleRecord);
						eventDayCounter++;
					}
				} else {
					System.err
							.println("No league schedule entry found - Team: " + team + " Date: " + iceEvent.getDate());
				}
			}

			prepareForProcessing(iceEvent);
		}
		// process games that aren't in ice schedule but are in league schedule
		processUnmatchedEvents(team, schedule, usedCalendarEvents);
		postProcess(team);

	}

	private void processUnmatchedEvents(String team, AbstractLeagueSchedule schedule,
			List<ScheduleRecord> usedCalendarEvents) {

		Collections.sort(usedCalendarEvents);

		// find that last processed ice record from ice schedule
		ScheduleRecord lastRecord;
		if (usedCalendarEvents.size() > 0) {
			lastRecord = usedCalendarEvents.get(usedCalendarEvents.size() - 1);
		} else {
			lastRecord = new ScheduleRecord();
			lastRecord.setGameDate(new Date(0));
			lastRecord.setGameTime("0:00");
		}

		// process any records in league schedule that fall after last,
		// essentially populate future games
		for (ScheduleRecord scheduleRecord : schedule.getAllEntries()) {
			// make sure date falls beyond last record and is not prior to
			// schedule start
			if (scheduleRecord.compareTo(lastRecord) > 0
					&& scheduleRecord.getGameDate().compareTo(Context.getInstance().getScheduleStartDate()) < 0) {
				// Create ice event from schedule record
				Event iceEvent = new Event(team, scheduleRecord.getLocation(), scheduleRecord.getShareValue(team),
						scheduleRecord.getShareTeam(team), scheduleRecord.getGameDate(), scheduleRecord.getGameTime(),
						scheduleRecord.getGameNumber());
				prepareForProcessing(iceEvent);
			}
		}

	}

	private void prepareForProcessing(Event iceEvent) {

		// look up normalized arena name
		String resolvedLoction = ArenaMapper.getInstance().getProperty(iceEvent.getLocation());
		if (resolvedLoction != null) {
			iceEvent.setLocation(resolvedLoction);
		} else {
			ArenaMapper.getInstance().addError(iceEvent.getLocation());
		}
		process(iceEvent);

	}
}
