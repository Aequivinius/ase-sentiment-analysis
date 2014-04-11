import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.uzh.ifi.seal.ase.group3.db.Database;
import ch.uzh.ifi.seal.ase.group3.db.DatabaseConnection;
import ch.uzh.ifi.seal.ase.group3.db.interfaces.ISentimentDatabase;
import ch.uzh.ifi.seal.ase.group3.db.model.Result;
import ch.uzh.ifi.seal.ase.group3.worker.sentimentworker.SQSMessageUtil;
import ch.uzh.ifi.seal.ase.group3.worker.sentimentworker.Sentiment;
import ch.uzh.ifi.seal.ase.group3.worker.sentimentworker.ServletFileUtil;

import com.amazonaws.services.sqs.model.Message;

/**
 * An example Amazon Elastic Beanstalk Worker Tier application. This example
 * requires a Java 7 (or higher) compiler.
 */
public class WorkerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final SQSMessageUtil sqsMessageUtil;

	/**
	 * A client to use to access Amazon S3. Pulls credentials from the {@code AwsCredentials.properties} file
	 * if found on the classpath,
	 * otherwise will attempt to obtain credentials based on the IAM
	 * Instance Profile associated with the EC2 instance on which it is
	 * run.
	 */
	/*
	 * private final AmazonS3Client s3 = new AmazonS3Client(new AWSCredentialsProviderChain(
	 * new InstanceProfileCredentialsProvider(), new ClasspathPropertiesFileCredentialsProvider()));
	 */

	public WorkerServlet() {
		sqsMessageUtil = new SQSMessageUtil();

	}

	/**
	 * This method is invoked to handle POST requests from the local
	 * SQS daemon when a work item is pulled off of the queue. The
	 * body of the request contains the message pulled off the queue.
	 */
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		// configure the servlet
		if (!ServletFileUtil.getInstance().isConfigured()) {
			ServletFileUtil.getInstance().configure(getServletContext());
		}

		// poll for messages
		System.out.println("Start polling for new messages");
		List<Message> messages = sqsMessageUtil.getMessagesBlocking();
		System.out.println("Polling messages ends now.");

		// connect to DB
		ISentimentDatabase database;
		try {
			database = getDatabase();
		} catch (SQLException e) {
			System.out.println("Cannot connect to database");
			e.printStackTrace();
			return;
		}

		// iterate through them and process them one by one
		for (Message message : messages) {
			long startTime = System.currentTimeMillis();
			String searchTerm = message.getBody();

			// TODO get these values from the message
			Date startDate = new Date(0); // 1970
			Date endDate = new Date(); // now

			// temporary file to store the tweets in
			File storageFile = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
			try {
				database.searchToFile(storageFile, searchTerm, startDate, endDate);
			} catch (SQLException e) {
				System.out.println("Cannot search db for term " + searchTerm + ".");
				e.printStackTrace();
				continue;
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
			} catch (SQLException e) {
				System.out.println("Cannot write the result into the database");
				e.printStackTrace();
				continue;
			}

			// delete the message from the queue
			sqsMessageUtil.deleteMsg(message);

			// TODO notify the gui

			System.out.println("Processed sentiment for " + searchTerm);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		// TODO just for testing
		doPost(req, resp);
	}

	private ISentimentDatabase getDatabase() throws SQLException {
		DatabaseConnection conn = DatabaseConnection.getDefaultDatabase();
		return new Database(conn);
	}
}
