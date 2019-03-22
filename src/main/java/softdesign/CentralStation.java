//--------------------------------------------------------
//Code generated by Papyrus Java
//--------------------------------------------------------

package main.java.softdesign;

import java.awt.image.BufferedImage;

import javax.vecmath.Vector3d;

import simbad.sim.RangeSensorBelt;

/************************************************************/
/**
* 
*/
public class CentralStation {
	/**
	 * 
	 */
	private Robot[] robots;
	/**
	 * 
	 */
	private static CentralStation central_station = new CentralStation();
	/**
	 * 
	 */
	private static String[] behavior_patterns;
	private Color object_color;
	
	private int object_counter = 0;
	/**
	 * 
	 */
	private FileServer file_server;
	/**
	 * 
	 */
	private Coordinates[] starting_positions;
	/**
	 * 
	 */
	private Color goal_color;
	/**
	 * 
	 */
	public static CentralStation getinstance() {
		return central_station;
	}
	
	/**
	 * 
	 * @param position
	 * @param name
	 * @param count
	 */
	public Robot deploy_robot(Vector3d position, String name) {
		//parse name to find robot's number
		int robots_number = Integer.parseInt(name.replaceAll("\\D", ""));
	
		//instantiate new robot and add it to the robots array
		robots[robots_number - 1] = new Robot(position, name);
		robots[robots_number - 1].initBehavior();
		
		//store robot's position
		starting_positions[robots_number - 1] = new Coordinates(position.x, position.z);
		
		//remove the robot's current position 
		file_server.remove_coordinates(new Coordinates(position.x, position.z));
		return robots[robots_number - 1];
	}
	
	/**
	 * 
	 * @param color 
	 * @param position_color_found 
	 */
	public void start_mission(Color color) {
		//Set each robot's behavior pattern
		robots[0].set_behavior(behavior_patterns[0]);
		robots[1].set_behavior(behavior_patterns[0]);
		goal_color = color; 
	}
	
	public Coordinates[] get_starting_positions() {
		return starting_positions;
	}
	
	public void spiral(Robot robot, Coordinates coordinates, Coordinates prev) {
		Coordinates next_coordinates = get_next_coordinates(coordinates, prev), left = get_left_coordinates(coordinates, prev);

		if(file_server.visited(new Coordinates(coordinates.x + 0.5, coordinates.y)) && file_server.visited(new Coordinates(coordinates.x - 0.5, coordinates.y)) 
				&& file_server.visited(new Coordinates(coordinates.x, coordinates.y + 0.5)) && file_server.visited(new Coordinates(coordinates.x, coordinates.y - 0.5))) {
			robot.stop();
			robot.set_behavior(behavior_patterns[4]);
			if(robots[0].get_behavior() == behavior_patterns[3] && robots[1].get_behavior() == behavior_patterns[3])
				done_mapping();
		} else {
			if (!file_server.visited(left))
				robot.turn_left();
			else if (file_server.visited(next_coordinates))
				robot.turn_right();
		}
	}
	
	/**
	 * 
	 * @param robot
	 * @param position
	 */
	public void update_coordinates(Robot robot, Coordinates coordinates) {
		
		if (!file_server.visited(coordinates))
			file_server.remove_coordinates(coordinates);
				
	}
	
	private Coordinates get_left_coordinates(Coordinates coordinates, Coordinates prev) {

		if(coordinates.x - prev.x > 0) 
			return new Coordinates(coordinates.x, coordinates.y - 0.5);
		else if(coordinates.x - prev.x < 0)
			return new Coordinates(coordinates.x, coordinates.y + 0.5);
		else if(coordinates.y - prev.y > 0)
			return new Coordinates(coordinates.x + 0.5, coordinates.y);
		else
			return new Coordinates(coordinates.x - 0.5, coordinates.y);
		
	}
	
	private Coordinates get_next_coordinates(Coordinates coordinates, Coordinates prev) {
	
		if(coordinates.x - prev.x > 0)
			return new Coordinates(coordinates.x + 0.5, coordinates.y);
		else if(coordinates.x - prev.x < 0)
			return new Coordinates(coordinates.x - 0.5, coordinates.y);
		else if(coordinates.y - prev.y > 0)
			return new Coordinates(coordinates.x, coordinates.y + 0.5);
		else
			return new Coordinates(coordinates.x, coordinates.y - 0.5);
		
	}
	
	//removes coordinates to left of robot from unvisited array
	public void remove_left_coords(Coordinates coordinates, Coordinates prev) {
		Coordinates left, left_left;
		if(coordinates.x - prev.x > 0) {
			left = new Coordinates(coordinates.x, coordinates.y - 0.5);
			left_left = new Coordinates(coordinates.x, coordinates.y - 1.0);
		}
		else if(coordinates.x - prev.x < 0) {
			left = new Coordinates(coordinates.x, coordinates.y + 0.5);
			left_left = new Coordinates(coordinates.x, coordinates.y + 1.0);
		}
		else if(coordinates.y - prev.y > 0) {
			left = new Coordinates(coordinates.x + 0.5, coordinates.y);
			left_left = new Coordinates(coordinates.x + 1.0, coordinates.y);
		}
		else {
			left = new Coordinates(coordinates.x - 0.5, coordinates.y);
			left_left = new Coordinates(coordinates.x - 1.0, coordinates.y);
		}
			
		file_server.remove_coordinates(left);
		
		if(left_left.x >= -12.5 && left_left.x <= 12.5 && left_left.y >= -12.5 && left_left.y <= 12.5 )
			file_server.remove_coordinates(left_left);
	}

