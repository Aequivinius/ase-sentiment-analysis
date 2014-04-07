package ch.uzh.ifi.seal.ase.group3.db;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Set;

import org.apache.log4j.Logger;

import ch.uzh.ifi.seal.ase.group3.db.interfaces.IPopulateDatabase;
import ch.uzh.ifi.seal.ase.group3.db.interfaces.ISentimentDatabase;

public class Database implements IPopulateDatabase, ISentimentDatabase {

	private static final Logger logger = Logger.getLogger(Database.class);

	// some constants to avoid duplicate code
	private static final String TWEET_COLUMNS = "id, text, preprocessed";
	private static final String RESULT_COLUMNS = "query, score, computed_at";

	private final Connection conn;

	/**
	 * Automatically connects to the databse
	 */
	public Database(DatabaseConnection connection) throws SQLException {
		conn = connection.getConnection();
		conn.setAutoCommit(true);
		logger.info("Successfully connected to db");
	}

	@Override
	public void disconnect() {
		try {
			if (conn != null)
				conn.close();
			logger.debug("Disconnected from db");
		} catch (SQLException e) {
			logger.error("Cannot close connection", e);
		}
	}

	@Override
	public void addTweets(Set<Tweet> tweets) throws SQLException {
		// add them with batch insert
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO tweet (" + TWEET_COLUMNS
				+ ") values (?,?,?);");
		try {
			for (Tweet tweet : tweets) {
				int i = 1;
				stmt.setLong(i++, tweet.getId());
				stmt.setString(i++, tweet.getText());
				if (tweet.isPreprocessed()) {
					stmt.setString(i++, tweet.getPreprocessed());
				} else {
					stmt.setNull(i++, Types.VARCHAR); // preprocessed
				}
				stmt.addBatch();
			}

			stmt.executeBatch();
			logger.debug("Added " + tweets.size() + " to the database");
		} catch (SQLException e) {
			logger.error("Could not insert batch.", e.getNextException());
			throw e;
		} finally {
			stmt.close();
		}
	}

	@Override
	public void searchToFile(File file, String query) throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet resultSet = null;
		PrintWriter writer;

		try {
			writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(
					file)), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			logger.error("Cannot write to the file");
			return;
		}

		try {
			resultSet = stmt
					.executeQuery("SELECT preprocessed FROM tweet WHERE text_tsvector @@ plainto_tsquery('english', '"
							+ query + "')");
			stmt.setFetchSize(1000);
			while (resultSet.next()) {
				writer.println(resultSet.getString(1));
			}
			logger.debug("Wrote search result to file " + file.getAbsolutePath());
		} finally {
			if (writer != null)
				writer.close();
			if (resultSet != null)
				resultSet.close();
			stmt.close();
		}
	}

	@Override
	public void addResult(String query, double result) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO result (" + RESULT_COLUMNS
				+ ") values (?,?,?);");
		try {
			int i = 1;
			stmt.setString(i++, query);
			stmt.setDouble(i++, result);
			stmt.setDate(i++, new Date(System.currentTimeMillis()));

			stmt.executeUpdate();
			logger.debug("Added Result: '" + query + "' : " + result);
		} finally {
			stmt.close();
		}
	}
}