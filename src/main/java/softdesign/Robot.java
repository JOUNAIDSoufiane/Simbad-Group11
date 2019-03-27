package main.java.softdesign;

import java.awt.image.BufferedImage;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import simbad.sim.Agent;
import simbad.sim.CameraSensor;
import simbad.sim.RobotFactory;
import simbad.sim.RangeSensorBelt; 

public class Robot extends Agent
{
	/**
				 * 
				 */
				private String name;
	/**
	 * 
	 */
				private Point3d position = new Point3d();
				private Coordinates prevCoordinates;
				public Coordinates goal;

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
				 */
				private int leftCounter;
				
				private int crashCounter;
				/**
				 * 
				 */
				private BufferedImage cameraImage;
				
				private Coordinates[] tempMemory = new Coordinates[4];
	/**
				 * 
				 */
				
				private String behaviorPattern;
				
				private CentralStation centralStation;
				

	/**
	 * 
	 * @param position 
	 * @param name 
	 */
	public Robot(Vector3d position, String name) {
		
		super(position,name);
		this.name = name;
		this.leftCounter = 0;
		this.centralStation = CentralStation.getInstance();
		prevCoordinates = new Coordinates(position.x, position.z);
		
        // Add sonars
        sonars = RobotFactory.addSonarBeltSensor(this, 8);
        sonars.setUpdatePerSecond(1000);
        //add camera
        camera = RobotFactory.addCameraSensor(this);
        // reserve space for image capture
        cameraImage = camera.createCompatibleImage();
	}
	
	/**
	 * 
	 * @return 
	 */
	public String getName() {
		return name;
	}

    public void initBehavior() {
        System.out.println("I exist and my name is " + this.getName());
    }
    
    /**
	 * 
	 * @param behavior
	 */  
    public void setBehavior(String behavior) {
    	behaviorPattern = behavior;
    }
    /**
	 * sets a positive translational velocity 
	 */  
    public void move() {
		this.setRotationalVelocity(0);
		this.setTranslationalVelocity(0.5);
		
		if(behaviorPattern != "aroundObstacle") {
			//Detecting object in front, front right while next to wall, front left while next to wall (make sure it can or can't go through the gap between wall and object)
			if((sonars.hasHit(0) && sonars.getMeasurement(0) <= 0.5) 
					|| (sonars.hasHit(7) && sonars.getMeasurement(7) <= 0.4 && sonars.getMeasurement(2) <= 0.5)
					|| (sonars.hasHit(1) && sonars.getMeasurement(1) <= 0.4 &&  sonars.getMeasurement(6) <= 0.5)) {
				centralStation.foundObstacle(this, sonars);
			}
		}
			
		//Wall following code 
		if(behaviorPattern == "followWall") {
			//turn left when possible
			if(sonars.hasHit(3) && sonars.getMeasurement(3) >= 0.9 && !sonars.hasHit(4))
				turnLeft();
			this.getCoords(position);
			Coordinates coordinates = new Coordinates(position.x, position.z);
			
			//remove wall coordinates on left from unvisited array
			if((coordinates.x != prevCoordinates.x || coordinates.y != prevCoordinates.y)) {
				centralStation.removeLeftCoordinates(coordinates, prevCoordinates);
				centralStation.addBlocked(coordinates, prevCoordinates);
			}
			
			//Check if robot has reached any robot's starting position
			if(centralStation.reachedStartingPositions(coordinates) && this.getOdometer() > 1) {
				behaviorPattern = "spiral";
				turnRight();
			}
		}
		
		
		else if(behaviorPattern == "spiral") {
			this.getCoords(position);
			Coordinates coordinates = new Coordinates(position.x, position.z);
			
			if(sonars.hasHit(7) && sonars.getMeasurement(7) <= 0.9) { // encounters obstacle
				if (leftCounter == 0)
					camera.copyVisionImage(cameraImage);
				turnRight();
				behaviorPattern = "aroundObstacle";
			}
			
			if((coordinates.x != prevCoordinates.x || coordinates.y != prevCoordinates.y))
				centralStation.spiral(this, coordinates, prevCoordinates);
		}
		
		else if(behaviorPattern == "aroundObstacle") {
			
			if(sonars.hasHit(2) && sonars.getMeasurement(2) > 0.5 && sonars.hasHit(3))
				turnLeft();
			
			else if(sonars.hasHit(3) && sonars.getMeasurement(3) >= 0.9 && !sonars.hasHit(2) && !sonars.hasHit(4)){
				turnLeft();
				leftCounter++;
				this.getCoords(position);
				Coordinates coordinates = new Coordinates(position.x, position.z);
				tempMemory[leftCounter-1] = coordinates;
				if (leftCounter == 4){
					centralStation.foundObject(new Coordinates(position.x,position.z),cameraImage);
					centralStation.mapObject(tempMemory);
					leftCounter = 0;
					behaviorPattern = "spiral";
				}
			}
			else if(sonars.hasHit(0) && sonars.getMeasurement(0) <= 0.5) {
				turnRight();
			}
		}
		else if(behaviorPattern == "moveTo")
			moveTo();
    }
    
