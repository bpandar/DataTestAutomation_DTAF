package com.bmo.test.inputquery;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.bmo.database.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;;
//import com.bmo.excel.WritingToExcel;
//import com.bmo.query.QueryProvider;


public class UiQueryResults
{
	private static final Logger log = LogManager.getLogger(UiQueryResults.class);
	//public static HashMap<List<String>, List<String>> queryResults;
	public static List<String> columnNames;
	public static List<String> rowData;
	public static String queryColumnName;
	public static String getQueryResults;
	//public static Multimap<Object,Object> queryResults;
	//public static Multimap<Object,Object> outputData;
	public static Map<String, List<Object>> outputData;
	public static HashMap<String,Object> queryResults;
	public static Map<String, List<Object>> listOfResults;
	public static String[] sqlQuery;
	public static Connection dbConnection;
	public static Statement statement;
	
	public static String getQueryResults(TreeMap<String, String> getInputValues) throws Exception 
	{	
		 String operationMsg;
		try {
			
			dbConnection = DBConnection.dataBase_Connection(getInputValues);
			statement = dbConnection.createStatement();
			sqlQuery = InputSQLQuery.getRegressionQuery(getInputValues);

			// TODO Auto-generated method stub
			for (int i = 0; i < sqlQuery.length; i++)
			{
				switch (i) 
				{
				case 0:
					outputData = getResultSet(sqlQuery[i], statement);
					//WritingToExcel.setRunIDSheet(getInputValues,sqlQuery[i],statement);
					break;
				case 1:
					outputData = getResultSet(sqlQuery[i], statement);
					//WritingToExcel.getTradeCount(getInputValues,sqlQuery[i],statement);
					break;
				case 2:
					outputData = getResultSet(sqlQuery[i], statement);
					//WritingToExcel.getStandaloneValue(getInputValues,sqlQuery[i],statement);
					break;
				case 3:
					outputData = getResultSet(sqlQuery[i], statement);
					// WritingToExcel.getQueryData(outputData);
					break;
				case 4:
					outputData = getResultSet(sqlQuery[i], statement);
					// WritingToExcel.getQueryData(outputData);
					break;
				case 5:
					outputData = getResultSet(sqlQuery[i], statement);
					// WritingToExcel.getQueryData(outputData);
					break;
				case 6:
					outputData = getResultSet(sqlQuery[i], statement);
					// WritingToExcel.getRAWSourceResults(outputData);
					break;
				case 7:
					outputData = getResultSet(sqlQuery[i], statement);
					// WritingToExcel.getRAWAssetClassResults(outputData);
					break;
				default:
					log.debug(" .SQL file is empty ");

				}
			}
			
			operationMsg = "Done";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.debug(e.getStackTrace());
			operationMsg = " Exception: " + e.toString();
		}
		
		return operationMsg;

	}
		
	

	public static Map<String, List<Object>> getResultSet(String sqlQuery, Statement statement) throws SQLException 
	{
		queryResults = new HashMap<String, Object>();
		columnNames = new ArrayList<String>();
		rowData =new ArrayList<String>();
		System.out.println("=====>>>" + sqlQuery);
			ResultSet result = statement.executeQuery(sqlQuery);
			//if (result != null) 
			//{
				ResultSetMetaData columns = result.getMetaData();
				log.debug("Total column :> "+columns.getColumnCount());
				int columnCount = columns.getColumnCount();
				
			  listOfResults = new HashMap<String, List<Object>>();
			    for (int i = 1; i <= columnCount; ++i) 
			    {
			    	listOfResults.put(columns.getColumnName(i), new ArrayList<>());
			    	System.out.print(columns.getColumnName(i)+ "\t");
			    }
			    System.out.print("\n");
			    
			    while (result.next()) 
			    {	
			    	for (int k = 1; k <= columnCount; ++k) 
			    	{
			        	listOfResults.get(columns.getColumnName(k)).add(result.getString(k));
			            System.out.print( result.getString(k)+ "\t");
			    	}
			    	System.out.println();
			    }
				
			    
			return listOfResults;
		}
	
}

