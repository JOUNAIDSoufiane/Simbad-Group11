package main.java.softdesign;


import java.awt.image.BufferedImage;


import javax.vecmath.Vector3d;

import simbad.sim.Agent;
import simbad.sim.CameraSensor;
import simbad.sim.RobotFactory;
import simbad.sim.RangeSensorBelt; 

public class Robot extends Agent {
	
	private CameraSensor camera;
    private BufferedImage cameraImage;
    private RangeSensorBelt sonars, bumper;
    private String behaivior_patter = "search";

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
    }

    /** This method is call cyclically (20 times per second) by the simulator engine. */
    public void performBehavior() {
    	
    	double range_of_sonars = 0.5;
    	
    	if(behaivior_patter == "search")
    	{
    		if(bumper.oneHasHit())
    		{
    			this.setTranslationalVelocity(-0.5);
    		}
    		//avoid in front
    	
    		else if(sonars.hasHit(0) && sonars.getMeasurement(0) < range_of_sonars)
    		{
    			if(this.foundButton() == true)
    			{
    				this.setTranslationalVelocity(0.3);
        			this.setRotationalVelocity(0);
    				behaivior_patter = "found";
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
    			if(this.foundButton() == true)
    			{
    				this.setTranslationalVelocity(0.3);
        			this.setRotationalVelocity(0.5);
    				behaivior_patter = "found";
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
    			if(this.foundButton() == true)
    			{
    				this.setTranslationalVelocity(0.3);
        			this.setRotationalVelocity(0.8);
    				behaivior_patter = "found";
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
    			if(this.foundButton() == true)
    			{
    				this.setTranslationalVelocity(0.3);
        			this.setRotationalVelocity(-0.5);
    				behaivior_patter = "found";
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
    			if(this.foundButton() == true)
    			{
    				this.setTranslationalVelocity(0.3);
        			this.setRotationalVelocity(-0.8);
    				behaivior_patter = "found";
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
    	
    	if(behaivior_patter == "found")
		{
			this.setTranslationalVelocity(0);
            this.setRotationalVelocity(0);
			
		}
    	
    	
    }
    
    public boolean foundButton()
    {
		camera.copyVisionImage(cameraImage);
		int color = cameraImage.getRGB(cameraImage.getHeight()/2, cameraImage.getWidth()/2);
		int blue = color & 0xff;
		int green = (color & 0xff00) >> 8;
		int red = (color & 0xff0000) >> 16;
		if(red > 250 && green < 50 && blue < 50)
		{
			System.out.println("picture taken " + red);
			return true;
		}
		
		else
		{
			return false;
		}
	
    }
}