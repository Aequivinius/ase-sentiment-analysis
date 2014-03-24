package ch.uzh.ifi.seal.ase.group3;

import java.io.File;
import java.sql.SQLException;

import org.apache.log4j.BasicConfigurator;

import ch.uzh.ifi.seal.ase.group3.db.Database;
import ch.uzh.ifi.seal.ase.group3.db.DatabaseConnection;
import ch.uzh.ifi.seal.ase.group3.io.JsonParser;

public class File2DBStarter {

	public static void main(String[] args) throws SQLException {
		BasicConfigurator.configure();

		// start reading text file into the database
		Database db = new Database(DatabaseConnection.getDefaultDatabase());
		JsonParser jsonParser = new JsonParser(db);
		File dataFile = new File("/Users/nicorutishauser/Downloads/tweets.txt");

		jsonParser.parse(dataFile);
	}
}
