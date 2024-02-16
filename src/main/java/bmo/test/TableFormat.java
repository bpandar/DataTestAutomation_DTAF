package bmo.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableFormat {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder();
		
		Integer[] a = {39, 39, 39, 0, 0, 0, 0, 0};
		Integer[] a1 = {69, 34, 564, 0, 0, 0, 0, 0};
		Integer[] a2 = {649, 743, 234, 0, 0, 0, 0, 0};
		
		List<Integer> list = new ArrayList<Integer>(Arrays.asList(a));
		List<Integer> list1 = new ArrayList<Integer>(Arrays.asList(a1));
		List<Integer> list2 = new ArrayList<Integer>(Arrays.asList(a2));
		
		Map<String, List<Integer>> resultAsMap1 = new HashMap<String, List<Integer>>();
		resultAsMap1.put("key           1", list);
		resultAsMap1.put("key           2", list1);
		resultAsMap1.put("key           3", list2);
		builder.append(String.format("+----------------------------+--------+--------+---------+----------+--------+--------+-----------+-----------+%n"));
		String format = "| %-26s | %-6s | %-6s | %-7s | %-8s | %-6s | %-6s | %-8s | %-8s |%n";
		builder.append(String.format(format, "File Name ","SOURCE","TARGET","MATCHED","MISMATCH","SOURCE","TARGET","SOURCE   ","TARGET   "));
		builder.append(String.format(format, " ","COUNT ","COUNT "," COUNT ", " COUNT  ","MISSED","MISSED","DUPLICATE","DUPLICATE"));
		builder.append(String.format("+----------------------------+--------+--------+---------+----------+--------+--------+-----------+-----------+%n"));
		
		String[] arry = {"SOURCE","TARGET","MATCHED","MISMATCH","SOURCE","TARGET","SOURCE   ","TARGET   "};
		
		String leftAlignFormat = "| %-26s ";
		for (String key : resultAsMap1.keySet()) {	
			int i =0;
			builder.append(String.format(leftAlignFormat, key));
				for(int value : resultAsMap1.get(key))	{
					String leftAlignFormat1 = "| %"+arry[i].length()+"d ";
					builder.append(String.format(leftAlignFormat1, value));
					i++;
				}	
				
				builder.append(String.format("|\n"));
		}
		
		builder.append(String.format("+----------------------------+--------+--------+---------+----------+--------+--------+-----------+-----------+%n"));
		
		System.out.println(builder.toString());
	}

}
