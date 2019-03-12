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
				private BufferedImage camera_image;

	/**
				 * 
				 */
				private int timer = 0;

	/**
				 * 
				 */
				private String behavior_pattern;
				
				private CentralStation central_station;
				

	/**
	 * 
	 * @param position 
	 * @param name 
	 * @param central_station 
	 */
	public Robot(Vector3d position, String name) {
		
		super(position,name);
		this.name = name;
		this.central_station = CentralStation.getinstance();
		
        // Add sonars
        sonars = RobotFactory.addSonarBeltSensor(this, 8);
        //add camera
        camera = RobotFactory.addCameraSensor(this);
        // reserve space for image capture
        camera_image = camera.createCompatibleImage();
	}

	/**
	 * 
	 * @return 
	 */
	public String get_name() {
		return name;
	}

    public void initBehavior() {
        System.out.println("I exist and my name is " + this.get_name());
        behavior_pattern = "go_straight";
    }
    
    /**
	 * 
	 * @param behavior_pattern 
	 */  
    public void set_behavior(String behavior) {
    	behavior_pattern = behavior;
    }
    /**
	 * sets a positive translational velocity 
	 */  
    public void move(){
		this.setRotationalVelocity(0);
		this.setTranslationalVelocity(0.5);
		
		if(sonars.hasHit(0) && sonars.getMeasurement(0) <= 0.7 ) {
			//central_station.found_obstacle(this, sonars.hasHit(2) ,sonars.hasHit(6));
			this.turn_left();
		}
    }
    /**
	 * turns 90 degrees left  
	 */  
    public void turn_left(){ 
		this.setTranslationalVelocity(0);
		this.rotateY(1.57); // 90 degrees
			
    }
    /**
	 * turns 90 degrees right  
	 */  
    public void turn_right(){
		this.setTranslationalVelocity(0);
		this.rotateY(4.71); // 270 degrees
		
    }
    /**
	 * turns 180 degrees   
	 */     
    public void turn_around(){ 
		this.setTranslationalVelocity(0);
		this.rotateY(3.14); // 180 degrees
    }
    
    public void stop(){
		this.setTranslationalVelocity(0);
		this.setRotationalVelocity(0);
    }

    
    
	public void performBehavior() {
				
		// TODO implement that update_coordinates is only called when robot changed coordinates (now CS is checking if coordinates were new)
		this.getCoords(position);
		central_station.update_coordinates(this, position);
		
//		if(behavior_pattern == "search")
//		{
//			
//			if(bumper.oneHasHit())
//			{
//				this.setTranslationalVelocity(-0.5);
//			}
//			
//			//avoid in front
//			else if(sonars.hasHit(0) && sonars.getMeasurement(0) < range_of_sonars)
//			{
//				if(this.foundCube() == true)
//				{
//					this.setTranslationalVelocity(0.3);
//	    			this.setRotationalVelocity(0);
//	    			behavior_pattern = "found";
//				}
//				else
//				{
//					this.setTranslationalVelocity(-0.5);
//	    			this.setRotationalVelocity(0.8);
//				}
//				
//			}
//			
//			//avoid front left
//			else if(sonars.hasHit(1) && sonars.getMeasurement(1) < range_of_sonars)
//			{
//				if(this.foundCube() == true)
//				{
//					this.setTranslationalVelocity(0.3);
//	    			this.setRotationalVelocity(0.5);
//	    			behavior_pattern = "found";
//				}
//				else
//				{
//					this.setTranslationalVelocity(-0.3);
//	    			this.setRotationalVelocity(-0.5);
//				}
//			}
//			
//			//avoid left
//			else if(sonars.hasHit(2) && sonars.getMeasurement(2) < range_of_sonars)
//			{
//				if(this.foundCube() == true)
//				{
//					this.setTranslationalVelocity(0.3);
//	    			this.setRotationalVelocity(0.8);
//	    			behavior_pattern = "found";
//				}
//				else
//				{
//					this.setTranslationalVelocity(0.1);
//	    			this.setRotationalVelocity(-0.5);
//				}
//				
//			}
//			
//			//avoid front right
//			else if(sonars.hasHit(7) && sonars.getMeasurement(7) < range_of_sonars)
//			{
//				if(this.foundCube() == true)
//				{
//					this.setTranslationalVelocity(0.3);
//	    			this.setRotationalVelocity(-0.5);
//	    			behavior_pattern = "found";
//				}
//				else
//				{
//					this.setTranslationalVelocity(-0.3);
//	    			this.setRotationalVelocity(0.5);
//				}
//			}
//			
//			//avoid right
//			else if(sonars.hasHit(6) && sonars.getMeasurement(6) < range_of_sonars)
//			{
//				if(this.foundCube() == true)
//				{
//					this.setTranslationalVelocity(0.3);
//	    			this.setRotationalVelocity(-0.8);
//	    			behavior_pattern = "found";
//				}
//				else
//				{
//					this.setTranslationalVelocity(0.1);
//	    			this.setRotationalVelocity(0.5);
//				}
//			}
//			
//	    	else {
//	    		// the robot's speed is always 0.5 m/s
//	            this.setTranslationalVelocity(0.5);
//	            this.setRotationalVelocity(0);
//	    	}
//		}
//	
			
//		if(behavior_pattern == "go_straight") {
//			this.setRotationalVelocity(0);
//			this.setTranslationalVelocity(0.5);
//			if(sonars.hasHit(0) && sonars.getMeasurement(0) <= 0.7 ) {
//				if(sonars.hasHit(6))
//					behavior_pattern = "turn_left";
//				else
//					behavior_pattern = "turn_right";
//			}
//			if(sonars.hasHit(1) && sonars.hasHit(2) && sonars.hasHit(3)) {
//				if(sonars.hasHit(1) && sonars.getMeasurement(1) < 0.7) {
//					//move away from wall
//					this.setTranslationalVelocity(0.5);
//					this.setRotationalVelocity(-0.1);
//				}
//				if(sonars.hasHit(1) && sonars.getMeasurement(1) > 0.7) {
//					//move closer to wall
//					this.setRotationalVelocity(0.1);
//				}
//			}
//		}
//		
//		if(behavior_pattern == "turn_left") {
//			this.setTranslationalVelocity(0);
//			this.setRotationalVelocity(1);
//			if(!sonars.hasHit(0)) {
//				behavior_pattern = "go_straight";
//			}
//		}
//		
//		if(behavior_pattern == "turn_right") {
//			this.setTranslationalVelocity(0);
//			this.setRotationalVelocity(-1);
//			if(!sonars.hasHit(0)) {
//				behavior_pattern = "go_straight";
//			}
//		}
		
		
//		if(behavior_pattern == "follow_wall") {
//			
//			if(bumper.oneHasHit()) {
//				this.setTranslationalVelocity(-0.1);
//			} else {
//				this.setTranslationalVelocity(0.5);
//			}
//			
//			if (sonars.hasHit(0)) {
//				//turn right
//				if(sonars.hasHit(2) && sonars.hasHit(6)) {
//					this.setTranslationalVelocity(0);
//					this.setRotationalVelocity(1);
//				}
//				else if(sonars.getMeasurement(0) <= 0.7 && !sonars.hasHit(6)) {
//					this.setTranslationalVelocity(0);
//					this.setRotationalVelocity(-1.5);
//				} else if(sonars.getMeasurement(0) <= 0.7 && !sonars.hasHit(2)) {
//					//turn left
//					this.setTranslationalVelocity(0);
//					this.setRotationalVelocity(2);
//				}
//			}
//			if(!sonars.hasHit(0)){
//				if(sonars.hasHit(1) && sonars.hasHit(2) && sonars.hasHit(3)) {
//					if(sonars.hasHit(1) && sonars.getMeasurement(1) < 0.7) {
//						//move away from wall
//						this.setTranslationalVelocity(0.5);
//						this.setRotationalVelocity(-0.1);
//					}
//					if(sonars.hasHit(1) && sonars.getMeasurement(1) > 0.7) {
//						//move closer to wall
//						this.setRotationalVelocity(0.1);
//					}
//				}
//				
//				else if(!sonars.hasHit(2) && sonars.hasHit(3)) {
//					this.setTranslationalVelocity(0.1);
//					this.setRotationalVelocity(0.5);
//				}
//				
//				else if(sonars.hasHit(2) && sonars.hasHit(4) && sonars.hasHit(6)) {
//					this.setTranslationalVelocity(0.5);
//				}
//			}
//			
//		}
		
		if(behavior_pattern == "stop") {
			this.setTranslationalVelocity(0);
	        this.setRotationalVelocity(0);
		}
		
		//once the cube is found, rover stops
		if(behavior_pattern == "found")
		{
			this.setTranslationalVelocity(0);
	        this.setRotationalVelocity(0);
		}
}
	
	/**
	 * 
	 * @return 
	 */
    public boolean foundCube()
    {
		camera.copyVisionImage(camera_image);
		return central_station.found_object(new Coordinates(position.x,position.y),camera_image);
    }
    
	public String get_behavior() {
		return behavior_pattern;
	}
};