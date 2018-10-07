package schedule;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.Event;
import model.ShareValue;
import utils.Config;

public class IceSpreadsheet {

	private static final Logger LOGGER = Logger.getLogger(IceSpreadsheet.class.getName());

	// sheet config
	private final int ROW_COMMENT = 1;
	private final int ROW_DATE = 3;
	private final int START_COLUMN = 32;
	private final int COLUMNS_PER_WEEK = 14;

	// NOTE, this is only true for ice analysis
	private final boolean includeHomeGames = false;

	private final static String WORKBOOK_FILENAME = "Master.xlsx";
	private final static String SHEET_NAME = "Season";

	private XSSFWorkbook wb = null;
	private XSSFSheet currentSheet;

	private Hashtable<Integer, String> weekComment = new Hashtable<Integer, String>();
	private Hashtable<String, List<Integer>> teamsLookup = new Hashtable<String, List<Integer>>();
	private List<Event> iceEvents = new ArrayList<Event>();

	private static IceSpreadsheet instance = new IceSpreadsheet();

	private IceSpreadsheet() {
		try {
			wb = new XSSFWorkbook(WORKBOOK_FILENAME);
			currentSheet = wb.getSheet(SHEET_NAME);
			populateTeamRowMapping();
			loadWeekComments();
			load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static IceSpreadsheet getInstance() {
		return instance;
	}

	private void populateTeamRowMapping() {
		int rows = currentSheet.getPhysicalNumberOfRows();
		for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
			XSSFRow row = currentSheet.getRow(rowIndex);
			if (row != null) {
				int cells = row.getPhysicalNumberOfCells();
				if (cells > 0) {
					XSSFCell cell = row.getCell(0);
					if (cell != null) {
						String teamCellValue = cell.toString().trim();
						teamCellValue = teamCellValue.replaceAll("#", "-");

						if (Config.getInstance().GetConfig(teamCellValue) != null) {
							System.err.println(cell.toString().trim() + ":" + new Integer(rowIndex));
							String team = teamCellValue.trim();
							List<Integer> rowList = teamsLookup.get(team);
							if (rowList == null) {
								rowList = new ArrayList<Integer>();
								teamsLookup.put(team, rowList);
							}
							rowList.add(Integer.valueOf(rowIndex));
						}
					}

				}
			}

		}
		LOGGER.info("Teams: " + teamsLookup.size());
	}

	private void load() {

		LOGGER.info("Loading Started...");
		XSSFRow dateRow = currentSheet.getRow(ROW_DATE);

		try {
			for (String team : Collections.list(teamsLookup.keys())) {
				List<Integer> teamRowList = teamsLookup.get(team);
				// for each row
				for (int teamRowIndex : teamRowList) {

					XSSFRow row = currentSheet.getRow(teamRowIndex);
					if (row != null) {
						int cells = row.getLastCellNum() + 1;
						if (cells > 0) {
							for (int column = START_COLUMN; column < cells; column++) {

								XSSFCell shareCell = row.getCell(column);

								ShareValue shareValue = shareCell != null
										? ShareValue.fromShortString(shareCell.toString()) : ShareValue.OTHER;
								// load only practices, games will be loaded
								// from league schedules
								if (shareValue == ShareValue.FULL || shareValue == ShareValue.HALF
										|| (includeHomeGames && shareValue == ShareValue.HOME)) {

									XSSFCell iceCell = row.getCell((int) (column - 1));
									if (iceCell != null && !iceCell.toString().isEmpty()) {

										Date date = dateRow.getCell((int) (column - 1)).getDateCellValue();
										String share = shareCell.toString().trim();
										String iceInfo = iceCell.toString().trim();
										String iceTime = parseTimeFromIceInfo(iceInfo);
										String location = parseLocationFromIceInfo(iceInfo);
										if (location == null || location.isEmpty()) {
											LOGGER.severe("Location is null or empty in spreadsheet: " + team + "Row: "
													+ teamRowIndex + " Column: " + convertColumnToLetters(column));
										}
										String normalizedLocation = ArenaMapper.getInstance().getProperty(location);
										if (normalizedLocation == null) {
											ArenaMapper.getInstance().addError(location);
										}
										Event event = new Event(team,
												normalizedLocation != null ? normalizedLocation : location,
												ShareValue.fromShortString(share), null, date, iceTime, null);
										iceEvents.add(event);

									} else {
										LOGGER.severe("******************* Loader Error: No Ice Info for " + team
												+ "Row: " + (teamRowIndex + 1) + " Column: "
												+ convertColumnToLetters(column - 1));
									}

								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		LOGGER.info("Loading Done.");
	}

	private void loadWeekComments() {
		boolean finished = false;
		int offset = START_COLUMN;
		int count = 0;
		dumpRow(ROW_COMMENT);
		XSSFRow commentRow = currentSheet.getRow(ROW_COMMENT);
		do {
			XSSFCell commentCell = commentRow.getCell((int) offset);
			if (commentCell != null && commentCell.toString() != null && !commentCell.toString().trim().isEmpty()) {
				weekComment.put(Integer.valueOf(count + 1), commentCell.toString());
				count++;
			} else {
				finished = true;
			}
			offset += COLUMNS_PER_WEEK;
		} while (!finished);
	}

	public String getWeekComment(int week) {
		return weekComment.get(Integer.valueOf(week));
	}

	public List<String> getAllTeams() {
		return Collections.list(teamsLookup.keys());
	}

	public boolean isValidTeam(String team) {
		return teamsLookup.containsKey(team);
	}

	public List<Event> getIceEvents(String team) {
		List<Event> teamEvents = iceEvents.stream().filter(i -> i.getTeam().equals(team)).collect(Collectors.toList());
		Collections.sort(teamEvents);
		return teamEvents;
	}

	public List<Event> getIceEvents() {
		return iceEvents;
	}

	public String getShareTeam(Date date, String time, String location, String team) {
		if (date == null || time == null || location == null || team == null) {
			// no point running expensive lookup
			return null;
		}

		Event matchingEvent = null;
		try {
			String normalizedLocation = ArenaMapper.getInstance().getProperty(location);

			if (normalizedLocation != null) {
				matchingEvent = iceEvents.stream().filter(e -> normalizedLocation.equals(e.getLocation())
						&& date.equals(e.getDate()) && time.equals(e.getTime()) && !team.equals(e.getTeam())).findAny()
						.orElse(null);
			} else {
				LOGGER.severe("******************* Loader Error: No normalized location found for " + location);
			}
		} catch (Exception e) {
			dump();
		}
		if (matchingEvent == null) {
			dump();
		}
		return matchingEvent != null ? matchingEvent.getTeam() : null;
	}

	private String parseTimeFromIceInfo(String iceInfo) {
		// parse time form rink + time
		String time = null;
		int i = iceInfo.indexOf(":");
		if (i > -1) {
			time = iceInfo.substring(i - 2, i + 3);
			time = time.replace(" ", "0");
		}

		return time;
	}

	private String parseLocationFromIceInfo(String iceInfo) {
		// parse rink form rink + time
		String location = null;
		// if it contains a time in format 00:00
		if (iceInfo.matches(".*[ 12]*[0-9]:[0-5][0-9]")) {
			int i = iceInfo.trim().lastIndexOf(" ");
			if (i > -1) {
				location = iceInfo.substring(0, i);
			}
		} else {
			location = iceInfo;
		}

		return location;
	}

	// for debugging
	private String convertColumnToLetters(int column) {
		String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String majorLetters = " " + letters;

		int minorIndex = ((column) % 26);
		int majorIndex = (column / 26);
		String minor = letters.substring(minorIndex, minorIndex + 1);
		String major = majorLetters.substring(majorIndex, majorIndex + 1);

		return major + minor;
	}

	private void dumpRow(int rowNumber) {
		XSSFRow row = currentSheet.getRow(rowNumber);
		int cells = row.getPhysicalNumberOfCells();
		int firstCell = row.getFirstCellNum();
		for (int c = firstCell; c < cells; c++) {
			System.err.print(c + ":" + row.getCell(c).toString() + ";");
		}
		System.err.println();
	}

	public void dump() {
		for (Event event : iceEvents) {
			System.err.println(event.dump());
		}
		ArenaMapper.getInstance().dumpErrors();
	}

	public static void main(String[] args) {
		try {
			IceSpreadsheet ish = new IceSpreadsheet();
			SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd");
			String team = ish.getShareTeam(formater.parse("2017/09/25"), "18:00", "Carleton University", "U19AA Cram");
			System.err.println(team);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
