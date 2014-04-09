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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import ch.uzh.ifi.seal.ase.group3.db.interfaces.IPopulateDatabase;
import ch.uzh.ifi.seal.ase.group3.db.interfaces.IResultDatabase;
import ch.uzh.ifi.seal.ase.group3.db.interfaces.ISentimentDatabase;
import ch.uzh.ifi.seal.ase.group3.db.model.Result;
import ch.uzh.ifi.seal.ase.group3.db.model.Tweet;

public class Database implements IPopulateDatabase, ISentimentDatabase, IResultDatabase {

	private static final Logger logger = Logger.getLogger(Database.class);

	// some constants to avoid duplicate code
	private static final String TWEET_COLUMNS = "id, text, created_at, preprocessed";
	private static final String RESULT_COLUMNS = "query, score, num_tweets, start_date, end_date, user_id, computed_at, calculation_time";

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
				+ ") values (?,?,?,?);");

		for (Tweet tweet : tweets) {
			try {
				int i = 1;
				stmt.setLong(i++, tweet.getId());
				stmt.setString(i++, tweet.getText());
				stmt.setDate(i++, new Date(tweet.getDate()));
				if (tweet.isPreprocessed()) {
					stmt.setString(i++, tweet.getPreprocessed());
				} else {
					stmt.setNull(i++, Types.VARCHAR); // preprocessed
				}
				stmt.addBatch();
			} catch (Exception e) {
				logger.error("Invalid tweet: " + tweet);
			}
		}

		try {
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
	public void addResult(Result result) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO result (" + RESULT_COLUMNS
				+ ") values (?,?,?,?,?,?,?,?);");
		try {
			int i = 1;
			stmt.setString(i++, result.getQuery());
			stmt.setDouble(i++, result.getSentiment());
			stmt.setInt(i++, result.getNumTweets());
			stmt.setDate(i++, new Date(result.getStartDate().getTime()));
			stmt.setDate(i++, new Date(result.getEndDate().getTime()));
			stmt.setLong(i++, result.getUserId());
			stmt.setDate(i++, new Date(System.currentTimeMillis())); // computed at
			stmt.setLong(i++, result.getCalculationTime());
			stmt.executeUpdate();
			logger.debug("Added Result: '" + result.getQuery() + "' : " + result.getSentiment());
		} finally {
			stmt.close();
		}
	}

	@Override
	public List<Result> getAllResults() throws SQLException {
		List<Result> results = new ArrayList<Result>();
		Statement stmt = conn.createStatement();
		try {
			ResultSet rs = stmt.executeQuery("SELECT " + RESULT_COLUMNS
					+ " from result order by computed_at desc;");
			while (rs.next()) {
				int i = 1;
				String query = rs.getString(i++);
				double score = rs.getDouble(i++);
				int numTweets = rs.getInt(i++);
				Date startDate = rs.getDate(i++);
				Date endDate = rs.getDate(i++);
				long userId = rs.getLong(i++);
				Date computedAt = rs.getDate(i++);
				long calculationTime = rs.getLong(i++);
				results.add(new Result(query, score, startDate, endDate, numTweets, userId, computedAt,
						calculationTime));
			}

			logger.debug("Got " + results.size() + " results from database");
		} finally {
			stmt.close();
		}

		return results;
	}

	@Override
	public void deleteResults() throws SQLException {
		Statement stmt = conn.createStatement();
		try {
			stmt.executeUpdate("DELETE from result");
			logger.debug("Deleted all results from the database");
		} finally {
			stmt.close();
		}
	}
}