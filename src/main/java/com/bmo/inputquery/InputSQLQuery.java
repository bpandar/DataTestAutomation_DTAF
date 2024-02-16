package com.bmo.inputquery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.TreeMap;


public class InputSQLQuery 
{
	private static final Logger log = LogManager.getLogger(InputSQLQuery.class);
	//private static Properties osfiQuery = PropertyUtil.getPropertyFile("resources");

	public static String[] getRegressionQuery(TreeMap<String, String> getInputValues) throws SQLException 
	{
		String queryString = new String();
		StringBuffer stringBuff = new StringBuffer();
		String[] splitQuery = null;
		try {
			
			String rootPath = System.getProperty("user.dir");
			System.out.println(rootPath +"=================");
			FileReader query = new FileReader(new File(rootPath+"\\Regression.sql"));
			// be sure to not have line starting with "--" or "/*" or any other
			// non aplhabetical character
			BufferedReader br = new BufferedReader(query);
			while ((queryString = br.readLine()) != null) {
				stringBuff.append(queryString);
			}
			br.close();
			// we ensure that there is no spaces before or after the request
			// string
			// here is our splitter ! We use ";" as a delimiter for each request
			// then we are sure to have well formed statements
			
			String busDateFormat = "TO_DATE('"+ getInputValues.get("sourceBusDate") +"')";
			
			splitQuery = stringBuff.toString().replace(":BUS_DT", busDateFormat).replace(":BENCHMARK_RUN_ID", getInputValues.get("BenchMark"))
					.replace(":RUN_ID", getInputValues.get("sourceRunId")).split(";");
			// String[] splitQuery = stringBuff.toString().split(";");
			for (int i = 0; i < splitQuery.length; i++) {
				if (!splitQuery[i].trim().equals("")) {
					System.out.println(">>" + splitQuery[i]);
				} else {
					log.debug("\n Check the Query.... Provide the space to end of the each line... \n" + splitQuery[i]);
					System.out.println("\n Check the Query.... Provide the space to end of the each line... \n");
				}
			}

		} catch (Exception e) {
			System.out.println("*** Error : " + e.toString());
			log.debug(e.toString());
			log.error(e.toString());
			System.out.println("*** ");
			System.out.println("*** Error : ");
			e.printStackTrace();
			System.out.println("################################################");
			System.out.println(stringBuff.toString());
			log.debug(stringBuff.toString());
		}
		return splitQuery;

	}


}

