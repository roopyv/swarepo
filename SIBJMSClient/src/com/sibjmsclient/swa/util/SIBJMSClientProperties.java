package com.sibjmsclient.swa.util;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 */

/**
 * @author swaroop
 *
 */
public enum SIBJMSClientProperties {

	PROPS;

	private final Properties properties;

	String propertiesFile = SIBJMSClientConstants.PROPERTY_FILE;

	SIBJMSClientProperties() {
		properties = new Properties();
		try {
			properties.load(getClass().getClassLoader().getResourceAsStream(propertiesFile));
		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public String getPropertyValue(String key) {
		return properties.getProperty(key);
	}

}
