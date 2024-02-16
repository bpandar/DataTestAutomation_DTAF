package bmo.test;


import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.util.Properties;


import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class JFrameUI extends JFrame implements ActionListener
{
	Container container = new Container();
	
	JLabel sourceUserName = new JLabel("Enter the user Name: ");
	JTextField sourceUserInput = new JTextField(2);  
	
	JLabel sourcePassword = new JLabel("Enter the Password: ");
	JPasswordField sourcePwdInput = new JPasswordField(2);  
	
	//selecting the SourceServer name from drop-down
	JLabel sourceServer = new JLabel("Select Source Server : ");
	String[] serverLists = new String[] {"Select","DEV1-TDCCRO01","DEV2-TDCCRO02","SIT1-TSCCRO01","SIT2-TSCCRO02","SIT3-TSCCRO03"
										,"SIT4-TSCCRO04","SIT5-TSCCRO05","SIT6-TSCCRO06","SIT7-TSCCRO07","SIT8-TSCCRO08","UAT1-TUCCRO01","UAT2-TUCCRO02"
										,"UAT3-TUCCRO03","UAT5-TUCCRO05","UAT6-TUCCRO06","PRD1-TUCCPO01","PRD2-TUCCPO02" 	};
	JComboBox sourceServerNameCombo = new JComboBox(serverLists);

	
	JFrameUI()
	{
		credential();
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        addActionEvent();
	}
	
	private void credential() {
		try {
		String PropertyFileName = "dbconnectioninfo.properties";
		FileInputStream fileInput = new FileInputStream(PropertyFileName);
		Properties prop = new Properties();
		prop.load(fileInput);
		sourceUserInput.setText(prop.getProperty("SACCR_ORACLE_USERNAME"));
		sourcePwdInput.setText(prop.getProperty("SACCR_ORACLE_PASSWORD"));	
		
		}catch (Exception e) {
			e.getMessage();
		}
	}
	
	public void setLayoutManager() {
        container.setLayout(null);
    }
 
    public void setLocationAndSize() {
    	sourceUserName.setBounds(100, 30, 200, 25);
    	sourceUserInput.setBounds(260, 30, 100, 25);
    	sourcePassword.setBounds(450, 30, 200, 25);
    	sourcePwdInput.setBounds(570, 30, 100, 25);
    	
    	sourceServer.setBounds(100, 60, 200, 25);
    	sourceServerNameCombo.setPreferredSize(new Dimension(250, 30));
    	sourceServerNameCombo.setBounds(260, 60, 130, 25);
    	sourceServerNameCombo.setEditable(true);
    	   	
    	
	}
    
    public void addComponentsToContainer() {
    	container.add(sourceUserName);
    	container.add(sourceUserInput);
    	container.add(sourcePassword);
    	container.add(sourcePwdInput);
    	container.add(sourceServer);
    	container.add(sourceServerNameCombo);
    	
    }
    
    
	
	public void addActionEvent() {
       // loginButton.addActionListener(this);
	}
	public void actionPerformed(ActionEvent e) {
		// Coding Part of LOGIN button
		//if (e.getSource() == loginButton) {
		//}
	}
        
}
	