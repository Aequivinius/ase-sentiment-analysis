package ch.uzh.ifi.seal.ase.group3.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ch.uzh.ifi.seal.ase.group3.client.StoredTermService;
import ch.uzh.ifi.seal.ase.group3.db.Database;
import ch.uzh.ifi.seal.ase.group3.db.DatabaseConnection;
import ch.uzh.ifi.seal.ase.group3.db.interfaces.IResultDatabase;
import ch.uzh.ifi.seal.ase.group3.db.model.Result;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class StoredTermServiceImpl extends RemoteServiceServlet implements StoredTermService {

	private IResultDatabase db;

	private IResultDatabase getDatabase() {
		if (db == null) {
			try {
				db = new Database(DatabaseConnection.getDefaultDatabase());
			} catch (SQLException e) {
				// cannot initialize the database
			}
		}

		return db;
	}

	@Override
	public List<Result> getStoredTerms() {
		IResultDatabase database = getDatabase();
		if (database == null)
			return new ArrayList<Result>(0);
		else {
			try {
				return database.getAllResults();
			} catch (SQLException e) {
				// could not fetch the results
				return new ArrayList<Result>(0);
			}
		}

	}

	@Override
	public void clearAllStoredTerms() {
		IResultDatabase database = getDatabase();
		if (database != null) {
			try {
				database.deleteResults();
			} catch (SQLException e) {
				// could not delete the results
			}
		}
	}
}
