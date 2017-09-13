package convertTS;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import teamsnap.entities.DivisionLocation;
import teamsnap.main.Constants;

public class LoadLocationsToTS {

	Properties arenaMapper = new Properties();

	public LoadLocationsToTS() {
		try {
			arenaMapper.loadFromXML(new FileInputStream("properties/ArenaNameMapper.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadAndProcess() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("LocationUrl.txt"));

			String line = br.readLine();

			while (line != null) {
				loadUrl(br.readLine());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void loadUrl(String location) {
		try {

			String locationStr = null;
			String addressStr = null;
			String cityStr = null;
			String mapURLStr = null;

			String urlStr = "http://www.ottawaringette.on.ca/" + location;
			System.err.println("Loading: " + urlStr);
			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();

			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			String line = null;
			while ((line = rd.readLine()) != null) {

				String startDelim = "<td class=\"inside_text3\"><b>";
				String endDelim = "";

				if (line.contains(">Location:<")) {
					startDelim = "<td class=\"inside_text3\"><b>";
					endDelim = "</b></td>";
					int startIndex = line.lastIndexOf(startDelim) + startDelim.length();
					int endIndex = line.lastIndexOf(endDelim);
					locationStr = line.substring(startIndex, endIndex).trim();
				} else if (line.contains(">Address:<")) {
					startDelim = "<td class=\"inside_text3\">";
					endDelim = "</td>";
					int startIndex = line.lastIndexOf(startDelim) + startDelim.length();
					int endIndex = line.lastIndexOf(endDelim);
					addressStr = line.substring(startIndex, endIndex).trim();
				} else if (line.contains(">City<")) {
					startDelim = "<td class=\"inside_text3\">";
					endDelim = "</td>";
					int startIndex = line.lastIndexOf(startDelim) + startDelim.length();
					int endIndex = line.lastIndexOf(endDelim);
					cityStr = line.substring(startIndex, endIndex).trim();
				} else if (line.contains(">View Larger Map<")) {
					startDelim = "marginwidth=\"0\" src=\"";
					endDelim = "\"></iframe><br";
					int startIndex = line.lastIndexOf(startDelim) + startDelim.length();
					int endIndex = line.lastIndexOf(endDelim);
					mapURLStr = line.substring(startIndex, endIndex).trim();
				}
			}

			rd.close();
			if (arenaMapper.containsValue(locationStr)) {
				System.err.println("Location:" + locationStr + ":");
				System.err.println("Address:" + addressStr + ", " + cityStr + ":");
				System.err.println("URL:" + mapURLStr + ":");
				DivisionLocation divisionLocation = new DivisionLocation(Constants.MY_LEAGUE, locationStr,
						addressStr + ", " + cityStr, mapURLStr);
				divisionLocation.create();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("Done.");
	}

	public static void main(String[] args) {
		new LoadLocationsToTS().loadAndProcess();
	}

}
