import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Date;
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
import ch.uzh.ifi.seal.ase.group3.worker.sentimentworker.SQSMessageReplyUtil;
import ch.uzh.ifi.seal.ase.group3.worker.sentimentworker.Sentiment;
import ch.uzh.ifi.seal.ase.group3.worker.sentimentworker.ServletFileUtil;

/**
 * An example Amazon Elastic Beanstalk Worker Tier application. This example
 * requires a Java 7 (or higher) compiler.
 */
public class WorkerServlet extends HttpServlet {

	private static final Charset UTF_8 = Charset.forName("UTF-8");
	private static final int SUCCESS_STATE = 200;
	private static final int FAIL_STATE = 500;

	private static final long serialVersionUID = 5328746493311159850L;
	private static final Logger logger = Logger.getLogger(WorkerServlet.class);

	private final SQSMessageReplyUtil sqsMessageUtil;

	public WorkerServlet() {
		sqsMessageUtil = new SQSMessageReplyUtil();
		// BasicConfigurator.configure();
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
			response.setStatus(FAIL_STATE);
			if (database != null)
				database.disconnect();
			return;
		}

		// calculate the sentiment
		Sentiment sent = new Sentiment(storageFile);
		sent.calculate();

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
			response.setStatus(FAIL_STATE);
			return;
		} finally {
			database.disconnect();
		}

		// TODO notify the gui

		logger.info("Finished processing " + searchTerm);
		response.setStatus(SUCCESS_STATE);
	}

	private String getTerm(HttpServletRequest request) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String searchTerm = reader.readLine();
		logger.debug("Got search term " + searchTerm);
		return searchTerm;
	}
}
