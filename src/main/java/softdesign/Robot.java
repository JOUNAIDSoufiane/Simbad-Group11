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
		this.centralStation = CentralStation.getInstance();
		
		// Saving starting coordinates as previous coordinates, since no previous coordinates exist yet
		prevCoordinates = new Coordinates(position.x, position.z);
        sonars = RobotFactory.addSonarBeltSensor(this, 8);
        sonars.setUpdatePerSecond(1000);
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
		
		// Robot follows the wall on its left
		
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
		
		// inward spiral behavior
		
		else if(behaviorPattern == "spiral") {

			// When obstacle encountered take picture, turn right, and change behavior pattern in order to collect the object angle coordinates
			if(sonars.hasHit(7) && sonars.getMeasurement(7) <= 0.9) {
				if(leftCounter == 0)
					camera.copyVisionImage(cameraImage);
				turnRight();
				behaviorPattern = "aroundObstacle";
			}
			
			/* As soon as robot reaches new coordinates, calls central station to receive new instructions
			 * (This is to avoid calling central station every step and rather only call it when the rover has moved to new coordinates)
			 */
			if((coordinates.x != prevCoordinates.x || coordinates.y != prevCoordinates.y))
				centralStation.spiral(this, coordinates, prevCoordinates);
		}
		
		// Robot goes around the object collecting angle points to form an area
		else if(behaviorPattern == "aroundObstacle") {
			// Slight adjustment of the robot so it can remain in parallel to the wall at a  0.5 meters distance
			if(sonars.hasHit(2) && sonars.getMeasurement(2) > 0.5 && sonars.hasHit(3))
				turnLeft();
			
			// Turn left when possible
			else if(sonars.hasHit(3) && sonars.getMeasurement(3) >= 0.9 && !sonars.hasHit(2) && !sonars.hasHit(4)){
				turnLeft();
				leftCounter++;
				
				// Count left turns and store corner coordinates of object in array
				tempMemory[leftCounter-1] = coordinates;
				
				// Once full cycle around obstacle completed, send data to central station, return to spiral behavior
				if(leftCounter == 4){
					centralStation.mapObject(tempMemory, cameraImage);
					leftCounter = 0;
					behaviorPattern = "spiral";
				}
			}
			
			// Slight adjustment of the robot so it can remain in parallel to the wall at a  0.5 meters distance
			else if(sonars.hasHit(0) && sonars.getMeasurement(0) <= 0.5)
				turnRight();
		}
		
		else if(behaviorPattern == "moveTo")
			moveTo();
    }
	/**
	 * Behavior pattern moveTo : starts cleaning up the remaining coordinates by setting a goal unvisited area and then initiating spiral behavior in that area to clean it up.
	 */
    private void moveTo() {
    	this.getCoords(position);
		Coordinates coordinates = new Coordinates(position.x, position.z);
		
		// If known obstacle is to left of robot, turn right to not map known objects
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
			move();
			
			//robot sends its position to central station whenever its position has changed so central station can mark those coordinates as visited
			this.getCoords(position);
			Coordinates coordinates = new Coordinates(position.x, position.z);
			if(coordinates.x != prevCoordinates.x || coordinates.y != prevCoordinates.y) {
				centralStation.updateCoordinates(coordinates);
				prevCoordinates = coordinates;
			}
		}
	}
};