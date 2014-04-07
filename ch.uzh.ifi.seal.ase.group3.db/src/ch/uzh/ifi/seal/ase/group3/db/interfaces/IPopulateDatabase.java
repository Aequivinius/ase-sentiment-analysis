package ch.uzh.ifi.seal.ase.group3.db.interfaces;

import java.sql.SQLException;
import java.util.Set;

import ch.uzh.ifi.seal.ase.group3.db.model.Tweet;

public interface IPopulateDatabase extends IDatabase {

	/**
	 * Adds a set of tweets into the database in a batch
	 * 
	 * @param tweets the tweets to insert
	 * @throws SQLException in case an exception occurs when writing to the database
	 */
	void addTweets(Set<Tweet> tweets) throws SQLException;
}
