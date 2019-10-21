package com.sibjmsclient.swa;

import org.apache.log4j.Logger;

public final class SIBJMSClient {

	private static final Logger LOGGER = Logger.getLogger(SIBJMSClient.class.getName());

	public static void main(String args[]) {
		LOGGER.info("SIB JMS Client Main");
		try {
			SIBQueueManager.getInstance().initRead();
			while (true) {
				Thread.sleep(1000);
			}
		} catch (Exception exception) {
			SIBQueueManager.getInstance().closeConnection();
		} finally {
			SIBQueueManager.getInstance().closeConnection();
		}

	}

}