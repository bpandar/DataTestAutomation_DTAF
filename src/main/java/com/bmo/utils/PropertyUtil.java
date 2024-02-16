package com.bmo.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertyUtil {
	private static final Logger log = LogManager.getLogger(PropertyUtil.class);
	
	public static Properties getPropertyFile(String propertyFile) {
		Properties prop = new Properties();
		try {
			InputStream input = null;
			String rootDir = System.getProperty("user.dir");
			//System.out.println("Root Dir :>" + rootDir);
			if (rootDir.contains("\\")) {
				//System.out.println("File path :>"+ rootDir + "\\config\\"+propertyFile+".properties");
				input = new FileInputStream(rootDir + "\\src\\config\\"+propertyFile+".properties");
			} else {
				input = new FileInputStream(rootDir + "/src/config/"+propertyFile+".properties");
				//System.out.println("File path :>" + rootDir + "/"+"config"+propertyFile+".properties");
			}
			prop.load(input);
		} catch (Exception e) {
			log.debug("Unable to load the ("+propertyFile+") property file, please check the file path to complete the process.");
			log.error("Exception while reading the property file",e);
		}
		return prop;
	}
	
	/*
	 * public static void main(String arg[]) { Properties getProp=
	 * getPropertyFile("osfi_saccr_sql");
	 * System.out.println(getProp.getProperty("baseDir"));
	 * 
	 * }
	 */
	
}
