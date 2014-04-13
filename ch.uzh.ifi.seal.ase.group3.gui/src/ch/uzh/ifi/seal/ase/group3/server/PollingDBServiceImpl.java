package ch.uzh.ifi.seal.ase.group3.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import ch.uzh.ifi.seal.ase.group3.client.PollingDBService;

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
public class PollingDBServiceImpl extends RemoteServiceServlet implements
		PollingDBService {

	private static final String QUEUE_NAME_WORKER2GUI = "Group3-Worker2GUI";

	@Override
	public List<String> startPoll() {
	
		AmazonSQS sqs = getSQS();
	
		// Get our queue
		String myQueueUrl = getQueue(sqs, QUEUE_NAME_WORKER2GUI);
	
		List<Message> msgs = receiveMsgs(sqs, myQueueUrl);
		
		// try it again for a maximum of 10 seconds
		if (msgs.isEmpty()){
			
			for (int i = 0; i < 10; ++i) {

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				msgs = receiveMsgs(sqs, myQueueUrl);

				if (!msgs.isEmpty()) {
					break;
				}
			}
		}

		List<String> stringMsg = new ArrayList<String>();
		for (Message m : msgs){
			stringMsg.add(m.getBody());
			
		}
	
		return stringMsg;
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

	private void deleteMsg(AmazonSQS sqs, String myQueueUrl,
			List<Message> messages) {
		System.out.println("Deleting a message.\n");
		String messageRecieptHandle = messages.get(0).getReceiptHandle();
		sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl,
				messageRecieptHandle));
	}

	private List<Message> receiveMsgs(AmazonSQS sqs, String myQueueUrl) {
		System.out.println("Receiving messages from MyQueue.");
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(
				myQueueUrl);
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest)
				.getMessages();
		for (Message message : messages) {
			System.out.println("  Message");
			System.out.println("    MessageId:     " + message.getMessageId());
			System.out.println("    ReceiptHandle: "
					+ message.getReceiptHandle());
			System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
			System.out.println("    Body:          " + message.getBody());
			for (Entry<String, String> entry : message.getAttributes()
					.entrySet()) {
				System.out.println("  Attribute");
				System.out.println("    Name:  " + entry.getKey());
				System.out.println("    Value: " + entry.getValue());
			}
		}
		System.out.println();
		return messages;
	}

	private void sendMsg(String term, AmazonSQS sqs, String myQueueUrl) {
		System.out.println("Sending a message to " + QUEUE_NAME_WORKER2GUI);
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
		System.out.println("Creating a new SQS queue called " + queueName
				+ ".\n");
		CreateQueueRequest createQueueRequest = new CreateQueueRequest(
				queueName);
		String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
		return myQueueUrl;
	}

	private AmazonSQS getSQS() {
		AmazonSQS sqs = new AmazonSQSClient(
				new ClasspathPropertiesFileCredentialsProvider());
		Region euWest = Region.getRegion(Regions.EU_WEST_1);
		sqs.setRegion(euWest);
		return sqs;
	}
}
