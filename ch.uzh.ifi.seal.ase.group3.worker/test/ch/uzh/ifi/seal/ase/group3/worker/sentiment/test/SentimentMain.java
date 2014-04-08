package ch.uzh.ifi.seal.ase.group3.worker.sentiment.test;

import java.io.File;
import java.sql.SQLException;

import org.apache.log4j.BasicConfigurator;

import ch.uzh.ifi.seal.ase.group3.db.Database;
import ch.uzh.ifi.seal.ase.group3.db.DatabaseConnection;
import ch.uzh.ifi.seal.ase.group3.db.interfaces.ISentimentDatabase;
import ch.uzh.ifi.seal.ase.group3.worker.sentiment.Sentiment;

public class SentimentMain {

	public static void main(String[] args) throws SQLException {
		BasicConfigurator.configure();

		String companyName = "Google";

		ISentimentDatabase db = new Database(DatabaseConnection.getDefaultDatabase());

		File file = new File(System.getProperty("java.io.tmpdir"), "test.txt");
		db.searchToFile(file, companyName);

		Sentiment sent = new Sentiment();
		double result = sent.avg(file);

		db.addResult(companyName, result);
	}

}
