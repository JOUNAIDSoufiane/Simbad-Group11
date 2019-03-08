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
        
        CentralStation CS = new CentralStation();
        
        
        // adding two robots
        Robot robot1 = new Robot(new Vector3d(0, 0, 0), "Robot 1",CS);
        Robot robot2 = new Robot(new Vector3d(-2, 0, -2), "Robot 2",CS);

        // add the two robots to the environment
        environment.add(robot1);
        environment.add(robot2);
        
        
        // here we create an instance of the whole Simbad simulator and we assign the newly created environment 
        Simbad frame = new Simbad(environment, false);
        frame.update(frame.getGraphics());
        
    }

} 