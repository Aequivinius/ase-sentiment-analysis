package ch.uzh.ifi.seal.ase.group3.db.interfaces;

import java.sql.SQLException;
import java.util.List;

import ch.uzh.ifi.seal.ase.group3.db.Result;

public interface IResultDatabase extends IDatabase {

	/**
	 * Returns a list of results that have already been processed
	 */
	List<Result> getAllResults() throws SQLException;

	/**
	 * Deletes all results from the database
	 */
	void deleteResults() throws SQLException;
}
