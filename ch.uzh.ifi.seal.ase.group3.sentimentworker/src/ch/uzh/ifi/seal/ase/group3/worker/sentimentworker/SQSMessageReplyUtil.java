package ch.uzh.ifi.seal.ase.group3.worker.sentimentworker;

import org.apache.log4j.Logger;

/**
 * Handles messaging from the worker to the GUI
 * 
 * @author Nico
 * 
 */
public class SQSMessageReplyUtil extends BaseSQSHandler {

	private static final Logger logger = Logger.getLogger(SQSMessageReplyUtil.class);

	private static final String QUEUE_NAME = "Group3-Worker2GUI";

	public SQSMessageReplyUtil() {
		super(QUEUE_NAME);
	}

	/**
	 * Send a message to the GUI
	 * 
	 * @param content
	 */
	public void sendMsgToGUI(String content) {
		logger.debug("Sending a message to " + QUEUE_NAME);
		SQSUtil.sendMsg(content, sqs, queueURL);
	}

}
