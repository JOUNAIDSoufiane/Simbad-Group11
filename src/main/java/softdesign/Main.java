package main.java.softdesign;


import simbad.gui.*;
import simbad.sim.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.vecmath.Vector3d;

/**
  Derivate your own code from this example.
 */


class StopButton implements ActionListener {
	
	JFrame window = new JFrame();
	JButton button = new JButton("Stop Mission");
	
	StopButton(){
		initialize_window();
		initialize_button();
	}
	
	public void initialize_window() {
		 window.setTitle("Mission Control");
	     window.getContentPane().setLayout(null);
	     window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	     window.setVisible(true);
	     window.setBounds(200,200,200,200);
	}
	
	public void initialize_button(){
        button.setBounds(25,50,150,40);
        window.add(button);
        button.addActionListener(this);
    }
	
	@Override
    public void actionPerformed(ActionEvent e) {
		CentralStation CS = CentralStation.getinstance();
		CS.stop_mission();
    }
}

public class Main {

    public static void main(String[] args) {
        // request antialising so that diagonal lines are not "stairy"
    	
        System.setProperty("j3d.implicitAntialiasing", "true");
        
        // creation of the environment containing all obstacles and robots
        EnvironmentDescription environment = new Environment();
        
        //getting instance of Central Station
        CentralStation CS = CentralStation.getinstance();
        
        //Creating button to stop the mission
        new StopButton();
        
        // adding two robots
        Robot robot1 = CS.deploy_robot(new Vector3d(5, 0, -7), "Robot 1");
        Robot robot2 = CS.deploy_robot(new Vector3d(-2, 0, 4), "Robot 2");
        

        // add the two robots to the environment
        environment.add(robot1);
        environment.add(robot2);
        
        Coordinates position_color_found = CS.start_mission(new Color(255,0,0));
        
        System.out.println("Box found at Coordinates: " + position_color_found.x + "," + position_color_found.y);
        
        // here we create an instance of the whole Simbad simulator and we assign the newly created environment 
        Simbad frame = new Simbad(environment, false);
        frame.update(frame.getGraphics());
        
    }

} 