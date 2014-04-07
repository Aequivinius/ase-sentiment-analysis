package ch.uzh.ifi.seal.ase.group3.worker.sentiment.test;

import java.io.File;
import java.sql.SQLException;

import ch.uzh.ifi.seal.ase.group3.db.Database;
import ch.uzh.ifi.seal.ase.group3.db.DatabaseConnection;
import ch.uzh.ifi.seal.ase.group3.db.interfaces.ISentimentDatabase;
import ch.uzh.ifi.seal.ase.group3.worker.sentiment.Sentiment;

public class SentimentMain {

	public static void main(String[] args) throws SQLException {

		String companyName = "maria";

		ISentimentDatabase db = new Database(DatabaseConnection.getDefaultDatabase());

		File file = new File(System.getProperty("java.io.tmpdir"), "test.txt");
		db.searchToFile(file, companyName);

		Sentiment sent = new Sentiment();
		double result = sent.avg(file);
		System.out.println(result);

		db.addResult(companyName, result);
	}

}
