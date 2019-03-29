package main.java.softdesign;

import simbad.gui.*;
import simbad.sim.*;

import javax.vecmath.Vector3d;

public class Main {

    public static void main(String[] args) {
        // request antialising so that diagonal lines are not "stairy"
    		
        System.setProperty("j3d.implicitAntialiasing", "true");
        
        // creation of the environment containing all obstacles and robots
        EnvironmentDescription environment = new Environment();
        
        //getting instance of Central Station
        CentralStation centralStation = CentralStation.getInstance();
        
        //Creating button to stop the mission
        new StopButton();
        
        // adding two robots
        Robot robot1 = centralStation.deployRobot(new Vector3d(5, 0, -6.55), "Robot 1");
        Robot robot2 = centralStation.deployRobot(new Vector3d(-7, 0, 4.95), "Robot 2");
        

        // add the two robots to the environment
        environment.add(robot1);
        environment.add(robot2);
        
        //Starting mission with color for target box given in its rgb values (in this case values correspond to red)
        centralStation.startMission(new Color(255,0,0));
        
        // here we create an instance of the whole Simbad simulator and we assign the newly created environment 
        Simbad frame = new Simbad(environment, false);
        frame.update(frame.getGraphics());
    }
} 