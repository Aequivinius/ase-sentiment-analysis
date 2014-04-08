import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

/**
 * An example Amazon Elastic Beanstalk Worker Tier application. This example
 * requires a Java 7 (or higher) compiler.
 */
public class WorkerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Charset UTF_8 = Charset.forName("UTF-8");
	
	// writes to G3Queue, reads from Gruppe3
	private String queueName = "G3Queue";

	/**
	 * A client to use to access Amazon S3. Pulls credentials from the {@code AwsCredentials.properties} file
	 * if found on the classpath,
	 * otherwise will attempt to obtain credentials based on the IAM
	 * Instance Profile associated with the EC2 instance on which it is
	 * run.
	 */
	/*private final AmazonS3Client s3 = new AmazonS3Client(new AWSCredentialsProviderChain(
			new InstanceProfileCredentialsProvider(), new ClasspathPropertiesFileCredentialsProvider()));*/

	/**
	 * This method is invoked to handle POST requests from the local
	 * SQS daemon when a work item is pulled off of the queue. The
	 * body of the request contains the message pulled off the queue.
	 */
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {

		AmazonSQS sqs = getSQS();
		try {
        	// Get our queue        	
            String myQueueUrl = getQueue(sqs, queueName);
            
            
			// Parse the work to be done from the POST request body.

			// WorkRequest workRequest = WorkRequest.fromJson(request.getInputStream());


			// Write the "result" of the work into Amazon S3.

			// byte[] message = workRequest.getMessage().getBytes(UTF_8);
		
			// Send a message
            sendMsg("Das ist eine Antwort des Workers!", sqs, myQueueUrl);

			// Signal to beanstalk that processing was successful so this work
			// item should not be retried.

			response.setStatus(200);
			
			/*PrintWriter out = response.getWriter();
			out.println("Das ist eine Antwort!");*/

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } catch (RuntimeException exception) {

			// Signal to beanstalk that something went wrong while processing
			// the request. The work request will be retried several times in
			// case the failure was transient (eg a temporary network issue
			// when writing to Amazon S3).

			response.setStatus(500);
			try (PrintWriter writer = new PrintWriter(response.getOutputStream())) {
				exception.printStackTrace(writer);
			}
		}
	}

	private String getQueue(AmazonSQS sqs, String queueName) {
		// Fetch existing queue url
		String myQueueUrl = getQueueUrl(sqs, queueName);

		// If not found, create
		if (myQueueUrl.equals("")) {
			myQueueUrl = createQueue(sqs, queueName);
		}
		
		return myQueueUrl;
	}

	private String getQueueUrl(AmazonSQS sqs, String queueName) {
		System.out.println("Searching for our queue.\n");
		for (String queueUrl : sqs.listQueues().getQueueUrls()) {
		    System.out.println("Found - QueueUrl: " + queueUrl);
		    if (queueUrl.contains(queueName)) {
		    	return queueUrl;
		    }
		}
		System.out.println();
		return "";
	}
	
	private String createQueue(AmazonSQS sqs, String queueName) {
		System.out.println("Creating a new SQS queue called "+queueName+".\n");
		CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
		String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
		return myQueueUrl;
	}
	
	private AmazonSQS getSQS() {
		AmazonSQS sqs = new AmazonSQSClient(new ClasspathPropertiesFileCredentialsProvider());
		Region euWest = Region.getRegion(Regions.EU_WEST_1);
		sqs.setRegion(euWest);
		return sqs;
	}
	
	private void sendMsg(String term, AmazonSQS sqs, String myQueueUrl) {
		System.out.println("Sending a message to "+queueName+".\n");
		sqs.sendMessage(new SendMessageRequest(myQueueUrl, term));
	}
}
