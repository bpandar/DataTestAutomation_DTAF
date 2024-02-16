package com.bmo.tablecompare;

import com.bmo.csvcompare.CSVCompareWithSummary;
import com.bmo.database.DBConnection;
import com.bmo.mappingbean.MappingBean;
import com.bmo.utils.PropertyUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.sql.*;
import java.util.*;

public class CompareTables extends CSVCompareWithSummary
{
	private static final Properties configProp = PropertyUtil.getPropertyFile("config");
	private static final Properties queryProp = PropertyUtil.getPropertyFile("Queries");
	private static final Properties dbInfoProp = PropertyUtil.getPropertyFile("dbconnectioninfo");
	private static final Logger log = LogManager.getLogger(CompareTables.class);
		
	public static CompareTables tableCompareInstance;

	public static String forClass=null;
	public static Connection sDBConnection = null;
	public static Connection tDBConnection = null;
	public static String sourceDbURL, targetDbURL;
	public static Statement sourceStatement = null;
	public static Statement targetStatement = null;
	public static String userName;
	public static String passWord;
	
	public static CompareTables getInstance() {
		if (tableCompareInstance == null) {
			tableCompareInstance = new CompareTables();
		}
		return tableCompareInstance;
	}
	
	public static MappingBean compareDBTableAsMap(MappingBean mappingBeanobj) {
		MappingBean mappingBeanComTable = mappingBeanobj;

		String sourceServer = mappingBeanComTable.getSourceServer().substring(0,4);
		String targetServer = mappingBeanComTable.getTargetServer().substring(0,4);
		try {

			String sourceUserName 	= dbInfoProp.getProperty("SACCR_NEWORACLE_"+ sourceServer +"_USERNAME");
			String sourcePassword 	= dbInfoProp.getProperty("SACCR_NEWORACLE_"+ sourceServer +"_PASSWORD");
			String targetUserName 	= dbInfoProp.getProperty("SACCR_NEWORACLE_"+ targetServer +"_USERNAME");
			String targetPassword 	= dbInfoProp.getProperty("SACCR_NEWORACLE_"+ targetServer +"_PASSWORD");


			String sourceDbURL = DBConnection.getServerURL(sourceServer);
			String targetDbURL = DBConnection.getServerURL(targetServer);

			sDBConnection = DriverManager.getConnection(sourceDbURL, sourceUserName, sourcePassword);
			tDBConnection = DriverManager.getConnection(targetDbURL, targetUserName, targetPassword);

			mappingBeanComTable.setSourceDBConnection(sDBConnection);
			mappingBeanComTable.setTargetDBConnection(tDBConnection);

			String operationMsg1=null;
			String operationMsg2=null;
			String srcTableName = mappingBeanComTable.getSourceTableName();
			String tarTableName = mappingBeanComTable.getTargetTableName();

			if (srcTableName.contains("FROM") || srcTableName.contains("from") || srcTableName.contains("From") || tarTableName.contains("select")
					|| tarTableName.contains("Select") || tarTableName.contains("SELECT"))
			{
				mappingBeanComTable.setSourceBRFlag("DB_QUERY");
				mappingBeanComTable.setTargetBRFlag("DB_QUERY");
			}else {
				operationMsg1 = sourceCheck(mappingBeanComTable);
				operationMsg2 = targetCheck(mappingBeanComTable);
			}

			System.out.println(" Source Table Flag: "+mappingBeanComTable.getSourceBRFlag()
						+ "\n Target Table Flag: "+ mappingBeanComTable.getSourceBRFlag());

			String operationMsg = operationMsg1 + operationMsg2;// Returning error message string
			mappingBeanComTable.setOperationMsg(operationMsg);

			FetchTableData.getSourceDBResultSet(mappingBeanComTable);
			FetchTableData.getTargetDBResultSet(mappingBeanComTable);

			System.out.println("\n Source Composite Keys: " + mappingBeanComTable.getSourcePrimaryKey()
							 + "\n Target Composite Keys: " + mappingBeanComTable.getTargetPrimaryKey());
			System.out.println("\n Source Non-Composite Keys: " + mappingBeanComTable.getSourceColumns()
							 + "\n Target Non-Composite Keys: " + mappingBeanComTable.getTargetColumns());

			compareCSVFilesAsMap(mappingBeanComTable);

			System.out.println(mappingBeanComTable.getSummaryResultList());
			mappingBeanComTable.setOperationMsg(mappingBeanComTable.getOperationMsg());

		} catch (Exception e) {
			System.out.println(" Exception " + e.toString());
			log.error(e.toString());
			log.debug(e.toString());
			mappingBeanComTable.setOperationMsg("Failed");
		}

		return mappingBeanComTable;
	}

	
	public static MappingBean compareDBWithCSV(MappingBean mappingBeanobj) {

		MappingBean mappingBeanComTable = mappingBeanobj;
		String sourceServer = mappingBeanComTable.getSourceServer().substring(0, 4);
		try {

			String dbUserName 	= dbInfoProp.getProperty("SACCR_NEWORACLE_"+ sourceServer +"_USERNAME");
			String password 	= dbInfoProp.getProperty("SACCR_NEWORACLE_"+ sourceServer +"_PASSWORD");

			String sourceDbURL = DBConnection.getServerURL(mappingBeanComTable.getSourceServer());
			sDBConnection = DriverManager.getConnection(sourceDbURL, dbUserName, password);
			mappingBeanComTable.setSourceDBConnection(sDBConnection);

			if (!mappingBeanComTable.getSourceTableName().contains("Select") && !mappingBeanComTable.getSourceTableName().contains("from")
					&& !mappingBeanComTable.getSourceTableName().contains("select") && !mappingBeanComTable.getSourceTableName().contains("SELECT")
					&& !mappingBeanComTable.getSourceTableName().contains("FROM") && !mappingBeanComTable.getSourceTableName().contains("From"))
			{
				sourceCheck(mappingBeanComTable);
				FetchTableData.getSourceDBResultSet(mappingBeanComTable);
			}else {	
				mappingBeanComTable.setSourceBRFlag("DB_QUERY");
				FetchTableData.getSourceDBResultSet(mappingBeanComTable);
			}

			List<String> targetList = excludeSkipColumns(mappingBeanComTable);

			mappingBeanComTable.setTargetFields(targetList.toString().replace("[", "").replace("]", ""));
			mappingBeanComTable.setSourceFields(mappingBeanComTable.getSourceColumns());

			System.out.println("\n Source Composite Keys: " + mappingBeanComTable.getSourcePrimaryKey()
					+ "\n Target Composite Keys: " + mappingBeanComTable.getTargetPrimaryKey());
			System.out.println("\n Source Non-Composite Keys: " + mappingBeanComTable.getSourceFields()
					+ "\n Target Non-Composite Keys: " + mappingBeanComTable.getTargetFields());

			mappingBeanComTable
					.setOperationMsg(CSVCompareWithSummary.compareCSVFilesAsMap(mappingBeanComTable).getOperationMsg());

		} catch (Exception e) {
			System.out.println(" Exception " + e.toString());
			mappingBeanComTable.setOperationMsg("Failed");
			JOptionPane.showMessageDialog(null, e.toString());
		}
		return mappingBeanComTable;
	}

