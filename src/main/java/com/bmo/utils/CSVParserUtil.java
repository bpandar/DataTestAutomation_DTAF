/**
 *  Author      	: Bhoopathy P
 *  Date 			: Apr 13, 2018
 *  Class Name		: CSVParserUtil.java
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
package com.bmo.utils;

import com.bmo.mappingbean.CSVParserBean;
import com.bmo.mappingbean.ParserResultBean;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVParserUtil {

	static final Logger log = LogManager.getLogger(CSVParserUtil.class);

	private CSVParserUtil(){
		throw new IllegalStateException("CSVParserUtil class");
	}

	public static List<CSVParserBean> getCSVList(String  csvFilePath, Map<String,String> headerMap) throws Exception{
		//Apache common CSV
		try (
				Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
				CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
				) {
			// Reading all records at once into memory
			List<CSVRecord> csvRecords = csvParser.getRecords();
			log.debug("csvRecords : "+csvRecords.size()+" \n"+headerMap.toString());

			for (CSVRecord csvRecord : csvRecords) {//csvParser
				// Accessing Values by Column Index
				log.debug(csvRecord.size()+" : Record No - " + csvRecord.getRecordNumber() + " --> "+csvRecord.toString());
			}
		}
		return null;
	}

	public static ParserResultBean parseCSVFileLineByLine(String fileName) throws IOException {
		//Open CSV Parser
		//create CSVReader object
		ParserResultBean resultBean = new ParserResultBean();
		try(
				CSVReader reader = new CSVReader(new FileReader(fileName)); //, ',')); // we may use in case of any specific delimiter which we used in the CSV files
				){
			List<CSVParserBean> csvParserBeanList = new ArrayList<CSVParserBean>();
			//read line by line
			String[] record = null;
			//skip header row
			record = reader.readNext();
			List<String> headerList = new ArrayList<>();
			Map<String,String> headerNameMap = new LinkedHashMap<String, String>();
			for (int i=0 ; i<record.length;i++) {
				headerNameMap.put(csvTrim(record[i]), "column"+(i+1));
				//headerNameMap.put(record[i].trim().toUpperCase(), "column"+(i+1));
				headerList.add("setColumn"+(i+1));
			}

			log.debug(fileName+">---header mappings-("+headerNameMap.size()+")-->"+headerNameMap.toString());
			resultBean.setHeaderNameMappings(headerNameMap);
			Class<CSVParserBean> myProducedObjectClass = CSVParserBean.class;
			CSVParserBean tempProducedBeanObj = new CSVParserBean();

			while((record = reader.readNext()) != null){
				tempProducedBeanObj = new CSVParserBean();
				try {
					for (int i=0 ; i<headerList.size();i++) {
						myProducedObjectClass.getMethod(headerList.get(i), new Class[]{String.class }).invoke(tempProducedBeanObj, record[i]);
					}
					csvParserBeanList.add(tempProducedBeanObj);
				} catch (Exception e) {
					log.error("Exception while conveting to java bean",e);
				}
			}
			resultBean.setCvsParserList(csvParserBeanList);
		}catch(Exception e){
			log.error("Exception while parsing CSV file ["+fileName+"],",e);
		}
		return resultBean;
	}

	public static String csvTrim(String inputstr){
		inputstr =inputstr.toUpperCase();
		String CSV_PARTTERN = "(^\\s*)(.*)";
		Pattern ptn = Pattern.compile(CSV_PARTTERN);
		Matcher mtch = ptn.matcher(inputstr.trim());
		while(mtch.find()){
			inputstr = mtch.group(2);
		}
		return inputstr;
	}

	public static String csvTrimWithoutUpperCase(String inputstr){
		String CSV_PARTTERN = "(^\\s*)(.*)";
		Pattern ptn = Pattern.compile(CSV_PARTTERN);
		Matcher mtch = ptn.matcher(inputstr.trim());
		while(mtch.find()){
			inputstr = mtch.group(2);
		}
		return inputstr;
	}
	
	public static String csvSplitAndTrim(String inputstr){
		String fieldName[] =inputstr.split(",");
		StringBuilder sb = new StringBuilder();
		
		String CSV_PARTTERN = "(^\\s*)(.*)";
		Pattern ptn = Pattern.compile(CSV_PARTTERN);
		
		for (String string : fieldName) {
			Matcher mtch = ptn.matcher(string.trim());
			while(mtch.find()){
				string = mtch.group(2);
			}
			sb.append(","+string);
		}
		return sb.toString().replaceFirst(",", "");
	}
	@SuppressWarnings("deprecation")
	private List<CSVParserBean> parseCSVToBeanList(String csvFileName,String csvFileHeaderNames) throws IOException {

		HeaderColumnNameTranslateMappingStrategy<CSVParserBean> beanStrategy = new HeaderColumnNameTranslateMappingStrategy<>();
		beanStrategy.setType(CSVParserBean.class);
		//beanStrategy.setColumnMapping(getCSVHeaderWithJavaBeanMappings(csvFileHeaderNames));
		CsvToBean<CSVParserBean> csvToBean = new CsvToBean<>();
		List<CSVParserBean> csvList = null;
		try(
				CSVReader reader = new CSVReader(new FileReader(csvFileName));
				){
			csvList = csvToBean.parse(); //parse(beanStrategy, reader)
		}catch(Exception e){
			log.error("Exception in parseCSVToBeanList method",e);	
			throw e;
		}

		return csvList;
	}

}
