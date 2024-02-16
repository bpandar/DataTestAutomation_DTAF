package com.bmo.readmapping;

import com.bmo.mappingbean.MappingBean;
import com.bmo.utils.CSVParserUtil;
import com.bmo.utils.ConsoleUtil;
import com.bmo.utils.PropertyUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class ReadMappingSheet {

	public static InputStream inputStream = null;
	public static Workbook workbook = null;
	public static List<MappingBean> mappingBeanList = null;
	public static MappingBean mappingBean = null;

	public static int FLAG = 0;
	public static int SOURCE_SYSTEM = 1;
	public static int PHASE = 2;
	public static int SOURCE = 3;
	public static int SOURCE_COMPOSITE_PK = 4;
	public static int SOURCE_FIELDS = 5;
	public static int SOURCEEXTRAFIELD = 6;
	public static int TARGET = 7;
	public static int DESTINATION_COMPOSITE_PK = 8;
	public static int TARGET_FIELDS = 9;
	public static int TARGET_EXTRAFIELD = 10;
	public static int STATUS_FIELD = 11;

	private static String rootDir = System.getProperty("user.dir");
	private static final Logger log = LogManager.getLogger(ReadMappingSheet.class);

	private static final Properties dbInfoProp = PropertyUtil.getPropertyFile("dbconnectioninfo");
	private static final Properties configProp = PropertyUtil.getPropertyFile("config");

	public static List<MappingBean> readMappingFile(MappingBean mappingBeanObj) throws Exception {

		String fileName = mappingBeanObj.getMappingFilepath();
		String phaseFilter = mappingBeanObj.getCheckBoxPhase();
		mappingBean = mappingBeanObj;

		try { 
			File file = new File(fileName);
			System.out.println(file.getName());
			inputStream = new FileInputStream(file);
			workbook = WorkbookFactory.create(inputStream);
			XSSFSheet sheet = (XSSFSheet) workbook.getSheet("Mapping");
			int rowCount;
			rowCount = sheet.getLastRowNum();
			mappingBeanList = new LinkedList<>();

			String productionFilePath;
			String producedFilePath;
			if (rootDir.contains("\\")) {
				productionFilePath = rootDir + "\\Data\\Production\\";
				producedFilePath = rootDir + "\\Data\\Produced\\";
			} else {
				productionFilePath = rootDir + "/Data/Production/";
				producedFilePath = rootDir + "/Data/Produced/";
			}

			for (int i = 1; i <= rowCount; i++) {
				XSSFRow row = sheet.getRow(i);

				if (row.getCell(FLAG) != null && StringUtils.isNotEmpty(row.getCell(FLAG).getStringCellValue())
						&& row.getCell(FLAG).getStringCellValue().equalsIgnoreCase("Y")) {

					String lPhaseVal = row.getCell(PHASE) == null ? "null": CSVParserUtil.csvTrim(row.getCell(PHASE).getStringCellValue());
					System.out.println("lPhaseVal :> " + lPhaseVal + ":>phaseFilter:" + phaseFilter);
					if (StringUtils.isAllEmpty(phaseFilter)	|| (StringUtils.isNoneEmpty(phaseFilter) && lPhaseVal.equalsIgnoreCase(phaseFilter))) {

						System.out.println("lPhaseVal :> " + phaseFilter);
						//mappingBean.setStatus("Initiated");
						System.out.println("STATUS_FIELD = "+ row.getCell(STATUS_FIELD));
						
						if(row.getCell(STATUS_FIELD)== null||row.getCell(STATUS_FIELD).getStringCellValue().equalsIgnoreCase("INITIATED")
									|| row.getCell(STATUS_FIELD).getStringCellValue().equalsIgnoreCase("COMPLETED"))
							{
							row.getCell(STATUS_FIELD).setCellValue("INITIATED");
						}
						
						mappingBean.setRowCell(row.getCell(STATUS_FIELD));
						
						// The below field needs to be retrieve from Mapping Excel sheet since the same
						// field/attribute will be used for differentiate the Databases like ORACLE /
						// MYSQL
						// mappingBean.setDataBaseName("oracle");
						mappingBean.setMappingFlag(true);

						mappingBean.setFlag((CSVParserUtil.csvTrim(row.getCell(FLAG).getStringCellValue()).equalsIgnoreCase("Y")? true: false));
						mappingBean.setSourceSystem(row.getCell(SOURCE_SYSTEM) == null ? "null"	: CSVParserUtil.csvTrim(row.getCell(SOURCE_SYSTEM).getStringCellValue()));
						mappingBean.setPhase(row.getCell(PHASE) == null ? "null" : CSVParserUtil.csvTrim(row.getCell(PHASE).getStringCellValue()));
						if (!"CSV TO CSV".equalsIgnoreCase(mappingBean.getPhase())) {
							mappingBean.setSource(row.getCell(SOURCE) == null ? "null" : CSVParserUtil.csvTrimWithoutUpperCase(row.getCell(SOURCE).getStringCellValue()));
							mappingBean.setSourceTableName(mappingBean.getSource());
							// System.out.println("1------>"+mappingBean.getSourceTable());
						} else {
							mappingBean.setSource(row.getCell(SOURCE) == null ? "null" : productionFilePath + CSVParserUtil.csvTrimWithoutUpperCase(row.getCell(SOURCE).getStringCellValue()));
							// System.out.println("2------>"+mappingBean.getSource());
						}
						/*
						 * mappingBean.setSourceCompositePK(row.getCell(SOURCE_COMPOSITE_PK) ==
						 * null?"null":row.getCell(SOURCE_COMPOSITE_PK).getStringCellValue().
						 * replaceAll("\\\\", "").toUpperCase());
						 * mappingBean.setSourceFields(row.getCell(SOURCE_FIELDS) ==
						 * null?"null":row.getCell(SOURCE_FIELDS).getStringCellValue().
						 * replaceAll("\\\\", "").toUpperCase());
						 */

						mappingBean.setSourcePrimaryKey(row.getCell(SOURCE_COMPOSITE_PK) == null ? "null"
								: CSVParserUtil.csvSplitAndTrim(row.getCell(SOURCE_COMPOSITE_PK).getStringCellValue().toUpperCase()));

						if ("DB TO DB".equalsIgnoreCase(mappingBean.getPhase())	|| "DB TO CSV".equalsIgnoreCase(mappingBean.getPhase())) {
							mappingBean.setSrcSkipColumn(row.getCell(SOURCE_FIELDS) == null ? ""
									: CSVParserUtil.csvSplitAndTrim(row.getCell(SOURCE_FIELDS).getStringCellValue().toUpperCase()));
						} else {
							mappingBean.setSourceFields(row.getCell(SOURCE_FIELDS) == null ? ""
									: CSVParserUtil.csvSplitAndTrim(row.getCell(SOURCE_FIELDS).getStringCellValue().toUpperCase()));
						}

						mappingBean.setSourceExtraField(row.getCell(SOURCEEXTRAFIELD) == null ? "null"
								: row.getCell(SOURCEEXTRAFIELD).getStringCellValue());

						// if("DB TO CSV".equalsIgnoreCase(mappingBean.getPhase())){
						// mappingBean.setTarget(row.getCell(TARGET) ==
						// null?"null":producedFilePath+CSVParserUtil.csvTrimWithoutUpperCase(row.getCell(TARGET).getStringCellValue()));
						// }
						if ("DB TO DB".equalsIgnoreCase(mappingBean.getPhase())) {
							mappingBean.setTarget(row.getCell(TARGET) == null ? "null"
									: CSVParserUtil.csvTrimWithoutUpperCase(row.getCell(TARGET).getStringCellValue()));
							mappingBean.setTargetTableName(mappingBean.getTarget());
						} else {
							mappingBean.setTarget(row.getCell(TARGET) == null ? "null"
									: producedFilePath + CSVParserUtil.csvTrimWithoutUpperCase(row.getCell(TARGET).getStringCellValue()));
						}
						/*
						 * mappingBean.setDestinationCompositePK(row.getCell(DESTINATION_COMPOSITE_PK)
						 * == null?"null":row.getCell(DESTINATION_COMPOSITE_PK).getStringCellValue().
						 * replaceAll("\\\\", "").toUpperCase());
						 * mappingBean.setTargetFields(row.getCell(TARGET_FIELDS) ==
						 * null?"null":row.getCell(TARGET_FIELDS).getStringCellValue().
						 * replaceAll("\\\\", "").toUpperCase());
						 */

						mappingBean.setTargetPrimaryKey(row.getCell(DESTINATION_COMPOSITE_PK) == null ? "null"
								: CSVParserUtil.csvSplitAndTrim(row.getCell(DESTINATION_COMPOSITE_PK).getStringCellValue().toUpperCase()));
						if ("DB TO DB".equalsIgnoreCase(mappingBean.getPhase())) {
							mappingBean.setTarSkipColumn(row.getCell(TARGET_FIELDS) == null ? "null"
									: CSVParserUtil.csvSplitAndTrim(row.getCell(TARGET_FIELDS).getStringCellValue().toUpperCase()));
						} else {
							mappingBean.setTargetFields(row.getCell(TARGET_FIELDS) == null ? "null"
									: CSVParserUtil.csvSplitAndTrim(row.getCell(TARGET_FIELDS).getStringCellValue().toUpperCase()));
						}

						mappingBean.setTargetExtraField(row.getCell(TARGET_EXTRAFIELD) == null ? "null"	
								: row.getCell(TARGET_EXTRAFIELD).getStringCellValue());
								
						Set<String> srcSkipColumns = new HashSet<String>();
						Set<String> tarSkipColumns = new HashSet<String>();
						
						if (!phaseFilter.equalsIgnoreCase("CSV TO CSV")){							
							srcSkipColumns.addAll(Arrays.asList(mappingBean.getSrcSkipColumn().split(",")));
							srcSkipColumns.addAll(Arrays.asList(configProp.getProperty("SOURCE_COLUMNS_SKIP").trim().split(",")));

							System.out.println("SourceSkip Columns: >> "+srcSkipColumns);
							mappingBean.setSourceSkipColumn(srcSkipColumns);
							
							if(!phaseFilter.equalsIgnoreCase("DB TO CSV")) {
							tarSkipColumns.addAll(Arrays.asList(mappingBean.getTarSkipColumn().split(",")));
							tarSkipColumns.addAll(Arrays.asList(configProp.getProperty("SOURCE_COLUMNS_SKIP").trim().split(",")));

							System.out.println("TargetSkip Columns: >> "+tarSkipColumns);
							mappingBean.setTargetSkipColumn(tarSkipColumns);
							}		
							mappingBean = getDBInputBean(mappingBean);
						}else {
							System.out.println("Getting Skipped columns from Config file");
							
							srcSkipColumns.addAll(Arrays.asList(configProp.getProperty("SOURCE_COLUMNS_SKIP").trim().split(",")));
							System.out.println("SourceSkip Columns: >> "+srcSkipColumns);
							mappingBean.setSourceSkipColumn(srcSkipColumns);
							
							tarSkipColumns.addAll(Arrays.asList(configProp.getProperty("TARGET_COLUMNS_SKIP").trim().split(",")));
							System.out.println("TargetSkip Columns: >> "+tarSkipColumns);
							mappingBean.setTargetSkipColumn(tarSkipColumns);	
						}
						  mappingBeanList.add(mappingBean);
					}
				}
			}

			if (inputStream != null)
				inputStream.close();
			if (workbook != null) {
				workbook.close();
			}
			try {
				if (false) {
					ConsoleUtil.consoleAppender("\n" + "mappingBeanList size ::> " + mappingBeanList.size());
				}
			} catch (Exception e) {
				log.error("Calling the action from main method" + e);
			}
			log.debug("mappingBeanList size ::> " + mappingBeanList.size());
		} catch (Exception e) {
			log.error("Exception in Master job ", e);
			throw e;
		}

		System.out.println("mappingBeanList:>" + mappingBeanList.size());
		return mappingBeanList;
	}

	public static MappingBean getDBInputBean(MappingBean mappingBean)
	{
		String[] sourceInput = mappingBean.getSourceExtraField().split(",");
		String[] targetInput = mappingBean.getTargetExtraField().split(",");
		System.out.println("Selected Project: "+mappingBean.getProjectName());
		if (mappingBean.getPhase().equalsIgnoreCase("DB TO DB")
				|| mappingBean.getPhase().equalsIgnoreCase("DB TO CSV")) {

//			if (!mappingBean.getProjectName().isEmpty() && !mappingBean.getProjectName().equalsIgnoreCase("select") ) {
//				mappingBean.setSourceUserName(dbInfoProp.getProperty(mappingBean.getProjectName()+"_ORACLE_USERNAME"));
//				mappingBean.setSourcePassword(dbInfoProp.getProperty(mappingBean.getProjectName()+"_ORACLE_PASSWORD"));
//			}

			for (String srcData : sourceInput) {
				if (srcData.contains("-")) {
					mappingBean.setSourceBusDate(srcData.trim());
				} else if (srcData.startsWith("DEV") || srcData.startsWith("SIT") || srcData.startsWith("UAT")
						|| srcData.startsWith("PROD")) {
					mappingBean.setSourceServer(srcData.trim());
				} else if (srcData.isEmpty() || srcData.equals(" ")) {
					System.out.println( "Source Extra Field is Empty/Invalid...! : " + srcData);
				}
				else {
					mappingBean.setSourceRunID(srcData.trim());
				}
			}

			for (String tarData : targetInput) {
				if (tarData.contains("-")) {
					mappingBean.setTargetBusDate(tarData.trim());
				} else if (tarData.startsWith("DEV") || tarData.startsWith("SIT") || tarData.startsWith("UAT")
						|| tarData.startsWith("PROD")) {
					mappingBean.setTargetServer(tarData.trim());
				} else if (tarData.isEmpty() || tarData.equals(" ")) {
					System.out.println( "Source Extra Field is Empty/Invalid...! : " + tarData);
				}else {
					mappingBean.setTargetRunID(tarData.trim());
				}
			}
		}
		return mappingBean;
	}

}
