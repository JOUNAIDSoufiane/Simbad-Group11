package main.java.softdesign;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

class StopButton implements ActionListener {
	
	JFrame window = new JFrame();
	JButton button = new JButton("Stop Mission");
	
	StopButton(){
		initialize_window();
		initialize_button();
	}
	
	private void initialize_window() {
		 window.setTitle("Mission Control");
	     window.getContentPane().setLayout(null);
	     window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	     window.setVisible(true);
	     window.setBounds(200,200,200,200);
	}
	
	private void initialize_button(){
        button.setBounds(25,50,150,40);
        button.setBackground(java.awt.Color.RED);
        window.add(button);
        button.addActionListener(this);
    }
	
	@Override
    public void actionPerformed(ActionEvent e) {
		CentralStation CS = CentralStation.getinstance();
		CS.done_mapping();
		CS.stop_mission();
    }
}