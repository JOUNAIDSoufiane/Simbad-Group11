package main.java.softdesign;

import simbad.gui.*;
import simbad.sim.*;

import javax.vecmath.Vector3d;

public class Main {

    public static void main(String[] args) {
    		
        System.setProperty("j3d.implicitAntialiasing", "true"); // Antialiasing on
        
        // creation of the environment containing all obstacles and robots
        
        EnvironmentDescription environment = new Environment();
        
        CentralStation centralStation = CentralStation.getInstance();
        
        new StopButton();
        
        Robot robot1 = centralStation.deployRobot(new Vector3d(5, 0, -6.55), "Robot 1");
        Robot robot2 = centralStation.deployRobot(new Vector3d(-7, 0, 4.95), "Robot 2");
        
        environment.add(robot1);
        environment.add(robot2);
        
        // Starting mission, target : Object of red color (parameter in RGB)
        centralStation.startMission(new Color(255,0,0));
        
        // SIMBAD initialization 
        Simbad frame = new Simbad(environment, false);
        frame.update(frame.getGraphics());
    }
} 