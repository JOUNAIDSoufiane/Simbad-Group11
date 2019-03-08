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
				private static String name;
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
				private RangeSensorBelt bumper;

	/**
				 * 
				 */
				private String behavior_pattern;
				
				private CentralStation CS;
				

	/**
	 * 
	 * @param position 
	 * @param name 
	 * @param central_station 
	 */
	public Robot(Vector3d position, String name, CentralStation central_station) {
		
		super(position,name);
		
		CS = central_station;
		
        // Add bumpers
        bumper = RobotFactory.addBumperBeltSensor(this);
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
        behavior_pattern = "search";
    }
    
	/**
	 * 
	 * @param behavior_pattern 
	 */  
    
	public void performBehavior() {
		
		//rover will always keep a distance of at least 0.5m from any obstacle
		double range_of_sonars = 0.5;
		
		this.getCoords(position);
		
		if(behavior_pattern == "search")
		{
			if(bumper.oneHasHit())
			{
				this.setTranslationalVelocity(-0.5);
			}
			
			//avoid in front
			else if(sonars.hasHit(0) && sonars.getMeasurement(0) < range_of_sonars)
			{
				if(this.foundCube() == true)
				{
					this.setTranslationalVelocity(0.3);
	    			this.setRotationalVelocity(0);
	    			behavior_pattern = "found";
				}
				else
				{
					this.setTranslationalVelocity(-0.5);
	    			this.setRotationalVelocity(0.8);
				}
				
			}
			
			//avoid front left
			else if(sonars.hasHit(1) && sonars.getMeasurement(1) < range_of_sonars)
			{
				if(this.foundCube() == true)
				{
					this.setTranslationalVelocity(0.3);
	    			this.setRotationalVelocity(0.5);
	    			behavior_pattern = "found";
				}
				else
				{
					this.setTranslationalVelocity(-0.3);
	    			this.setRotationalVelocity(-0.5);
				}
			}
			
			//avoid left
			else if(sonars.hasHit(2) && sonars.getMeasurement(2) < range_of_sonars)
			{
				if(this.foundCube() == true)
				{
					this.setTranslationalVelocity(0.3);
	    			this.setRotationalVelocity(0.8);
	    			behavior_pattern = "found";
				}
				else
				{
					this.setTranslationalVelocity(0.1);
	    			this.setRotationalVelocity(-0.5);
				}
				
			}
			
			//avoid front right
			else if(sonars.hasHit(7) && sonars.getMeasurement(7) < range_of_sonars)
			{
				if(this.foundCube() == true)
				{
					this.setTranslationalVelocity(0.3);
	    			this.setRotationalVelocity(-0.5);
	    			behavior_pattern = "found";
				}
				else
				{
					this.setTranslationalVelocity(-0.3);
	    			this.setRotationalVelocity(0.5);
				}
			}
			
			//avoid right
			else if(sonars.hasHit(6) && sonars.getMeasurement(6) < range_of_sonars)
			{
				if(this.foundCube() == true)
				{
					this.setTranslationalVelocity(0.3);
	    			this.setRotationalVelocity(-0.8);
	    			behavior_pattern = "found";
				}
				else
				{
					this.setTranslationalVelocity(0.1);
	    			this.setRotationalVelocity(0.5);
				}
			}
			
	    	else {
	    		// the robot's speed is always 0.5 m/s
	            this.setTranslationalVelocity(0.5);
	            this.setRotationalVelocity(0);
	    	}
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
		return CS.found_obstacle(position,camera_image);
    }
    
	public String get_behavior() {
		return behavior_pattern;
	}
};