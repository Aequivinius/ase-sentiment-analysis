package ch.uzh.ifi.seal.ase.group3.worker.sentimentworker;

import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

/**
 * Contains some helper methods for the SQS
 * 
 * @author Nico
 * 
 */
public class SQSUtil {

	private static final Logger logger = Logger.getLogger(SQSUtil.class);

	/**
	 * Returns the queue service for this account
	 * 
	 * @return the queue service
	 */
	public static AmazonSQS getSQS() {
		AmazonSQS sqs = new AmazonSQSClient(new ClasspathPropertiesFileCredentialsProvider());
		Region euWest = Region.getRegion(Regions.EU_WEST_1);
		sqs.setRegion(euWest);
		return sqs;
	}

	/**
	 * Returns the queue URL for a given queue name
	 * 
	 * @param sqs the queue service
	 * @param queueName the name of the queue
	 * @return the url or <code>null</code> if no queue has been found
	 */
	public static String getQueueURL(AmazonSQS sqs, String queueName) {
		// Fetch existing queue url
		String myQueueUrl = null;

		logger.debug("Searching for our queue.");
		for (String queueUrl : sqs.listQueues().getQueueUrls()) {
			logger.debug("Found - QueueUrl: " + queueUrl);
			if (queueUrl.contains(queueName)) {
				myQueueUrl = queueUrl;
				break;
			}
		}

		return myQueueUrl;
	}

	/**
	 * Reads the messages in the queue
	 * 
	 * @param sqs the queue service
	 * @param queueURL the url of the queue
	 * @return a list of messages, which can be emtpy
	 */
	public static List<Message> getMessages(AmazonSQS sqs, String queueURL) {
		logger.debug("Receiving messages from " + queueURL);
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueURL);
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		for (Message message : messages) {
			logger.trace("  Message");
			logger.trace("    MessageId:     " + message.getMessageId());
			logger.trace("    ReceiptHandle: " + message.getReceiptHandle());
			logger.trace("    MD5OfBody:     " + message.getMD5OfBody());
			logger.trace("    Body:          " + message.getBody());
			for (Entry<String, String> entry : message.getAttributes().entrySet()) {
				logger.trace("  Attribute");
				logger.trace("    Name:  " + entry.getKey());
				logger.trace("    Value: " + entry.getValue());
			}
		}
		return messages;
	}

	public static void sendMsg(String content, AmazonSQS sqs, String queueURL) {
		logger.debug("Sending a message to " + queueURL);
		sqs.sendMessage(new SendMessageRequest(queueURL, content));
	}

	public static void deleteMsg(AmazonSQS sqs, String queueURL, Message message) {
		logger.debug("Deleting a message.\n");
		String messageRecieptHandle = message.getReceiptHandle();
		sqs.deleteMessage(new DeleteMessageRequest(queueURL, messageRecieptHandle));
	}
}
