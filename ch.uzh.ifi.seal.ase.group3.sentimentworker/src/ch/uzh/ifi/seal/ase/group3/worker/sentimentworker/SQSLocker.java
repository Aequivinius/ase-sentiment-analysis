package ch.uzh.ifi.seal.ase.group3.worker.sentimentworker;

import java.util.List;

import org.apache.log4j.Logger;

import com.amazonaws.services.sqs.model.Message;

/**
 * A best-effort locker ensuring that not two workers process the same query.
 * 
 * @author Nico
 * 
 */
public class SQSLocker extends BaseSQSHandler {

	private static final Logger logger = Logger.getLogger(SQSLocker.class);

	private static final String QUEUE_NAME = "Group3-WorkerSync";

	public SQSLocker() {
		super(QUEUE_NAME);
	}

	public boolean lock(String identifier) {
		if (isLocked(identifier)) {
			logger.warn("'" + identifier + "' is already locked");
			return false;
		}

		SQSUtil.sendMsg(identifier, sqs, queueURL);
		logger.debug("Locked '" + identifier + "'");
		return true;
	}

	public void releaseLock(String identifier) {
		List<Message> messages = SQSUtil.getMessages(sqs, queueURL);
		for (Message message : messages) {
			if (message.getBody().equals(identifier)) {
				SQSUtil.deleteMsg(sqs, queueURL, message);
				logger.debug("Released lock '" + identifier + "'");
			}
		}
	}

	private boolean isLocked(String identifier) {
		List<Message> messages = SQSUtil.getMessages(sqs, queueURL);
		for (Message message : messages) {
			if (message.getBody().equals(identifier)) {
				return true;
			}
		}

		return false;
	}
}
