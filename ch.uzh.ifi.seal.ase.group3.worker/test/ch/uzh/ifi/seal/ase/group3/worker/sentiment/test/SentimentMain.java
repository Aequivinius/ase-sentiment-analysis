package ch.uzh.ifi.seal.ase.group3.worker.sentiment.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.BasicConfigurator;

import ch.uzh.ifi.seal.ase.group3.db.Database;
import ch.uzh.ifi.seal.ase.group3.db.DatabaseConnection;
import ch.uzh.ifi.seal.ase.group3.db.interfaces.ISentimentDatabase;
import ch.uzh.ifi.seal.ase.group3.db.model.Result;
import ch.uzh.ifi.seal.ase.group3.worker.sentiment.Sentiment;

public class SentimentMain {

	public static void main(String[] args) throws SQLException, FileNotFoundException {
		BasicConfigurator.configure();

		String companyName = "Microsoft";

		ISentimentDatabase db = new Database(DatabaseConnection.getDefaultDatabase());

		File file = new File(System.getProperty("java.io.tmpdir"), "test.txt");

		// start of calculation
		long startTime = System.currentTimeMillis();

		Date startDate = new Date(0); // 1970
		Date endDate = new Date(); // now
		db.searchToFile(file, companyName, startDate, endDate);
		Sentiment sent = new Sentiment(file);
		sent.calculate();

		// end of calculation
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;

		Result result = new Result(companyName, startDate, endDate, -1);
		result.setCalculationTime(duration);
		result.setNumTweets(sent.getTweetsProcessed());
		result.setSentiment(sent.getResult());

		db.addResult(result);
	}
}
