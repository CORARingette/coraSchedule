package processing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ice.ArenaMapper;
import ice.IceDataGlobal;
import leagueSched.AbstractLeagueSchedule;
import leagueSched.EmptySchedule;
import leagueSched.LERQSchedule;
import leagueSched.LeagueScheduleFactory;
import leagueSched.NCRRLSchedule;
import leagueSched.ScheduleRecord;
import lombok.extern.java.Log;
import model.Event;
import model.GameType;
import model.ShareValue;
import schedule.Context;
import utils.Config;
import utils.Config.ConfigItem;

@Log
public abstract class AbstractTeamEventProcessor {

	protected abstract void preProcess(String team);

	protected abstract void postProcess(String team);

	protected abstract void process(Event iceEvent);

	public void doProcessingAll() {
		for (String team : IceDataGlobal.getInstance().getAllTeams()) {
			ConfigItem teamData = Config.getInstance().GetConfig(team);
			if (teamData == null) {
				throw new Error("This team is not in the TeamConfig file: '" + team + "'");
			}
			if (teamData.isActive()) {
				doProcessing(team);
			} else {
				log.warning("TEAM '" + team + "' IS DISABLED");
			}
		}

	}

	public void doProcessing(String team) {

		AbstractLeagueSchedule schedule = LeagueScheduleFactory.getInstance().getLeagueSchedule(team);
		if (schedule instanceof EmptySchedule) {
			log.warning("Empty schedule created for team: " + team + ", no league schedule configured or found");
		}

		preProcess(team);

		int eventDayCounter = 0;
		// keep a list of events that match spreadsheet, will load rest of
		// events from league schedule later
		List<ScheduleRecord> usedCalendarEvents = new ArrayList<ScheduleRecord>();
		Date currentDate = new Date(0);
		List<ScheduleRecord> records = null;

		for (Event iceEvent : IceDataGlobal.getInstance().getIceEvents(team)) {

			if (ShareValue.isPractice(iceEvent.getShareValue()) && !ShareValue.isFullIce(iceEvent.getShareValue())) {
				iceEvent.setShareTeam(IceDataGlobal.getInstance().getShareTeam(iceEvent.getDate(), iceEvent.getTime(),
						iceEvent.getLocation(), iceEvent.getSheet(), team));
				if (iceEvent.getShareTeam() == null) {
					log.warning("Share team not found: " + iceEvent.getTeam() + ":" + iceEvent.getFullDateTime());
				}
			}

			// reset on new date
			if (!iceEvent.getDate().equals(currentDate)) {
				eventDayCounter = 0;
				currentDate = iceEvent.getDate();
				records = schedule.findEntriesForDay(iceEvent.getDate());
			}

			// This is very rarely used. 99% of games are handled below in
			// processUnmatchedEvents()
			if (ShareValue.isGame(iceEvent.getShareValue())) {
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

	@SuppressWarnings("unused")
	private void processUnmatchedEvents(String team, AbstractLeagueSchedule schedule,
			List<ScheduleRecord> usedCalendarEvents) {
		if (schedule instanceof NCRRLSchedule && !Context.loadNCRRL) {
			return;
		}

		if (schedule instanceof LERQSchedule && !Context.loadLERQ) {
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

		String location = iceEvent.getLocation();
		// Once there was a location list "Richmond\nGame moved to 6:00" so we get rid
		// of the \n and everything after that.
		if (location.contains("\n")) {
			location = location.split("\n")[0];
		}
		if (location == null) {
			log.severe("Location is null: " + iceEvent.getTeam() + ":" + iceEvent.getDate() + ":" + iceEvent.getTime());
		}
		// look up normalized arena name
		String resolvedLoction = ArenaMapper.getInstance().getProperty(location);
		if (resolvedLoction != null) {
			iceEvent.setLocation(resolvedLoction);
		} else {
			ArenaMapper.getInstance().addError(location);
		}
		if (iceEvent.getLocation() == null || iceEvent.getLocation().isEmpty()
				|| iceEvent.getLocation().equals("Unknown")) {
			log.severe("Location is null, empty or unknown: " + iceEvent.getTeam() + ":" + iceEvent.getTime());
		}
		process(iceEvent);

	}
}
