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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import ch.uzh.ifi.seal.ase.group3.db.Database;
import ch.uzh.ifi.seal.ase.group3.db.model.Tweet;
import classifier.Preprocesser;

import com.google.gson.Gson;

public class JsonParser {

	private static final Logger logger = Logger.getLogger(JsonParser.class);
	private static final int BATCH_SIZE = 10000;

	private final Database db;
	private final Gson gson;
	private final Preprocesser preprocesser;
	private final ExecutorService executor;

	public JsonParser(Database db) {
		this.db = db;
		gson = new Gson();
		preprocesser = new Preprocesser();
		executor = Executors.newFixedThreadPool(16);
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
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					Charset.forName("UTF8")));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// skip non-json lines
				try {
					if (strLine.startsWith("{")) {
						JsonTweet tweet = gson.fromJson(strLine,
								JsonTweet.class);
						buffer.add(preprocess(tweet));

						if (buffer.size() >= BATCH_SIZE) {
							insertBuffer(buffer);
							buffer.clear();
						}
					}
				} catch (Exception e) {
					logger.error("Cannot process line; skip it");
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

		executor.shutdown();
		logger.info("Done parsing file " + file.getAbsolutePath());
	}

	private void insertBuffer(Set<Tweet> buffer) {
		executor.execute(new BufferRunnable(new HashSet<Tweet>(buffer)));
	}

	private Tweet preprocess(JsonTweet tweet) {
		String text = tweet.getText();
		if (text == null)
			return null;
		String preprocessed = preprocesser.preprocessDocument(text);
		return new Tweet(tweet.getId(), text, tweet.getDate(), preprocessed);
	}

	private class BufferRunnable implements Runnable {

		private final Set<Tweet> buffer;

		public BufferRunnable(Set<Tweet> buffer) {
			this.buffer = buffer;
		}

		@Override
		public void run() {
			try {
				db.addTweets(buffer);
			} catch (SQLException e) {
				logger.error("Could not insert batch", e.getNextException());
			}
		}
	}
}
