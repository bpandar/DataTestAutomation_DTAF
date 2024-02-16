/**
 *  Author      	: Bhoopathy P
 *  Date 			: Apr 21, 2021
 *  Class Name		: CompareDataCall.java
 *
 *  Project			: BMO - Risk ODS
 *
 *	Changes			: Compare DB, CSV files
 *
 *  Modification history
 *  Date			ChangeNo	Modified By							Description
 *
 *
 */


package com.bmo.tablecompare;

import com.bmo.csvcompare.CSVCompareWithSummary;
import com.bmo.mappingbean.MappingBean;
import com.bmo.readmapping.ReadMappingSheet;
import com.bmo.utils.ConsoleUtil;
import com.bmo.utils.PropertyUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.*;


public class CompareDataCall {
	
		private static final Properties configProp = PropertyUtil.getPropertyFile("config");
		private static final Logger log = LogManager.getLogger(CompareDataCall.class);
	
		//private static TextArea applicationConsole = new TextArea();
		//public static BorderPane root = new BorderPane();
		private static CompareDataCall compareDataInstance;
		//private CompareDataCall(){}
	
		public static CompareDataCall getInstance() {
			if (compareDataInstance == null) {
				compareDataInstance = new CompareDataCall();
			}
			return compareDataInstance;
		}

	public static List<MappingBean> getMappingData(MappingBean mappingBean) throws Exception {

		List<MappingBean> mappingDataList = null;
		try {
			mappingDataList = ReadMappingSheet.readMappingFile(mappingBean);

		} catch (Exception e) {
			log.error("Exception in readMappingFile ", e);
			ConsoleUtil.consoleAppender("Unable to Read the Mapping sheet, Please check the data mapping sheet..." + e.getMessage());
			mappingDataList = new ArrayList<>();
			throw new Exception("Unable to Read the Mapping sheet, Please check the data mapping sheet...");
		}
		return mappingDataList;
	}
		
		
	
	@SuppressWarnings("static-access")
	public static MappingBean getDataCompareCall(MappingBean mappingBean) throws Exception {

		String filePath = mappingBean.getMappingFilepath();
		Map<String, List<Integer>> resultAsMap = new HashMap<>();
		Map<String, String> statusMap = new HashMap<>();

		List<MappingBean> selectDataList = null;
				
		if (filePath == null) {
			JOptionPane.showMessageDialog(null, configProp.getProperty("ERR_SELECT_MAPPING_SHEET"));
					
		} else {
			ConsoleUtil.consoleAppender("Compare action to be called here");
			
			//selectDataList = getMappingDataBean(mappingBean);
			selectDataList = getMappingData(mappingBean);
			
			//System.out.println(mappingBean.getDBUserName() +"-----" +mappingBean.getDBPassword());
			
			try {
				if (CollectionUtils.isNotEmpty(selectDataList)) {
					System.out.println("Compare test ");

					boolean isRecordSelected = false;
					for (MappingBean mapping : selectDataList) {
						if (!mapping.isFlag()) {
							ConsoleUtil.consoleAppender(
									mapping.getPhase() + "<: Edited/Updated list value :>" + mapping.isFlag());
						} else {
							isRecordSelected = true;
						}
					}
					if (!isRecordSelected) {
							JOptionPane.showMessageDialog(null, configProp.getProperty("ERR_SET_FLAG"));
					}

					for (int i = 0; i < selectDataList.size(); i++) {
						MappingBean mappingBeanObj = selectDataList.get(i);
						try {
							//if (mappingBeanObj.isFlag()) {
								//mappingBeanObj.setStatus("Comparison task initiated");
								System.out.println("Comparison taks initiated : Index =>" + i);
								ConsoleUtil.consoleAppender("Comparison taks initiated : Index =>" + i);
								selectDataList.set(i, mappingBeanObj);
								//table.getSelectionModel().select(i);
								//table.getSelectionModel().getSelectedItem().setStatus("Comparison taks initiated");
								//table.refresh();// TODO: To be validated later
								// System.out.println(mappingBeanObj.getPhase().toUpperCase().trim());
								switch (mappingBeanObj.getPhase().toUpperCase().trim()) {
								case "SDR TO L0":
									log.debug("\n\n---------------------------------------------------------------------\n");
									log.debug(mappingBeanObj.getPhase().toUpperCase() + " "
											+ mappingBeanObj.getSourceSystem());
									// CSVCompare.getInstance().compareSDRWithCSVFiles(mappingBeanObj);
									// CSVCompareWithSummary.getInstance().compareCSVFilesAsMap(mappingBeanObj,true);
									break;
								case "DB TO CSV":
									log.debug(mappingBeanObj.getPhase().toUpperCase());
									mappingBean = CompareTables.getInstance().compareDBWithCSV(mappingBeanObj);//getColumnNames(mappingBeanObj);
									break;
								case "CSV TO CSV":
									log.debug("\n\n---------------------------------------------------------------------\n");
									log.debug(mappingBeanObj.getPhase().toUpperCase() + " "	+ mappingBeanObj.getSourceSystem());
									mappingBean = CSVCompareWithSummary.getInstance().compareCSVFilesAsMap(mappingBeanObj);
									break;
								case "DB TO DB":
									log.debug("\n\n---------------------------------------------------------------------\n");
									log.debug(mappingBeanObj.getPhase().toUpperCase() + " " + mappingBeanObj.getSourceSystem());
									mappingBean = CompareTables.getInstance().compareDBTableAsMap(mappingBeanObj);
									break;
								case "XML TO CSV":
									log.debug(mappingBeanObj.getPhase().toUpperCase());
									break;
								default:
									log.debug("Nothing mentioned::" + mappingBeanObj.getPhase().toUpperCase());
									break;
								}
								ConsoleUtil.consoleAppender("Comparison task completed : Index =>" + i);
								//table.getSelectionModel().getSelectedItem().setStatus("Completed");
								System.out.println("Comparison task completed succesfully: Index =>" + i);
								// table.refresh();

							List<Integer> reportList = mappingBean.getSummaryResultList();
							//System.out.println("\n"+mappingBeanObj.getSourceSystem() + "\t\t List: "+ reportList);
							resultAsMap.put(mappingBeanObj.getSourceSystem(),reportList);
							statusMap.put(mappingBeanObj.getSourceSystem(),"Completed");
								
						} catch (Exception e1) {
							e1.printStackTrace();
							ConsoleUtil.consoleAppender("Comparison task didn't completed succesfully: Index =>" + i);
							statusMap.put(mappingBeanObj.getSourceSystem(),"Failed");
							System.out.println(mappingBean.getStatusAsMap());
							// table.refresh();
							log.error("Exception in while comparing the CSV file", e1);
							ConsoleUtil.logException(e1);
						}
					}
					//table.refresh();
					// new MasterJob().compareMappingfile(selectedList);
						
					mappingBean.setResultMap(resultAsMap);
					mappingBean.setStatusAsMap(statusMap);

					//System.out.println(mappingBean.getResultMap() + "\n"+ mappingBean.getStatusAsMap());
				}
			} catch (Exception e1) {
				ConsoleUtil.logException(e1);
			}
		}
		
		return mappingBean;
	}

}
