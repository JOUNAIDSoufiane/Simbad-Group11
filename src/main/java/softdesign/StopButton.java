package main.java.softdesign;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

class StopButton implements ActionListener {
	
	private JFrame window = new JFrame();
	private JButton button = new JButton("Stop Mission");
	
	StopButton(){
		initializeWindow();
		initializeButton();
	}
	
	private void initializeWindow() {
		 window.setTitle("Mission Control");
	     window.getContentPane().setLayout(null);
	     window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     window.setVisible(true);
	     window.setBounds(200,200,200,200);
	}
	
	private void initializeButton(){
        button.setBounds(25,50,150,40);
        button.setBackground(java.awt.Color.RED);
        window.add(button);
        button.addActionListener(this);
    }
	
	@Override
    public void actionPerformed(ActionEvent e) {
		CentralStation CS = CentralStation.getInstance();
		button.setEnabled(false);
		CS.stopMission();
    }
}