package ch.uzh.ifi.seal.ase.group3.shared;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Database {

	private static final Logger logger = Logger.getLogger(Database.class);
	private Connection conn;

	// some constants to avoid duplicate code
	private static final String TWEET_COLUMNS = "id, text, preprocessed";

	public Database(DatabaseConnection connection) throws SQLException {
		conn = connection.getConnection();
		conn.setAutoCommit(true);
		logger.info("Successfully connected to db");
	}

	/**
	 * Disconnects from the DB and makes this instance unusable anymore
	 */
	public void disconnect() {
		try {
			if (conn != null)
				conn.close();
			logger.debug("Disconnected from db");
		} catch (SQLException e) {
			logger.error("Cannot close connection", e);
		}
	}

	public void addTweet(Tweet tweet) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO tweet (" + TWEET_COLUMNS
				+ ") values (?,?,?);");
		try {
			int i = 1;
			stmt.setLong(i++, tweet.getId());
			stmt.setString(i++, tweet.getText());

			if (tweet.isPreprocessed()) {
				stmt.setString(i++, tweet.getPreprocessed());
			} else {
				stmt.setNull(i++, Types.VARCHAR);
			}
			if (stmt.executeUpdate() != 1) {
				logger.error("Cannot insert tweet " + tweet.getId());
			}
		} finally {
			stmt.close();
		}
	}

	/**
	 * Hint: this does not scale for a large data set. Use with caution for testing purpose only!
	 */
	public List<Tweet> getAllTweets() throws SQLException {
		List<Tweet> tweets = new ArrayList<Tweet>();
		Statement stmt = conn.createStatement();
		try {
			ResultSet rs = stmt.executeQuery("SELECT " + TWEET_COLUMNS + " FROM tweet;");
			while (rs.next()) {
				int i = 1;
				Tweet tweet = new Tweet(rs.getLong(i++), rs.getString(i++), rs.getString(i++));
				tweets.add(tweet);
			}
		} finally {
			stmt.close();
		}

		return tweets;
	}

	public void removeTweet(long id) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("DELETE FROM tweet WHERE id = ?;");
		try {
			stmt.setLong(1, id);
			if (stmt.executeUpdate() != 1) {
				logger.error("Cannot delete tweet " + id);
			}
		} finally {
			stmt.close();
		}
	}

	public void addTweets(List<Tweet> tweets) throws SQLException {
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
		} catch (SQLException e) {
			logger.error("Could not insert batch.", e.getNextException());
			throw e;
		} finally {
			stmt.close();
		}
	}
}