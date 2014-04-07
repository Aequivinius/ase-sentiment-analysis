package ch.uzh.ifi.seal.ase.group3.utils.populate;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import ch.uzh.ifi.seal.ase.group3.db.Database;
import ch.uzh.ifi.seal.ase.group3.db.Tweet;
import classifier.Preprocesser;

import com.google.gson.Gson;

public class JsonParser {

	private static final Logger logger = Logger.getLogger(JsonParser.class);
	private static final int BATCH_SIZE = 1000;

	private final Database db;
	private final Gson gson;
	private final Preprocesser preprocesser;

	public JsonParser(Database db) {
		this.db = db;
		gson = new Gson();
		preprocesser = new Preprocesser();
	}

	public void parse(File file) {
		if (!file.exists()) {
			System.out.println("File does not exist: " + file);
			return;
		}

		Set<Tweet> buffer = new HashSet<Tweet>(BATCH_SIZE);

		try {
			FileInputStream fstream = new FileInputStream(file);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF8")));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// skip non-json lines
				if (strLine.startsWith("{")) {
					JsonTweet tweet = gson.fromJson(strLine, JsonTweet.class);
					buffer.add(preprocess(tweet));

					if (buffer.size() >= BATCH_SIZE) {
						insertBuffer(buffer);
						buffer.clear();
					}
				}
			}

			// insert the rest in the buffer
			insertBuffer(buffer);

			// Close the input stream
			in.close();
			br.close();
		} catch (IOException e) {// Catch exception if any
			logger.error("File read exception", e);
		}
	}

	private void insertBuffer(Set<Tweet> buffer) {
		try {
			db.addTweets(buffer);
			logger.debug("Added " + buffer.size() + " tweets to the DB.");
		} catch (SQLException e) {
			logger.error("Could not insert batch", e.getNextException());
		}
	}

	private Tweet preprocess(JsonTweet tweet) {
		String text = tweet.getText();
		if (text == null)
			return null;
		String preprocessed = preprocesser.preprocessDocument(text);
		return new Tweet(tweet.getId(), text, preprocessed);
	}
}