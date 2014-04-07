package ch.uzh.ifi.seal.ase.group3.db.interfaces;

import java.io.File;
import java.sql.SQLException;

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
	 * Adds the result of the search query to the database
	 * 
	 * @param query the company name where the sentiment was calculated
	 * @param result the sentiment analysis result
	 * @throws SQLException
	 */
	void addResult(String query, double result) throws SQLException;
}