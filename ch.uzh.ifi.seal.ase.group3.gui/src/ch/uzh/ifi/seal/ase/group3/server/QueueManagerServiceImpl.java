package ch.uzh.ifi.seal.ase.group3.server;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import ch.uzh.ifi.seal.ase.group3.client.QueueManagerService;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class QueueManagerServiceImpl extends RemoteServiceServlet implements QueueManagerService {

	private static final String QUEUE_NAME_GUI2WORKER = "Group3-GUI2Worker";

	@Override
	public void addNewSearchTerm(String term, Date startDate, Date endDate) throws IllegalArgumentException {
		// Adds the term to our processing queue
		AmazonSQS sqs = getSQS();

		final String newTerm = term + ";" + startDate.getTime() + ";" + endDate.getTime();

		try {
			// Get our queue
			String myQueueUrl = getQueue(sqs, QUEUE_NAME_GUI2WORKER);

			// Send a message
			System.out.println("Submitting new search query: " + newTerm);
			sendMsg(newTerm, sqs, myQueueUrl);
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon SQS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with SQS, such as not "
					+ "being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
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

	@SuppressWarnings("unused")
	private void deleteQueue(AmazonSQS sqs, String myQueueUrl) {
		System.out.println("Deleting the test queue.\n");
		sqs.deleteQueue(new DeleteQueueRequest(myQueueUrl));
	}

	private void deleteMsg(AmazonSQS sqs, String myQueueUrl, List<Message> messages) {
		System.out.println("Deleting a message.\n");
		String messageRecieptHandle = messages.get(0).getReceiptHandle();
		sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageRecieptHandle));
	}

	private List<Message> receiveMsgs(AmazonSQS sqs, String myQueueUrl) {
		System.out.println("Receiving messages from MyQueue.");
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		for (Message message : messages) {
			System.out.println("  Message");
			System.out.println("    MessageId:     " + message.getMessageId());
			System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
			System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
			System.out.println("    Body:          " + message.getBody());
			for (Entry<String, String> entry : message.getAttributes().entrySet()) {
				System.out.println("  Attribute");
				System.out.println("    Name:  " + entry.getKey());
				System.out.println("    Value: " + entry.getValue());
			}
		}
		System.out.println();
		return messages;
	}

	private void sendMsg(String term, AmazonSQS sqs, String myQueueUrl) {
		System.out.println("Sending a message to " + QUEUE_NAME_GUI2WORKER);
		sqs.sendMessage(new SendMessageRequest(myQueueUrl, term));
	}

	private String getQueueUrl(AmazonSQS sqs, String queueName) {
		System.out.println("Searching for our queue.");
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
		System.out.println("Creating a new SQS queue called " + queueName + ".\n");
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

	@Override
	public void removeSearchTerms() {
		AmazonSQS sqs = getSQS();
		// Get our queue
		String myQueueUrl = getQueue(sqs, QUEUE_NAME_GUI2WORKER);

		// Receive messages
		List<Message> messages = receiveMsgs(sqs, myQueueUrl);

		// Delete a message
		deleteMsg(sqs, myQueueUrl, messages);
	}
}
