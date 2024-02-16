//package com.bmo.utils;
//
//import java.io.FileNotFoundException;
//
//import org.apache.log4j.FileAppender;
//import org.apache.log4j.Level;
//import org.apache.log4j.Logger;
//import org.apache.log4j.PatternLayout;
//
//public class LogUtil {
//
//
//	public static void initiateLogger() throws FileNotFoundException{
//
//		String rootDir = System.getProperty("user.dir");
//
//		// creates pattern layout
//		PatternLayout layout = new PatternLayout();
//		String conversionPattern = "%-7p %d [%t] %c %x - %m%n";
//		layout.setConversionPattern(conversionPattern);
//
//		// creates console appender
//		/* ConsoleAppender consoleAppender = new ConsoleAppender();
//		        consoleAppender.setLayout(layout);
//		        consoleAppender.activateOptions();*/
//
//		// creates file appender
//		FileAppender fileAppender = new FileAppender();
//		if(rootDir.contains("\\")){
//			System.out.println("Log file creation...");
//			fileAppender.setFile(rootDir+"\\LogFile.log");
//		}else{
//			System.out.println("Log file creation...");
//			fileAppender.setFile(rootDir+"/LogFile.log");
//		}
//		fileAppender.setLayout(layout);
//		fileAppender.activateOptions();
//		// configures the root logger
//		Logger rootLogger = Logger.getRootLogger();
//		rootLogger.setLevel(Level.DEBUG);
//		//rootLogger.addAppender(consoleAppender);
//		rootLogger.addAppender(fileAppender);
//	}
//
//}
