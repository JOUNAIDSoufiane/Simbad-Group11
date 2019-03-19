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
				private Coordinates prev_coordinates;

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
		prev_coordinates = new Coordinates(position.x, position.z);
		
        // Add sonars
        sonars = RobotFactory.addSonarBeltSensor(this, 8);
        sonars.setUpdatePerSecond(1000);
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
		
		//Detecting object in front, front right while next to wall, front left while next to wall (make sure it can or can't go through the gap between wall and object)
		if((sonars.hasHit(0) && sonars.getMeasurement(0) <= 0.5) 
				|| (sonars.hasHit(7) && sonars.getMeasurement(7) <= 0.4 && sonars.getMeasurement(2) <= 0.5)
				|| (sonars.hasHit(1) && sonars.getMeasurement(1) <= 0.4 &&  sonars.getMeasurement(6) <= 0.5)) {
			central_station.found_obstacle(this, sonars);
		}
		
		//Wall following code 
		if(behavior_pattern == "follow_wall") {
			//turn left when possible
			if(sonars.hasHit(3) && sonars.getMeasurement(3) >= 0.9 && !sonars.hasHit(4))
				turn_left();
		}
		
		//FIXME spiral robot code (so far works until robot hits wall or object) XXX Code is still very buggy lol 
		else if(behavior_pattern == "spiral") {
			central_station.control_spiral(this);
		}
		
		else if(behavior_pattern == "spiral_down") {
			this.getCoords(position);
			Coordinates coordinates = new Coordinates(position.x, position.z);
			if(coordinates.x != prev_coordinates.x || coordinates.y != prev_coordinates.y) {
				central_station.spiral_down(this, coordinates, prev_coordinates);
			}
		}
    }
    /**
	 * turns 90 degrees left  
	 */  
    public void turn_left(){ 
		this.setTranslationalVelocity(0);
		this.rotateY(1.5707963268); // 90 degrees
			
    }
    /**
	 * turns 90 degrees right  
	 */  
    public void turn_right(){
		this.setTranslationalVelocity(0);
		this.rotateY(4.7123889804); // 270 degrees
		
    }
    /**
	 * turns 180 degrees   
	 */     
    public void turn_around(){ 
		this.setTranslationalVelocity(0);
		this.rotateY(3.1415926536); // 180 degrees
    }
    
    public void stop(){
		this.setTranslationalVelocity(0);
		this.setRotationalVelocity(0);
    }

    
    
	public void performBehavior() {
		
		if(behavior_pattern != "stop") {
			//Robot starts moving straight
			move();
			
			//robot sends its position to central station whenever its position has changed coordinates
			this.getCoords(position);
			Coordinates coordinates = new Coordinates(position.x, position.z);
			if(coordinates.x != prev_coordinates.x || coordinates.y != prev_coordinates.y) {
				central_station.update_coordinates(this, coordinates);
				prev_coordinates = coordinates;
			}
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