package schedule;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnnection {
	private static DBConnnection instance = new DBConnnection();
	Connection dbConnection = null;

	private DBConnnection() {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			String strUrl = "jdbc:derby:ScheduleDB;create=true";
			dbConnection = DriverManager.getConnection(strUrl);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static DBConnnection getInstance() {
		return instance;
	}

	public Connection getDbConnection() {
		return dbConnection;
	}

	
}
