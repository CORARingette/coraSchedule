package schedule;

import java.io.IOException;
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
import utils.Config;

public class IceSpreadsheet {

	private static final Logger LOGGER = Logger.getLogger(IceSpreadsheet.class.getName());

	// sheet config
	private final int ROW_COMMENT = 1;
	private final int ROW_DATE = 3;
	private final int START_COLUMN = 18;
	private final int COLUMNS_PER_WEEK = 14;

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

						if (Config.instance.GetConfig(teamCellValue) != null) {
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

								// locate the marker for the type of ice
								if (shareCell != null && (shareCell.toString().equals("0.5")
										|| shareCell.toString().equals("1.0") || shareCell.toString().equals("H")
										|| shareCell.toString().equals("V") || shareCell.toString().equals("F")
										|| shareCell.toString().equals("<EOL>"))) {

									XSSFCell iceCell = row.getCell((int) (column - 1));
									if (iceCell != null && !iceCell.toString().isEmpty()) {

										Date date = dateRow.getCell((int) (column - 1)).getDateCellValue();
										String share = shareCell.toString();
										String iceInfo = iceCell.toString();
										String iceTime = parseTimeFromIceInfo(iceInfo);
										String location = parseLocationFromIceInfo(iceInfo);

										Event event = new Event(team, location, share, null, date, iceTime, null);
										iceEvents.add(event);

									} else {
										LOGGER.severe(
												"******************* Loader Error: No Ice Info for " + team + "Row: "
														+ teamRowIndex + " Column: " + convertColumnToLetters(column));
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
		int offset = 0;
		int count = 0;
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

	public List<Event> getIceEvents(String team) {
		List<Event> teamEvents = iceEvents.stream().filter(i -> i.getTeam().equals(team)).collect(Collectors.toList());
		Collections.sort(teamEvents);
		return teamEvents;
	}

	public String getShareTeam(Date date, String location, String team) {
		Event matchingEvent = iceEvents.stream().findAny()
				.filter(e -> e.getLocation().equals(location) && e.getDate().equals(date) && !e.getTeam().equals(team))
				.orElse(null);
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

}
