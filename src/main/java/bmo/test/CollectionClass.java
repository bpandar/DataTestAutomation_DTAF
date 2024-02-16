package bmo.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionClass {

	public static void main(String[] args) {
		
			
		String []  srcInput = {"UAT1","2021-05-31","3420"};
		List<String> srcInputDetails = new ArrayList<>();
		
		System.out.println(srcInputDetails.toString());
		Collections.sort(srcInputDetails);
		System.out.println("After sort: "+srcInputDetails.toString());
				
		for (String input: srcInputDetails)
		{
			
		}
		
		
	}

}