    public void moveTo() {
    	this.getCoords(position);
		Coordinates coordinates = new Coordinates(position.x, position.z);
		
		if (sonars.hasHit(0) && sonars.getMeasurement(0) < 0.1) // XXX CRASH FIX
			turnLeft();
		else if (sonars.hasHit(1) && sonars.getMeasurement(1) < 0.2) // XXX CRASH FIX
			turnRight();
		else if (sonars.hasHit(7) && sonars.getMeasurement(7) < 0.2) // XXX CRASH FIX
			turnLeft();
		
		
		//XXX this part could check if object is a new one and map it if it is otherwise it could just do same shit it does now
		if(sonars.hasHit(3) && sonars.getMeasurement(3) >= 0.9 && !sonars.hasHit(4)){ // XXX makes it turn infinitely over a stand alone polygon
			if (crashCounter < 42){
				turnLeft();
				crashCounter++;
			}
			else{
				System.out.println("too many left turns");
				turnRight();
				crashCounter = 0;
			}
	    }
	    	
		centralStation.updateCoordinates(this, coordinates);
		
		if(coordinates.x == goal.x && coordinates.y == goal.y) {
			centralStation.updateCoordinates(this, coordinates);
			centralStation.isFree(this, coordinates, prevCoordinates);
			System.out.println("Reached Goal. Now Spiraling");
			crashCounter = 0;
			behaviorPattern = "spiral";
		}
		else if((coordinates.x != prevCoordinates.x)) {
			if(coordinates.x == goal.x && centralStation.nothingBetween(coordinates, goal)) {
				turnRight();
				crashCounter = 0;
			}
		}
		
    }
    /**
	 * turns 90 degrees left  
	 */  
    public void turnLeft(){ 
		this.setTranslationalVelocity(0);
		this.rotateY(90 * Math.PI / 180); // 90 degrees
			
    }
    /**
	 * turns 90 degrees right  
	 */  
    public void turnRight(){
		this.setTranslationalVelocity(0);
		this.rotateY(270 * Math.PI / 180); // 270 degrees
		
    }
    /**
	 * turns 180 degrees   
	 */     
    public void turnAround(){ 
		this.setTranslationalVelocity(0);
		this.rotateY(Math.PI); // 180 degrees
    }
    
    public void stop(){
		this.setTranslationalVelocity(0);
		this.setRotationalVelocity(0);
    }

    
    
	public void performBehavior() {
		
		if(behaviorPattern != "stop" && behaviorPattern != "finished") {
			//Robot starts moving straight
			move();
			
			//robot sends its position to central station whenever its position has changed coordinates
			this.getCoords(position);
			Coordinates coordinates = new Coordinates(position.x, position.z);
			if(coordinates.x != prevCoordinates.x || coordinates.y != prevCoordinates.y) {
				centralStation.updateCoordinates(this, coordinates);
				prevCoordinates = coordinates;
			}
		}
		
}
    
	public String get_behavior() {
		return behaviorPattern;
	}

};