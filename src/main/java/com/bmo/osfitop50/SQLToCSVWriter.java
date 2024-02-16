package com.bmo.osfitop50;

import com.bmo.utils.PropertyUtil;
import com.opencsv.CSVWriter;
import com.opencsv.ResultSetHelperService;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;


public class SQLToCSVWriter {
	
	private static Properties osfiTop50Prop = PropertyUtil.getPropertyFile("osfi_saccr_sql");
	
	public static OsfitTop50MappingBean mappingObj = null;
	
	public static void getInputData()
	{
		mappingObj = new OsfitTop50MappingBean();
			
		
		  Scanner serverScan = new Scanner(System.in); 
		  Scanner busDateScan = new Scanner(System.in); 
		  System.out.println("Enter ServerName: "); 
		  String  serverName = serverScan.next();
		  
		  System.out.println("Enter As of Date: " ); 
		  String busDate = busDateScan.next();
		 
	    
	   // System.out.println( serverName+ busDate);
	    mappingObj.setserverName(serverName);
	    mappingObj.setBusinessDate(busDate);
	    
	   System.out.println("===>"+osfiTop50Prop.getProperty("resources"));
	   System.out.println("===>"+osfiTop50Prop.getProperty("sql_Dir"));
	    
	   // List <String> inputList = new ArrayList<String>();
		
	   //busDateScan.close();
	   //serverScan.close();
	    
	   // return inputList;
	}
	
	
	public static void executeSQLQuery() {
		
		OsfitTop50MappingBean newMapping = new OsfitTop50MappingBean();
		Map<String, String> actualQueryMap = null;
		
		//List <String> getinputList = new ArrayList<String>();
		//getinputList = getInputData();
		
		System.out.println(mappingObj.getbusDate()+"\n"+mappingObj.getserverName());
		
		try {
			
			Connection dbConnection = ConnectSACCRDB.dataBase_Connection(mappingObj.getserverName());
			
			Statement sourceStmt = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			sourceStmt.setFetchSize(1000);
			
			actualQueryMap = new LinkedHashMap<>();
			newMapping = SQLQueryReader.readQueryFile();
			
			actualQueryMap =newMapping.getQueryAsMap();
			
			// System.out.println(
			// "\n+=========+==========================================================+");
			// System.out.format("%-2s%2s%-2s%50s","|RowCount","|"," FileName","|");
			// System.out.println(
			// "\n+=========+==========================================================+");
			 
			System.out.format("+-----+----------+------------------------------------------------------+%n");
			System.out.format("|S.No | RowCount | FileName                                             |%n");
			System.out.format("+-----+----------+------------------------------------------------------+%n");
			int sNum = 0 ;
			for (String key : actualQueryMap.keySet()) {
				sNum++;
				//if (key.equalsIgnoreCase("part3_SACCR_Override_SACCR_Override.sql")){
				//System.out.println(key + "\nvalue : "+  actualQueryMap.get("part3_SACCR_Override_SACCR_Override.sql"));
				String businessDate = "'"+mappingObj.getbusDate()+"'";
				String sqlQuery = actualQueryMap.get(key).toString().replaceAll("\\?", businessDate);
				
				//System.out.println(actualQueryMap.get(key) + "\n "+sqlQuery);

				ResultSet resultSet = sourceStmt.executeQuery(sqlQuery);
				
				//StringWriter stringWriter = new StringWriter();
				FileWriter csvFile =  new FileWriter(osfiTop50Prop.getProperty("Results_Path")+"\\"+key+".csv");
			    CSVWriter csvWriter = new CSVWriter(csvFile);

//						(csvFile, CSVWriter.DEFAULT_SEPARATOR
//			    									,CSVWriter.DEFAULT_ESCAPE_CHARACTER
//			    									, CSVWriter.RFC4180_LINE_END);
			    								//, CSVWriter.NO_QUOTE_CHARACTER

			    ResultSetHelperService resultSetHelperService= new ResultSetHelperService();
			    resultSetHelperService.setDateFormat("yyyy-MM-dd HH:mm:ss");
			    resultSetHelperService.setDateTimeFormat("yyyy-MM-dd");
			    csvWriter.setResultService(resultSetHelperService);
			    
			    csvWriter.writeAll(resultSet, true); // including column names
			    csvWriter.flush();
			   // String result = stringWriter.toString();
			     		    
			    String leftAlignFormat = "| %-3d | %-8d | %-52s |%n";
				if (resultSet.last()) {
					System.out.format(leftAlignFormat, sNum, resultSet.getRow(), key);
				}else 
				{
					System.out.format(leftAlignFormat, sNum, resultSet.getRow(), key);
				}
			    csvWriter.close();
			}
			
			System.out.format("+-----+----------+------------------------------------------------------+%n");
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	

	public static void main(String[] args) {
		
		getInputData();
		executeSQLQuery();
				
	}
	
	
	
	

}
