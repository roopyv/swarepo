package com.sibjmsclient.swa.receive;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.sibjmsclient.swa.util.JNDIUtils;

public class SIBQueueListener implements MessageListener {

	private static final Logger LOGGER = Logger.getLogger("getLogger");

	private String queueName;
	private long countOfMessages = 0;

	public SIBQueueListener(String queue) {
		queueName = queue;
	}

	public String getQueueName() {
		return queueName;
	}

	@Override
	public void onMessage(Message msg) {
		LOGGER.info(String.format("Received Message From Queue: %s", queueName));
		if (msg != null) {
			try {

				String body = null;
				JNDIUtils.logJMSProperties(msg);
				if (msg instanceof TextMessage) {
					body = ((TextMessage) msg).getText();
				} else if (msg instanceof BytesMessage) {
					long msgLen = ((BytesMessage) msg).getBodyLength();
					if (msgLen > 0) {
						body = ((BytesMessage) msg).readUTF(); // Assume UTF-8.
					}
				} else {
					LOGGER.info("Unknown Message Type");
				}

				if (null != body && !body.isEmpty()) {
					countOfMessages++;
					LOGGER.info(String.format("Message Content: \n %s", body));
					LOGGER.info("Message Count " + countOfMessages);
				}

			} catch (JMSException e) {
				LOGGER.error(String.format("Exception Reading Message From Queue: %s", queueName));
			}
		} else {
			LOGGER.info(String.format("Empty Message From Queue: %s", queueName));
		}

	}

}
