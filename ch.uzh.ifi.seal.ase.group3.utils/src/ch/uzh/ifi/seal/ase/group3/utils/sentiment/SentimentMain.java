package ch.uzh.ifi.seal.ase.group3.utils.sentiment;

import java.io.File;
import java.sql.SQLException;

import ch.uzh.ifi.seal.ase.group3.worker.database.Database;
import ch.uzh.ifi.seal.ase.group3.worker.database.DatabaseConnection;
import ch.uzh.ifi.seal.ase.group3.worker.sentiment.Sentiment;

public class SentimentMain {

	public static void main(String[] args) {

		String companyName = "maria";

		Database db = null;

		try {
			db = new Database(DatabaseConnection.getDefaultDatabase());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File file = new File(System.getProperty("java.io.tmpdir"), "test.txt");

		try {
			db.searchToFile(file, companyName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Sentiment sent = new Sentiment();
		System.out.println(sent.avg(file));

	}

}
