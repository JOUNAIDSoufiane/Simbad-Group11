package main.java.softdesign;


import java.awt.image.BufferedImage;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import simbad.sim.Agent;
import simbad.sim.CameraSensor;
import simbad.sim.RobotFactory;
import simbad.sim.RangeSensorBelt; 

public class Robot extends Agent {
	
	private CameraSensor camera;
    private BufferedImage cameraImage;
    private RangeSensorBelt sonars, bumper;
    private String behavior_pattern = "search";
    private Point3d coordinates = new Point3d();

    public Robot(Vector3d position, String name) {
        super(position, name);
        
        // Add bumpers
        bumper = RobotFactory.addBumperBeltSensor(this);
        // Add sonars
        sonars = RobotFactory.addSonarBeltSensor(this, 8);
        //add camera
        camera = RobotFactory.addCameraSensor(this);
        // reserve space for image capture
        cameraImage = camera.createCompatibleImage();
    }

    /** This method is called by the simulator engine on reset. */
    public void initBehavior() {
        System.out.println("I exist and my name is " + this.name);
        behavior_pattern = "search";
    }

    /** This method is call cyclically (20 times per second) by the simulator engine. */
    public void performBehavior() {
    	
    	//rover will always keep a distance of at least 0.5m from any obstacle
    	double range_of_sonars = 0.5;
    	
    	this.getCoords(coordinates);
    	
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
    
    //returns true once a rover finds the red cube in the environment
    public boolean foundCube()
    {
		camera.copyVisionImage(cameraImage);
		int color = cameraImage.getRGB(cameraImage.getHeight()/2, cameraImage.getWidth()/2);
		int blue = color & 0xff;
		int green = (color & 0xff00) >> 8;
		int red = (color & 0xff0000) >> 16;
		
    	if(red > 250 && green < 50 && blue < 50) //these values are used to truly find red and not black
		{
			System.out.println("Picture taken " + red);
			return true;
		}
		
		else
		{
			return false;
		}
	
    }
}