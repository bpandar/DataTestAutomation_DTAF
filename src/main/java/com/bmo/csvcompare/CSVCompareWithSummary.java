/**
 *  Author      	: Bhoopathy P
 *  Date 			: Apr 17, 2021
 *  Class Name		: CSVCompareWithSummary.java
 *  
 *  Project			: BMO - Risk ODS
 *
 *	Changes			: Compare 2 CSV files (CSV to CSV)	
 *  
 *  Modification history 
 *  Date			ChangeNo	Modified By							Description
 *  
 *  
 */
package com.bmo.csvcompare;

import com.bmo.mappingbean.CSVResultBean;
import com.bmo.mappingbean.MappingBean;
import com.bmo.tablecompare.FetchTableData;
import com.bmo.utils.CSVParserUtil;
import com.bmo.utils.PropertyUtil;
import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class CSVCompareWithSummary {

	private static final Logger log = LogManager.getLogger(CSVCompareWithSummary.class);
	//private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("csvmappings", Locale.US);
	private static Properties properties = PropertyUtil.getPropertyFile("ResultSummaryMappings");
	private static Properties configProp = PropertyUtil.getPropertyFile("config");
	
	public static void main(String[] args) {
		properties.getProperty("CSV_SOURCE_NAME");
		properties.getProperty("CSV_PRODUCTION_RECORD");
		properties.getProperty("CSV_PRODUCED_RECORD");
	}

	private static CSVCompareWithSummary csvCompareObj = null;

	public static CSVCompareWithSummary getInstance(){ //public static synchronized CSVCompare getInstance() //Thread safe singleton class
		if(csvCompareObj == null){
			csvCompareObj = new CSVCompareWithSummary();
			log.debug("CSVCompare new object initialized...");
		}
		return csvCompareObj;
	}
	 

	/**
	 * Returns boolean value to identify whether the CSV comparison job executed properly without any issue or not.
	 *
	 * @param mappingBeanObj will have CSV compare details like, source/DB & target file along with composite & not primary fields to be compared.
	 *
	 * @return a {@executionFlag boolean} for the corresponding CSV compare report.
	 */	
	public static MappingBean compareCSVFilesAsMap(MappingBean mappingBeanObj) throws Exception{

		CSVResultBean csvResultBean = new CSVResultBean();
		try {
			csvResultBean.setStartTime(LocalDateTime.now());
			log.debug("Mapping key validations :>"+LocalDateTime.now());
			System.out.println("\n Mapping key validations :>"+LocalDateTime.now());

			List<String> sourceCompositeKeyList = new ArrayList<>(Arrays.asList(mappingBeanObj.getSourcePrimaryKey().split(",")));
			List<String> targetCompositeKeyList = new ArrayList<>(Arrays.asList(mappingBeanObj.getTargetPrimaryKey().split(",")));

			List<String> sourceNonPrimaryKeyList = null;
			List<String> targetNonPrimaryKeyList = null; 
				if(mappingBeanObj.getPhase().equalsIgnoreCase("DB TO DB")) {
					sourceNonPrimaryKeyList =  new ArrayList<>(Arrays.asList(mappingBeanObj.getSourceColumns().split(",")));
					targetNonPrimaryKeyList =  new ArrayList<>(Arrays.asList(mappingBeanObj.getTargetColumns().split(",")));
				}else if(mappingBeanObj.getPhase().equalsIgnoreCase("CSV TO CSV")) 
				{
					sourceNonPrimaryKeyList =  new ArrayList<>(Arrays.asList(mappingBeanObj.getSourceFields().trim().split(",")));
					Set<String> SrcSkipColumns = new HashSet<String>(mappingBeanObj.getSourceSkipColumn());
					for(String skipField :SrcSkipColumns )
					{
						if(sourceNonPrimaryKeyList.contains(skipField)) {
							sourceNonPrimaryKeyList.remove(skipField);
						}
					}				
					targetNonPrimaryKeyList =  new ArrayList<>(Arrays.asList(mappingBeanObj.getTargetFields().trim().split(",")));
					Set<String> tarSkipColumns = new HashSet<String>(mappingBeanObj.getTargetSkipColumn());
					for(String skipField :tarSkipColumns )
					{
						if(targetNonPrimaryKeyList.contains(skipField)) {
							targetNonPrimaryKeyList.remove(skipField);
						}
					}
					
				}else {
					sourceNonPrimaryKeyList =  new ArrayList<>(Arrays.asList(mappingBeanObj.getSourceFields().trim().split(",")));
					targetNonPrimaryKeyList =  new ArrayList<>(Arrays.asList(mappingBeanObj.getTargetFields().trim().split(",")));
				}
			log.debug("Source Final keyes:>"+sourceNonPrimaryKeyList.toString() +" \n"+sourceCompositeKeyList.toString());
			log.debug("Target Final keyes:>"+targetNonPrimaryKeyList.toString() +" \n"+targetCompositeKeyList.toString());

			//System.out.println("Target Final keyes:>"+targetNonPrimaryKeyList.toString() +" \n"+targetCompositeKeyList.toString());
			//A-B details
			//Collection<String> aMinusBResult = CollectionUtils.subtract(sourceNonPrimaryKeyList, sourceCompositeKeyList);
			//System.out.println("A-B=>"+aMinusBResult.size() + " Data ==> "+aMinusBResult.toString());
			//System.out.println(StringUtils.join(aMinusBResult, ','));

			mappingBeanObj.setSourceFields(StringUtils.join(CollectionUtils.subtract(sourceNonPrimaryKeyList, sourceCompositeKeyList),','));
			//mappingBeanObj.setTargetFields(StringUtils.join(CollectionUtils.subtract(targetNonPrimaryKeyList, targetCompositeKeyList),','));
			mappingBeanObj.setTargetFields(CollectionUtils.subtract(targetNonPrimaryKeyList, targetCompositeKeyList).stream().map(Object::toString).collect(Collectors.joining(",")));
			//System.out.println("Target  No.of primary keys :>"+mappingBeanObj.getTargetFields());
			log.debug("\n Source Fields:>"+mappingBeanObj.getSourceFields()+"\n Target Fields:>"+mappingBeanObj.getTargetFields()+"\n CSV Parser activity started:>"+LocalDateTime.now());

			System.out.println("\n Source Fields:>"+mappingBeanObj.getSourceFields() +"\n Target Fields:>"+mappingBeanObj.getTargetFields());
			Map<String,String> allSourceRows = null;
			Map<String,String> allTargetRows = null;
			
			//log.debug("Reading CSV data from Data base ..."+LocalDateTime.now());
			//allSourceRows = FetchTableData.getSourceResultSetAsMap(mappingBeanObj,csvResultBean);		
			//allTargetRows = FetchTableData.getTargetResultSetAsMap(mappingBeanObj,csvResultBean);
			//validateCompositeKeyMappings(mappingBeanObj);			
			
			if(mappingBeanObj.getPhase().equalsIgnoreCase("DB TO CSV")){
				log.debug("Reading Table data from DataBase ..."+LocalDateTime.now());
				allSourceRows = FetchTableData.getSourceResultSetAsMap(mappingBeanObj,csvResultBean);
				
				System.out.println("Reading Table data from DataBase has been completed..."+LocalDateTime.now()+"\n\n");
				log.debug("Reading Table data from DataBase has been completed..."+LocalDateTime.now()+"\n\n");
				
				validateCompositeKeyMappings(mappingBeanObj);
				
				System.out.println("--------------------source completed ---------------"+LocalDateTime.now());
				allTargetRows = getCSVMap(false,mappingBeanObj,csvResultBean);
				
			}else if (mappingBeanObj.getPhase().equalsIgnoreCase("DB TO DB")) {
				log.debug("--------------Table to Table compare ---------------------"+LocalDateTime.now());
				allSourceRows = FetchTableData.getSourceResultSetAsMap(mappingBeanObj,csvResultBean);		
				
				validateCompositeKeyMappings(mappingBeanObj);
				
				System.out.println("--------------------source table completed ---------------"+LocalDateTime.now());
				allTargetRows = FetchTableData.getTargetResultSetAsMap(mappingBeanObj,csvResultBean);
				
			} else{
				log.debug("CSV to CSV compare ---------------------"+LocalDateTime.now());
				allSourceRows = getCSVMap(true,mappingBeanObj,csvResultBean);
				
				System.out.println("--------------------source CSV completed ---------------"+LocalDateTime.now());
				allTargetRows = getCSVMap(false,mappingBeanObj,csvResultBean);
			}		
			
			/** Compare using flat file and a List **/
			List<String> resultList = new ArrayList<>();
			long misMatchedCount = 0;

			log.debug("allSourceRows :>"+allSourceRows.size()+" allTargetRows:>"+allTargetRows.size());
			System.out.println("\n allSourceRows :>"+allSourceRows.size()+"\n allTargetRows :>"+allTargetRows.size());
			System.out.println("\n <:CSV Comparison started:::>"+LocalDateTime.now());
			log.debug("<:CSV Comparison started:::>"+LocalDateTime.now());

			//csvResultBean.setMissedProductionRecordDetails((List<String>)CollectionUtils.subtract(allSourceRows.keySet(), allTargetRows.keySet()));
			//csvResultBean.setMissedProducedRecordDetails((List<String>)CollectionUtils.subtract(allTargetRows.keySet(), allSourceRows.keySet()));

			log.debug("Missed Records details :>"+csvResultBean.getMissedProductionRecordDetails()+" \n "+csvResultBean.getMissedProducedRecordDetails());

			StringBuilder sb = new StringBuilder();
			sb.append(properties.getProperty("CSV_SOURCE_NAME")+","+mappingBeanObj.getSourcePrimaryKey()+","+mappingBeanObj.getSourceFields());
			String line = sb.toString();

			resultList.add(line);
			long exactMatchCount=0;
			long rowValue =0;
			List<String> dummyList = new ArrayList<>();

			for(String sourceCompositeData : allSourceRows.keySet()) {
				rowValue++;
				
				
				
				if(allTargetRows.containsKey(sourceCompositeData)){
					exactMatchCount++;
					if(!allSourceRows.get(sourceCompositeData).equalsIgnoreCase(allTargetRows.get(sourceCompositeData))){
						misMatchedCount++;
						log.debug(sourceCompositeData+"\t"+allTargetRows.containsKey(sourceCompositeData));
						log.debug("Souce Data:>"+allSourceRows.get(sourceCompositeData));
						log.debug("Destination Data :>"+allTargetRows.get(sourceCompositeData));

						int sourceCnt = (properties.getProperty("CSV_PRODUCTION_RECORD")+","+sourceCompositeData).split(",").length;
						//Here, individual field value to be compared for preparing mismatch report

						String[] ss = null;//(resourceBundle.getProperty("CSV_PRODUCTION_RECORD")+","+sourceCompositeData+","+allSourceRows.get(sourceCompositeData)).split(",");
						String[] tt = null;// (resourceBundle.getProperty("CSV_PRODUCED_RECORD")+","+sourceCompositeData+","+allTargetRows.get(sourceCompositeData)).split(",");

						//The below logic has been implemented to address the data(line) end with ,,, issue 
						if(allSourceRows.get(sourceCompositeData).endsWith(",")){
							ss= (properties.getProperty("CSV_PRODUCTION_RECORD")+","+sourceCompositeData+","+allSourceRows.get(sourceCompositeData)+"DUMMY_DATA").split(",");
							ss[ss.length-1] = "";
						}else{
							ss = (properties.getProperty("CSV_PRODUCTION_RECORD")+","+sourceCompositeData+","+allSourceRows.get(sourceCompositeData)).split(",");
						}
						if(allTargetRows.get(sourceCompositeData).endsWith(",")){
							tt=(properties.getProperty("CSV_PRODUCED_RECORD")+","+sourceCompositeData+","+allTargetRows.get(sourceCompositeData)+"DUMMY_DATA").split(",");
							tt[tt.length-1] = "";
						}else{
							tt = (properties.getProperty("CSV_PRODUCED_RECORD")+","+sourceCompositeData+","+allTargetRows.get(sourceCompositeData)).split(",");
						}
						//String[] tempSrc = Arrays.stream(ss).toArray(String[]::new);
						//String[] tempDest = Arrays.stream(tt).toArray(String[]::new);
						String[] tempSrc = SerializationUtils.clone(ss);
						String[] tempDest = SerializationUtils.clone(tt);
						//log.debug(ss.length+"<:SS :>"+convertStringArrayToString(ss,","));
						//log.debug(tt.length+"<:TT:>"+convertStringArrayToString(tt,",") +" ::>>\n"+allTargetRows.get(sourceCompositeData) +" ::>"+allTargetRows.get(sourceCompositeData).split(",").length);
						//log.debug("ArrayCompare start position :>"+sourceCnt);
						
						for (int j =sourceCnt ; j<ss.length; j++) {
							//log.debug("J -->"+j);
							if(StringUtils.isNotEmpty(ss[j]) && StringUtils.isNotEmpty(tt[j]) && !ss[j].equalsIgnoreCase(tt[j])){
								tempDest[j] = tt[j];
								tempSrc[j] = ss[j];
							}else if(StringUtils.isNotEmpty(ss[j]) && StringUtils.isEmpty(tt[j])){
								tempDest[j] = "";
								tempSrc[j] = ss[j];
							}else if(StringUtils.isEmpty(ss[j]) && StringUtils.isNotEmpty(tt[j])){
								tempDest[j] = tt[j];
								tempSrc[j] = "" ;
							}else{
								tempDest[j] = "" ;
								tempSrc[j] = "" ;
							}
						}

						resultList.add(convertStringArrayToString(tempSrc, ","));
						resultList.add(convertStringArrayToString(tempDest, ","));
					}
					allTargetRows.remove(sourceCompositeData);
				}else{
					dummyList.add(sourceCompositeData);
				}
				if(rowValue%10000==0){
					log.debug(rowValue+" Records has been processed...:>"+LocalDateTime.now()+" & Data ["+sourceCompositeData+"]:>"+allTargetRows.containsKey(sourceCompositeData));
					log.debug("Souce Data:>"+allSourceRows.get(sourceCompositeData)+" Destination Data :>"+allTargetRows.get(sourceCompositeData));
				}
				
			}
			
			csvResultBean.setMisMatchRecordsCount(misMatchedCount);
			csvResultBean.setMatchRecordsCount(exactMatchCount);

			log.debug("duplicateList:>"+resultList.size());
			log.debug("Task Completed :>"+LocalDateTime.now());
			if(resultList.size()>0){
				log.debug("Writing remaing records :>"+LocalDateTime.now());
				/*for (String[] strings : resultList) {
					System.out.println(convertStringArrayToString(strings,","));
				}*/
				writeCSVData(resultList,mappingBeanObj,csvResultBean);
				//writeCSVDataOld(resultList,mappingBeanObj,csvResultBean);
			}

			csvResultBean.setMissedProductionRecordDetails(dummyList);
			csvResultBean.setMissedProducedRecordDetails(new ArrayList(allTargetRows.keySet()));

			csvResultBean.setEndTime(LocalDateTime.now());
			csvResultBean.setTimeDiff(timeTaken(csvResultBean.getStartTime(), csvResultBean.getEndTime(), csvResultBean));

			System.out.println("<:CSV Comparison has been completed:::>"+LocalDateTime.now());//Comparison

			writeSummaryReport(csvResultBean,mappingBeanObj);

			System.out.println("\n\n****** <:CSV Comparison & Report has been completed:::> *******"+LocalDateTime.now()+"\n");

			//System.out.println( "\n After Writing Summary: "+mappingBeanObj.getSummaryResultList());
			mappingBeanObj.setOperationMsg("Done");
					
		} catch (Exception e) {
			log.debug(properties.getProperty("UNABLE_TO_PARSE_ISSUE"));
			log.error("Exception",e);
			throw new Exception(properties.getProperty("UNABLE_TO_PARSE_ISSUE"));
		} /*
			 * finally{ csvResultBean=null; mappingBeanObj=null; }
			 */
		return mappingBeanObj;
	}

	private static void writeCSVData(List<String> resultList, MappingBean mappingBeanObj,CSVResultBean csvResultBean) throws Exception {
		try{
			log.debug("<: Writing mismatched records in CSV file:::>"+LocalDateTime.now());
						
			System.out.println("<:CSV Comparison has been completed:::>"+LocalDateTime.now() +"\n");
			//String reportFilePath = new File(mappingBeanObj.getTarget()).getPath();
			//System.out.println(System.getProperty("user.dir"));
			String reportFilePath = configProp.getProperty("DATA_DIR");
			
			try{
				String indexStr = "/";
				if(reportFilePath.contains("\\")){
					indexStr="\\";
				}
				
				reportFilePath=reportFilePath.substring(0,reportFilePath.lastIndexOf(indexStr ))+indexStr+"CSV_REPORTS"+indexStr;
				
				/*
				 * if(!mappingBeanObj.getPhase().equalsIgnoreCase("DB TO DB")) { reportFilePath
				 * = reportFilePath.substring(0,reportFilePath.lastIndexOf(indexStr));
				 * reportFilePath=reportFilePath.substring(0,reportFilePath.lastIndexOf(indexStr
				 * ))+indexStr+"CSV_REPORTS"+indexStr; }else {
				 * System.out.println(reportFilePath.substring(0,reportFilePath.lastIndexOf(
				 * indexStr)));
				 * reportFilePath=reportFilePath.substring(0,reportFilePath.lastIndexOf(indexStr
				 * ))+indexStr+"CSV_REPORTS"+indexStr; }
				 */
			}catch(Exception e){
				log.error("Exception while writting CSV report",e);
			}
			System.out.println("Report file path :>"+reportFilePath);
			if (!Files.exists(Paths.get(reportFilePath))) {
				try {
					Files.createDirectories(Paths.get(reportFilePath));
				} catch (IOException e) {
					//fail to create directory
					log.debug("Unable to create folder/file to write the CSV data");
					log.error("Unable to create folder/file to write the CSV data",e);
					throw e;
				}
			}

			String resultFileStr = null;
			if(StringUtils.isNotEmpty(csvResultBean.getCompareResultFile())){
				resultFileStr = csvResultBean.getCompareResultFile();
			}else{
				if(mappingBeanObj.getPhase().equalsIgnoreCase("DB TO DB")&& !mappingBeanObj.getSourceBRFlag().equalsIgnoreCase("DB_QUERY") ) {
					resultFileStr = reportFilePath+mappingBeanObj.getPhase()+"_"+mappingBeanObj.getSourceTableName()+"_reports_"+DateTimeFormatter.ofPattern("ddMMyyyyHHmmss").format(LocalDateTime.now())+".csv";
				}else {
					resultFileStr = reportFilePath+mappingBeanObj.getPhase()+"_"+mappingBeanObj.getSourceSystem()+"_reports_"+DateTimeFormatter.ofPattern("ddMMyyyyHHmmss").format(LocalDateTime.now())+".csv";
				}
				csvResultBean.setCompareResultFile(resultFileStr);
			}

			Files.write(Paths.get(resultFileStr), resultList, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (IOException e) {
			log.error("IOException ",e);
			throw new RuntimeException(e);
		}catch(Exception e){
			log.error("Exception while writing data in CSV file",e);
			throw e;
		}finally{
			
		}
	}

	private static String timeTaken(LocalDateTime dateTime,LocalDateTime dateTime2,CSVResultBean csvResultBean){

		long hours = dateTime.until(dateTime2, ChronoUnit.HOURS);
		dateTime = dateTime.plusHours(hours);

		long minutes = dateTime.until(dateTime2, ChronoUnit.MINUTES);
		dateTime = dateTime.plusMinutes(minutes);

		long seconds = dateTime.until(dateTime2, ChronoUnit.SECONDS);
		dateTime = dateTime.plusSeconds(seconds);

		long millis = dateTime.until(dateTime2, ChronoUnit.MILLIS);
		csvResultBean.setTimeDiff("The difference(time taken) is :>"+hours+" hours, "+minutes+" minutes, "+seconds+" seconds, "+millis+" millis");
		return "The difference(time taken) is :>"+hours+" hour(s), "+minutes+" minute(s), "+seconds+" second(s), "+millis+" millis";
	}


	private static boolean validateCompositeKeyMappings(MappingBean mappingBeanObj) throws Exception {
		try{

			if(mappingBeanObj.getSourcePrimaryKey() != null && mappingBeanObj.getTargetPrimaryKey() != null){
				String[] sourceCompositeKeyArry = mappingBeanObj.getSourcePrimaryKey().split(",");
				String[] destinationCompositeKeyArry = mappingBeanObj.getTargetPrimaryKey().split(",");

				if(sourceCompositeKeyArry.length != destinationCompositeKeyArry.length){
					log.debug(properties.getProperty("COMPOSITE_KEY_MAPPING_ISSUE"));
					throw new Exception(properties.getProperty("COMPOSITE_KEY_MAPPING_ISSUE"));
				}
			}

			//System.out.println(mappingBeanObj.getSourceFields());
			if(mappingBeanObj.getSourceFields() != null && mappingBeanObj.getTargetFields() != null){
				String[] sourceKeyArry = mappingBeanObj.getSourceFields().split(",");
				String[] destinationKeyArry = mappingBeanObj.getTargetFields().split(",");

				if(sourceKeyArry.length != destinationKeyArry.length){
					log.debug(properties.getProperty("NON_PRIMARY_KEY_MAPPING_ISSUE") +" Soruce key count  :"+sourceKeyArry.length+" Target Key count:"+destinationKeyArry.length);
					throw new Exception(properties.getProperty("NON_PRIMARY_KEY_MAPPING_ISSUE"));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			log.error("Exception while validateCompositeKeyMappings",e);
			throw e;
		}
		return true;
	}

	private static Map<String,String> getCSVMap(boolean isSourceFile, MappingBean mappingBeanObj,CSVResultBean csvResultBean) throws Exception{

		Map<String,String> allRows =new LinkedHashMap<>();
		String line=null;
		try{
			String csvFileName;

			if(isSourceFile){
				csvFileName=mappingBeanObj.getSource();			
				log.debug("getSourceFields:>"+mappingBeanObj.getSourceFields());
				
			}else{
				csvFileName=mappingBeanObj.getTarget();
				log.debug("getTargetFields:>"+mappingBeanObj.getTargetFields());
			}

			System.out.println("\n CSV parser Settings...");
			CsvParserSettings settings = new CsvParserSettings();
			settings.getFormat().setLineSeparator("\n");
			settings.getFormat().getQuoteEscape();
			
			//**this method accept all kind of delimiter **/
			settings.setMaxCharsPerColumn(50000);
			//settings.setDelimiterDetectionEnabled(true, '|');
			settings.setDelimiterDetectionEnabled(true, ',', '|', ';', ':');
			//settings.setDelimiterDetectionEnabled(false, '/');
			// creates a CSV parser

			CsvParser parser = new CsvParser(settings);

			log.debug("CSV Parsing Started ("+csvFileName+"):>"+LocalDateTime.now());
			List<String[]> allRowsList = parser.parseAll(new FileReader(csvFileName));
			//allRows = parser.parseAll(new FileReader(csvFileName));

			//log.debug("No of Records :>"+allRows.size());
			//log.debug("CSV Parsing Completed ("+csvFileName+"):>"+LocalDateTime.now());	
			// parses all rows in one go.
			Map<String,Integer> headerMap = new LinkedHashMap<>();
			List<String> duplicateList = new ArrayList<>();

			int[] compositeIndexes;
			compositeIndexes = new int[mappingBeanObj.getTargetPrimaryKey().split(",").length];

			int[] nonPrimaryCompositeIndexes;

			nonPrimaryCompositeIndexes=new int[mappingBeanObj.getTargetFields().split(",").length];

			System.out.println("isSourceFile ("+isSourceFile+"):>"+csvFileName);
			IterableResult<Record,ParsingContext>  iterableResult = parser.iterateRecords(new FileReader(csvFileName));
			Iterator<Record> itr = iterableResult.iterator();
			
			boolean headerCheck = true;
			long rowNum = 0;
			long headTrailFlag = 0;
			
			while (itr.hasNext()) {
				Record rec = itr.next();
				if(headerCheck)
				{
					String[] rowValue = rec.getValues();
					List<String> lineList = new LinkedList<String>(Arrays.asList(rowValue));
					lineList.replaceAll(t -> Objects.isNull(t) ? "" : t);
					lineList.replaceAll(String::toUpperCase);
					
					List<String> columnList = new ArrayList<String>();
					String headerColumn[];
					
					if(isSourceFile) {
						headerColumn = mappingBeanObj.getSourcePrimaryKey().toString().split(",");
					}else {
						headerColumn = mappingBeanObj.getTargetPrimaryKey().toString().split(",");
					}
					
					for (String headAttr : headerColumn) {
						columnList.add(headAttr.trim());
					}
					Collection<String> intersection = CollectionUtils.intersection(lineList, columnList);
					if (intersection.size() == 0) {
						rec = itr.next();
						headTrailFlag++;
					}
					
					String[] wholeValue = rec.getValues();
					log.debug("-------------------whole header size --------------------"+wholeValue.length);
					int headerPosition=0;
					for (String headerName : wholeValue) {
						log.debug("headerName:>"+headerName);
						headerName=CSVParserUtil.csvTrim(headerName);
						headerMap.put(headerName.toUpperCase(), Integer.valueOf(headerPosition++));
					}
					if(isSourceFile){
						mappingBeanObj.setSourceActualHeader(headerMap);
						log.debug("--------------------source-------------------"+mappingBeanObj.getSourceActualHeader().toString());
						int c=0;
						for (String header : mappingBeanObj.getSourcePrimaryKey().split(",")) {
							header=CSVParserUtil.csvTrim(header);
							compositeIndexes[c++]= mappingBeanObj.getSourceActualHeader().get(header);
						}
						c=0;
						log.debug("mappingBeanObj.getSourceFields(): "+mappingBeanObj.getSourceFields());
						for (String header : mappingBeanObj.getSourceFields().split(",")) {
							header=CSVParserUtil.csvTrim(header);
							if( mappingBeanObj.getSourceActualHeader().get(header) != null){
								log.debug("Source Column :>"+header+"<:Location:>"+c);
								nonPrimaryCompositeIndexes[c++]= mappingBeanObj.getSourceActualHeader().get(header);
							}else{
								log.debug("Not found ------------------------>"+header);
								throw new Exception(header+" Not found in source field list, please check and upadte");
							}
						}
					}else{
						mappingBeanObj.setTargetActualHeader(headerMap);
						log.debug("--------------------target-------------------"+mappingBeanObj.getTargetActualHeader().toString());
						int c=0;
						for (String header : mappingBeanObj.getTargetPrimaryKey().split(",")) {
							header=CSVParserUtil.csvTrim(header);
							compositeIndexes[c++]= mappingBeanObj.getTargetActualHeader().get(header);
						}
						c=0;
						log.debug(mappingBeanObj.getTargetFields().split(",").length+"<:mappingBeanObj.getTargetFields(): "+mappingBeanObj.getTargetFields());
						for (String header : mappingBeanObj.getTargetFields().split(",")) {
							header=CSVParserUtil.csvTrim(header);
							if( mappingBeanObj.getTargetActualHeader().get(header) != null){
								nonPrimaryCompositeIndexes[c++]= mappingBeanObj.getTargetActualHeader().get(header);
							}else{
								log.debug("Not found ------------------------>"+header);
								throw new Exception(header+" Not found in Target field list, please check and upadte");
							}
						}
					}
					headerCheck=false;
				} else {
					rowNum++;

					if (headTrailFlag == 1 && rowNum != allRowsList.size() - 2) {
						line = convertStringArrayToString(rec.getValues(compositeIndexes), ",");
						if (line != null && line.length() > 0 && !allRows.containsKey(line)) {
							allRows.put(line,
									convertStringArrayToString(rec.getValues(nonPrimaryCompositeIndexes), ","));
						} else if (line != null && line.length() > 0) {
							duplicateList.add(line);
						}
					} else if (headTrailFlag == 0) {
						line = convertStringArrayToString(rec.getValues(compositeIndexes), ",");
						if (line != null && line.length() > 0 && !allRows.containsKey(line)) {
							allRows.put(line,
									convertStringArrayToString(rec.getValues(nonPrimaryCompositeIndexes), ","));
						} else if (line != null && line.length() > 0) {
							duplicateList.add(line);
						}
					}
				}
			}

			if(isSourceFile){
				csvResultBean.setProductionRecordsCount(rowNum);
				if(headTrailFlag==1) {
					csvResultBean.setProductionRecordsCount(rowNum-1);
				}
				csvResultBean.setDuplicateRecordInProduction(duplicateList);
			}else{
				csvResultBean.setProducedRecordsCount(rowNum);
				if(headTrailFlag==1) {
					csvResultBean.setProducedRecordsCount(rowNum-1);
				}
				csvResultBean.setDuplicateRecordInProduced(duplicateList);
			}
			log.debug("Unique:>"+allRows.size()+" Duplicate : "+duplicateList.size());
		} catch (Exception e) {
			if(isSourceFile){
				System.out.println("\t*** Kindly check  No.of data in source csv file:>"+line);
				log.debug("\t*** Kindly check  No.of data in source csv file:>"+line);
			}else{
				System.out.println("\t*** Kindly check  No.of data in Target csv file:>"+line);
				log.debug("\t*** Kindly check  No.of data in Target csv file:>"+line);
			}
			System.out.println("Exception while converting CSV data to List..\n"+ e.getMessage());
			log.error("Exception while converting CSV data to List",e);
			throw e;
		}
		return allRows;
	}

	private static MappingBean writeSummaryReport(CSVResultBean csvResultBean,MappingBean mappingBeanObj){
			
		List<Integer> summaryList = new ArrayList<Integer>();

		StringBuilder buffer = new StringBuilder();
		System.out.println("Summary Report :>"+csvResultBean.getCompareResultFile());
		log.debug("Summary Report :>"+csvResultBean.getCompareResultFile());
		String resultPath = csvResultBean.getCompareResultFile().substring(0,csvResultBean.getCompareResultFile().lastIndexOf(".")+1).replace("_reports_","_summary_report_");
		//System.out.println("resultPath:>>"+resultPath);
		resultPath = resultPath.substring(0,resultPath.lastIndexOf('.')+1)+"txt";
		Path path = Paths.get(resultPath);
		System.out.println("Result File path :"+resultPath);
		log.debug("Result File path :"+resultPath);
		try (BufferedWriter writerObj = Files.newBufferedWriter(path)) {
			log.debug("Total Records available in Source Table 			:>"+csvResultBean.getProductionRecordsCount());
			log.debug("Total Records available in Target Table 			:>"+csvResultBean.getProducedRecordsCount());
			log.debug("Total Mismatched Records 	:>"+csvResultBean.getMisMatchRecordsCount());

			writerObj.write("\nTask Start time ("+DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a").format(csvResultBean.getStartTime())+") and End time  "
					+ "("+DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a").format(csvResultBean.getEndTime())+")  & "+timeTaken(csvResultBean.getStartTime(), csvResultBean.getEndTime(),csvResultBean));
			
			buffer.append("\n\nTask Start time ("+DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a").format(csvResultBean.getStartTime())+") and End time  "
					+ "("+DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a").format(csvResultBean.getEndTime())+")  \t\t\t\n& "+timeTaken(csvResultBean.getStartTime(), csvResultBean.getEndTime(),csvResultBean));

			if("DB TO DB".equalsIgnoreCase(mappingBeanObj.getPhase()) || "DB TO CSV".equalsIgnoreCase(mappingBeanObj.getPhase()))
			{
				System.out.println("=================\t"+mappingBeanObj.getSourceBRFlag());
				if(!mappingBeanObj.getSourceBRFlag().contains("DB_QUERY")) {
					writerObj.write("\n");
					writerObj.write("\nExecution Phase :>"+mappingBeanObj.getSourceTableName()+" vs "+mappingBeanObj.getTargetTableName()+" -> "+mappingBeanObj.getPhase());  
					buffer.append("\n\nExecution Phase :>"+mappingBeanObj.getSourceTableName()+" vs "+mappingBeanObj.getTargetTableName()+" -> "+mappingBeanObj.getPhase());
					
					writerObj.write("\n");
					writerObj.write("\nSource Table :>"+mappingBeanObj.getSourceTableName()+"_"+mappingBeanObj.getSourceServer()+"_"+mappingBeanObj.getSourceBusDate()+"_"+mappingBeanObj.getSourceRunID()); 
					writerObj.write("\nTarget Table :>"+mappingBeanObj.getTargetTableName()+"_"+mappingBeanObj.getTargetServer()+"_"+mappingBeanObj.getTargetBusDate()+"_"+mappingBeanObj.getTargetRunID()); 	
				
					buffer.append("\n\nSource Table :>"+mappingBeanObj.getSourceTableName()+"_"+mappingBeanObj.getSourceServer()+"_"+mappingBeanObj.getSourceBusDate()+"_"+mappingBeanObj.getSourceRunID());
					buffer.append("\nTarget Table :>"+mappingBeanObj.getTargetTableName()+"_"+mappingBeanObj.getTargetServer()+"_"+mappingBeanObj.getTargetBusDate()+"_"+mappingBeanObj.getTargetRunID());
				}else {			
					writerObj.write("\n");
					writerObj.write("\nExecution Phase :>"+mappingBeanObj.getSourceSystem()+" -> "+mappingBeanObj.getPhase());  
					buffer.append("\nExecution Phase :>"+mappingBeanObj.getSourceSystem()+" -> "+mappingBeanObj.getPhase());
			
					writerObj.write("\n\n");
					writerObj.write("\nSource File :>"+mappingBeanObj.getSourceSystem()+"_"+mappingBeanObj.getSourceServer()+"_"+mappingBeanObj.getSourceBusDate()+"_"+mappingBeanObj.getSourceRunID()); 
					writerObj.write("\nTarget File :>"+mappingBeanObj.getSourceSystem()+"_"+mappingBeanObj.getTargetServer()+"_"+mappingBeanObj.getTargetBusDate()+"_"+mappingBeanObj.getTargetRunID());  
					
					buffer.append("\nSource File :>"+mappingBeanObj.getSourceSystem()+"_"+mappingBeanObj.getSourceServer()+"_"+mappingBeanObj.getSourceBusDate()+"_"+mappingBeanObj.getSourceRunID());
					buffer.append("\nTarget File :>"+mappingBeanObj.getSourceSystem()+"_"+mappingBeanObj.getTargetServer()+"_"+mappingBeanObj.getTargetBusDate()+"_"+mappingBeanObj.getTargetRunID()); 
				}
				}else {
					writerObj.write("\n");
					writerObj.write("\nExecution Phase :>"+mappingBeanObj.getSourceSystem()+" -> "+mappingBeanObj.getPhase());  
					buffer.append("\nExecution Phase :>"+mappingBeanObj.getSourceSystem()+" -> "+mappingBeanObj.getPhase());
			
					writerObj.write("\n\n");
					writerObj.write("\nSource File :>"+mappingBeanObj.getSource()); 
					writerObj.write("\nTarget File :>"+mappingBeanObj.getTarget());  
					
					buffer.append("\nSource File :>"+mappingBeanObj.getSource()); 
					buffer.append("\nTarget File :>"+mappingBeanObj.getTarget());  					
			}

			if(csvResultBean.getCompareResultFile()!= null){
				writerObj.write("\n");
				writerObj.write("\nCompare result file path :>"+csvResultBean.getCompareResultFile()+"\n"); 
			}

			writerObj.write("\n");
			writerObj.write("\n"+properties.getProperty("NO_OF_RECORDS_IN_PRODUCTION_FILE")+"  :>"+csvResultBean.getProductionRecordsCount());
			writerObj.write("\n"+properties.getProperty("NO_OF_RECORDS_IN_PRODUCED_FILE")+"  :>"+csvResultBean.getProducedRecordsCount());
			
			buffer.append("\n");
			buffer.append("\n"+properties.getProperty("NO_OF_RECORDS_IN_PRODUCTION_FILE")+"  :>"+csvResultBean.getProductionRecordsCount());
			buffer.append("\n"+properties.getProperty("NO_OF_RECORDS_IN_PRODUCED_FILE")+"  :>"+csvResultBean.getProducedRecordsCount());
			
			writerObj.write("\n");
			writerObj.write("\n"+properties.getProperty("NO_OF_MATCHED_RECORDS")+" :>"+csvResultBean.getMatchRecordsCount()); 
			
			buffer.append("\n\n"+properties.getProperty("NO_OF_MATCHED_RECORDS")+" :>"+csvResultBean.getMatchRecordsCount()); 
			
			writerObj.write("\n");
			writerObj.write("\n"+properties.getProperty("NO_OF_MIS_MATCHED_RECORDS")+" :>"+csvResultBean.getMisMatchRecordsCount());

			buffer.append("\n");
			buffer.append("\n"+properties.getProperty("NO_OF_MIS_MATCHED_RECORDS")+" :>"+csvResultBean.getMisMatchRecordsCount());
					
			writerObj.write("\n");
			writerObj.write("\n"+properties.getProperty("NO_OF_MISSED_RECORDS_IN_PRODUCTION_FILE")+" :>"+csvResultBean.getMissedProducedRecordDetails().size());
			writerObj.write("\n"+properties.getProperty("NO_OF_MISSED_RECORDS_IN_PRODUCED_FILE")+" :>"+csvResultBean.getMissedProductionRecordDetails().size());
			
			buffer.append("\n");
			buffer.append("\n"+properties.getProperty("NO_OF_MISSED_RECORDS_IN_PRODUCTION_FILE")+" :>"+csvResultBean.getMissedProducedRecordDetails().size());
			buffer.append("\n"+properties.getProperty("NO_OF_MISSED_RECORDS_IN_PRODUCED_FILE")+" :>"+csvResultBean.getMissedProductionRecordDetails().size());
			
			writerObj.write("\n");
			writerObj.write("\n"+properties.getProperty("NO_OF_DUPLICATE_RECORDS_IN_PRODUCTION_FILE")+" :>"+ (csvResultBean.getDuplicateRecordInProduction()!= null ? csvResultBean.getDuplicateRecordInProduction().size():0));
			writerObj.write("\n"+properties.getProperty("NO_OF_DUPLICATE_RECORDS_IN_PRODUCED_FILE")+" :>"+(csvResultBean.getDuplicateRecordInProduced() != null ? csvResultBean.getDuplicateRecordInProduced().size():0));
			writerObj.write("\n");
			
			buffer.append("\n");
			buffer.append("\n"+properties.getProperty("NO_OF_DUPLICATE_RECORDS_IN_PRODUCTION_FILE")+" :>"+ (csvResultBean.getDuplicateRecordInProduction()!= null ? csvResultBean.getDuplicateRecordInProduction().size():0));
			buffer.append("\n"+properties.getProperty("NO_OF_DUPLICATE_RECORDS_IN_PRODUCED_FILE")+" :>"+(csvResultBean.getDuplicateRecordInProduced() != null ? csvResultBean.getDuplicateRecordInProduced().size():0));
			

			if(!csvResultBean.getMissedProductionRecordDetails().isEmpty()){
				//log.debug(resourceBundle.getProperty("PRODUCED_FILE_MISSED_RECORDS_DETAILS")+" 	:>"+csvResultBean.getMissedProductionRecordDetails().size()+"<: Details ::>>"+csvResultBean.getMissedProductionRecordDetails().toString());
				writerObj.write("\n"+properties.getProperty("PRODUCED_FILE_MISSED_RECORDS_DETAILS")+"	:>"+csvResultBean.getMissedProductionRecordDetails().toString());
				writerObj.write("\n");
				
				buffer.append("\n"+properties.getProperty("PRODUCED_FILE_MISSED_RECORDS_DETAILS")+"	:>"+csvResultBean.getMissedProductionRecordDetails().toString());
				buffer.append("\n");
			}
			if(!csvResultBean.getMissedProducedRecordDetails().isEmpty()){
				//log.debug(resourceBundle.getProperty("PRODUCTION_FILE_MISSED_RECORDS_DETAILS")+ " 	:>"+csvResultBean.getMissedProducedRecordDetails().size()+"<: Details ::>>"+csvResultBean.getMissedProducedRecordDetails().toString());
				writerObj.write("\n"+properties.getProperty("PRODUCTION_FILE_MISSED_RECORDS_DETAILS")+"	:>"+csvResultBean.getMissedProducedRecordDetails().toString());
				writerObj.write("\n");
				
				buffer.append("\n"+properties.getProperty("PRODUCTION_FILE_MISSED_RECORDS_DETAILS")+"	:>"+csvResultBean.getMissedProducedRecordDetails().toString());
				buffer.append("\n");
			}

			if(!csvResultBean.getDuplicateRecordInProduction().isEmpty()){
				log.debug("\n"+properties.getProperty("PRODUCTION_FILE_DUPLICATE_RECORD_DETAIL")+"  	:>"+csvResultBean.getDuplicateRecordInProduction().size()+"<: but not available in Production - Details ::>>"+csvResultBean.getDuplicateRecordInProduction().toString());
				writerObj.write("\n");
				writerObj.write("\n"+properties.getProperty("PRODUCTION_FILE_DUPLICATE_RECORD_DETAIL")+"	:>"+csvResultBean.getDuplicateRecordInProduction().toString());
				
				buffer.append("\n");
				buffer.append("\n"+properties.getProperty("PRODUCTION_FILE_DUPLICATE_RECORD_DETAIL")+"	:>"+csvResultBean.getDuplicateRecordInProduction().toString());
			}
			if(!csvResultBean.getDuplicateRecordInProduced().isEmpty()){
				log.debug("\n"+properties.getProperty("PRODUCED_FILE_DUPLICATE_RECORD_DETAIL")+"   	:>"+csvResultBean.getDuplicateRecordInProduced().size()+"<: but not available in Production - Details ::>>"+csvResultBean.getDuplicateRecordInProduced().toString());
				writerObj.write("\n"+properties.getProperty("PRODUCED_FILE_DUPLICATE_RECORD_DETAIL")+"  	:>"+csvResultBean.getDuplicateRecordInProduced().toString());
				
				buffer.append("\n"+properties.getProperty("PRODUCED_FILE_DUPLICATE_RECORD_DETAIL")+"  	:>"+csvResultBean.getDuplicateRecordInProduced().toString());
			}
			writerObj.write("\n\n Time taken to complete the task		:>"+timeTaken(csvResultBean.getStartTime(), csvResultBean.getEndTime(),csvResultBean));
		} catch (Exception e) {
			log.error("Exception while writing summary report ",e);
		} 
		mappingBeanObj.setSummaryReport(buffer.toString());
		
		mappingBeanObj.setOperationMsg("DoneDone");

		summaryList.add((int) csvResultBean.getProductionRecordsCount());
		summaryList.add((int) csvResultBean.getProducedRecordsCount());
		summaryList.add((int) csvResultBean.getMatchRecordsCount());
		summaryList.add((int) csvResultBean.getMisMatchRecordsCount());
		summaryList.add(csvResultBean.getMissedProducedRecordDetails().size());
		summaryList.add(csvResultBean.getMissedProductionRecordDetails().size());
		summaryList.add((csvResultBean.getDuplicateRecordInProduction()!= null ? csvResultBean.getDuplicateRecordInProduction().size():0));
		summaryList.add((csvResultBean.getDuplicateRecordInProduced() != null ? csvResultBean.getDuplicateRecordInProduced().size():0));

		mappingBeanObj.setSummaryResultList(summaryList);

		//System.out.println("\n\n ********** Summary Report for Comparison ************ \n"+ mappingBeanObj.getSummaryReport()
		//		+ "\n\n Summary List: " +mappingBeanObj.getSummaryResultList());

		return mappingBeanObj;
	}
	
	
	private static String convertStringArrayToString(String[] strArr, String delimiter) {
		StringBuilder sb = new StringBuilder();
		try{
			for (String str : strArr){
				if(StringUtils.isNotEmpty(str)){
					sb.append(str).append(delimiter);
				}else{
					sb.append("").append(delimiter);
				}
			}
			if(sb.length()>1){
				return sb.substring(0, sb.length() - 1);
			}else{
				return sb.toString();
			}
		}catch(Exception e){
			log.debug("Exception convertStringArrayToString "+e.getMessage());
			throw e;
		}
	}
}
