package com.bmo.osfitop50;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.bmo.osfitop50.OsfitTop50MappingBean;
import com.bmo.utils.PropertyUtil;

public class SQLQueryReader {

	private static Properties osfiTop50Prop = PropertyUtil.getPropertyFile("osfi_saccr_sql");
	private static String sqlFilePath = osfiTop50Prop.getProperty("sql_Dir");

	private static OsfitTop50MappingBean mappingBeanObj = new OsfitTop50MappingBean();;

	public static List<File> listf(String directoryName) {

		List<File> resultList = new ArrayList<File>();
		File directory = new File(directoryName);

		// get all the files from a directory
		File[] fList = directory.listFiles();
		resultList.addAll(Arrays.asList(fList));

		// String[] arrayList = new String[fList.length];
		for (File file : fList) {
			if (file.isFile()) {
				// System.out.println(file);

			} else if (file.isDirectory()) {

				// System.out.println(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("\\")
				// + 1));

				resultList.addAll(listf(file.getAbsolutePath()));
			}

		}
		return resultList;
	}

	public static OsfitTop50MappingBean readQueryFile() throws IOException {

		List<File> fileList = new ArrayList<File>();
		String queryString = new String();
		StringBuffer stringBuff = new StringBuffer();

		// saccrSQLProp = PropertyUtil.getPropertyFile("osfi_saccr_sql");

		mappingBeanObj.setResourcesPath(sqlFilePath);

		System.out.println("===>> " + sqlFilePath);
		File directory = new File(sqlFilePath);
		fileList = listf(directory.getAbsolutePath());

		Map<String, String> queryAsMap = new HashMap<String, String>();

		String sqlFileName = null;
		try {
			BufferedReader br;
			for (File filePath : fileList) {

				if (filePath.toString().contains(".sql")) {

					stringBuff.setLength(0);
					FileReader query = new FileReader(filePath.toString());
					br = new BufferedReader(query);
					while ((queryString = br.readLine()) != null) {
						stringBuff.append(queryString);
						stringBuff.append("\n ");
					}
					br.close();

					sqlFileName = filePath.getAbsolutePath().substring(sqlFilePath.length() + 1);
					mappingBeanObj.setAbsolutePath(filePath.getAbsolutePath());
					System.out.println(sqlFileName);// + "\n"+stringBuff);
					String keyValue = sqlFileName.replaceAll("\\\\", "_");

					String sqlQuery = stringBuff.toString();

					if (sqlQuery.contains("SA_CCR.")) {
						queryAsMap.put(keyValue, sqlQuery);
					}
				} else {
					// System.out.println(filePath.toString());
				}
			}

			mappingBeanObj.setQueryAsMap(queryAsMap);
			System.out.println("\nMap Size = " + queryAsMap.size());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return mappingBeanObj;
	}

	/*
	 * public static void main (String Arg[]) {
	 * 
	 * try { readQueryFile(); } catch (IOException e) { e.printStackTrace(); }
	 * 
	 * }
	 */

}
