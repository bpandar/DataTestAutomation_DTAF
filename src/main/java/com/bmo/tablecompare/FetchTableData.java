/**
 *  Author      	: Bhoopathy P
 *  Date 			: Apr 17, 2021
 *  Class Name		: FetchDBData.java
 *  
 *  Project			: BMO - Risk ODS
 *
 *	Changes			: 	
 *  
 *  Modification history 
 *  Date			ChangeNo	Modified By							Description
 *  
 *  
 */
package com.bmo.tablecompare;

import com.bmo.csvcompare.CSVCompareWithSummary;
import com.bmo.mappingbean.CSVResultBean;
import com.bmo.mappingbean.MappingBean;
import com.bmo.utils.CSVParserUtil;
import com.bmo.utils.PropertyUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;

public class FetchTableData {
	private static final Logger log = LogManager.getLogger(FetchTableData.class);
	//private static Properties csvResourceBundle = PropertyUtil.getPropertyFile("ResultSummaryMappings");
	private static Properties sqlQueryProp = PropertyUtil.getPropertyFile("Queries");
	private static Properties configProp = PropertyUtil.getPropertyFile("config");
	
	private static CSVCompareWithSummary fetchTableObj = null;

	public static CSVCompareWithSummary getInstance() { // public static synchronized CSVCompare getInstance() //Thread
														// safe singleton class
		if (fetchTableObj == null) {
			fetchTableObj = new CSVCompareWithSummary();
			log.debug("Fetch Table data new object initialized...");
		}
		return fetchTableObj;
	}

	public static MappingBean getSourceDBResultSet(MappingBean mappingBeanObj) throws Exception {

			mappingBeanObj = getSourceMetaData(mappingBeanObj);

			ResultSet result = mappingBeanObj.getSourceResultSet();
			ResultSetMetaData rm = result.getMetaData();
			
			log.debug("Total column :> " + rm.getColumnCount());
			StringBuilder sb = new StringBuilder();
			//StringBuilder valueColumn = new StringBuilder();		
			
			List<String> linkedList = new LinkedList<String>();
			
			for (int i = 1; i <= rm.getColumnCount(); i++) {
				log.debug(rm.getColumnName(i) + "\t");
				if (i != 1) {
					sb.append(",");
					//valueColumn.append("||\',\'||");
				}
				sb.append(rm.getColumnName(i));
				//valueColumn.append(rm.getColumnName(i));
				linkedList.add(rm.getColumnName(i));				
			}				
				
			Set<String> skipColumns = new HashSet<String>();
			String[] inputSkipColumns = null;		
			if(mappingBeanObj.getSourceSkipColumn().isEmpty()) 
			{
				inputSkipColumns = configProp.getProperty("SOURCE_COLUMNS_SKIP").trim().split(",");
				skipColumns =  new HashSet<String>(Arrays.asList(inputSkipColumns));
				//removeSkipColumns(linkedList, skipColumns);
			}else {		
				skipColumns = mappingBeanObj.getSourceSkipColumn();
				skipColumns.addAll(Arrays.asList(configProp.getProperty("SOURCE_COLUMNS_SKIP").trim().split(",")));
			}
			for(String skipField :skipColumns )
			{
				if(linkedList.contains(skipField)) {
					linkedList.remove(skipField);
				}
			}				
			mappingBeanObj.setSourceColumns(linkedList.toString().replace("[", "").replace("]", ""));		
			log.debug("Source column header :>" + linkedList.toString());		
				
		/*
		 * String filePath = "\\JavaWorkSpace\\SACCRTablesCompare\\Data\\Source\\"; //
		 * String targetFilePath = new File(filePath); String rootDir =
		 * System.getProperty("user.dir"); if (rootDir.contains("\\")) { filePath =
		 * filePath.substring(0, filePath.lastIndexOf("\\")) + "\\" +
		 * csvResourceBundle.getProperty("SDR_FILE_NAME").replace("###",
		 * DateTimeFormatter.ofPattern("ddMMyyyyHHmmss").format(LocalDateTime.now())); }
		 * else { filePath = filePath.substring(0, filePath.lastIndexOf("/")) + "/" +
		 * csvResourceBundle.getProperty("SDR_FILE_NAME").replace("###",
		 * DateTimeFormatter.ofPattern("ddMMyyyyHHmmss").format(LocalDateTime.now())); }
		 */
			result.close();
			return mappingBeanObj;
		} 

