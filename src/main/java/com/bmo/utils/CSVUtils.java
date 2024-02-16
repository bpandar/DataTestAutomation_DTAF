package com.bmo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVUtils {
	private static final String REGEX = "\\bcat\\b";
	private static final String CSV_PARTTERN = "(^\\s*)(.*)";
	private static final String INPUT = "                    cat cat cat cattie cat       ";

	public static void main( String args[] ) {
		System.out.println(INPUT.length());
		Pattern ptn = Pattern.compile(CSV_PARTTERN);
		Matcher mtch = ptn.matcher(INPUT.trim());
		while(mtch.find()){
		String mtchString = mtch.group(2);
		System.out.println(mtchString);
		System.out.println(mtchString.length());
		}
	}
}
