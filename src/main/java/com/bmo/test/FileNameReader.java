package com.bmo.test;

import com.bmo.utils.PropertyUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class FileNameReader {
	
		private static Properties osfiTop50Prop = PropertyUtil.getPropertyFile("osfi_saccr_sql");
		private static String sqlFilePath = osfiTop50Prop.getProperty("Input_Dir");

		//private static OsfitTop50MappingBean mappingBeanObj = new OsfitTop50MappingBean();;

		public static List<File> listf(String directoryName) throws IOException {

			List<File> resultList = new ArrayList<File>();
			File directory = new File(directoryName);

			// get all the files from a directory
			File[] fList = directory.listFiles();
			//resultList.addAll(Arrays.asList(fList));

			// String[] arrayList = new String[fList.length];
			for (File file : fList) {
				if (file.isFile()) {				
					resultList.add(file);
				} else if (file.isDirectory()) {
					//System.out.println(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("\\") + 1));
					resultList.addAll(listf(file.getAbsolutePath()));
				}
			}

			return resultList;
		}

		public static void readQueryFile() throws IOException {

		try {
			Map<String, Long> fileMap = new HashMap<String, Long>();
			List<Long> fileSize = new ArrayList<Long>();
			Set<String> fileSet = new HashSet<String>();
			
			String queryString = new String();
			StringBuffer stringBuff = new StringBuffer();

			System.out.println("===>> " + sqlFilePath);
			File directory = new File(sqlFilePath);
			List<File> fileList = listf(directory.getAbsolutePath());
			
		
			for (File filePath : fileList) {			
				String fileName = filePath.getAbsolutePath().substring(filePath.getAbsolutePath().lastIndexOf("\\") + 1);
				long size = filePath.length() / 1024;
				fileSize.add(size);
				fileSet.add(fileName);	
				System.out.println(fileName +"\t\t\t"+size);
				if(!fileMap.isEmpty()) {
					fileMap.put(fileName, size);
				}else {
					fileMap.put(fileName, size);
				}			
			}
			
			//SortedSet<String> keys = new TreeSet<String>(fileMap.keySet());
			//Collections.sort(keys);
			System.out.println(fileMap.size());
			String format = "%2s\t%10d %n";
			for(String key: new TreeSet<String>(fileMap.keySet())) 
			{
				//System.out.format(format, key ,fileMap.get(key));
			}
			
			String sqlFileName = null;
			//System.out.println("\nMap Size = " + fileMap.size());

			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}

	public static void main(String[] args) throws IOException {
		
		readQueryFile();
	}

}
