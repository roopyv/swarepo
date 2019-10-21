package com.sibjmsclient.swa.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

public final class JNDIUtils {

	private static final Logger LOGGER = Logger.getLogger(JNDIUtils.class.getName());
	private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public static InitialContext initializeJNDIContext() throws NamingException {
		LOGGER.info(String.format("Initializing Context: %s", formatter.format(new Date())));
		LOGGER.info(String.format("WAS Context: %s",
				SIBJMSClientProperties.PROPS.getPropertyValue(SIBJMSClientConstants.WAS_CONTEXT)));
		LOGGER.info(String.format("WAS Context: %s",
				SIBJMSClientProperties.PROPS.getPropertyValue(SIBJMSClientConstants.IIOP_ADDRESS)));
		Hashtable<String, Object> env = new Hashtable<String, Object>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				SIBJMSClientProperties.PROPS.getPropertyValue(SIBJMSClientConstants.WAS_CONTEXT));
		env.put(Context.PROVIDER_URL,
				SIBJMSClientProperties.PROPS.getPropertyValue(SIBJMSClientConstants.IIOP_ADDRESS));
		// env.put("com.ibm.CORBA.ORBInit", "com.ibm.ws.sib.client.ORB");
		// env.put(Context.SECURITY_PRINCIPAL, "wsadmin");
		// env.put(Context.SECURITY_CREDENTIALS, "passw0rd");
		LOGGER.info(String.format("Connecting To: %s",
				SIBJMSClientProperties.PROPS.getPropertyValue(SIBJMSClientConstants.IIOP_ADDRESS)));
		final InitialContext context = new InitialContext(env);
		if (context != null && context.getNameInNamespace() != null) {
			LOGGER.info(String.format("Connected To: %s",
					SIBJMSClientProperties.PROPS.getPropertyValue(SIBJMSClientConstants.IIOP_ADDRESS)));
		}
		return context;
	}

	public static Queue queueLookup(final InitialContext context, String queueName) throws NamingException {
		LOGGER.info(String.format("Checking JMS Queue: %s", queueName));
		Queue queue = (Queue) context.lookup(queueName);
		LOGGER.info(String.format("Found JMS Queue: %s", queueName));
		return queue;
	}

	public static QueueConnectionFactory qcfLookup(final InitialContext context, String qcfName)
			throws NamingException {
		LOGGER.info(String.format("Checking JMS Queue Connection Factory: %s", qcfName));
		QueueConnectionFactory qcf = (QueueConnectionFactory) context.lookup(qcfName);
		LOGGER.info(String.format("Found JMS Queue Connection Factory: %s", qcfName));
		return qcf;
	}

	public static void logJMSProperties(Message msg) throws JMSException {
		if (msg != null) {
			LOGGER.info(String.format("JMSCorrelationID", msg.getJMSCorrelationID()));
			LOGGER.info(String.format("JMSTimestamp", msg.getJMSTimestamp()));
			LOGGER.info(String.format("JMSType", msg.getJMSType()));
			LOGGER.info(String.format("JMSMessageID", msg.getJMSMessageID()));
			LOGGER.info(String.format("JMSReplyTo", msg.getJMSReplyTo()));
		}

	}

	public static TextMessage addMessageProps(String text, TextMessage message) throws JMSException {
		if (text != null && message != null) {
			message.setJMSCorrelationID(Double.toString(Math.random()));
			message.setJMSTimestamp(System.currentTimeMillis());
			message.setJMSType("text");
			message.setText(text);
		}
		return message;
	}

	@SuppressWarnings("unused")
	private void timeTaken(Date date1, Date date2) {

		long diff = date2.getTime() - date1.getTime();

		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);

		LOGGER.info("Time Taken: " + diffSeconds + " seconds.");
	}
}