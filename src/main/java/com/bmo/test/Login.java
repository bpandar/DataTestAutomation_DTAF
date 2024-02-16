package com.bmo.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 
public class Login {
	private static final Font BTN_FONT = new Font(Font.DIALOG, Font.BOLD, 40);
	
	Login()
	{
		LoginFrame frame = new LoginFrame();
		frame.setTitle("Login Form");
        frame.setFont(BTN_FONT);
        frame.setVisible(true);
        frame.setBounds(500, 200, 350, 220);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
       
	}
	
    public static void main(String[] a) {
    	Login obj = new Login();
    	//InputJFrameUI swingUI = new InputJFrameUI();
    	//ScanTest2 test = new ScanTest2();
    	
    }
}