package com.sibjmsclient.swa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.sibjmsclient.swa.receive.SIBQueueExceptionListener;
import com.sibjmsclient.swa.receive.SIBQueueListener;
import com.sibjmsclient.swa.util.JNDIUtils;
import com.sibjmsclient.swa.util.SIBJMSClientConstants;
import com.sibjmsclient.swa.util.SIBJMSClientProperties;

public final class SIBQueueManager {

	private static final Logger LOGGER = Logger.getLogger("sendLogger");
	static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private static SIBQueueManager sibInstance = null;
	private static Object singletonObject = new Object();
	QueueConnection queueConnection = null;
	InitialContext context = null;
	int iCount = 0;

	public static SIBQueueManager getInstance() {
		if (null == sibInstance) {
			synchronized (singletonObject) {
				if (null == sibInstance) {
					sibInstance = new SIBQueueManager();
				}
			}
		}
		return sibInstance;
	}

	public void initRead() {
		connect();
		activateListener();
		startConnection();
	}

	public void initSend() {
		connect();
		startConnection();
	}

	private void connect() {

		try {
			String qcfName = SIBJMSClientProperties.PROPS
					.getPropertyValue(SIBJMSClientConstants.JMS_JND_QUEUE_CONN_FACT_NAME);

			context = JNDIUtils.initializeJNDIContext();
			QueueConnectionFactory qcf = JNDIUtils.qcfLookup(context, qcfName);
			queueConnection = qcf.createQueueConnection();
			queueConnection.setExceptionListener(new SIBQueueExceptionListener());
		} catch (NamingException | JMSException e) {
			LOGGER.error(e.getMessage(), e);
		}

	}

	private void startConnection() {
		try {
			if (null != queueConnection) {
				LOGGER.info("Starting SIBQ Connection");
				queueConnection.start();
				LOGGER.info("Started SIBQ Connection");
			}
		} catch (JMSException e) {
			LOGGER.info("Error Connecting To SIBQ");
		}
	}

	public void closeConnection() {
		try {
			LOGGER.info("Closing SIBQ Connection");
			if (queueConnection != null) {
				queueConnection.close();
			}
			LOGGER.info("Closed SIBQ Connection");
		} catch (JMSException e) {
			LOGGER.info("Error Closing SIBQ Connection");
		} finally {
			queueConnection = null;

		}
	}

	private void activateListener() {
		try {
			QueueSession session = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			List<String> queues = new ArrayList<String>();
			queues.add(SIBJMSClientProperties.PROPS.getPropertyValue(SIBJMSClientConstants.JMS_JNDI_QUEUE_NAME));
			for (String queueName : queues) {
				Queue queue = JNDIUtils.queueLookup(context, queueName);
				QueueReceiver receiver = session.createReceiver(queue);
				SIBQueueListener listener = new SIBQueueListener(queueName);
				receiver.setMessageListener(listener);
				LOGGER.info(String.format("Setting Listener For JMS Queue: %s", queueName));
			}
		} catch (NamingException | JMSException e) {
			LOGGER.error(e.getMessage(), e);
		}

	}

	public void sendMessage(String textMessage) {
		try {
			QueueSession session = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			List<String> queues = new ArrayList<String>();
			queues.add(SIBJMSClientProperties.PROPS.getPropertyValue(SIBJMSClientConstants.JMS_JNDI_QUEUE_NAME));
			for (String queueName : queues) {
				Queue queue = JNDIUtils.queueLookup(context, queueName);
				QueueSender sender = session.createSender(queue);
				TextMessage message = session.createTextMessage();
				if (textMessage != null) {
					message = JNDIUtils.addMessageProps(textMessage, message);
				}
				LOGGER.info(String.format("Sending Text Message For JMS Queue: %s", queueName));
				if (message != null) {
					LOGGER.info(message.getText());
					sender.send(message);
					LOGGER.info("Message Count: " + iCount++);
				}

			}
		} catch (NamingException | JMSException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}