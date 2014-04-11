package ch.uzh.ifi.seal.ase.group3.worker.sentimentworker;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map.Entry;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SQSMessageUtil {

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	private static final String QUEUE_NAME_GUI2WORKER = "Group3-GUI2Worker";
	private static final String QUEUE_NAME_WORKER2GUI = "Group3-Worker2GUI";

	private final AmazonSQS sqs;
	private String readQueue;
	private String writeQueue;

	public SQSMessageUtil() {
		sqs = getSQS();
		readQueue = getQueueURL(sqs, QUEUE_NAME_GUI2WORKER);
		writeQueue = getQueueURL(sqs, QUEUE_NAME_WORKER2GUI);
	}

	private String getQueueURL(AmazonSQS sqs, String queueName) {
		// Fetch existing queue url
		String myQueueUrl = null;

		System.out.println("Searching for our queue.");
		for (String queueUrl : sqs.listQueues().getQueueUrls()) {
			System.out.println("Found - QueueUrl: " + queueUrl);
			if (queueUrl.contains(queueName)) {
				myQueueUrl = queueUrl;
				break;
			}
		}

		// If not found, create
		if (myQueueUrl == null) {
			myQueueUrl = createQueue(sqs, queueName);
		}

		return myQueueUrl;
	}

	private String createQueue(AmazonSQS sqs, String queueName) {
		System.out.println("Creating a new SQS queue called " + queueName);
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

	public List<Message> getMessagesBlocking() {
		List<Message> messages = null;

		while (messages == null || messages.isEmpty()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Cannot sleep");
			}

			messages = receiveMsgs();
		}

		System.out.println("Received " + messages.size() + " messages");
		return messages;
	}

	private List<Message> receiveMsgs() {
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(readQueue);
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
		return messages;
	}

	public void deleteMsg(Message message) {
		System.out.println("Deleting a message.");
		String messageRecieptHandle = message.getReceiptHandle();
		sqs.deleteMessage(new DeleteMessageRequest(readQueue, messageRecieptHandle));
	}

	public void sendMsgToGUI(String content) {
		System.out.println("Sending a message to " + QUEUE_NAME_WORKER2GUI);
		sqs.sendMessage(new SendMessageRequest(QUEUE_NAME_WORKER2GUI, content));
	}

}
