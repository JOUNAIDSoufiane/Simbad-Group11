package main.java.softdesign;

import java.awt.image.BufferedImage;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import simbad.sim.Agent;
import simbad.sim.CameraSensor;
import simbad.sim.RobotFactory;
import simbad.sim.RangeSensorBelt; 

public class Robot extends Agent {
	/**
	 * 
	 */
	private String name;
	/**
	 * 
	 */
	private Point3d position = new Point3d();
	/**
	 * 
	 */
	private Coordinates prevCoordinates;
	/**
	 * 
	 */
	private Coordinates goal;
	/**
	 * 
	 */
	private CameraSensor camera;
	/**
	 * 
	 */
	private RangeSensorBelt sonars;
	/**
	 * 
	 * Used to coordinate the robot circling around objects exactly once
	 */
	private int leftCounter;
	/**
	 * 
	 */
	private BufferedImage cameraImage;
	/**
	 * 
	 * Used to store all coordinates of an object's corners for mapping it
	 */
	private Coordinates[] tempMemory = new Coordinates[4];
	/**
	 * 
	 */
	private String behaviorPattern;
	/**
	 * 
	 */
	private CentralStation centralStation;
	/**
	 * 
	 * @param position 
	 * @param name 
	 */
	public Robot(Vector3d position, String name) {
		
		// Initializing Robot
		super(position,name);
		this.name = name;
		this.leftCounter = 0;
		// Getting instance of central station
		this.centralStation = CentralStation.getInstance();
		// Saving starting coordinates as previous coordinates, since no previous coordinates exist yet
		prevCoordinates = new Coordinates(position.x, position.z);
        // Add sonars
        sonars = RobotFactory.addSonarBeltSensor(this, 8);
        sonars.setUpdatePerSecond(1000);
        // Add camera
        camera = RobotFactory.addCameraSensor(this);
        // Reserve space for image capture
        cameraImage = camera.createCompatibleImage();
	}
	/**
	 * 
	 * @return 
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 * @param behavior
	 */  
    public void setBehavior(String behavior) {
    	behaviorPattern = behavior;
    }
	public String getBehavior() {
		return behaviorPattern;
	}
	/**
	 * 
	 * Setting the goal coordinates to which the robot will drive
	 */
	public void setGoal(Coordinates coordinates) {
		this.goal = coordinates;
	}
    /**
	 * 
	 */
    public void initBehavior() {
        System.out.println("I exist and my name is " + this.getName());
    }
    /**
	 * sets a positive translational velocity, coordinates all different behavior patterns and obstacle avoidance
	 */  
    public void move() {
		this.setRotationalVelocity(0);
		this.setTranslationalVelocity(0.5);
		
		this.getCoords(position);
		Coordinates coordinates = new Coordinates(position.x, position.z);
		
		if(behaviorPattern != "aroundObstacle") {
			// Detecting object in front, front right while next to wall, front left while next to wall (make sure it can or can't go through the gap between wall and object)
			if((sonars.hasHit(0) && sonars.getMeasurement(0) <= 0.5) 
					|| (sonars.hasHit(7) && sonars.getMeasurement(7) <= 0.4 && sonars.getMeasurement(2) <= 0.5)
					|| (sonars.hasHit(1) && sonars.getMeasurement(1) <= 0.4 &&  sonars.getMeasurement(6) <= 0.5)) {
				centralStation.foundObstacle(this, sonars);
			}
		}
		
		// In case object was recognized too late, turn in the appropriate direction to avoid crashing into it
		if (sonars.hasHit(0) && sonars.getMeasurement(0) < 0.1)
			turnLeft();
		else if (sonars.hasHit(1) && sonars.getMeasurement(1) < 0.2)
			turnRight();
		else if (sonars.hasHit(7) && sonars.getMeasurement(7) < 0.2)
			turnLeft();
		
		// Following along the walls with the wall on the robot's left side
		if(behaviorPattern == "followWall") {
			// Turn left when possible
			if(sonars.hasHit(3) && sonars.getMeasurement(3) >= 0.9 && !sonars.hasHit(4))
				turnLeft();
			
			// Remove wall coordinates on left from unvisited array, and save them as blocked coordinates which the robot can't visit
			if((coordinates.x != prevCoordinates.x || coordinates.y != prevCoordinates.y)) {
				centralStation.removeLeftCoordinates(coordinates, prevCoordinates);
				centralStation.addBlocked(coordinates, prevCoordinates);
			}
			
			// Check if robot has reached any robot's starting position, to initiate a new behavior pattern at that point
			if(centralStation.reachedStartingPositions(coordinates) && this.getOdometer() > 1) {
				behaviorPattern = "spiral";
				turnRight();
			}
		}
		
		// Coordinates the inward spiral
		else if(behaviorPattern == "spiral") {

			// When obstacle encountered take picture, turn right, and change behavior pattern to go around the obstacle
			if(sonars.hasHit(7) && sonars.getMeasurement(7) <= 0.9) {
				if(leftCounter == 0)
					camera.copyVisionImage(cameraImage);
				turnRight();
				behaviorPattern = "aroundObstacle";
			}
			
			/* As soon as robot reaches new coordinates, calls central station to coordinate robot's spiral
			 * (This is to avoid calling central station every step and rather only call it when the rover has moved to new coordinates)
			 */
			if((coordinates.x != prevCoordinates.x || coordinates.y != prevCoordinates.y))
				centralStation.spiral(this, coordinates, prevCoordinates);
		}
		
		// Coordinates the robot circling around the obstacle once
		else if(behaviorPattern == "aroundObstacle") {
			// Turn towards the object when distance to it is getting larger (used to maintain perfect distance of 0.5m to the object)
			if(sonars.hasHit(2) && sonars.getMeasurement(2) > 0.5 && sonars.hasHit(3))
				turnLeft();
			
			// Turn left when possible
			else if(sonars.hasHit(3) && sonars.getMeasurement(3) >= 0.9 && !sonars.hasHit(2) && !sonars.hasHit(4)){
				turnLeft();
				leftCounter++;
				
				// Count left turns and store corner coordinates of object in array
				tempMemory[leftCounter-1] = coordinates;
				
				// Once full cycle around obstacle completed, send data to central station to analyze, return to spiral behavior
				if(leftCounter == 4){
					centralStation.mapObject(tempMemory, cameraImage);
					leftCounter = 0;
					behaviorPattern = "spiral";
				}
			}
			
			// When going towards obstacle to decrease distance to it, turn right once distance is 0.5m to be going parallel to the objects' walls again
			else if(sonars.hasHit(0) && sonars.getMeasurement(0) <= 0.5)
				turnRight();
		}
		
		// Coordinates robots to move to given coordinates set in the goal coordinates variable
		else if(behaviorPattern == "moveTo")
			moveTo();
    }
	/**
	 * 
	 */
    private void moveTo() {
    	this.getCoords(position);
		Coordinates coordinates = new Coordinates(position.x, position.z);
		
		// If known obstacle is to left of robot, turn right to not circle around known objects
		if(sonars.hasHit(3) && sonars.getMeasurement(3) >= 0.9 && !sonars.hasHit(4)){
			if(centralStation.isObject(coordinates, prevCoordinates))
				turnRight();
			else 
				turnLeft();
	    }
	    	
		// When robot reaches goal coordinates, change behavior to start spiraling in area of unvisited coordinates
		if(coordinates.x == goal.x && coordinates.y == goal.y) {
			centralStation.updateCoordinates(coordinates);
			
			//Checks which direction of the robot has unvisited coordinates and turns the robot towards that direction
			centralStation.isFree(this, coordinates, prevCoordinates);
			behaviorPattern = "spiral";
		}
		
		//When robot reaches same x coordinate as goal coordinates turn right, if there is no wall or object in the way
		else if((coordinates.x != prevCoordinates.x) && (coordinates.x == goal.x && centralStation.nothingBetween(coordinates, goal)))
				turnRight();
		
    }
    /**
	 * turns 90 degrees left  
	 */  
    public void turnLeft(){ 
		this.setTranslationalVelocity(0);
		this.rotateY(90 * Math.PI / 180);
			
    }
    /**
	 * turns 90 degrees right  
	 */  
    public void turnRight(){
		this.setTranslationalVelocity(0);
		this.rotateY(270 * Math.PI / 180);
		
    }
    /**
	 * turns 180 degrees   
	 */     
    public void turnAround(){ 
		this.setTranslationalVelocity(0);
		this.rotateY(Math.PI);
    }
	/**
	 * 
	 * Fully stops the robot
	 */
    public void stop(){
		this.setTranslationalVelocity(0);
		this.setRotationalVelocity(0);
    }
	/**
	 * 
	 */
	public void performBehavior() {
		if(behaviorPattern != "stop") {
			//Robot starts moving straight
			move();
			
			//robot sends its position to central station whenever its position has changed coordinates so central station can mark those coordinates as visited
			this.getCoords(position);
			Coordinates coordinates = new Coordinates(position.x, position.z);
			if(coordinates.x != prevCoordinates.x || coordinates.y != prevCoordinates.y) {
				centralStation.updateCoordinates(coordinates);
				prevCoordinates = coordinates;
			}
		}
	}
};