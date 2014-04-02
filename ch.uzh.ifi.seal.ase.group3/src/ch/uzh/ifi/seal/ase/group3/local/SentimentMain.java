package ch.uzh.ifi.seal.ase.group3.local;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;

import ch.uzh.ifi.seal.ase.group3.server.sentiment.Sentiment;
import ch.uzh.ifi.seal.ase.group3.shared.Database;
import ch.uzh.ifi.seal.ase.group3.shared.DatabaseConnection;

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
		
		
		File file = new File(FileUtils.getTempDirectory(), "test.txt");
		
		try {
			db.searchToFile(file, companyName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			System.out.println(FileUtils.readFileToString(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Sentiment sent = new Sentiment();
		System.out.println(sent.avg(file));
		
		
		
	}

}
