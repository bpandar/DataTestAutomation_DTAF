/**
 *  Author      	: Bhoopathy P
 *  Date 			: Apr 19, 2021
 *  Class Name		: MappingBean.java
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
package com.bmo.mappingbean;

import org.apache.poi.xssf.usermodel.XSSFCell;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MappingBean {
	
	private Boolean	flag;
	private String	source; //source file name along with location or DB Query
	private String	sourceCompositePK; //will be used for compare fields
	private String	sourceFields; //Source header or CSV file header
	private String	sourceExtraField;
	private String	target;  //Target file name along with location or DB Query
	private String	destinationCompositePK; //will be used for compare fields
	private String	targetFields; //Target header or CSV file header
	private String	targetExtraField;
	private String	phase; // SDR TO CSV or CSV TO CSV etc., compare
	private String	sourceSystem; //File type -> MUREX,ENDURE, etc.,
	private Map<String,String>	sourceHeaderNameMappingsWithJavaFields; //Header mapping with java fields
	private Map<String,String>	targetHeaderNameMappingsWithJavaFields; //Header mapping with java fields
	private Map<String,String>	sourceCompositeKeyHeaderNameMappingsWithJavaFields; //Composite Key Header mapping with java fields
	private Map<String,String>	targetCompositeKeyHeaderNameMappingsWithJavaFields; //Composite Key Header mapping with java fields
	
	private Map<String, Integer> sourceActualHeader;
	private Map<String, Integer> targetActualHeader;
	private String dataBaseName;
	private boolean isHeaderRequired;
	private String status;
	private Statement sourceStatement,targetStatement;
	private String sourceServer, targetServer;
	private String sourceBusDate, targetBusDate;
	private String sourceRunID,targetRunID;
	private String sourceUserName, sourcePassword;
	private String targetUserName, targetPassword;
	private Connection sDBConnection, tDBConnection;
	private String sourceTableName,targetTableName;
	private String operationMsg;
	private String sourceKeysList, targetKeysList;
	private String sourceBRFlag, targetBRFlag;
	private String sourcePK, targetPK;
	private String sourceColumns, targetColumns;
	private String sourceColumnSQL, targetColumnSQL;
	private String absolutePath;
	private String checkBoxPhase;
	private String srcSkipColumn, tarSkipColumn;
	private boolean mappingFlag;
	private Set<String> sourceSkipColumn;
	private Set<String> targetSkipColumn;
	private XSSFCell cell;
	private Map<String, List<Integer>> summaryMap;
	private List<Integer> summaryList;
	private String summaryReport;
	private String tableResults;
	private Map<String, String> statusMap;
	private String targetSQL, sourceSQL;
	private ResultSet sourceResultSet, targetResultSet ;
	private String projectName;


	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSourceCompositePK() {
		return sourceCompositePK;
	}
	public void setSourceCompositePK(String sourceCompositePK) {
		this.sourceCompositePK = sourceCompositePK;
	}
	public String getSourceFields() {
		return sourceFields;
	}
	public void setSourceFields(String sourceFields) {
		this.sourceFields = sourceFields;
	}
	public String getSourceExtraField() {
		return sourceExtraField;
	}
	public void setSourceExtraField(String sourceExtraField) {
		this.sourceExtraField = sourceExtraField;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getDestinationCompositePK() {
		return destinationCompositePK;
	}
	public void setDestinationCompositePK(String destinationCompositePK) {
		this.destinationCompositePK = destinationCompositePK;
	}
	public String getTargetFields() {
		return targetFields;
	}
	public void setTargetFields(String targetFields) {
		this.targetFields = targetFields;
	}
	public String getTargetExtraField() {
		return targetExtraField;
	}
	public void setTargetExtraField(String targetExtraField) {
		this.targetExtraField = targetExtraField;
	}
	public void setSrcSkipColumn(String srcSkipColumn) {
		this.srcSkipColumn=srcSkipColumn;
	}
	public String getSrcSkipColumn() {
		return srcSkipColumn;
	}
	public void setTarSkipColumn(String tarSkipColumn) {
		this.tarSkipColumn=tarSkipColumn;
	}
	public String getTarSkipColumn() {
		return tarSkipColumn;
	}
	public String getPhase() {
		return phase;
	}
	public void setPhase(String phase) {
		this.phase = phase;
	}
	public String getSourceSystem() {
		return sourceSystem;
	}
	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}
	public Map<String, String> getSourceHeaderNameMappingsWithJavaFields() {
		return sourceHeaderNameMappingsWithJavaFields;
	}
	public void setSourceHeaderNameMappingsWithJavaFields(Map<String, String> sourceHeaderNameMappingsWithJavaFields) {
		this.sourceHeaderNameMappingsWithJavaFields = sourceHeaderNameMappingsWithJavaFields;
	}
	public Map<String, String> getTargetHeaderNameMappingsWithJavaFields() {
		return targetHeaderNameMappingsWithJavaFields;
	}
	public void setTargetHeaderNameMappingsWithJavaFields(Map<String, String> targetHeaderNameMappingsWithJavaFields) {
		this.targetHeaderNameMappingsWithJavaFields = targetHeaderNameMappingsWithJavaFields;
	}
	public Map<String, String> getSourceCompositeKeyHeaderNameMappingsWithJavaFields() {
		return sourceCompositeKeyHeaderNameMappingsWithJavaFields;
	}
	public void setSourceCompositeKeyHeaderNameMappingsWithJavaFields(
			Map<String, String> sourceCompositeKeyHeaderNameMappingsWithJavaFields) {
		this.sourceCompositeKeyHeaderNameMappingsWithJavaFields = sourceCompositeKeyHeaderNameMappingsWithJavaFields;
	}
	public Map<String, String> getTargetCompositeKeyHeaderNameMappingsWithJavaFields() {
		return targetCompositeKeyHeaderNameMappingsWithJavaFields;
	}
	public void setTargetCompositeKeyHeaderNameMappingsWithJavaFields(
			Map<String, String> targetCompositeKeyHeaderNameMappingsWithJavaFields) {
		this.targetCompositeKeyHeaderNameMappingsWithJavaFields = targetCompositeKeyHeaderNameMappingsWithJavaFields;
	}
	public Map<String, Integer> getSourceActualHeader() {
		return sourceActualHeader;
	}
	public void setSourceActualHeader(Map<String, Integer> sourceActualHeader) {
		this.sourceActualHeader = sourceActualHeader;
	}
	public Map<String, Integer> getTargetActualHeader() {
		return targetActualHeader;
	}
	public void setTargetActualHeader(Map<String, Integer> targetActualHeader) {
		this.targetActualHeader = targetActualHeader;
	}
	public boolean isHeaderRequired() {
		return isHeaderRequired;
	}
	public void setHeaderRequired(boolean isHeaderRequired) {
		this.isHeaderRequired = isHeaderRequired;
	}
	
	public String getDataBaseName() {
		return dataBaseName;
	}
	public void setDataBaseName(String dataBaseName) {
		this.dataBaseName = dataBaseName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Map<String, String> getStatusAsMap() {
		return statusMap;
	}
	public void setStatusAsMap(Map<String, String> statusMap) {
		this.statusMap = statusMap;
	}

	@Override
	public String toString() {
		return "\n******** \t Selected Row Value \t *******\nSource System:>\t\t\t\t"+sourceSystem+"\nPhase:>\t\t\t\t\t\t"
					+phase+"\nSource Table/File:>\t\t\t"+source+"\nSource Composite Key:>\t\t"+sourceCompositePK+"\nTarget Table/File:>\t\t\t"
					+target+"\nTarget Composite Key:>\t\t"+destinationCompositePK+"\n#####";
	}
	
	public String getSourceUserName() {
		return sourceUserName;
	}
	public void setSourceUserName(String sourceUserName) {
		this.sourceUserName=sourceUserName;
	}
	public String getSourcePassword() {
		return sourcePassword;
	}
	public void setSourcePassword(String sourcePassword) {
		this.sourcePassword=sourcePassword;
	}
	public String getTargetUserName() {
		return targetUserName;
	}
	public void setTargetUserName(String targetUserName) {
		this.targetUserName=targetUserName;
	}
	public String getTargetPassword() {
		return targetPassword;
	}
	public void setTargetPassword(String targetPassword) {
		this.targetPassword=targetPassword;
	}
	public String getSourceServer() {
		return sourceServer;		
	}
	public void setSourceServer(String sourceServer) {
		this.sourceServer=sourceServer;		
	}
	public String getSourceBusDate() {
		return sourceBusDate;		
	}
	public void setSourceBusDate(String sourceBusDate) {
		this.sourceBusDate=sourceBusDate;		
	}
	public String getSourceRunID() {
		return sourceRunID;		
	}
	public void setSourceRunID(String sourceRunID) {
		this.sourceRunID=sourceRunID;		
	}
	public String getTargetServer() {
		return targetServer;		
	}
	public void setTargetServer(String targetServer) {
		this.targetServer=targetServer;		
	}
	public String getTargetBusDate() {
		return targetBusDate;		
	}
	public void setTargetBusDate(String targetBusDate) {
		this.targetBusDate=targetBusDate;		
	}
	public String getTargetRunID() {
		return targetRunID;		
	}
	public void setTargetRunID(String targetRunID) {
		this.targetRunID=targetRunID;		
	}
	public Statement getSourceStatement() {
		return sourceStatement;		
	}
	public void setSourceStatement(Statement sourceStatement) {
		this.sourceStatement=sourceStatement;	
	}
	public Statement getTargetStatement() {
		return targetStatement;		
	}
	public void setTargetStatement(Statement targetStatement) {
		this.targetStatement=targetStatement;	
	}
	public String getSourceTableName() {
		return sourceTableName;		
	}
	public void setSourceTableName(String sourceTableName) {
		this.sourceTableName=sourceTableName;	
	}
	public String getTargetTableName() {
		return targetTableName;		
	}
	public void setTargetTableName(String targetTableName) {
		this.targetTableName=targetTableName;	
	}
	public Connection getSourceDBConnection() {
		return sDBConnection;		
	}
	public void setSourceDBConnection(Connection sDBConnection) {
		this.sDBConnection=sDBConnection;	
	}
	public Connection getTargetDBConnection() {
		return tDBConnection;		
	}
	public void setTargetDBConnection(Connection tDBConnection) {
		this.tDBConnection=tDBConnection;	
	}
	public String getSourcePrimaryKey() {
		return sourceKeysList;
	}
	public void setSourcePrimaryKey(String sourceKeysList) {
		this.sourceKeysList=sourceKeysList;
	}
	public String getSourcePK() {
		return sourcePK;
	}
	public void setSourcePK(String sourcePK) {
		this.sourcePK=sourcePK;
	}
	public String getTargetPrimaryKey() {
		return targetKeysList;
	}
	public void setTargetPrimaryKey(String targetKeysList) {
		this.targetKeysList=targetKeysList;
	}
	public String getTargetPK() {
		return targetPK;
	}
	public void setTargetPK(String targetPK) {
		this.targetPK=targetPK;
	}
	public String getOperationMsg() {
		return operationMsg;
	}
	public void setOperationMsg(String operationMsg) {
		this.operationMsg=operationMsg;		
	}
	public String getSourceBRFlag() {
		return sourceBRFlag;
	}
	public void setSourceBRFlag(String sourceBRFlag) {
		this.sourceBRFlag=sourceBRFlag;
	}
	public String getTargetBRFlag() {
		return targetBRFlag;
	}
	public void setTargetBRFlag(String targetBRFlag) {
		this.targetBRFlag=targetBRFlag;
	}
	public String getSourceColumns() {
		return sourceColumns;
	}
	public void setSourceColumns(String sourceColumns) {
		this.sourceColumns=sourceColumns;
	}
	public String getTargetColumns() {
		return targetColumns;
	}
	public void setTargetColumns(String targetColumns) {
		this.targetColumns=targetColumns;
	}

	public String getSummaryReport() {

		return summaryReport;
	}
	public void setSummaryReport(String summaryReport)
	{
		this.summaryReport = summaryReport;
	}

	public List<Integer> getSummaryResultList()
	{
		return summaryList;
	}
	public void setSummaryResultList(List<Integer> summaryList)
	{
		this.summaryList=summaryList;
	}

	public String getSourceColumnSQL() {
		return sourceColumnSQL;
	}
	public void setSourceColumnSQL(String sourceColumnSQL) {
		this.sourceColumnSQL=sourceColumnSQL;
	}
	public String getTargetColumnSQL() {
		return targetColumnSQL;
	}
	public void setTargetColumnSQL(String targetColumnSQL) {
		this.targetColumnSQL=targetColumnSQL;
	}
	public void setMappingFilepath(String absolutePath) {
		this.absolutePath=absolutePath;
	}
	public String getMappingFilepath() {
		return absolutePath;
	}
	public void setCheckBoxPhase(String checkBoxPhase) {
		this.checkBoxPhase=checkBoxPhase;
	}
	public String getCheckBoxPhase() {
		return checkBoxPhase;
	}
	public void setMappingFlag(boolean mappingFlag) {
		this.mappingFlag=mappingFlag;		
	}
	public boolean getMappingFlag() {
		return mappingFlag;
	}
	public void setSourceSkipColumn(Set<String> sourceSkipColumn) {
		this.sourceSkipColumn=sourceSkipColumn;			
	}	
	public Set<String> getSourceSkipColumn() {
		return sourceSkipColumn;
	}
	public void setTargetSkipColumn(Set<String> targetSkipColumn) {
		this.targetSkipColumn=targetSkipColumn;			
	}	
	public Set<String> getTargetSkipColumn() {
		return targetSkipColumn;
	}
	public void setRowCell(XSSFCell cell) {
		this.cell=cell;
	}
	public XSSFCell getRowCell() {
		return cell;
	}

	public void setResultMap(Map<String, List<Integer>> summaryMap) {
		this.summaryMap=summaryMap;
	}
	public Map<String, List<Integer>> getResultMap() { return summaryMap; }
	public void setTableResult(String tableResults) {
		this.tableResults=tableResults;
	}
	public String getTableResult() {
		return tableResults;
	}

	//get Source & target Table/Query
    public void setTargetSQLQuery(String targetSQL) { this.targetSQL=targetSQL; }
	public String getTargetSQLQuery() {
		return targetSQL;
	}
	public void setSourceSQLQuery(String sourceSQL) {this.sourceSQL=sourceSQL;	}
	public String getSourceSQLQuery() {
		return sourceSQL;
	}

	//get Source & target ResultSet in getMetadata Method
	public void setSourceResultSet(ResultSet sourceResultSet) {this.sourceResultSet=sourceResultSet;}
	public ResultSet getSourceResultSet() {return sourceResultSet;}
	public void setTargetResultSet(ResultSet targetResultSet) {this.targetResultSet=targetResultSet;}
	public ResultSet getTargetResultSet() {
		return targetResultSet;
	}

	public void setProjectName(String projectName) {
		this.projectName=projectName;
	}
	public String getProjectName() {
		return projectName;
	}
}
