package bmo.test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class FolderFileReader {

    public void readFiles(String filePath, String oldRunID, String newRunID) throws IOException {
        File folder = new File(filePath);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++)
        {
            if (listOfFiles[i].isFile()) {
                System.out.println("Old File " + listOfFiles[i].getName());

                if (listOfFiles[i].getName().toString().contains(oldRunID)) {
                    //System.out.println("\n Test -----------");
                    String oldFileName = listOfFiles[i].getName().toString();
                    String newFileName = oldFileName.replace(oldRunID, newRunID);

                    //System.out.println("\nNew File Name:  " + newFileName);
                    File file = new File(folder+"\\"+oldFileName);
                    //System.out.println("----------\n"+file);
                    file.renameTo(new File(folder+"\\"+newFileName));
                    System.out.println("New File " + newFileName+"\n");
                }
            } else if (listOfFiles[i].isDirectory()) {

                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
    }

    public static void main(String arg[]) throws IOException {
        FolderFileReader obj = new FolderFileReader();
        Scanner sc= new Scanner(System.in);
        System.out.println("Please enter file Path: ");
        String filePath = sc.nextLine();
        Scanner sc_old= new Scanner(System.in);
        System.out.println("Please Old RunID : ");
        String oldRunID = sc_old.nextLine();
        Scanner sc_new= new Scanner(System.in);
        System.out.println("Please new Run_ID: ");
        String newRunID = sc_new.nextLine();

        obj.readFiles(filePath, oldRunID, newRunID);
    }

}
