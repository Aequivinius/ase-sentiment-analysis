package ch.uzh.ifi.seal.ase.group3.worker.sentimentworker;

import com.amazonaws.services.sqs.AmazonSQS;

/**
 * Abstract class for multiple managers / handlers operating with SQS
 */
public abstract class BaseSQSHandler {

	protected final AmazonSQS sqs;
	protected final String queueURL;

	public BaseSQSHandler(String queueName) {
		sqs = SQSUtil.getSQS();
		queueURL = SQSUtil.getQueueURL(sqs, queueName);
	}
}
