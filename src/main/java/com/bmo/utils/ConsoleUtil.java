	/**
	 *  Author      	: Bhoopathy P
	 *  Date 			: Aug 02, 2021
	 *  Class Name		: WriteErrorConsoleUtil.java
	 *  
	 *  Project			: BMO - SACCR
	 *
	 *	Changes			: 	
	 *  
	 *  Modification history 
	 *  Date			ChangeNo	Modified By							Description
	 *  
	 *  
	 */
package com.bmo.utils;

import com.bmo.main.InputJFrameUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ConsoleUtil {
	
		private static final Logger log = LogManager.getLogger(ConsoleUtil.class);
		public static boolean isAppConsoleEnabled=false;
		public static void logException(Exception ex){
			try{
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				ex.printStackTrace(pw);
				consoleAppender(sw.toString());
				pw.flush();
				pw.close();
			}catch (Exception e) {
				log.error("Unable to append the error in AppConsole",e);
			}
		}
		public static void consoleAppender(String txt){
			if(ConsoleUtil.isAppConsoleEnabled){
				InputJFrameUI.getApplicationConsole().append("\n"+txt);
				//applicationConsole.setFont(value);
			}
		}
	}
