package ch.uzh.ifi.seal.ase.group3.worker.sentimentworker;

import org.apache.log4j.Logger;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SQSMessageReplyUtil {

	private static final Logger logger = Logger.getLogger(SQSMessageReplyUtil.class);

	private static final String QUEUE_NAME_WORKER2GUI = "Group3-Worker2GUI";

	private final AmazonSQS sqs;
	private String writeQueue;

	public SQSMessageReplyUtil() {
		sqs = getSQS();
		writeQueue = getQueueURL(sqs, QUEUE_NAME_WORKER2GUI);
	}

	private String getQueueURL(AmazonSQS sqs, String queueName) {
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

		// If not found, create
		if (myQueueUrl == null) {
			myQueueUrl = createQueue(sqs, queueName);
		}

		return myQueueUrl;
	}

	private String createQueue(AmazonSQS sqs, String queueName) {
		logger.debug("Creating a new SQS queue called " + queueName);
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

	public void sendMsgToGUI(String content) {
		logger.debug("Sending a message to " + QUEUE_NAME_WORKER2GUI);
		sqs.sendMessage(new SendMessageRequest(QUEUE_NAME_WORKER2GUI, content));
	}

}
