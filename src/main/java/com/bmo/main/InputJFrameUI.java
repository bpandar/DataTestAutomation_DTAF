/**
 *  Author      	: Bhoopathy P
 *  Date 			: Apr 17, 2021
 *  Class Name		: InputJFrameUI.java
 *  
 *  Project			: BMO - Risk ODS
 *
 *	Changes			: Compare Table to Table in SACCR project	
 *  
 *  Modification history 
 *  Date			ChangeNo	Modified By							Description
 *  
 *  
 */

package com.bmo.main;


import com.bmo.csvcompare.ConsoleReport;
import com.bmo.database.DBConnection;
import com.bmo.mappingbean.MappingBean;
import com.bmo.tablecompare.CompareDataCall;
import com.bmo.tablecompare.CompareTables;
import com.bmo.utils.PropertyUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class InputJFrameUI extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private static final Logger log = LogManager.getLogger(InputJFrameUI.class);
	private static final Properties dbInfoProp = PropertyUtil.getPropertyFile("dbconnectioninfo");
	private static final Properties sqlQueryProp = PropertyUtil.getPropertyFile("Queries");
	private static final Properties configProp = PropertyUtil.getPropertyFile("config");
	
	private static JLabel sourceUserName;
	private static JTextField sourceUserInput;
	private static JLabel sourcePassword;
	private static JPasswordField sourcePwdInput;
	private static JLabel sourceRun_id, targetRun_id;
	private static JLabel sourceBus_Date,targetBus_Date , sourceTableInput, targetTableInput;
	private static JLabel sourceServer, targetServer;
	private static JLabel sourcePrimaryKeys,targetPrimaryKeys, logInfoArea;
	private static JCheckBox checkBoxDB;
	private static JCheckBox checkBoxCSV;
	private static JCheckBox checkBoxDBCSV;
	private static JComboBox<String> sourceServerNameCombo;
	private static JComboBox<String> targetServerNameCombo;
	private static JComboBox<String> sourceBusDateCombo, targetBusDateCombo;
	private static JComboBox<String> sourceRunIdCombo, targetRunIdCombo;
	private static JTextArea textArea;
	private static Font setFont = new Font(null);
	private static JButton doDBCompare, reset;
	private static JButton selectFile, runButton;
	public static JTextField sourcePrimaryKeyText,targetPrimaryKeyText, sourceTableName, targetTableName;
	public static JTextField selectMapping;
	public static TreeMap<String, String> getInputValues;
	private static JFrame mainFrame;
	
	private static String[] serverLists;
	private static String[]	serverName;
	private static MappingBean mappingBeanObj=null;
	private static JLabel projectName;
	private static String[] projectLists;
	private static JComboBox projectListsNameCombo;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public InputJFrameUI()
	{
		mappingBeanObj = new MappingBean();
					
		JFrame mainFrame = new JFrame(configProp.getProperty("TITLE"));
		Container container = getContentPane();
		
		getInputValues = new TreeMap<String, String>();

		projectName = new JLabel(configProp.getProperty("PROJECT"));
		projectName.setBounds(100, 30, 200, 25);
		mainFrame.add(projectName);

		projectLists = configProp.getProperty("PROJECT_LIST").split(",");
		//System.out.println(serverLists.toString());
		projectListsNameCombo = new JComboBox(projectLists);
		projectListsNameCombo.setPreferredSize(new Dimension(250, 30));
		projectListsNameCombo.setEditable(true);
		projectListsNameCombo.setBounds(260, 30, 130, 25);
		mainFrame.add(projectListsNameCombo);

		/*
		sourceUserName = new JLabel(configProp.getProperty("USERNAME"));
		sourceUserName.setBounds(450, 30, 200, 25);
		mainFrame.add(sourceUserName);
		sourceUserInput = new JTextField(2);
		//sourceUserInput.setText(dbInfoProp.getProperty("SACCR_ORACLE_USERNAME"));
		sourceUserInput.setBounds(530, 30, 100, 25);
		mainFrame.add(sourceUserInput);
		
		sourcePassword = new JLabel(configProp.getProperty("PASSWORD"));
		sourcePassword.setBounds(640, 30, 200, 25);
		mainFrame.add(sourcePassword);
		sourcePwdInput = new JPasswordField(2);  
		sourcePwdInput.setBounds(710, 30, 100, 25);
		//sourcePwdInput.setText(dbInfoProp.getProperty("SACCR_ORACLE_PASSWORD"));
		mainFrame.add(sourcePwdInput);
		*/

		projectListsNameCombo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					projectItemStateChanged(e);
				}
			}
		});

		//selecting the SourceServer name from drop-down
		sourceServer = new JLabel(configProp.getProperty("SOURCE_SERVER"));
		sourceServer.setBounds(100, 60, 200, 25);
		mainFrame.add(sourceServer);
		
		//serverLists = configProp.getProperty("SACCR_SERVER_LIST").split(",");
		sourceServerNameCombo = new JComboBox<String>();
		sourceServerNameCombo.setPreferredSize(new Dimension(250, 30));
		//sourceServerNameCombo.addItemListener(this);
		sourceServerNameCombo.setEditable(true);
		sourceServerNameCombo.setBounds(260, 60, 130, 25);
		mainFrame.add(sourceServerNameCombo);

		sourceServerNameCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                	sourceServerItemStateChanged(e);
                }
            }
        });
		
		//Target server name from drop-down
		targetServer = new JLabel(configProp.getProperty("TARGET_SERVER"));
		targetServer.setBounds(450, 60, 200, 25);
		mainFrame.add(targetServer);
				
		//System.out.println(serverLists.toString());
		targetServerNameCombo = new JComboBox<String>();
		targetServerNameCombo.setPreferredSize(new Dimension(250, 30));
		//targetServerNameCombo.addItemListener(this);
		targetServerNameCombo.setEditable(true);
		targetServerNameCombo.setBounds(570, 60, 130, 25);
		mainFrame.add(targetServerNameCombo);
		
		targetServerNameCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    targetServerItemStateChanged(e);
                }
            }

        });

			
		// user can enter the input here
		sourceBus_Date = new JLabel(configProp.getProperty("SRC_BUS_DATE"));
		mainFrame.add(sourceBus_Date);
		sourceBus_Date.setBounds(100, 90, 150, 25);

		sourceBusDateCombo = new JComboBox<String>();
		//sourceBusDateCombo.addItemListener(this);
		sourceBusDateCombo.setEditable(false);
		sourceBusDateCombo.setBounds(260, 90, 130, 25);
		mainFrame.add(sourceBusDateCombo);
		
		sourceBusDateCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                	sourceServerItemStateChanged(e);
                }
            }
        });
		
		// Target Business date
		targetBus_Date = new JLabel(configProp.getProperty("TAR_BUS_DATE"));
		mainFrame.add(targetBus_Date);
		targetBus_Date.setBounds(450, 90, 150, 25);

		targetBusDateCombo = new JComboBox<String>();
		//targetBusDateCombo.addItemListener(this);
		targetBusDateCombo.setEditable(false);
		targetBusDateCombo.setBounds(570, 90, 130, 25);
		mainFrame.add(targetBusDateCombo);
		
		targetBusDateCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                	targetServerItemStateChanged(e);
                }
            }
        });
		
		sourceRun_id = new JLabel(configProp.getProperty("SRC_RUNID"));
		sourceRun_id.setBounds(100, 120, 200, 25);
		mainFrame.add(sourceRun_id);
		
		sourceRunIdCombo = new JComboBox<String>();
		//sourceRunIdCombo.addItemListener(this);
		sourceRunIdCombo.setEditable(false);
		sourceRunIdCombo.setBounds(260, 120, 130, 25);
		mainFrame.add(sourceRunIdCombo);
		
		sourceRunIdCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                	sourceServerItemStateChanged(e);
                }
            }
        });
		
		//Target Run Id 
		targetRun_id = new JLabel(configProp.getProperty("TAR_RUNID"));
		targetRun_id.setBounds(450, 120, 200, 25);
		mainFrame.add(targetRun_id);
		
		targetRunIdCombo = new JComboBox<String>();
		//targetRunIdCombo.addItemListener(this);
		targetRunIdCombo.setEditable(false);
		targetRunIdCombo.setBounds(570, 120, 130, 25);
		mainFrame.add(targetRunIdCombo);
		
		targetRunIdCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                	targetServerItemStateChanged(e);
                }
            }
        });
							
		//Select table Name
		sourceTableInput = new JLabel(configProp.getProperty("SRC_TABLE"));
		sourceTableInput.setBounds(100, 150, 200, 25);
		mainFrame.add(sourceTableInput);

		sourceTableName = new JTextField(100); // accepts up to 100 characters
		sourceTableName.setBounds(260, 150, 175, 25);
		sourceTableName.setToolTipText(configProp.getProperty("TOOL_TIP"));  
		mainFrame.add(sourceTableName);	
		
		//Select table Name
		targetTableInput = new JLabel(configProp.getProperty("TAR_TABLE"));
		targetTableInput.setBounds(450, 150, 200, 25);
		mainFrame.add(targetTableInput);

		targetTableName = new JTextField(100); // accepts up to 100 characters
		targetTableName.setBounds(570, 150, 175, 25);
		targetTableName.setToolTipText(configProp.getProperty("TOOL_TIP"));  
		mainFrame.add(targetTableName);
		
		
		//Select Primary Keys to compare 
		sourcePrimaryKeys = new JLabel(configProp.getProperty("SRC_PRIMKEY"));
		sourcePrimaryKeys.setBounds(100, 180, 150, 25);
		mainFrame.add(sourcePrimaryKeys);
				
		sourcePrimaryKeyText = new JTextField(100); // accepts up to 100 characters
		sourcePrimaryKeyText.setBounds(260, 180, 485, 25);
		mainFrame.add(sourcePrimaryKeyText);
		
		//Select Primary Keys to compare 
		targetPrimaryKeys = new JLabel(configProp.getProperty("TAR_PRIMKEY"));
		targetPrimaryKeys.setBounds(100, 210, 150, 25);
		mainFrame.add(targetPrimaryKeys);
				
		targetPrimaryKeyText = new JTextField(100); // accepts up to 100 characters
		targetPrimaryKeyText.setBounds(260, 210, 485, 25);
		mainFrame.add(targetPrimaryKeyText);				
	
	
		doDBCompare = new JButton(configProp.getProperty("RUN_BUTTON"));
		doDBCompare.setBounds(260, 245, 60, 25);
		mainFrame.add(doDBCompare);
		
		doDBCompare.addActionListener(this);
		
		reset = new JButton(configProp.getProperty("RESET_BUTTON"));
		reset.setBounds(340, 245, 67, 25);
		mainFrame.add(reset);
		
		reset.addActionListener(this);
		
			
		//Radio Button box for selecting Config file
		checkBoxCSV = new JCheckBox(configProp.getProperty("CSV_TO_CSV"));  
		checkBoxCSV.setBounds(430, 245, 100, 25);  
        checkBoxCSV.setHorizontalAlignment(JCheckBox.LEFT);
        mainFrame.add(checkBoxCSV);
        
        checkBoxDB = new JCheckBox(configProp.getProperty("DB_TO_DB"));  
        checkBoxDB.setBounds(530, 245, 90, 25);  
        checkBoxDB.setHorizontalAlignment(JCheckBox.LEFT);
        mainFrame.add(checkBoxDB);
        
        checkBoxDBCSV = new JCheckBox(configProp.getProperty("DB_TO_CSV"));  
        checkBoxDBCSV.setBounds(620, 245, 90, 25);  
        checkBoxDBCSV.setHorizontalAlignment(JCheckBox.LEFT);
        mainFrame.add(checkBoxDBCSV);
        
        JLabel configFile = new JLabel(configProp.getProperty("SELECT_MAPPING"));
		configFile.setBounds(100, 280, 360, 25);
		mainFrame.add(configFile);
		
		checkBoxCSV.addItemListener(new ItemListener() {
        	@Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                	selectMapping.setEnabled(true);
                	selectFile.setEnabled(true);
                	runButton.setEnabled(true);
                	doDBCompare.setEnabled(false);
                	checkBoxDB.setEnabled(false);
                	checkBoxDBCSV.setEnabled(false);
                	mappingBeanObj.setCheckBoxPhase(configProp.getProperty("CSV_TO_CSV"));
                }else if(e.getStateChange() == ItemEvent.DESELECTED){
                	selectMapping.setEnabled(false);
                	selectFile.setEnabled(false);
                	runButton.setEnabled(false);
                	doDBCompare.setEnabled(true);
                	selectMapping.setText("");
                	if (checkBoxDB.isSelected()||checkBoxDBCSV.isSelected()) {
                	checkBoxCSV.setEnabled(false);
                	}else {
                    	checkBoxDB.setEnabled(true);
                    	checkBoxDBCSV.setEnabled(true);
                	}
                }
            }
        });
		
		
		checkBoxDB.addItemListener(new ItemListener() {
        	@Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                	selectMapping.setEnabled(true);
                	selectFile.setEnabled(true);
                	runButton.setEnabled(true);
                	doDBCompare.setEnabled(false);
                	checkBoxCSV.setEnabled(false);
                	checkBoxDBCSV.setEnabled(false);
                	mappingBeanObj.setCheckBoxPhase(configProp.getProperty("DB_TO_DB"));
                }else if(e.getStateChange() == ItemEvent.DESELECTED){
                	selectMapping.setEnabled(false);
                	selectFile.setEnabled(false);
                	runButton.setEnabled(false);
                	doDBCompare.setEnabled(true);
                	selectMapping.setText("");
                	if (checkBoxCSV.isSelected()||checkBoxDBCSV.isSelected()) {
                    	checkBoxDB.setEnabled(false);
                    	}else {
                    		checkBoxCSV.setEnabled(true);
                        	checkBoxDBCSV.setEnabled(true);
                    	}
                }
            }
        });
				
		checkBoxDBCSV.addItemListener(new ItemListener() {
        	@Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                	selectMapping.setEnabled(true);
                	selectFile.setEnabled(true);
                	runButton.setEnabled(true);
                	doDBCompare.setEnabled(false);
                	checkBoxCSV.setEnabled(false);
                	checkBoxDB.setEnabled(false);
                	mappingBeanObj.setCheckBoxPhase(configProp.getProperty("DB_TO_CSV"));               	
                }else if(e.getStateChange() == ItemEvent.DESELECTED){
                	selectMapping.setEnabled(false);
                	selectFile.setEnabled(false);
                	runButton.setEnabled(false);
                	doDBCompare.setEnabled(true);
                	selectMapping.setText("");
                	if (checkBoxCSV.isSelected()||checkBoxDB.isSelected()) {
                    	checkBoxDBCSV.setEnabled(false);
                    	}else {
                    		checkBoxCSV.setEnabled(true);
                        	checkBoxDB.setEnabled(true);
                    	}
                }
            }
        });
		
                        
        selectMapping = new JTextField(100); // accepts up to 100 characters
        selectMapping.setBounds(260, 280, 360, 25);
        selectMapping.setEnabled(false);
		mainFrame.add(selectMapping);
		
		selectFile = new JButton(configProp.getProperty("SELECT_BUTTON"));
		selectFile.setBounds(650, 280, 75, 25);
		selectFile.setEnabled(false);
		mainFrame.add(selectFile);	
				
		selectFile.addActionListener(this);
		//System.out.println("Present Project Directory : "+ System.getProperty("user.dir"));

		runButton = new JButton(configProp.getProperty("RUN_BUTTON"));
		runButton.setBounds(750, 280, 60, 25);
		runButton.setEnabled(false);
		mainFrame.add(runButton);
			
		runButton.addActionListener(this);
				
		//Log Information Text Area
		logInfoArea = new JLabel(configProp.getProperty("LOG"));
		logInfoArea.setBounds(100, 310, 150, 40);
		mainFrame.add(logInfoArea);
		
		textArea = new JTextArea(configProp.getProperty("TEXT_AREA"));
		mainFrame.getContentPane().add(textArea, BorderLayout.CENTER);
		mainFrame.add(textArea);
       //textArea.setLineWrap(true);
       //textArea.append(log.toString());
        textArea.setFont(setFont.deriveFont(12f));
		
        JScrollPane scroller = new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 
		scroller.setBounds(100, 340, 750, 320);
		mainFrame.add(scroller);
		 
	
		mainFrame.setSize(1000, 800); 
		//mainFrame.setTitle("SACCR Automation FrameWork");
		mainFrame.setBackground(Color.BLUE);
		mainFrame.setFont(setFont.deriveFont(12f));
		mainFrame.setLayout(null);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
		
	}
	

	public static void main(String[] args) throws SQLException 
	{ 
		try {
			//LogUtil.initiateLogger();
	
			@SuppressWarnings("unused")
			InputJFrameUI swingUI = new InputJFrameUI();
		} catch (Exception e) {
			log.error("ClassNotFoundException", e);
		}		
	}

	public static void projectItemStateChanged(ItemEvent e){
		if(e.getSource() == projectListsNameCombo ) {
			String projectName = projectListsNameCombo.getSelectedItem().toString();
//			if (!projectName.isEmpty() && !projectName.equalsIgnoreCase("select")){
//				sourceUserInput.setText(dbInfoProp.getProperty(projectName+"_ORACLE_USERNAME"));
//				sourcePwdInput.setText(dbInfoProp.getProperty(projectName+"_ORACLE_PASSWORD"));
//			}
//			/*if (projectName.equals("SA-CCR")) {
//				sourceUserInput.setText(dbInfoProp.getProperty("SACCR_ORACLE_USERNAME"));
//				sourcePwdInput.setText(dbInfoProp.getProperty("SACCR_ORACLE_PASSWORD"));
//			} else if (projectName.equals("SA-CVA")) {
//				sourceUserInput.setText(dbInfoProp.getProperty("SACVA_ORACLE_USERNAME"));
//				sourcePwdInput.setText(dbInfoProp.getProperty("SACVA_ORACLE_PASSWORD"));
//			} */
//			else {
//				sourceUserInput.setText("");
//				sourcePwdInput.setText("");
//			}

			if (projectListsNameCombo.getSelectedItem() != null && e.getStateChange() == 1
					&& !projectName.contentEquals("Select")) {
				sourceServerNameCombo.setEnabled(true);
				sourceServerNameCombo.removeAllItems();
				targetServerNameCombo.setEnabled(true);
				targetServerNameCombo.removeAllItems();

				if (!projectName.isEmpty() && !projectName.equalsIgnoreCase("select"))
				{
					serverLists = configProp.getProperty(projectName+"_SERVER_LIST").split(",");
					for(int i =0 ; i< serverLists.length; i++ ){
						sourceServerNameCombo.addItem(serverLists[i]);
						targetServerNameCombo.addItem(serverLists[i]);
					}
				}else {
					sourceServerNameCombo.addItem("");
					targetServerNameCombo.addItem("");
				}
			}else {
				sourceServerNameCombo.setEnabled(false);
				targetServerNameCombo.setEnabled(false);
			}
		}
	}

	public static void sourceServerItemStateChanged(ItemEvent ie)  
	{
		String projectName = projectListsNameCombo.getSelectedItem().toString();
//		String sourceUserName = sourceUserInput.getText();
//		String sourcePassWord = sourcePwdInput.getText();
		System.out.println("Selected Project : "+projectName);
		getInputValues.put("project", projectName);
//		getInputValues.put("username", sourceUserName);
//		getInputValues.put("password", sourcePassWord);
		
		// Fetch Source table inputs 
		 if (ie.getSource() == sourceServerNameCombo) 
		 {	
			getInputValues.remove("Selected");
			getInputValues.put("Selected", "Source");		
			String sourceServer = null;
			sourceServer = sourceServerNameCombo.getSelectedItem().toString();
			getInputValues.put("sourceServer", sourceServer);	
			System.out.println("\n*************\n"+sourceServer);
							
				if (sourceServerNameCombo.getSelectedItem() != null && ie.getStateChange()==1 && !sourceServer.contentEquals("Select"))
				{					
					sourceBusDateCombo.setEnabled(true);
					sourceBusDateCombo.removeAllItems();				
					ArrayList<String> busDateArrayList = new ArrayList<>();
					try{

		           	 	String sqlBusDateQuery = sqlQueryProp.getProperty("SELECT_BUSDATE");
		           	 	
		           	 	Connection dbConnection = DBConnection.dataBase_Connection(getInputValues);
		           	 	PreparedStatement preparedStmt = dbConnection.prepareStatement(sqlBusDateQuery, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			 			preparedStmt.setFetchSize(1000);
			 			
		           	 	ResultSet results= preparedStmt.executeQuery();           	 	          	 	
		           	 	
		           	 	String busDate = null;
		                   while(results.next())
		                    {
		                	   busDate = results.getString("BUS_DT"); 
		                	   busDateArrayList.add(busDate);
		                    }                  
		                   int listSize = busDateArrayList.size();
		                   String[] busDateArry = new String[listSize];
		                   for (int i = 0; i < listSize; i++) 
		                   {
		                	   busDateArry[i] = busDateArrayList.get(i).substring(0, 10);
		                	   sourceBusDateCombo.addItem(busDateArrayList.get(i).substring(0, 10));
		                	   //sourceBusDateCombo.addItem(busDateArry[i]);
		                   }               
		           	 }catch (ClassNotFoundException | SQLException | IOException e) 
						{
		 					System.out.println(e.getMessage());
		 					e.printStackTrace();
		           	 	}				
				}else if (sourceServer.equalsIgnoreCase("Select")){
					sourceBusDateCombo.removeAllItems();
					sourceBusDateCombo.setEnabled(false);
					sourceRunIdCombo.removeAllItems();
					sourceRunIdCombo.setEnabled(false);
				} else {
					System.out.println("Please select Server Name!");
					JOptionPane.showMessageDialog(null, "Please select Server Name!");;
				}
		 }
		 
		 if (ie.getSource() == sourceBusDateCombo && ie.getStateChange()==1) 
		 	{
				if (sourceBusDateCombo.getSelectedItem() == null) 
				{
					sourceRunIdCombo.setEnabled(false);
				}
				else if (sourceBusDateCombo.getSelectedItem() != null && ie.getStateChange()==1 )
				{					
					String busDateInput = sourceBusDateCombo.getSelectedItem().toString();
					getInputValues.put("sourceBusDate", busDateInput);
					sourceRunIdCombo.setEnabled(true);
					sourceRunIdCombo.removeAllItems();
					
					getInputValues.remove("Selected");
					getInputValues.put("Selected", "Source");
						try{
				       	 	String sqlRunId = sqlQueryProp.getProperty("SELECT_RUNID").replace(":BUS_DT", busDateInput);
				       	 	     	 	
							Connection dbConnection = DBConnection.dataBase_Connection(getInputValues);							
							PreparedStatement  preStatement = dbConnection.prepareStatement(sqlRunId, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				       	 	ResultSet results = preStatement.executeQuery();
				       	 	
				               while(results.next())
				                {
				            	   String runId = results.getString("RUN_ID"); 
				            	   sourceRunIdCombo.addItem(runId);
				                }
	                   
				       	 }catch (ClassNotFoundException | SQLException | IOException e) 
							{
									e.printStackTrace();
									textArea.setText("RC_RUN_CNTRL" + e.toString());
									
				       	 	}					
						} 
			}
		 	
		 	if (ie.getSource() == sourceRunIdCombo && ie.getStateChange()==1 ) 
			{
				if (sourceRunIdCombo.getSelectedItem() == null) 
				{
					sourceRunIdCombo.setEnabled(false);
				} 
				else if (sourceRunIdCombo.getSelectedItem() != null && ie.getStateChange()==1 )
				{	
					System.out.println(sourceRunIdCombo.getSelectedItem().toString());
				}
				getInputValues.put("sourceRunID", sourceRunIdCombo.getSelectedItem().toString());
			}
	}


	// Fetch Target table inputs 	 
		 
	public static void targetServerItemStateChanged(ItemEvent ie) 
	{
//		String sourceUserName = sourceUserInput.getText();
//		String sourcePassWord = sourcePwdInput.getText();
//		getInputValues.put("username", sourceUserName);
//		getInputValues.put("password", sourcePassWord);
		
		String targetServer = targetServerNameCombo.getSelectedItem().toString();
		
		if (ie.getSource() == targetServerNameCombo ) 
		{
			getInputValues.remove("Selected");
			getInputValues.put("Selected", "Traget");
			 
			if (targetServerNameCombo.getSelectedItem() != null && ie.getStateChange()==1 && !targetServer.contentEquals("Select"))
			{					
				getInputValues.put("targetServer", targetServer);
				targetServerNameCombo.setEnabled(true);
				targetBusDateCombo.removeAllItems();
				//System.out.println(getInputValues);
				//String[] targetBusDateArray = getBusinessDate(getInputValues);
				ArrayList<String> busDateArrayList = new ArrayList<>();
				try{
	           	 	String sqlBusDateQuery = sqlQueryProp.getProperty("SELECT_BUSDATE") ;        	 	
	           	 	
	           	 	Connection dbConnection = DBConnection.dataBase_Connection(getInputValues);         
	           	 	PreparedStatement preparedStmt = dbConnection.prepareStatement(sqlBusDateQuery, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		 			preparedStmt.setFetchSize(1000);
		 			
		 			ResultSet results = preparedStmt.executeQuery();	
           	 	          	 	
	           	 	String busDate = null;
	                   while(results.next())
	                    {
	                	   busDate = results.getString("BUS_DT"); 
	                	   busDateArrayList.add(busDate);
	                    }                  
	                   int listSize = busDateArrayList.size();
	                   String[] busDateArry = new String[listSize];
	                   for (int i = 0; i < listSize; i++) 
	                   {
	                	   busDateArry[i] = busDateArrayList.get(i).substring(0, 10);
	                	   targetBusDateCombo.addItem(busDateArrayList.get(i).substring(0, 10));
	                   }               
	           	 }catch (ClassNotFoundException | SQLException | IOException e) 
					{
	 					System.out.println(e.getMessage());
	 					e.printStackTrace();
	           	 	}					
			}else if (targetServer.equalsIgnoreCase("Select")){
				targetBusDateCombo.removeAllItems();
				targetBusDateCombo.setEnabled(false);
				targetRunIdCombo.removeAllItems();
				targetRunIdCombo.setEnabled(false);
			} else {
				System.out.println("Please select Server Name!");
				JOptionPane.showMessageDialog(null, "Please select Server Name!");;
			}
			 
		}
			
		 if (ie.getSource() == targetBusDateCombo && ie.getStateChange()==1) 
		 	{
			 	getInputValues.remove("Selected");
				getInputValues.put("Selected", "Traget");
				if (targetBusDateCombo.getSelectedItem() == null) 
				{
					targetBusDateCombo.setEnabled(false);
				}
				else if (targetBusDateCombo.getSelectedItem() != null && ie.getStateChange()==1 )
				{					
					String targetBusDateInput = targetBusDateCombo.getSelectedItem().toString();
					getInputValues.put("targetBusDate", targetBusDateInput);
					targetBusDateCombo.setEnabled(true);
					targetRunIdCombo.removeAllItems();
						try{
							String sqlQuery = sqlQueryProp.getProperty("SELECT_RUNID").replace(":BUS_DT", targetBusDateInput);
				       	 	
							Connection dbConnection = DBConnection.dataBase_Connection(getInputValues);							
							PreparedStatement  preparedStmt = dbConnection.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				       	 	ResultSet results = preparedStmt.executeQuery(sqlQuery);	 	       	 	
				               
				       	 	while(results.next())
				                {
				            	   String runId = results.getString("RUN_ID"); 
				            	   targetRunIdCombo.addItem(runId);
				                }
	                   
				       	 }catch (ClassNotFoundException | SQLException | IOException e1) 
							{
								e1.printStackTrace();
								textArea.setText(e1.toString());	
									
				       	 	}					
						} 
			}
		 	
		 	if (ie.getSource() == targetRunIdCombo && ie.getStateChange()==1) 
			{
		 		targetRunIdCombo.setEnabled(true);
				if (targetRunIdCombo.getSelectedItem() == null) 
				{
					targetRunIdCombo.setEnabled(false);
				} 
				else if (targetRunIdCombo.getSelectedItem() != null && ie.getStateChange()==1 )
				{	
					System.out.println(targetRunIdCombo.getSelectedItem().toString());
					getInputValues.put("targetRunID", targetRunIdCombo.getSelectedItem().toString());
				}
			}
		 
	}
		
		public static void doCompareOperation(MappingBean mappingBean) throws ClassNotFoundException, SQLException, IOException
		{
			MappingBean mappingBeanObj = null;
			mappingBeanObj=mappingBean;
			String validFlag = null;
			//Source Details
			System.out.println("\n ........Source table Details..........\n");
			System.out.println("Source server   : " + mappingBeanObj.getSourceServer());
			System.out.println("Source Bus Date : " + mappingBeanObj.getSourceBusDate());
			System.out.println("Source Run ID   : " + mappingBeanObj.getSourceRunID());
			System.out.println("Source Table 	: " + mappingBeanObj.getSourceTableName());
			System.out.println("Source PrimaryKeys: " + mappingBeanObj.getSourcePrimaryKey());
			
			textArea.append(" \n ........Source table Details..........\n");
			log.debug("=>>> Source Server Name:" + mappingBeanObj.getSourceServer() 
					+ "\n=>>> Source Bus Date: " + mappingBeanObj.getSourceBusDate()
					  + "\n=>>> Source Run Id: " + mappingBeanObj.getSourceRunID() 
				  + "\n=>>> Source Table Name: " + mappingBeanObj.getSourceTableName()
		     		+"=>>> Source PrimaryKeys: " + mappingBeanObj.getSourcePrimaryKey()
		   );
			
			textArea.append(">> Source Server Name: " + mappingBeanObj.getSourceServer() 
						   + "\n>> Source Bus Date: " + mappingBeanObj.getSourceBusDate()
						   	  +"\n>> Source Run Id: " + mappingBeanObj.getSourceRunID() 
						 + "\n>> Source Table Name: " + mappingBeanObj.getSourceTableName()
						+ "\n>> Source PrimaryKeys: " + mappingBeanObj.getSourcePrimaryKey()
			);
			
			//Target Details
			System.out.println("\n ........Target table Details..........\n");
			System.out.println("Target server   : " + mappingBeanObj.getTargetServer());
			System.out.println("Target Bus Date : " + mappingBeanObj.getTargetBusDate());
			System.out.println("Target Run ID   : " + mappingBeanObj.getTargetRunID());
			System.out.println("Target Table  	: " + mappingBeanObj.getTargetTableName());
			System.out.println("Target PrimaryKeys: " + mappingBeanObj.getTargetPrimaryKey());
						
			textArea.append(" \n ........Target table Details..........\n");
			log.debug("=>>> Target Server Name	: " + mappingBeanObj.getTargetServer() 
					+ "\n=>>> Target BusDate 	: " + mappingBeanObj.getTargetBusDate()
						+"=>>> Target Run Id 	: " + mappingBeanObj.getTargetRunID() 
				  + "\n=>>> Target Table Name 	: " + mappingBeanObj.getTargetTableName()
				  	  + "\n>> Target PrimaryKeys: " + mappingBeanObj.getTargetPrimaryKey()
			);
						
			textArea.append(">> Target Server Name: " + mappingBeanObj.getTargetServer()  
						   + "\n>> Target BusDate : " + mappingBeanObj.getTargetBusDate()
							+ "\n>> Target Run Id : " + mappingBeanObj.getTargetRunID() 
						+ "\n>> Target Table Name : " + mappingBeanObj.getTargetTableName()
						+ "\n>> Target PrimaryKeys: " + mappingBeanObj.getTargetPrimaryKey()
			);
			
				
			try
			{
				 mappingBeanObj = CompareTables.uiTableCompare(mappingBeanObj);
				validFlag = mappingBeanObj.getOperationMsg();
				//mappingBeanObj.getOperationMsg2();
					
				if (validFlag.equalsIgnoreCase("Done"))
				{
					textArea.append("\n"+mappingBeanObj.getSummaryReport().toString());
					textArea.append("\n..........Table compare has been completed..........\n\n");
					
					System.out.println("\n\n*********************** \n" +mappingBeanObj.getSummaryReport().toString());
					System.out.println("\n..........Table compare has been completed..........\n\n");
								
					StringBuilder results = ConsoleReport.getTableFormat(mappingBeanObj);
					textArea.append(results.toString());
					System.out.println("\n"+ results.toString());
					
					JOptionPane.showMessageDialog(null, " Query Executed Successfully....!");

				}else
				{
					StringBuilder results = ConsoleReport.getTableFormat(mappingBeanObj);
					textArea.append(results.toString());
					System.out.println("\n"+ results.toString());
					//textArea.append("\n "+ validFlag);
					JOptionPane.showMessageDialog(null, "Error while Executing the Query....!");
				}
			}catch(Exception e){ 
				
				e.printStackTrace();
				log.debug(e.toString());
				log.error(e.toString());
				//textArea.append(e.toString()+"\n "+ validFlag);
				StringBuilder results = ConsoleReport.getTableFormat(mappingBeanObj);
				textArea.append(results.toString());
				System.out.println("\n"+ results.toString());
				JOptionPane.showMessageDialog(null, "Error while Executing the Query....!");
			}
			System.out.println("\n"+ "*********The End***********");
			//MappingBean mappingBeanObj1= new MappingBean();
			mappingBeanObj=null;
		}


	public void actionPerformed(ActionEvent arg) {
						
			if(arg.getSource()== doDBCompare ) { 
				
				mappingBeanObj.setPhase("DB TO DB");
				mappingBeanObj.setSourceUserName(getInputValues.get("source_username"));
				mappingBeanObj.setSourcePassword(getInputValues.get("source_password"));
				mappingBeanObj.setTargetUserName(getInputValues.get("target_username"));
				mappingBeanObj.setTargetPassword(getInputValues.get("target_password"));
				
				mappingBeanObj.setSourceServer(getInputValues.get("sourceServer"));
				mappingBeanObj.setSourceBusDate(getInputValues.get("sourceBusDate"));
				mappingBeanObj.setSourceRunID(getInputValues.get("sourceRunID"));
				
				mappingBeanObj.setTargetServer(getInputValues.get("targetServer"));
				mappingBeanObj.setTargetBusDate(getInputValues.get("targetBusDate"));
				mappingBeanObj.setTargetRunID(getInputValues.get("targetRunID"));
				
				String sourcePrimaryKey = sourcePrimaryKeyText.getText().trim().toUpperCase(); 
				String targetPrimaryKey = targetPrimaryKeyText.getText().trim().toUpperCase();
				
				String sourceTable = sourceTableName.getText().trim().toUpperCase();
				String targetTable = targetTableName.getText().trim().toUpperCase();
				
			try {														
					if(!sourcePrimaryKey.isEmpty() && !targetPrimaryKey.isEmpty()
							&& !sourceTable.isEmpty() && !targetTable.isEmpty() )
					{	
											
						getInputValues.put("sourceTable", sourceTable); 
						getInputValues.put("targetTable", targetTable);
						
						mappingBeanObj.setSourceTableName(sourceTable);
						mappingBeanObj.setTargetTableName(targetTable);
						
						getInputValues.put("sourceKeys", sourcePrimaryKey); 
						getInputValues.put("targetKeys", targetPrimaryKey);									 

						
						System.out.println(sourcePrimaryKey.replaceAll(" ", "") + targetPrimaryKey.replaceAll(" ", ""));
						mappingBeanObj.setSourcePrimaryKey(sourcePrimaryKey.replaceAll(" ", ""));
						mappingBeanObj.setTargetPrimaryKey(targetPrimaryKey.replaceAll(" ", ""));
						
						Set <String> srcColumnSet = new HashSet<String>();
								// (Arrays.asList(configProp.getProperty("SOURCE_COLUMNS_SKIP")));
						mappingBeanObj.setSourceSkipColumn(srcColumnSet);
						Set <String> tarColumnSet = new HashSet<String>();
								// (Arrays.asList(configProp.getProperty("TARGET_COLUMNS_SKIP")));
						mappingBeanObj.setTargetSkipColumn(tarColumnSet);
						
						textArea.setText("");
						
						doCompareOperation(mappingBeanObj);
						//mainFrame.dispose();
						
					}
					else 
					{
						JOptionPane.showMessageDialog(mainFrame, "Warning: Check Table Name and PrimaryKeys");
						
					}
									
				} catch (ClassNotFoundException | SQLException | IOException e) 
					{
						e.printStackTrace();
						log.error("Comparison Failed", e);
						textArea.append(e.toString());
						System.out.println("....Comparison Failed Due.....\n " + e.toString());
					}
				
			}
			
			if(arg.getSource()== selectFile )
			{ 
				JFileChooser fileChooser = new JFileChooser();
				String proj_path = System.getProperty("user.dir") ;
	    		fileChooser.setCurrentDirectory(new File(proj_path + "\\" + configProp.getProperty("RESOURCE_DIR")) );
	    		int result = fileChooser.showOpenDialog(this);
	    		if (result == JFileChooser.APPROVE_OPTION) {
	    		    File selectedFile = fileChooser.getSelectedFile();   		    
	    		    mappingBeanObj.setMappingFilepath(selectedFile.getPath());
	    		    selectMapping.setText(mappingBeanObj.getMappingFilepath());
	    		    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
	    			}
			}
			
			//Select mapping document call
		if (arg.getSource() == runButton) {

			if (!selectMapping.getText().isEmpty()) {
				StringBuilder results = null;
				String projectName = projectListsNameCombo.getSelectedItem().toString();
				mappingBeanObj.setProjectName(projectName);
				try {
					textArea.setText("");
					MappingBean returnMappingBean = null;
					returnMappingBean = CompareDataCall.getDataCompareCall(mappingBeanObj);
					String validFlag = returnMappingBean.getOperationMsg();

					if (validFlag.equalsIgnoreCase("Done")) {
						results = ConsoleReport.getTableFormat(returnMappingBean);
						textArea.append("\n" + returnMappingBean.getSummaryReport().toString());
						textArea.append("\n..........Table compare has been completed..........\n\n");
						
						//System.out.println("\n\n*********************** \n" + returnMappingBean.getSummaryReport().toString());
						System.out.println("\n..........Table compare has been completed..........\n\n");

						textArea.append(results.toString());
						System.out.println("\n"+ results.toString());
						
						JOptionPane.showMessageDialog(null, "Comparison completed....!");

					} else {
						JOptionPane.showMessageDialog(null, "Error while Executing the Query....!");
						textArea.append(String.valueOf(results));
						System.out.println("\n"+ results);
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.debug(e.toString());
					log.error("Comparison failed..."+e.toString());
					
					JOptionPane.showMessageDialog(null, "Comparison Failed/Check Mapping File..\n"+e.toString());
				}
				
			} else {
				JOptionPane.showMessageDialog(null, "mapping File is not selected.! select the Mapping File.");
			}
		}
		
		
		if(arg.getSource()== reset )
		{ 
			sourceTableName.setText("");
			targetTableName.setText("");
			sourcePrimaryKeyText.setText("");
			targetPrimaryKeyText.setText("");
			sourceServerNameCombo.setSelectedIndex(0);
			targetServerNameCombo.setSelectedIndex(0);
			sourceBusDateCombo.removeAllItems();
			targetBusDateCombo.removeAllItems();
			sourceRunIdCombo.removeAllItems();
			targetRunIdCombo.removeAllItems();
		}
	}
	public static PrintWriter getApplicationConsole() {
		return null;
	}

}