/**
 *  Author      	: Bhoopathy P
 *  Date 			: Apr 17, 2021
 *  Class Name		: CSVCompareResultBean.java
 *  
 *  Project			: BMO - Risk ODS
 *
 *	Changes			: Compare 2 CSV files (CSV to CSV)	
 *  
 *  Modification history 
 *  Date			ChangeNo	Modified By							Description
 *  
 */
package com.bmo.mappingbean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class CSVResultBean {

	private LocalDateTime startTime;
	private long productionRecordsCount;
	private long producedRecordsCount;
	private long misMatchRecordsCount;
	private long matchRecordsCount;
	private List<String> missedProductionRecordDetails;
	private List<String> missedProducedRecordDetails;
	private String timeDiff;
	private String errorMessage;
	private List<String> duplicateRecordInProduction;
	private List<String> duplicateRecordInProduced;
	private String compareResultFile;
	private LocalDateTime endTime;
	private String resultSummary;
	
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	public long getProductionRecordsCount() {
		return productionRecordsCount;
	}
	public void setProductionRecordsCount(long productionRecordsCount) {
		this.productionRecordsCount = productionRecordsCount;
	}
	public long getProducedRecordsCount() {
		return producedRecordsCount;
	}
	public void setProducedRecordsCount(long producedRecordsCount) {
		this.producedRecordsCount = producedRecordsCount;
	}
	public long getMisMatchRecordsCount() {
		return misMatchRecordsCount;
	}
	public void setMisMatchRecordsCount(long misMatchRecordsCount) {
		this.misMatchRecordsCount = misMatchRecordsCount;
	}
	public long getMatchRecordsCount() {
		return matchRecordsCount;
	}
	public void setMatchRecordsCount(long matchRecordsCount) {
		this.matchRecordsCount = matchRecordsCount;
	}
	public List<String> getMissedProductionRecordDetails() {
		return missedProductionRecordDetails;
	}
	public void setMissedProductionRecordDetails(List<String> missedProductionRecordDetails) {
		this.missedProductionRecordDetails = missedProductionRecordDetails;
	}
	public List<String> getMissedProducedRecordDetails() {
		return missedProducedRecordDetails;
	}
	public void setMissedProducedRecordDetails(List<String> missedProducedRecordDetails) {
		this.missedProducedRecordDetails = missedProducedRecordDetails;
	}
	public String getTimeDiff() {
		return timeDiff;
	}
	public void setTimeDiff(String timeDiff) {
		this.timeDiff = timeDiff;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public List<String> getDuplicateRecordInProduction() {
		return duplicateRecordInProduction;
	}
	public void setDuplicateRecordInProduction(List<String> duplicateRecordInProduction) {
		this.duplicateRecordInProduction = duplicateRecordInProduction;
	}
	public List<String> getDuplicateRecordInProduced() {
		return duplicateRecordInProduced;
	}
	public void setDuplicateRecordInProduced(List<String> duplicateRecordInProduced) {
		this.duplicateRecordInProduced = duplicateRecordInProduced;
	}
	public String getCompareResultFile() {
		return compareResultFile;
	}
	public void setCompareResultFile(String compareResultFile) {
		this.compareResultFile = compareResultFile;
	}
	public LocalDateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
	
}

