package com.bmo.osfitop50;

import java.util.Map;

public class OsfitTop50MappingBean {

	private String serverName;
	private String busDate;
	private String resourcePath;
	private String absolutePath;
	private Map<String, String> queryAsMap;
	
	public String getserverName() {
		return serverName;
	}
	public void setserverName(String serverName) {
		this.serverName = serverName;
	}
	public String getbusDate() {
		return busDate;
	}
	public void setBusinessDate(String busDate) {
		this.busDate = busDate;
	}	
	public String getResourcesPath() {
		return resourcePath;
	}
	public void setResourcesPath(String resourcePath) {
		this.resourcePath=resourcePath;
	}
	public String getAbsolutePath() {
		return absolutePath;
	}
	public void setAbsolutePath(String absolutePath) {
		this.absolutePath=absolutePath;
	}
	public Map<String, String> getQueryAsMap() {
		return queryAsMap;
	}
	public void setQueryAsMap(Map<String, String> queryAsMap2) {
		this.queryAsMap=queryAsMap2;	
	}
	
	
}
