package com.bmo.csvcompare;

import com.bmo.mappingbean.MappingBean;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConsoleReport {

    public static StringBuilder getTableFormat(MappingBean mappingBeanObj ) {

        Map<String, List<Integer>> resultAsMap = mappingBeanObj.getResultMap();
        Map<String, String> statusMap = mappingBeanObj.getStatusAsMap();

        StringBuilder builder = new StringBuilder();

        String format = "| %-30s | %-6s | %-6s | %-7s | %-8s | %-6s | %-6s | %-8s | %-8s | %-8s |%n";

        builder.append(String.format("+--------------------------------+--------+--------+---------+----------+--------+--------+-----------+-----------+-----------+%n"));
        builder.append(String.format(format, "File Name ","SOURCE","TARGET","MATCHED","MISMATCH","SOURCE","TARGET","SOURCE   ","TARGET   ", "STATUS   "));
        builder.append(String.format(format, " ","COUNT ","COUNT "," COUNT ", " COUNT  ","MISSED","MISSED","DUPLICATE","DUPLICATE", "         "));
        builder.append(String.format("+--------------------------------+--------+--------+---------+----------+--------+--------+-----------+-----------+-----------+%n"));

        String[] arry = {"SOURCE","TARGET","MATCHED","MISMATCH","SOURCE","TARGET","SOURCE   ","TARGET   ", "STATUS   "};

        String leftAlignFormat = "| %-30s ";
        List<Integer> list= Arrays.asList(0, 0, 0, 0, 0, 0, 0 ,0);
        for (String key1 : statusMap.keySet()) {
            if(!resultAsMap.containsKey(key1)) {
                resultAsMap.put(key1, list);
            }
        }
        //System.out.println(statusMap.entrySet().toString());
        if (!resultAsMap.isEmpty()) {
            for (String key : resultAsMap.keySet()) {
                int i = 0;
                builder.append(String.format(leftAlignFormat, key));
                for (int value : resultAsMap.get(key)) {
                    String leftAlignFormat1 = "| %" + arry[i].length() + "d ";
                    builder.append(String.format(leftAlignFormat1, value));
                    i++;
                }
                builder.append(String.format("| %" + arry[i].length() + "s ", statusMap.get(key)));
                builder.append(String.format("|\n"));
            }
        }
        builder.append(String.format("+--------------------------------+--------+--------+---------+----------+--------+--------+-----------+-----------+-----------+%n"));

        return builder;
    }
}
