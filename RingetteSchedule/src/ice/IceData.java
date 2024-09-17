package ice;

import java.util.Date;
import java.util.List;

import model.Event;

public interface IceData {

	String getWeekComment(int week);

	List<String> getAllTeams();

	boolean isValidTeam(String team);

	List<Event> getIceEvents(String team);

	List<Event> getIceEvents();

	String getShareTeam(Date date, String time, String location, String team);

	void dump();

}