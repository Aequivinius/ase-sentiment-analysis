package ch.uzh.ifi.seal.ase.group3.db.interfaces;

import java.io.File;
import java.sql.SQLException;
import java.util.Date;

import ch.uzh.ifi.seal.ase.group3.db.model.Result;

public interface ISentimentDatabase extends IDatabase {

	/**
	 * Searches for tweets containing the query and storing the (preprocessed) result to a file (each tweet on
	 * a line)
	 * 
	 * @param file the file to store the results
	 * @param query the buzz-word to find (e.g. company name)
	 * @throws SQLException
	 */
	void searchToFile(File file, String query) throws SQLException;

	/**
	 * Searches for tweets containing the query and storing the (preprocessed) result to a file (each tweet on
	 * a line)
	 * 
	 * @param file the file to store the results
	 * @param query the buzz-word to find (e.g. company name)
	 * @param startDate the start period of the searched tweets
	 * @param endDate the end period of the searched tweets
	 * @throws SQLException
	 */
	void searchToFile(File file, String query, Date startDate, Date endDate) throws SQLException;

	/**
	 * Adds the result of the search query to the database
	 * 
	 * @param result the sentiment analysis result
	 * @throws SQLException
	 */
	void addResult(Result result) throws SQLException;
}