	public static MappingBean getTargetDBResultSet(MappingBean mappingBeanObj) throws Exception {

			mappingBeanObj = getTargetMetaData(mappingBeanObj);
			System.out.println(" Target Table Name/Query: "+ mappingBeanObj.getTargetSQLQuery());

			ResultSet result = mappingBeanObj.getTargetResultSet();
			ResultSetMetaData rm = result.getMetaData();
			
			log.debug("Total column :> " + rm.getColumnCount());
			StringBuilder sb = new StringBuilder();
			//StringBuilder valueColumn = new StringBuilder();
					
			List<String> targetList = new LinkedList<String>();
			
			for (int i = 1; i <= rm.getColumnCount(); i++) {
				log.debug(rm.getColumnName(i) + "\t");
				if (i != 1) {
					sb.append(",");
					//valueColumn.append("||\',\'||");
				}
				sb.append(rm.getColumnName(i));
				//valueColumn.append(rm.getColumnName(i));
				targetList.add(rm.getColumnName(i));
			}			
			Set<String> skipColumns = new HashSet<String>();
			String[] inputSkipColumns = null;
			if(mappingBeanObj.getTargetSkipColumn().isEmpty()) 
			{
				inputSkipColumns = configProp.getProperty("TARGET_COLUMNS_SKIP").trim().split(",");
				skipColumns =  new HashSet<String>(Arrays.asList(inputSkipColumns));
				//removeSkipColumns(targetList, skipColumns);
			}else {						
				skipColumns = mappingBeanObj.getTargetSkipColumn();
				skipColumns.addAll(Arrays.asList(configProp.getProperty("TARGET_COLUMNS_SKIP").trim().split(",")));
			}
			for(String skipField :skipColumns )
			{
				if(targetList.contains(skipField)) {
					targetList.remove(skipField);
				}
			}	
			mappingBeanObj.setTargetColumns(targetList.toString().replace("[", "").replace("]", ""));
			log.debug("Traget column header :>" + targetList.toString());
			
		/*
		 * String filePath = "\\JavaWorkSpace\\SACCRTablesCompare\\Data\\Target\\";
		 * String rootDir = System.getProperty("user.dir"); if (rootDir.contains("\\"))
		 * { filePath = filePath.substring(0, filePath.lastIndexOf("\\")) + "\\" +
		 * csvResourceBundle.getProperty("SDR_FILE_NAME").replace("###",
		 * DateTimeFormatter.ofPattern("ddMMyyyyHHmmss").format(LocalDateTime.now())); }
		 * else { filePath = filePath.substring(0, filePath.lastIndexOf("/")) + "/" +
		 * csvResourceBundle.getProperty("SDR_FILE_NAME").replace("###",
		 * DateTimeFormatter.ofPattern("ddMMyyyyHHmmss").format(LocalDateTime.now())); }
		 */
			
			result.close();
			return mappingBeanObj;
	}
	
	/*
	 * public static List<String> removeSkipColumns(List<String> linkedList,
	 * Set<String> set){
	 * 
	 * for(String skipField :set ) { if(linkedList.contains(skipField)) {
	 * linkedList.remove(skipField); } } return linkedList; }
	 */

