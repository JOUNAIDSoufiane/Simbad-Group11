package main.java.softdesign;


import simbad.gui.*;
import simbad.sim.*;

import javax.vecmath.Vector3d;

/**
  Derivate your own code from this example.
 */


public class Main {

    public static void main(String[] args) {
        // request antialising so that diagonal lines are not "stairy"
    	
        System.setProperty("j3d.implicitAntialiasing", "true");
        
        // creation of the environment containing all obstacles and robots
        EnvironmentDescription environment = new Environment();
        
        //getting instance of Central Station
        CentralStation CS = CentralStation.getinstance();
        
        //Creating variable to store coordinates of object once it's found
        Coordinates position_color_found = new Coordinates(0,0);
        
        
        // adding two robots
        Robot robot1 = CS.deploy_robot(new Vector3d(0, 0, 0), "Robot 1");
        Robot robot2 = CS.deploy_robot(new Vector3d(-2, 0, -2), "Robot 2");
        

        // add the two robots to the environment
        environment.add(robot1);
        environment.add(robot2);
        
        
        CS.start_mission(new Color(255,0,0) , position_color_found);
        
        // here we create an instance of the whole Simbad simulator and we assign the newly created environment 
        Simbad frame = new Simbad(environment, false);
        frame.update(frame.getGraphics());
        
    }

} 