	public static List<String> excludeSkipColumns(MappingBean mappingBeanComTable)
	{
		List<String> targetList = null;
		Set<String> skipColumnSet = null;

		skipColumnSet = new HashSet<String>(Arrays.asList(configProp.getProperty("TARGET_COLUMNS_SKIP").trim().split(",")));
		targetList = new LinkedList<String>(Arrays.asList(mappingBeanComTable.getTargetFields().split(",")));

		for (String skipField : skipColumnSet) {
			if (targetList.contains(skipField)) {
				targetList.remove(skipField);
			}
		}
		return targetList;
	}

	private static String sourceCheck(MappingBean mappingBeanComTable)
	{
		List<String> sourceColumns = new ArrayList<String>();
		String sTabelName = mappingBeanComTable.getSourceTableName().toUpperCase();
		String primarykeys = mappingBeanComTable.getSourcePrimaryKey().toString().toUpperCase();;
		String operationMsg = null;
		String sqlQuery = null ;
		
	try {
			if (!sTabelName.contains("SA_CCR") || !sTabelName.contains("SELECT"))
			{
				sqlQuery = queryProp.getProperty("ROWNUM_QUERY").replace(":TABLE_NAME", sTabelName);
			}else{
				sqlQuery = sTabelName.replace(":BUS_DT", "'"+mappingBeanComTable.getSourceBusDate()+"'").replace(":RUN_ID",mappingBeanComTable.getSourceRunID());
			}
			//System.out.println(sqlQuery);
			Connection dbConnection = mappingBeanComTable.getSourceDBConnection();
			PreparedStatement  preparedStmt = dbConnection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
       	 	
			ResultSet results = preparedStmt.executeQuery();	 	       	 	
			ResultSetMetaData metaData = results.getMetaData();
			int columncount = metaData.getColumnCount();
			if(!(sTabelName == null) && !(primarykeys == null))
			{
				if (columncount < 1)
				{
					JOptionPane.showMessageDialog (null, "Table is Empty");
				}					
				operationMsg = "Done";
			}else 
			{
				JOptionPane.showMessageDialog(null, " Table is not present or Primary Keys are missing....!");
			}
			String[] busRun = {"BUS_DT","RUN_ID"};
			PreparedStatement  preparedStmt1 = dbConnection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
       	 	
			ResultSet resultSet = preparedStmt1.executeQuery();	 	       	 	
			
			ResultSetMetaData metaData1 = resultSet.getMetaData();
			int columncount1 = metaData1.getColumnCount();
			for(int i =1; i<=columncount1;i++ )
			{
				sourceColumns.add(metaData1.getColumnName(i));
			}
			StringBuilder checkBR = new StringBuilder(); 
			for (int j=0; j<busRun.length; j++)
			{
				if(sourceColumns.contains(busRun[j]))
				{
					checkBR.append(busRun[j]);
				}
			}
			mappingBeanComTable.setSourceBRFlag(checkBR.toString());
			
		}catch (SQLException e) 
		{
			System.out.println(e.getMessage().toString());
			log.debug(e.getMessage());
			if (e.toString().contains("table or view does not"))
			{
				JOptionPane.showMessageDialog(null, e.toString() + sTabelName );
			}
			else if (e.toString().contains("invalid identifier"))
			{
				JOptionPane.showMessageDialog(null, e.toString()+" in "+ sTabelName);
			}
			else
			{
				JOptionPane.showMessageDialog(null, e.toString());
			}
			operationMsg = e.toString();
		}           	 	          	 	
      return  operationMsg;     	
	}

