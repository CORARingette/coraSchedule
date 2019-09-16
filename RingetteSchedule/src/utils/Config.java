package utils;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.extern.java.Log;

@Log
public class Config {
	private Hashtable<String, ConfigItem> configItems = new Hashtable<String, ConfigItem>();

	private final static Config instance = new Config();

	private Config() {
		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();

			Document document = builder.parse(new FileInputStream("properties\\TeamConfig.2018-2019.xml"));

			XPath xPath = XPathFactory.newInstance().newXPath();

			NodeList nodeList = (NodeList) xPath.evaluate("//team", document, XPathConstants.NODESET);

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node teamNode = nodeList.item(i);
				String team = xPath.evaluate("name", teamNode);
				String url = xPath.evaluate("url", teamNode);
				String map = xPath.evaluate("map", teamNode);
				String division = xPath.evaluate("division", teamNode);
				String active = xPath.evaluate("active", teamNode);
				boolean isActive = active.equals("true");
				if (division == null || division.isEmpty()) {
					log.severe("Missing config entry for team: " + team + ":" + url + ":" + map + ":" + division);
				}
				configItems.put(team, new ConfigItem(team, url, map, division, isActive));
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}

	public static Config getInstance() {
		return instance;
	}

	public class ConfigItem {

		private final String team;
		private final String url;
		private final String map;
		private final String division;
		private final boolean active;

		public ConfigItem(String team, String url, String map, String division, boolean active) {
			this.team = team;
			this.url = url;
			this.map = map;
			this.division = division;
			this.active = active;
		}

		public String getTeam() {
			return team;
		}

		public String getUrl() {
			return url;
		}

		public String getMap() {
			return map;
		}

		public String getDivision() {
			return division;
		}

		public boolean isActive() {
			return active;
		}

	}

	public ConfigItem GetConfig(String team) {
		return configItems.get(team);
	}

	public List<String> GetTeams() {
		return Collections.list(configItems.keys());
	}

	public static void main(String[] args) {
		// Config c = new Config();
	}

}
