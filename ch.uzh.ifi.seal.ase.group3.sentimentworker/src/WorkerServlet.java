import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import ch.uzh.ifi.seal.ase.group3.db.Database;
import ch.uzh.ifi.seal.ase.group3.db.DatabaseConnection;
import ch.uzh.ifi.seal.ase.group3.db.interfaces.ISentimentDatabase;
import ch.uzh.ifi.seal.ase.group3.db.model.Result;
import ch.uzh.ifi.seal.ase.group3.worker.sentimentworker.SQSLocker;
import ch.uzh.ifi.seal.ase.group3.worker.sentimentworker.SQSMessageReplyUtil;
import ch.uzh.ifi.seal.ase.group3.worker.sentimentworker.Sentiment;
import ch.uzh.ifi.seal.ase.group3.worker.sentimentworker.ServletFileUtil;

/**
 * An example Amazon Elastic Beanstalk Worker Tier application. This example
 * requires a Java 7 (or higher) compiler.
 */
public class WorkerServlet extends HttpServlet {

	private static final long serialVersionUID = 5328746493311159850L;
	private static final Logger logger = Logger.getLogger(WorkerServlet.class);

	private static final int SUCCESS_STATE = 200;
	private static final int FAIL_STATE = 500;

	private static final long MIN_WAIT_TIME_MS = 3000;
	private static final long MAX_WAIT_TIME_MS = 20000;

	private final SQSLocker sqsLocker;
	private final SQSMessageReplyUtil sqsMessageUtil;

	public WorkerServlet() {
		sqsMessageUtil = new SQSMessageReplyUtil();
		sqsLocker = new SQSLocker();
	}

	/**
	 * This method is invoked to handle POST requests from the local
	 * SQS daemon when a work item is pulled off of the queue. The
	 * body of the request contains the message pulled off the queue.
	 */
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		// configure the servlet paths
		if (!ServletFileUtil.getInstance().isConfigured()) {
			ServletFileUtil.getInstance().configure(getServletContext());
		}

		String searchTerm = getTerm(request);
		if (!processSearchTerm(searchTerm)) {
			logger.debug("Other worker already processes '" + searchTerm);
			response.setStatus(FAIL_STATE);
			return;
		}

		// do the long running calculation
		calculateScore(searchTerm);
		response.setStatus(SUCCESS_STATE);

		// release the lock
		sqsLocker.releaseLock(searchTerm);

		// TODO notify the GUI
	}

	private String getTerm(HttpServletRequest request) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String searchTerm = reader.readLine();
		logger.debug("Got search term " + searchTerm);
		return searchTerm;
	}

	private boolean processSearchTerm(String searchTerm) {
		/**
		 * Idea (Aloha principle): Wait for a random time, then lock the search term. Only process it, if it
		 * is not locked by another worker. This way, we can ensure that only one worker processes a search
		 * term at a time.
		 */
		int waitTime = new Random().nextInt((int) (MAX_WAIT_TIME_MS - MIN_WAIT_TIME_MS));
		try {
			Thread.sleep(waitTime + MIN_WAIT_TIME_MS);
		} catch (InterruptedException e) {
			logger.error("Cannot wait for random time");
		}

		return sqsLocker.lock(searchTerm);
	}

	private void calculateScore(String searchTerm) {
		logger.info("Start processing " + searchTerm);

		long startTime = System.currentTimeMillis();

		// TODO get these values from the message
		Date startDate = new Date(0); // 1970
		Date endDate = new Date(); // now

		// temporary file to store the tweets in
		File storageFile = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());

		DatabaseConnection conn = DatabaseConnection.getDefaultDatabase();
		ISentimentDatabase database = null;

		try {
			database = new Database(conn);
			database.searchToFile(storageFile, searchTerm, startDate, endDate);
		} catch (SQLException e) {
			logger.error("Cannot search db for term " + searchTerm, e);
			if (database != null)
				database.disconnect();
			return;
		}

		// calculate the sentiment
		Sentiment sent = new Sentiment(storageFile);
		try {
			sent.calculate();
		} catch (FileNotFoundException e) {
			logger.error("File for search term " + searchTerm + " could not be found.", e);
			if (database != null)
				database.disconnect();
			return;
		}

		// end of calculation
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;

		Result result = new Result(searchTerm, startDate, endDate, -1);
		result.setCalculationTime(duration);
		result.setNumTweets(sent.getTweetsProcessed());
		result.setSentiment(sent.getResult());

		try {
			database.addResult(result);
			logger.debug("Added result for term " + searchTerm + " to db");
		} catch (SQLException e) {
			logger.error("Cannot write the result into the database", e);
			return;
		} finally {
			database.disconnect();
		}

		logger.info("Finished processing " + searchTerm);
	}
}
