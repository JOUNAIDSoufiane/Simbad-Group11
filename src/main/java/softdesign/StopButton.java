package main.java.softdesign;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

class StopButton implements ActionListener {
	/**
	 * 
	 * Creating instance of the new Window
	 */
	private JFrame window = new JFrame();
	/**
	 * 
	 * Creating instance of the new Button
	 */
	private JButton button = new JButton("Stop Mission");
	/**
	 * 
	 */
	StopButton(){
		initializeWindow();
		initializeButton();
	}
	/**
	 * 
	 * Initializes the window with a title, layout, certain properties, and a size
	 */
	private void initializeWindow() {
		 window.setTitle("Mission Control");
	     window.getContentPane().setLayout(null);
	     window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     window.setVisible(true);
	     window.setBounds(200,200,200,200);
	}
	/**
	 * 
	 * Initializes the Button with a size, color, adds it to the window, and adds the action listener for clicking the button
	 */
	private void initializeButton(){
        button.setBounds(25,50,150,40);
        button.setBackground(java.awt.Color.RED);
        window.add(button);
        button.addActionListener(this);
    }
	/**
	 * @param event
	 * 
	 * When button is clicked, call central station to stop mission and disable button so it can't be clicked again
	 */
	@Override
    public void actionPerformed(ActionEvent event) {
		CentralStation CS = CentralStation.getInstance();
		button.setEnabled(false);
		CS.stopMission();
    }
}