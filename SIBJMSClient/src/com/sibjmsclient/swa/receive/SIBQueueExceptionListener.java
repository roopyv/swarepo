package com.sibjmsclient.swa.receive;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.apache.log4j.Logger;

public class SIBQueueExceptionListener implements ExceptionListener {

	private static final Logger LOGGER = Logger.getLogger(SIBQueueExceptionListener.class.getName());

	public SIBQueueExceptionListener() {

	}

	@Override
	public void onException(JMSException e) {
		LOGGER.error(e.getMessage(), e);
	}
}