	public static Map<String, String> getSourceResultSetAsMap(MappingBean mappingBeanObj, CSVResultBean csvResultBean)
			throws Exception {
	
			Map<String, String> sourceResultMap = null;

			mappingBeanObj = getSourceMetaData(mappingBeanObj);
			ResultSet result = mappingBeanObj.getSourceResultSet();
			ResultSetMetaData rm = result.getMetaData();
			log.debug("Total column :> " + rm.getColumnCount());
						
			Map<String,Integer> actualHeaderMap = new LinkedHashMap<>();
			StringBuilder sb = new StringBuilder();
			for(int i =1; i<=rm.getColumnCount();i++ ){
				log.debug("DB Column Headers:>"+CSVParserUtil.csvTrim(rm.getColumnName(i)));
				//System.out.println(CSVParserUtil.csvTrim(rm.getColumnName(i)));			
				actualHeaderMap.put(CSVParserUtil.csvTrim(rm.getColumnName(i)), i);
				//System.out.println(actualHeaderMap + mappingBeanObj.getSourcePrimaryKey());				
				if(!mappingBeanObj.getSourcePrimaryKey().contains(CSVParserUtil.csvTrim(rm.getColumnName(i))))
				{
					sb.append(","+CSVParserUtil.csvTrim(rm.getColumnName(i)));
				}
			}
			mappingBeanObj.setSourceActualHeader(actualHeaderMap);
			//log.debug("+mappingBean.getSourceFields() :>"+mappingBean.getSourceFields()+"<:Length:>"+mappingBean.getSourceFields().length());
			if(StringUtils.isEmpty(mappingBeanObj.getSourceFields()) || "null".equalsIgnoreCase( mappingBeanObj.getSourceFields() ) || mappingBeanObj.getSourceFields() == null){
				log.debug("Source field non primary keys are empty hence systme will take the non primary key mappring from the the SDR qurey");
				mappingBeanObj.setSourceFields(sb.toString().replaceFirst(",", ""));
			}
			log.debug("Source column header :>"+mappingBeanObj.getSourceFields());

			String[] keyArry =mappingBeanObj.getSourcePrimaryKey().split(",");
			int[] compositePrimaryKeyArry = new int[keyArry.length];
			int i=0;
			for (String key : keyArry) {
				log.debug(key+"<:"+actualHeaderMap.get(CSVParserUtil.csvTrim(key))+":>"+actualHeaderMap.toString());
				compositePrimaryKeyArry[i++]=actualHeaderMap.get(CSVParserUtil.csvTrim(key));
			}

			keyArry = mappingBeanObj.getSourceFields().trim().split(",");
			int[] nonCompositePrimaryKeyArry = new int[keyArry.length];
			i=0;
			for (String key : keyArry) {
				log.debug(key+"<:"+actualHeaderMap.get(CSVParserUtil.csvTrim(key))+":>"+actualHeaderMap.toString());
				nonCompositePrimaryKeyArry[i++]=actualHeaderMap.get(CSVParserUtil.csvTrim(key));
			}

			String key="";
			String value="";

			sourceResultMap = new HashMap<>();
			List<String> duplicateList = new ArrayList<>();
			long rowNum = 0;
			while(result.next()){

				key="";
				for (int columnIndex : compositePrimaryKeyArry) {
					key+=","+result.getString(columnIndex);
				}

				value="";
				for (int columnIndex : nonCompositePrimaryKeyArry) {
					value+=","+result.getString(columnIndex);
				}

				key=key.replaceFirst(",","");
				value=value.replaceFirst(",","");

				rowNum++;
				if(key != null && key.length()>0 && !sourceResultMap.containsKey(key)){
					sourceResultMap.put(key, value);
				}else if (key != null && key.length()>0 ){
					duplicateList.add(key);
				}
					
				if (rowNum % 10000 == 0) {
					log.debug("So for " + rowNum + " records converted as CSV:>" + LocalDateTime.now());
					System.out.println("So for " + rowNum + " records converted as CSV:>" + LocalDateTime.now());
				}	
			}

			System.out.println("\n.......Source Table Data Fetch completed :>>> Total Row: >>>" + rowNum + "\n");
			
			csvResultBean.setProductionRecordsCount(rowNum);
			csvResultBean.setDuplicateRecordInProduction(duplicateList);
			log.debug("Unique list :> " + sourceResultMap.size() + " Duplicate List :>" + duplicateList.size());

		return sourceResultMap;
	}

