package com.bmo.database;

import com.bmo.tablecompare.CompareTables;
import com.bmo.utils.PropertyUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;


	public class DBConnection 
	{
		private static final Properties dbInfoProp = PropertyUtil.getPropertyFile("dbconnectioninfo");
		private static final Logger log = LogManager.getLogger(CompareTables.class);
		
		public static FileInputStream fileInput;
		public static String forClass=null;
		public static Connection dbConnection = null;
		public static String dbURL;
		public static String userName;
		public static String passWord;
		public static String sourceUserName, targetUserName;
		public static String sourcePassword, targetPassword;
		public static Statement statement = null;
		public static ResultSet result = null;
		public static List<String> columnNames = new ArrayList<String>();
		public static List<String> dbResults = new ArrayList<String>();
		public static LinkedHashMap<Integer, LinkedHashMap<String, String>> tradeCount = new LinkedHashMap<>();
		public static String sqlQuery = null;
		public static String benchMarkRunid = "null";
		
		
		public static Connection dataBase_Connection(TreeMap<String, String> getInputValues) throws ClassNotFoundException, SQLException, IOException
		{ 
			String serverName = null;
			String selectedServer = null;
			selectedServer = getInputValues.get("Selected");
			// Oracle Database connection details from property file
			forClass = dbInfoProp.getProperty("SACCR_ORACLE_DRIVERNAME");

			Class.forName(forClass);

			try {
				getInputValues.put("BenchMark", benchMarkRunid);

					if (selectedServer.equalsIgnoreCase("Source"))
					{
						serverName 		= getInputValues.get("sourceServer").substring(0,4);

						sourceUserName 	= dbInfoProp.getProperty("SACCR_NEWORACLE_"+ serverName +"_USERNAME");
						sourcePassword 	= dbInfoProp.getProperty("SACCR_NEWORACLE_"+ serverName +"_PASSWORD");
						getInputValues.put("source_username",sourceUserName);
						getInputValues.put("source_password",sourcePassword);


						if (serverName.equalsIgnoreCase("SIT2") || serverName.equalsIgnoreCase("UAT2"));
						{
							serverName = serverName.replaceAll("2", "1");
						}

						dbURL = dbInfoProp.getProperty("SACCR_NEWORACLE_"+ serverName+"_URL");
						System.out.println("Source Server URL is: " + dbURL);

						dbConnection = DriverManager.getConnection(dbURL, sourceUserName, sourcePassword);
					}else
					{
						serverName 		= getInputValues.get("targetServer").substring(0,4);

						targetUserName 	= dbInfoProp.getProperty("SACCR_NEWORACLE_"+ serverName +"_USERNAME");
						targetPassword 	= dbInfoProp.getProperty("SACCR_NEWORACLE_"+ serverName +"_PASSWORD");
						getInputValues.put("target_username",targetUserName);
						getInputValues.put("target_password",targetPassword);

						if (serverName.equalsIgnoreCase("SIT2") || serverName.equalsIgnoreCase("UAT2"));
						{
							serverName = serverName.replaceAll("2", "1");
						}

						dbURL = dbInfoProp.getProperty("SACCR_NEWORACLE_"+ serverName+"_URL");
						System.out.println("Target Server URL is: " + dbURL);

						// Connect to Oracle Database
						dbConnection = DriverManager.getConnection(dbURL, targetUserName, targetPassword);
					}
				} catch (Exception e) {
					System.out.println(" Exception: " + e.toString());
					}
			return dbConnection;				
			
		}
		
		public static String getServerURL(String serverName)
		{
			//String serverName = mappingBeanComTable.getTargetServer();
			String sereverURL = null;
			String subServer = null;
			subServer = serverName.substring(0, 3); 
			String server = serverName.substring(0,4);
					if(subServer.equalsIgnoreCase("DEV"))
					{
						sereverURL = dbInfoProp.getProperty("SACCR_NEWORACLE_"+ server +"_URL");
					}
					else if(subServer.equalsIgnoreCase("SIT"))
					{
						sereverURL = dbInfoProp.getProperty("SACCR_NEWORACLE_"+ server +"_URL");
					}
					else if(subServer.equalsIgnoreCase("UAT"))
					{
						sereverURL = dbInfoProp.getProperty("SACCR_NEWORACLE_"+ server +"_URL");
					}
					else if (subServer.equalsIgnoreCase("PROD"))
					{
						sereverURL = dbInfoProp.getProperty("SACCR_NEWORACLE_"+ server +"_URL");
					}

			return sereverURL;
		}


}