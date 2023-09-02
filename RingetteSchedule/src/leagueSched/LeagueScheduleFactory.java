package leagueSched;

import java.util.Hashtable;

import utils.Config;

public class LeagueScheduleFactory {
	private Hashtable<String, AbstractLeagueSchedule> leagueScheduleLookup = new Hashtable<String, AbstractLeagueSchedule>();
	private static LeagueScheduleFactory instance = new LeagueScheduleFactory();

	private LeagueScheduleFactory() {
	}

	public static LeagueScheduleFactory getInstance() {
		return instance;
	}

	public AbstractLeagueSchedule getLeagueSchedule(String team) {
		AbstractLeagueSchedule schedule = leagueScheduleLookup.get(team);
		if (schedule == null) {
			String url = Config.getInstance().GetConfig(team).getUrl();

			if (url != null && (url.contains("ncrrl"))) {
				schedule = new NCRRLSchedule(team);
			} else if (url != null && url.contains("https://membres.ringuette-quebec.qc.ca")) {
				schedule = new LERQSchedule(team);
			} else if (url != null && url.contains("http://www.nationalringetteleague.ca")) {
				schedule = new NRLSchedule(team);
			} else {
				// default, create an empty schedule
				schedule = new EmptySchedule(team);
			}

			leagueScheduleLookup.put(team, schedule);
		}
		return schedule;
	}

}