	public static Map<String, String> getTargetResultSetAsMap(MappingBean mappingBeanObj, CSVResultBean csvResultBean)
			throws Exception {

			Map<String, String> targetResultMap = null;

			mappingBeanObj = getTargetMetaData(mappingBeanObj);
			ResultSet result = mappingBeanObj.getTargetResultSet();
			ResultSetMetaData rm = result.getMetaData();
			log.debug("Total column :> " + rm.getColumnCount());
			
			Map<String,Integer> actualHeaderMap = new LinkedHashMap<>();
			StringBuilder sb = new StringBuilder();
			for(int i =1; i<=rm.getColumnCount();i++ ){
				log.debug("DB Column Headers:>"+CSVParserUtil.csvTrim(rm.getColumnName(i)));	
				actualHeaderMap.put(CSVParserUtil.csvTrim(rm.getColumnName(i)), i);
				
				if(!mappingBeanObj.getTargetPrimaryKey().contains(CSVParserUtil.csvTrim(rm.getColumnName(i))))
				{
					sb.append(","+CSVParserUtil.csvTrim(rm.getColumnName(i)));
				}
			}
			mappingBeanObj.setTargetActualHeader(actualHeaderMap);
			//log.debug("+mappingBean.getSourceFields() :>"+mappingBean.getSourceFields()+"<:Length:>"+mappingBean.getSourceFields().length());
			if(StringUtils.isEmpty(mappingBeanObj.getTargetFields()) || "null".equalsIgnoreCase( mappingBeanObj.getTargetFields() ) || mappingBeanObj.getTargetFields() == null){
				log.debug("Source field non primary keys are empty hence system will take the non primary key mapping from the the SDR query");
				mappingBeanObj.setTargetFields(sb.toString().replaceFirst(",", ""));
			}
			log.debug("Target column header :>" + mappingBeanObj.getTargetFields());
			
			String[] keyArry =mappingBeanObj.getTargetPrimaryKey().split(",");
			int[] compositePrimaryKeyArry = new int[keyArry.length];
			int i=0;
			for (String key : keyArry) {
				log.debug(key+"<:"+actualHeaderMap.get(CSVParserUtil.csvTrim(key))+":>"+actualHeaderMap.toString());
				compositePrimaryKeyArry[i++]=actualHeaderMap.get(CSVParserUtil.csvTrim(key));
			}

			keyArry = mappingBeanObj.getTargetFields().split(",");
			int[] nonCompositePrimaryKeyArry = new int[keyArry.length];
			i=0;
			for (String key : keyArry) {
				//log.debug(key+"<:"+actualHeaderMap.get(CSVParserUtil.csvTrim(key))+":>"+actualHeaderMap.toString());
				nonCompositePrimaryKeyArry[i++]=actualHeaderMap.get(CSVParserUtil.csvTrim(key));
			}

			String key="";
			String value="";

			targetResultMap = new HashMap<>();
			List<String> duplicateList = new ArrayList<>();
			long rowNum = 0;
			while(result.next()){

				key="";
				for (int columnIndex : compositePrimaryKeyArry) {
					key+=","+result.getString(columnIndex);
				}

				value="";
				for (int columnIndex : nonCompositePrimaryKeyArry) {
					value+=","+result.getString(columnIndex);
				}
				
				key=key.replaceFirst(",","");
				value=value.replaceFirst(",","");

				rowNum++;
				if(key != null && key.length()>0 && !targetResultMap.containsKey(key)){
					targetResultMap.put(key, value);
				}else if (key != null && key.length()>0 ){
					duplicateList.add(key);
				}
				
				if (rowNum % 10000 == 0) {
					log.debug("So for " + rowNum + " records converted as CSV:>" + LocalDateTime.now());
					System.out.println("So for " + rowNum + " records converted as CSV:>" + LocalDateTime.now());
				}
			}
			System.out.println("\n...Target Table Data Fetch completed:>>> Total Row: >>>"+ rowNum +"\n");
			
			csvResultBean.setProducedRecordsCount(rowNum);
			csvResultBean.setDuplicateRecordInProduced(duplicateList);
			log.debug("Unique list :> " + targetResultMap.size() + " Duplicate List :>" + duplicateList.size());

		return targetResultMap;
	}
	
	
	public static MappingBean getSourceMetaData(MappingBean mappingBeanObj) throws Exception
	{
		String sourceTable = mappingBeanObj.getSourceTableName();
		String busDT = mappingBeanObj.getSourceBusDate();
		String runID = mappingBeanObj.getSourceRunID();
		String flag = mappingBeanObj.getSourceBRFlag();
		Connection sourceConn = mappingBeanObj.getSourceDBConnection();
		ResultSet result = null;
		try {
			String sourceSQL = null;
			if (flag.contentEquals("BUS_DTRUN_ID")) {
				sourceSQL = sqlQueryProp.getProperty("BUSDATE_RUNID_QUERY").replace(":TABLE_NAME", sourceTable).replace(":BUS_DT", busDT).replace(":RUN_ID", runID);
			} else if (flag.contentEquals("BUS_DT")) {
				sourceSQL = sqlQueryProp.getProperty("BUSDATE_QUERY").replace(":TABLE_NAME", sourceTable).replace(":BUS_DT", busDT);
			} else if (flag.contentEquals("RUN_ID")) {
				sourceSQL = sqlQueryProp.getProperty("RUNID_QUERY").replace(":TABLE_NAME", sourceTable).replace(":RUN_ID", runID);
			} else if (flag.contentEquals("QUERY")) {
				sourceSQL = sourceTable.replace(":BUS_DT", "'"+busDT+"'").replace(":RUN_ID", runID).replace(":bus_dt", "'"+busDT+"'").replace(":run_id", runID);
			}else if (flag.contentEquals("DB_QUERY") && busDT!=null && runID!=null) {
				sourceSQL = sourceTable.replace(":BUS_DT", "'"+busDT+"'").replace(":RUN_ID", runID).replace(":bus_dt", "'"+busDT+"'").replace(":run_id", runID);
			}else if (flag.contentEquals("DB_QUERY") && busDT!=null) {
				sourceSQL = sourceTable.replace(":BUS_DT", "'"+busDT+"'").replace(":bus_dt", "'"+busDT+"'");
			}else if (flag.contentEquals("DB_QUERY") && busDT.equals(null) && runID.equals(null)) {
				sourceSQL = sourceTable;
			}else {
				sourceSQL = sqlQueryProp.getProperty("SELECT_QUERY").replace(":TABLE_NAME", sourceTable);
			}

			System.out.println("\n Source Table Name/Query: "+ sourceSQL );
			mappingBeanObj.setSourceSQLQuery(sourceSQL);

			//PreparedStatement preparedStmt = sourceConn.prepareStatement(sourceSQL, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			//preparedStmt.setFetchSize(100);
			
			Statement sourceStmt = sourceConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			sourceStmt.setFetchSize(1000);
			
			result = sourceStmt.executeQuery(sourceSQL);
			mappingBeanObj.setSourceResultSet(result);

		}catch (Exception e) {
			log.error("Exception", e);
			mappingBeanObj.setOperationMsg("Failed");
			throw new Exception("Unable to get Database connection hence can't proceed further\n" + e.getMessage());
		}
			return mappingBeanObj;
	}
	
