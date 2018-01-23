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
import leagueSched.ScheduleRecord;
import model.Event;
import model.GameType;
import model.ShareValue;
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
			if (Config.getInstance().GetConfig(team).isActive()) {
				doProcessing(team);
			}
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

			if (iceEvent.isPractice() && !iceEvent.isFullIce()) {
				iceEvent.setShareTeam(IceSpreadsheet.getInstance().getShareTeam(iceEvent.getDate(), iceEvent.getTime(),
						iceEvent.getLocation(), team));
			}

			// reset on new date
			if (!iceEvent.getDate().equals(currentDate)) {
				eventDayCounter = 0;
				currentDate = iceEvent.getDate();
				records = schedule.findEntriesForDay(iceEvent.getDate());
			}

			if (iceEvent.isGame()) {
				// look up records that match
				if (records.size() > 0) {
					// for each daily event, we need a schedule record
					// match
					if (eventDayCounter <= (records.size() - 1)) {
						ScheduleRecord scheduleRecord = records.get(eventDayCounter);
						if (iceEvent.getShareTeam() == null) {
							calculateShareTeamAndValue(iceEvent, scheduleRecord);
						}
						if (iceEvent.getLocation() == null
								|| iceEvent.getLocation().equals(GameType.AWAY_GAME.getGameType())) {
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

	private void calculateShareTeamAndValue(Event event, ScheduleRecord scheduleRecord) {
		String teamName = Config.getInstance().GetConfig(event.getTeam()).getMap();
		if (scheduleRecord.getHome().equals(teamName)) {
			event.setShareTeam(scheduleRecord.getVisitor());
			event.setShareValue(ShareValue.HOME);
		} else {
			event.setShareTeam(scheduleRecord.getHome());
			event.setShareValue(ShareValue.VISITOR);
		}
	}

	private void processUnmatchedEvents(String team, AbstractLeagueSchedule schedule,
			List<ScheduleRecord> usedCalendarEvents) {
		if (schedule instanceof NCRRLSchedule && !Context.loadNCRRL)
		{
			return;
		}
		
		
		if (schedule instanceof LERQSchedule && !Context.loadLERQ)
		{
			return;
		}
		
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
					&& scheduleRecord.getGameDate().compareTo(Context.getInstance().getScheduleStartDate()) > 0) {
				// Create ice event from schedule record

				Event iceEvent = new Event(team, scheduleRecord.getLocation(), null, null, scheduleRecord.getGameDate(),
						scheduleRecord.getGameTime(), scheduleRecord.getGameNumber());
				calculateShareTeamAndValue(iceEvent, scheduleRecord);
				prepareForProcessing(iceEvent);
			}
		}

	}

	private void prepareForProcessing(Event iceEvent) {

		if (iceEvent.getLocation() == null)
		{
			LOGGER.severe("Location is null: " + iceEvent.getTeam() + ":" + iceEvent.getDate() + ":" + iceEvent.getTime());
		}
		// look up normalized arena name
		String resolvedLoction = ArenaMapper.getInstance().getProperty(iceEvent.getLocation());
		if (resolvedLoction != null) {
			iceEvent.setLocation(resolvedLoction);
		} else {
			ArenaMapper.getInstance().addError(iceEvent.getLocation());
		}
		if (iceEvent.getLocation() == null || iceEvent.getLocation().isEmpty()
				|| iceEvent.getLocation().equals("Unknown")) {
			LOGGER.severe("Location is null, empty or unknown: " + iceEvent.getTeam() + ":" + iceEvent.getTime());
		}
		process(iceEvent);

	}
}
