package ch.uzh.ifi.seal.ase.group3.io;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ch.uzh.ifi.seal.ase.group3.db.Database;
import ch.uzh.ifi.seal.ase.group3.model.Preprocessor;
import ch.uzh.ifi.seal.ase.group3.model.Tweet;

import com.google.gson.Gson;

public class JsonParser {

	private static final Logger logger = Logger.getLogger(JsonParser.class);
	private static final int BATCH_SIZE = 10000;

	private final Database db;
	private final Gson gson;

	public JsonParser(Database db) {
		this.db = db;
		gson = new Gson();
	}

	public void parse(File file) {
		if (!file.exists()) {
			System.out.println("File does not exist: " + file);
			return;
		}

		List<Tweet> buffer = new ArrayList<Tweet>(BATCH_SIZE);

		try {
			FileInputStream fstream = new FileInputStream(file);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// skip non-json lines
				if (strLine.startsWith("{")) {
					JsonTweet tweet = gson.fromJson(strLine, JsonTweet.class);
					buffer.add(preprocess(tweet));

					if (buffer.size() >= BATCH_SIZE) {
						db.addTweets(buffer);
						logger.debug("Added " + BATCH_SIZE + " tweets to the DB.");
					}
				}
			}

			// insert the rest in the buffer
			db.addTweets(buffer);
			logger.debug("Inserted " + buffer.size() + " tweets to finalize.");

			// Close the input stream
			in.close();
			br.close();
		} catch (IOException e) {// Catch exception if any
			logger.error("File read exception", e);
		} catch (SQLException e) {
			logger.error("Database exception", e);
		}
	}

	private Tweet preprocess(JsonTweet tweet) {
		String preprocessed = Preprocessor.preprocessDocument(tweet.getText());
		return new Tweet(tweet.getId(), tweet.getText(), preprocessed);
	}
}