package main.java.softdesign;

import java.awt.Color;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import simbad.sim.Arch;
import simbad.sim.Box;
import simbad.sim.EnvironmentDescription;
import simbad.sim.Wall;

public class Environment extends EnvironmentDescription {
	public Environment() {
		
		// turn on the lights
        this.light1IsOn = true;
        this.light2IsOn = true;
        
        // enable the physics engine in order to have better physics effects on the objects
        this.setUsePhysics(true);
        
        // show the axes so that we know where things are
        this.showAxis(true);
        
        this.setWorldSize(25);
        
        Wall w1 = new Wall(new Vector3d(-12.5, 0, 0), 25, 2, this);
        w1.setColor(new Color3f(Color.BLUE));
        w1.rotate90(1);
        add(w1);
        
        Wall w2 = new Wall(new Vector3d(12.5, 0, 0), 25, 2, this);
        w2.setColor(new Color3f(Color.BLUE));
        w2.rotate90(1);
        add(w2);
        
        Wall w3 = new Wall(new Vector3d(0, 0, 12.5), 25, 2, this);
        w3.setColor(new Color3f(Color.BLUE));
        add(w3);
        
        Wall w4 = new Wall(new Vector3d(0, 0, -12.5), 25, 2, this);
        w4.setColor(new Color3f(Color.BLUE));
        add(w4);
        
        Wall room1Wall1 = new Wall(new Vector3d(0, 0, -10), 5, 1, this);
        room1Wall1.setColor(new Color3f(Color.BLUE));
        room1Wall1.rotate90(1);
        add(room1Wall1);
        
        Wall room1Wall2 = new Wall(new Vector3d(6.25, 0, -10), 5, 1, this);
        room1Wall2.setColor(new Color3f(Color.BLUE));
        room1Wall2.rotate90(1);
        add(room1Wall2);
        
        Wall room1Wall3 = new Wall(new Vector3d(6.25, 0, -7.5), 6, 1, this);
        room1Wall3.setColor(new Color3f(Color.BLUE));
        add(room1Wall3);
        
        Wall room1wall4 = new Wall(new Vector3d(0.4, 0, -7.5), 1, 1, this);
        room1wall4.setColor(new Color3f(Color.BLUE));
        add(room1wall4);

        Wall room2Wall2 = new Wall(new Vector3d(7.5, 0, -4), 7, 1, this);
        room2Wall2.setColor(new Color3f(Color.BLUE));
        room2Wall2.rotate90(1);
        add(room2Wall2);
        
        Wall room2Wall3 = new Wall(new Vector3d(7.9, 0, -0.5), 1, 1, this);
        room2Wall3.setColor(new Color3f(Color.BLUE));
        add(room2Wall3);
        
        Wall room2Wall4 = new Wall(new Vector3d(12, 0, -0.5), 1, 1, this);
        room2Wall4.setColor(new Color3f(Color.BLUE));
        add(room2Wall4);
        
        Wall room3Wall1 = new Wall(new Vector3d(-8.5, 0, 4), 8, 1, this);
        room3Wall1.setColor(new Color3f(Color.BLUE));
        add(room3Wall1);
        
        Wall room3Wall2 = new Wall(new Vector3d(-8.5, 0, -4), 8, 1, this);
        room3Wall2.setColor(new Color3f(Color.BLUE));
        add(room3Wall2);
        
        Wall room3Wall3 = new Wall(new Vector3d(-4.5, 0, 3.15), 2, 1, this);
        room3Wall3.setColor(new Color3f(Color.BLUE));
        room3Wall3.rotate90(1);
        add(room3Wall3);
        
        Wall room3Wall4 = new Wall(new Vector3d(-4.5, 0, -2.5), 3, 1, this);
        room3Wall4.setColor(new Color3f(Color.BLUE));
        room3Wall4.rotate90(1);
        add(room3Wall4);
        
        Wall room4Wall1 = new Wall(new Vector3d(-10.5, 0, 2), 4, 1, this);
        room4Wall1.setColor(new Color3f(Color.BLUE));
        add(room4Wall1);
        
        Wall room4Wall2 = new Wall(new Vector3d(-8.5, 0, 0.15), 4, 1, this);
        room4Wall2.setColor(new Color3f(Color.BLUE));
        room4Wall2.rotate90(1);
        add(room4Wall2);
        
        Wall room4Wall3 = new Wall(new Vector3d(-12, 0, -2), 1, 1, this);
        room4Wall3.setColor(new Color3f(Color.BLUE));
        add(room4Wall3);
        
        Wall room4Wall4 = new Wall(new Vector3d(-8.84, 0, -2), 1, 1, this);
        room4Wall4.setColor(new Color3f(Color.BLUE));
        add(room4Wall4);
        
        Wall Wall1 = new Wall(new Vector3d(2, 0, 5.5), 14, 1, this);
        Wall1.setColor(new Color3f(Color.BLUE));
        Wall1.rotate90(1);
        add(Wall1);
        
        Box box1 = new Box(new Vector3d(-5, 0, 10), new Vector3f(1, 1, 1), this);
        box1.setColor(new Color3f(Color.GREEN));
        add(box1);
        
        Box box2 = new Box(new Vector3d(10, 0, 5), new Vector3f(1, 1, 1), this);
        box2.setColor(new Color3f(Color.BLACK));
        add(box2);
        
        Box box3 = new Box(new Vector3d(5, 0, 5), new Vector3f(2, 1, 2), this);
        box3.setColor(new Color3f(Color.ORANGE));
        add(box3);
        
        Box button = new Box(new Vector3d(-9, 0, -9), new Vector3f(3, 1, 3), this);
        button.setColor(new Color3f(Color.RED));
        add(button);
//        
//        Arch arch1 = new Arch(new Vector3d(-5, 0, 9), this);
//        arch1.rotate90(1);
//        add(arch1);
        
    }
	
}
