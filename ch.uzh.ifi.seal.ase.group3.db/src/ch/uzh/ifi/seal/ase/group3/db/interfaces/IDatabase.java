package ch.uzh.ifi.seal.ase.group3.db.interfaces;

public interface IDatabase {

	/**
	 * Disconnects from the DB and makes this instance unusable anymore
	 */
	void disconnect();
}
