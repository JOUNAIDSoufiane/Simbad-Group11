package main.java.softdesign;

import java.awt.image.BufferedImage;
import java.io.IOException;

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
				private Coordinates[] starting_coordinates;
				private Coordinates goal = new Coordinates(99,99);

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
				private int left_counter;
				/**
				 * 
				 */
				private BufferedImage camera_image;
				
				private Coordinates[] temp_memory = new Coordinates[4];

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
		this.left_counter = 0;
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
     * @throws IOException 
	 */  
    public void move() {
		this.setRotationalVelocity(0);
		this.setTranslationalVelocity(0.5);
		
		if(behavior_pattern != "around_obstacle") {
			//Detecting object in front, front right while next to wall, front left while next to wall (make sure it can or can't go through the gap between wall and object)
			if((sonars.hasHit(0) && sonars.getMeasurement(0) <= 0.5) 
					|| (sonars.hasHit(7) && sonars.getMeasurement(7) <= 0.4 && sonars.getMeasurement(2) <= 0.5)
					|| (sonars.hasHit(1) && sonars.getMeasurement(1) <= 0.4 &&  sonars.getMeasurement(6) <= 0.5)) {
				central_station.found_obstacle(this, sonars);
			}
		}
			
		//Wall following code 
		if(behavior_pattern == "follow_wall") {
			//turn left when possible
			if(sonars.hasHit(3) && sonars.getMeasurement(3) >= 0.9 && !sonars.hasHit(4))
				turn_left();
			this.getCoords(position);
			Coordinates coordinates = new Coordinates(position.x, position.z);
			
			//remove wall coordinates on left from unvisited array
			if((coordinates.x != prev_coordinates.x || coordinates.y != prev_coordinates.y)) {
				central_station.remove_left_coords(coordinates, prev_coordinates);
			}
			
			//Check if robot has reached any robot's starting position
			starting_coordinates = central_station.get_starting_positions();
			for (int i = 0; i < starting_coordinates.length; i++) {
				if(coordinates.x == starting_coordinates[i].x && coordinates.y == starting_coordinates[i].y && this.getOdometer() > 1) {
					behavior_pattern = "spiral";
					turn_right();
				}
			}
		}
		
		
		else if(behavior_pattern == "spiral") {
			this.getCoords(position);
			Coordinates coordinates = new Coordinates(position.x, position.z);
			
			if(sonars.hasHit(7) && sonars.getMeasurement(7) <= 0.9) {
				if (left_counter == 0)
					camera.copyVisionImage(camera_image);
				turn_right();
				behavior_pattern = "around_obstacle";
			}
			
			if((coordinates.x != prev_coordinates.x || coordinates.y != prev_coordinates.y)) {
				central_station.spiral(this, coordinates, prev_coordinates);
			}
		}
		
		else if(behavior_pattern == "around_obstacle") {
			//TODO Return to spiral behavior when next coords in front is free
			if(sonars.hasHit(2) && sonars.getMeasurement(2) > 0.5 && sonars.hasHit(3))
				turn_left();
			
			else if(sonars.hasHit(3) && sonars.getMeasurement(3) >= 0.9 && !sonars.hasHit(2) && !sonars.hasHit(4)){
				turn_left();
				left_counter++;
				this.getCoords(position);
				Coordinates coordinates = new Coordinates(position.x, position.z);
				temp_memory[left_counter-1] = coordinates;
				if (left_counter == 4){
					central_station.found_object(new Coordinates(position.x,position.z),camera_image);
					central_station.map_object(temp_memory);
					// TODO : NEED to revert the behavior pattern to spiral in a logical way
					left_counter = 0;
					behavior_pattern = "spiral";
				}
			}
			else if(sonars.hasHit(0) && sonars.getMeasurement(0) <= 0.5) {
				turn_right();
			}

		}
		else if (behavior_pattern == "clean_up"){   		//XXX Follows wall until it's reached x coordinate of goal then turns toward goal
			this.getCoords(position);
			Coordinates current_position = new Coordinates(position.x, position.z);
			
			if(goal.x == 99) {
				goal = central_station.get_unvisited(current_position); // returns a closeby unvisited coordinate.
				System.out.println("Goal: " + goal.x + " " + goal.y);
			}
			
			if(goal.x == 98)
				central_station.done_mapping();
			
			if(current_position.x == goal.x && current_position.y == goal.y) {
				System.out.println("Reached goal");
				goal.x = 99;
				goal.y = 99;
				behavior_pattern = "spiral";
			}
			// TODO : implement a goto method to visit the returned coordinate
			
			if(sonars.hasHit(3) && sonars.getMeasurement(3) >= 0.9 && !sonars.hasHit(4) && goal.x != current_position.x)
				turn_left();
			else if(goal.x == current_position.x && sonars.hasHit(2)) {
				turn_right();
			}
			
		}
    }
    /**
	 * turns 90 degrees left  
	 */  
    public void turn_left(){ 
		this.setTranslationalVelocity(0);
		this.rotateY(90 * Math.PI / 180); // 90 degrees
			
    }
    /**
	 * turns 90 degrees right  
	 */  
    public void turn_right(){
		this.setTranslationalVelocity(0);
		this.rotateY(270 * Math.PI / 180); // 270 degrees
		
    }
    /**
	 * turns 180 degrees   
	 */     
    public void turn_around(){ 
		this.setTranslationalVelocity(0);
		this.rotateY(Math.PI); // 180 degrees
    }
    
    public void stop(){
		this.setTranslationalVelocity(0);
		this.setRotationalVelocity(0);
    }

    
    
	public void performBehavior() {
		
		if(behavior_pattern != "stop" && behavior_pattern != "finished") {
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
    
	public String get_behavior() {
		return behavior_pattern;
	}

};