	public static MappingBean getTargetMetaData(MappingBean mappingBeanObj) throws Exception
	{
		String targetTable = mappingBeanObj.getTargetTableName();
		String busDT = mappingBeanObj.getTargetBusDate();
		String runID = mappingBeanObj.getTargetRunID();
		String flag = mappingBeanObj.getTargetBRFlag();
		Connection targetConn = mappingBeanObj.getTargetDBConnection();
		ResultSet result = null;
		try {
			String targetSQL = null;
				if (flag.contentEquals("BUS_DTRUN_ID")) {
					targetSQL = sqlQueryProp.getProperty("BUSDATE_RUNID_QUERY").replace(":TABLE_NAME", targetTable).replace(":BUS_DT", busDT).replace(":RUN_ID", runID);
				} else if (flag.contentEquals("BUS_DT")) {
					targetSQL = sqlQueryProp.getProperty("BUSDATE_QUERY").replace(":TABLE_NAME", targetTable).replace(":BUS_DT", busDT);
				} else if (flag.contentEquals("RUN_ID")) {
					targetSQL = sqlQueryProp.getProperty("RUNID_QUERY").replace(":TABLE_NAME", targetTable).replace(":RUN_ID", runID);
				}else if (flag.contentEquals("DB_QUERY")) {
					targetSQL = targetTable.replace(":BUS_DT", "'"+busDT+"'").replace(":RUN_ID", runID).replace(":bus_dt", "'"+busDT+"'").replace(":run_id", runID);
				} else {
					targetSQL = sqlQueryProp.getProperty("SELECT_QUERY").replace(":TABLE_NAME", targetTable);
				}

				mappingBeanObj.setTargetSQLQuery(targetSQL);

			//PreparedStatement preparedStmt = targetConn.prepareStatement(targetSQL, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			//preparedStmt.setFetchSize(100);
			
			Statement targetStmt = targetConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			targetStmt.setFetchSize(1000);
			result = targetStmt.executeQuery(targetSQL);
			mappingBeanObj.setTargetResultSet(result);
		}
		catch (Exception e) {
			log.error("Exception", e);
			throw new Exception("Unable to get Database connection hence cann't proceed further");
		}
		return mappingBeanObj;
	}
	
}
