package com.itlaborer.apitools.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesUtils {

	private static Logger logger = Logger.getLogger(ApiUtils.class.getName());

	public PropertiesUtils() {
	}

	// 读取Properties
	public static Properties ReadProperties(File file) {
		Properties properties = new Properties();
		try {
			properties.load(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		} catch (FileNotFoundException e) {
			logger.error("异常", e);
			return null;
		} catch (IOException e) {
			logger.error("异常", e);
			return null;
		}
		return properties;
	}
}
