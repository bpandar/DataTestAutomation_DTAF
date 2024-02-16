package com.bmo.test;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;

public class JpanelClass {

	public static void main(String[] args) 
	{
	    // TODO code application logic here

	    JFrame frame = new JFrame();
	    frame.setSize( 300, 300);
	    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	    
	    JMenu menu = new JMenu();
	    menu.add("open");
	    
	    JPanel panelOne = new JPanel();
	    panelOne.setBackground( Color.white );

	    JPanel panelTwo = new JPanel();
	    panelTwo.setBackground(Color.blue);

	    frame.setContentPane( panelOne );
	    frame.add(menu);
	    frame.setVisible(true);

	    //This delay is just here so you can see the transition
	    try 
	    {
	        Thread.sleep( 1000 );
	    }
	    catch ( InterruptedException ie )
	    {
	        ie.printStackTrace();
	    }

	    panelTwo.setSize( frame.getContentPane().getSize() );
	    frame.setContentPane( panelTwo );

	}
}