	private static String targetCheck(MappingBean mappingBeanComTable) 
	{
		String tTabelName = mappingBeanComTable.getTargetTableName().toUpperCase();
		String primarykeys = mappingBeanComTable.getTargetPrimaryKey().toString().toUpperCase();
		//String primarykeys = keys.substring(1, keys.length()-1);
		//mappingBeanComTable.setTargetPrimaryKey(primarykeys);
		String operationMsg = null;
	try {
			if(!(tTabelName == null) && !(primarykeys == null))
			{
				String sqlQuery  = queryProp.getProperty("PRIMARY_KEY_QUERY").replace(":PRIMARY_KEY",primarykeys).replace(":TABLE_NAME", tTabelName);

				Connection dbConnection = mappingBeanComTable.getSourceDBConnection();
				PreparedStatement  preparedStmt = dbConnection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	       	 	
				ResultSet resultSet = preparedStmt.executeQuery();	 
				
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columncount = metaData.getColumnCount();
				if (columncount < 1)
				{
					JOptionPane.showMessageDialog (null, "Table is Empty");
				}
				List<String> targetColumns = new ArrayList<String>();
				String[] busRun = {"BUS_DT","RUN_ID"};

				String sqlQuery1  = queryProp.getProperty("ROWNUM_QUERY").replace(":TABLE_NAME", tTabelName);

				PreparedStatement  preparedStmt1 = dbConnection.prepareStatement(sqlQuery1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);		
				ResultSet resultSet1 = preparedStmt1.executeQuery();
				
				ResultSetMetaData metaData1 = resultSet1.getMetaData();
				
				int columncount1 = metaData1.getColumnCount();
				for(int i =1; i<=columncount1;i++ )
				{
					targetColumns.add(metaData1.getColumnName(i));
				}
				StringBuilder checkBR = new StringBuilder(); 
				for (int j=0; j<busRun.length; j++)
				{
					if(targetColumns.contains(busRun[j]))
					{
						checkBR.append(busRun[j]);
					}
				}
				mappingBeanComTable.setTargetBRFlag(checkBR.toString());
				operationMsg = "Done";
			}else 
			{
				JOptionPane.showMessageDialog(null, " Table is not present or Primary Keys are missing....!");
			}
		}catch (SQLException e) 
			{
				System.out.println(e.getMessage().toString());
				
				if (e.toString().contains("table or view does not"))
				{
					JOptionPane.showMessageDialog(null, e.toString() + tTabelName );
				}
				else if (e.toString().contains("invalid identifier"))
				{
					JOptionPane.showMessageDialog(null, e.toString()+" in "+ tTabelName);
				} 
				else
				{
					JOptionPane.showMessageDialog(null, e.toString());
				}
				operationMsg = e.toString();
			}           	 	          	 	
      return  operationMsg;  
		
	}

	public static MappingBean uiTableCompare(MappingBean mappingBeanObj) {
		Map<String, List<Integer>> resultAsMap = new HashMap<>();
		Map<String, String> statusMap = new HashMap<>();

		mappingBeanObj = compareDBTableAsMap(mappingBeanObj);

		resultAsMap.put(mappingBeanObj.getSourceTableName(), mappingBeanObj.getSummaryResultList());
		statusMap.put(mappingBeanObj.getSourceTableName(), "Completed");

		mappingBeanObj.setResultMap(resultAsMap);
		mappingBeanObj.setStatusAsMap(statusMap);

		return mappingBeanObj;
	}
}