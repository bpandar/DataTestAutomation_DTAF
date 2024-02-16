package com.bmo.osfitop50;

import com.bmo.utils.PropertyUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;


	public class ConnectSACCRDB 
	{
		private static Properties dbInfoProp = null; //= PropertyUtil.getPropertyFile("dbconnectioninfo");
		
		//private static Properties saccrSQLProp = PropertyUtil.getPropertyFile("osfi_saccr_sql");
		
		public static FileInputStream fileInput;
		public static String forClass=null;
		public static Connection dbConnection = null;
		public static String dbURL;
		public static String userName;
		public static String passWord;
		public static Statement statement = null;
		public static ResultSet result = null;
		public static List<String> columnNames = new ArrayList<String>();
		public static List<String> dbResults = new ArrayList<String>();
		public static LinkedHashMap<Integer, LinkedHashMap<String, String>> tradeCount = new LinkedHashMap<>();
		public static String sqlQuery = null;
		public static String benchMarkRunid = "null";
		
		
		public static Connection dataBase_Connection(String serverDetail) throws ClassNotFoundException, SQLException, IOException
		{ 
			String serverName = serverDetail;
			// Oracle Database connection details from property file
			
			dbInfoProp = PropertyUtil.getPropertyFile("dbconnectioninfo");
					//.getPropertyFile("dbconnectioninfo");
			
			forClass = dbInfoProp.getProperty("SACCR_ORACLE_DRIVERNAME");
			
			if (serverName.equalsIgnoreCase("SIT2") || serverName.equalsIgnoreCase("UAT2"));
			{
				serverName = serverName.replaceAll("2","1");
			}			
				
			String sourceUserName 	= dbInfoProp.getProperty("SACCR_ORACLE_USERNAME");
			String sourcePassword 	= dbInfoProp.getProperty("SACCR_ORACLE_PASSWORD");		
			
			try {				
					dbURL = getServerURL(serverName);
					System.out.println("Server URL is: " + dbURL);			
					
					// Load Oracle JDBC Driver
					Class.forName(forClass);
					
					// Connect to Oracle Database
					dbConnection = DriverManager.getConnection(dbURL, sourceUserName, sourcePassword);
						
				} catch (Exception e) {
					System.out.println(" Exception: " + e.toString());
					}
			return dbConnection;				
			
		}
		
		public static String getServerURL(String serverName)
		{
			String sereverURL = null;
			String subServer = null;
			subServer = serverName.substring(0, 3); 
			String server = serverName.substring(0,4);
					if(subServer.equalsIgnoreCase("DEV"))
					{
						sereverURL = dbInfoProp.getProperty("SACCR_NEWORACLE_"+ server+"");
					}
					else if(subServer.equalsIgnoreCase("SIT"))
					{
						sereverURL = dbInfoProp.getProperty("SACCR_NEWORACLE_"+ server+"");
					}
					else if(subServer.equalsIgnoreCase("UAT"))
					{
						sereverURL = dbInfoProp.getProperty("SACCR_NEWORACLE_"+ server+"");				
					}
					else if (subServer.equalsIgnoreCase("PROD"))
					{
						sereverURL = dbInfoProp.getProperty("SACCR_NEWORACLE_"+ server+"");
					}

			return sereverURL;
		}

}
