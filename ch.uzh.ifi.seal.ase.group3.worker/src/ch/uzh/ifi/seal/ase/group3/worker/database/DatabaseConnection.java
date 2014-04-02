package ch.uzh.ifi.seal.ase.group3.worker.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

	public String host;
	public int port;
	public String db;
	public String userName;
	public String password;
	public boolean sslRequired;

	private DatabaseConnection(String host, int port, String db, String userName, String password,
			boolean sslRequired) {
		this.host = host;
		this.port = port;
		this.db = db;
		this.userName = userName;
		this.password = password;
		this.sslRequired = sslRequired;
	}

	public Connection getConnection() throws SQLException {
		String url = host;
		if (!url.startsWith("jdbc:postgresql://")) {
			url = "jdbc:postgresql://" + url;
		}
		url += ":" + port;

		if (!url.endsWith("/")) {
			url += "/";
		}
		url += db;

		url += "?characterEncoding=utf8";

		Properties props = new Properties();
		if (userName != null) {
			props.setProperty("user", userName);
		}
		if (password != null) {
			props.setProperty("password", password);
		}
		if (sslRequired) {
			props.setProperty("ssl", "true");
			props.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
		}
		Connection db = DriverManager.getConnection(url, props);
		return db;
	}

	public static DatabaseConnection getDefaultDatabase() {
		// TODO: Add alternative db's here

		return new DatabaseConnection("team-3-database.cybcazz75duw.us-east-1.rds.amazonaws.com", 5432,
				"team3_db", "ase_team3", "WwDXPTHaaCmk8tauN8XBMctLAKXFcq", false);
	}
}