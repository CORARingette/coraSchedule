package processing;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import model.Event;
import schedule.EventManager;

public class TeamopolisEventGenerator extends AbstractTeamEventProcessor {

	private PrintWriter scheduleFile;
	SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
	SimpleDateFormat tf = new SimpleDateFormat("h:mm a");

	public TeamopolisEventGenerator(PrintWriter scheduleFile) {
		this.scheduleFile = scheduleFile;
	}

	@Override
	protected void postProcess(String team) {

	}

	@Override
	protected void preProcess(String team) {
	}

	@Override
	protected void process(Event iceEvent) {
		if (iceEvent.getShareValue().equals("X")) {
			return;
		}

		String line = "";
		// YYYY/MM/DD
		line += df.format(iceEvent.getDate()) + ",";
		// HH:MI AM/PM
		line += tf.format(iceEvent.getTime()) + ",";

		Calendar end = Calendar.getInstance();
		end.setTime(iceEvent.getFullDateTime());
		end.add(Calendar.HOUR_OF_DAY, 1);

		// YYYY/MM/DD
		line += df.format(end.getTime()) + ",";
		// HH:MI AM/PM
		line += tf.format(end.getTime()) + ",";

		// location
		line += iceEvent.getLocation();

		if (iceEvent.getShareValue().equals("V") || iceEvent.getShareValue().equals("H")) {
			line += "Game,";
		} else {
			line += "Practice,";
		}

		line += iceEvent.getSummary() + ",";

		line += "Active" + ",";

		// Division
		String team = iceEvent.getTeam();
		String div;
		if (team.equals("NRL")) {
			div = "NRL";
		} else if (team.contains("Bunnies")) {
			div = "Bunnies";
		} else {
			int divEnd = team.indexOf(" ");
			div = team.substring(0, divEnd);
		}
		line += div + ",";

		// Home , vistor always blank
		line += iceEvent.getTeam() + ", ,";

		line += EventManager.getInstance().makeEventKeyString(iceEvent.getTeam(), iceEvent.getFullDateTime()) + ",";

		line += "FALSE";
		scheduleFile.println(line);

	}
}
