/**
 *  Author      	: Bhoopathy P
 *  Date 			: Apr 13, 2018
 *  Class Name		: ParserResultBean.java
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

import java.util.List;
import java.util.Map;

public class ParserResultBean {

	private List<CSVParserBean> cvsParserList;
	private Map<String,String> headerNameMappings;
	
	public List<CSVParserBean> getCvsParserList() {
		return cvsParserList;
	}
	public void setCvsParserList(List<CSVParserBean> cvsParserList) {
		this.cvsParserList = cvsParserList;
	}
	public Map<String, String> getHeaderNameMappings() {
		return headerNameMappings;
	}
	public void setHeaderNameMappings(Map<String, String> headerNameMappings) {
		this.headerNameMappings = headerNameMappings;
	}
}
