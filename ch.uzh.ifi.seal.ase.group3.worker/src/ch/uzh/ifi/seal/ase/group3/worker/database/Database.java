package ch.uzh.ifi.seal.ase.group3.worker.database;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
		} catch (SQLException e) {
			logger.error("Could not insert batch.", e.getNextException());
			throw e;
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

	/**
	 * Searches for tweets containing the query and storing the (preprocessed) result to a file (each tweet on
	 * a line)
	 * 
	 * @param file the file to store the results
	 * @param query the buzz-word to find (e.g. company name)
	 * @throws SQLException
	 */
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
		} finally {
			if (writer != null)
				writer.close();
			if (resultSet != null)
				resultSet.close();
			stmt.close();
		}
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
}