	public void map_object(Coordinates[] coordinates) {
		
		//   OBJECT IN INVERTED SIMBAD AXIS (Origin,(vector) y, (vector) x)
		//
		//      x  _______
		//        |       | 
		//        | Object| length  
		//        |       |
		// origin |_______| y 
		//          width
		//
		Coordinates origin = coordinates[0], x = new Coordinates(0,0), y = new Coordinates(0,0);
		double length = 0, width = 0;
		int directionx, directiony;
		
		for (int i = 1; i < 4; i++) { // getting the values of x and y
			if(origin.x == coordinates[i].x) {
				x = coordinates[i];
				length = Math.abs(Math.abs(origin.y) - Math.abs(x.y));
			}
			if(origin.y == coordinates[i].y) {
				y = coordinates[i];
				width = Math.abs(Math.abs(origin.x) - Math.abs(y.x));
			}
		}
		
		
		if(origin.y > x.y) // TODO : Meaningful comments 
			directiony = -1;
		else
			directiony = 1;
		
		if(origin.x > y.x)
			directionx = -1;
		else 
			directionx = 1;
		
		int array_size = (int)((width+0.5)/0.5) *(int)((length+0.5)/0.5);
		file_server.objects[object_counter] = new Object(array_size);
		file_server.objects[object_counter].color = object_color;
		int counter = 0;
		
		for (double i = 0; i <= width; i+= 0.5){ // removing the coordinates occupied by the object from the unvisited array
			for (double j = 0; j <= length; j+=0.5){
				Coordinates new_coordinates = new Coordinates(origin.x +i * directionx, origin.y + j * directiony);
				file_server.remove_coordinates(new_coordinates);
				file_server.objects[object_counter].coordinates_array[counter] = new_coordinates;
				counter++;
			}
		}
		object_counter++;

	}
	/**
	 * 
	 * @param coordinates 
	 * @param color 
	 * @return 
	 */
	public void found_object(Coordinates coordinates, BufferedImage cameraImage) {
		
		int rgb_value = cameraImage.getRGB(cameraImage.getHeight() - 1, cameraImage.getWidth()/2);
		
		int blue = rgb_value & 0xff;
		int green = (rgb_value & 0xff00) >> 8;
		int red = (rgb_value & 0xff0000) >> 16;
		
		Color color = new Color(red,green,blue);
		
		object_color = color;
		
		if (goal_color.detect_color() == color.detect_color())
			System.out.println("Found an object of the right color: " + color.detect_color());
		
	}
	
	public Coordinates get_unvisited(Coordinates robot_position){
		return file_server.get_coordinates(robot_position);
	}
	
	
	public void found_obstacle(Robot robot, RangeSensorBelt sonars){
		
		//Hitting dead end 
		if (sonars.hasHit(2) && sonars.hasHit(6) && sonars.hasHit(0)){
			//If there's no space on either side, turn around
			if (sonars.getMeasurement(6) < 0.5 && sonars.getMeasurement(2) < 0.5)
				robot.turn_around();
			//if enough space on right, turn right
			else if (sonars.getMeasurement(6) > 0.5)
				robot.turn_right();
			//if enough space on left, turn left
			else if (sonars.getMeasurement(2) > 0.5)
				robot.turn_left();
		}
		//When left is blocked, turn right
		else if (sonars.hasHit(2) && !sonars.hasHit(6)){
			robot.turn_right();
		}
		//When right is blocked, turn left
		else if (!sonars.hasHit(2) && sonars.hasHit(6)){
			robot.turn_left();
		}
		//When front is blocked
		else if (!sonars.hasHit(2) && !sonars.hasHit(6))
		{	
			
			/*	    _________
			 * 	   |
			 * 	    O
			 * 	   |			turns right before wall
			 */
			if(sonars.hasHit(1) && !sonars.hasHit(4))
				robot.turn_right();
			
			/*  _________
			 *    	     |
			 *  	    O	
			 * 	    	 |		turns left before wall
			 */
			else if(sonars.hasHit(7) && !sonars.hasHit(4))
				robot.turn_left();
			//When front left is blocked, turn right (there's a gap in the wall on the front right)
			else if(sonars.hasHit(1) && !sonars.hasHit(7))
				robot.turn_right();
			//When front right is blocked, turn left (there's a gap in the wall on the front left)
			else if(sonars.hasHit(7) && !sonars.hasHit(1))
				robot.turn_left();
		}
	}

	/**
	 * 
	 */
	private CentralStation() {
		//instantiating the robots array to hold maximum of 2 robots
		robots = new Robot[2];
		
		//instantiating array to store current position of robots as coordinates for maximum 2 robots
		starting_positions = new Coordinates[2];
		
		//Instantiating array with all possible behavior patterns
		behavior_patterns = new String[5];
		behavior_patterns[0] = "follow_wall";
		behavior_patterns[1] = "spiral";
		behavior_patterns[2] = "stop";
		behavior_patterns[3] = "finished";
		behavior_patterns[4] = "clean_up";
		
		//getting instance of File Server
		file_server = FileServer.getinstance();
		
	}

	/**
	 * 
	 */
	public void done_mapping() {
		robots[0].stop();
		robots[1].stop();
		robots[0].set_behavior(behavior_patterns[2]);
		robots[1].set_behavior(behavior_patterns[2]);
		file_server.count();
	}
	
	public void stop_mission() {
		robots[0].stop();
		robots[1].stop();
		robots[0].set_behavior(behavior_patterns[2]);
		robots[1].set_behavior(behavior_patterns[2]);
		System.out.println("Mission Stopped.");
	}
};