package com.sibjmsclient.swa.publish;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import com.sibjmsclient.swa.SIBQueueManager;

public class SIBJMSServer {

	private static final Logger LOGGER = Logger.getLogger("sendLogger");
	public static String vesselCallEvent = null;

	public static void main(String args[]) {

		LOGGER.info("SIB JMS Server Main");
		try {
			SIBQueueManager.getInstance().initSend();
			vesselCallEvent = new String(Files
					.readAllBytes(Paths.get("D:\\SWAROOP\\CODE\\WS\\SWA_v1\\SIBJMSClient\\src\\vesselcallevent.json")));
			for (int i = 0; i <= 3000; i++) {
				SIBQueueManager.getInstance().sendMessage(vesselCallEvent);
			}
		} catch (Exception exception) {
			SIBQueueManager.getInstance().closeConnection();
		} finally {
			SIBQueueManager.getInstance().closeConnection();
		}

	}

}
