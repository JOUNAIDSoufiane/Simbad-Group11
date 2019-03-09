//--------------------------------------------------------
//Code generated by Papyrus Java
//--------------------------------------------------------

package main.java.softdesign;

import java.awt.image.BufferedImage;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;


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
	/**
	 * 
	 */
	private FileServer file_server;
	/**
	 * 
	 */
	private Coordinates[] robots_positions;

	/**
	 * 
	 */
	private int obstacle_counter = 0;
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
	public Robot deploy_robot(Vector3d position, String name, int count) {
		robots[count - 1] = new Robot(position, name, central_station);
		robots[count - 1].initBehavior();
		robots_positions[count - 1] = new Coordinates(position.x, position.y);
		file_server.remove_coordinates(new Coordinates(position.x, position.y));
		return robots[count - 1];
	}
	
	/**
	 * 
	 * @param color 
	 * @param position_color_found 
	 */
	public void start_mission(Color color, Coordinates position_color_found) {
		
	}

	/**
	 * 
	 * @param coordinates 
	 * @param color 
	 * @return 
	 */
	public boolean found_obstacle(Point3d coordinates, BufferedImage cameraImage) {
		
		int color = cameraImage.getRGB(cameraImage.getHeight()/2, cameraImage.getWidth()/2);
		int blue = color & 0xff;
		int green = (color & 0xff00) >> 8;
		int red = (color & 0xff0000) >> 16;
		
		Color objColor = new Color(red,green,blue);
		
		Coordinates obstacle_coords = new Coordinates(coordinates.x,coordinates.y);
		
		Object found = new Object(obstacle_coords,objColor) ; // needs to be added to the file server
		
		file_server.objects[obstacle_counter] = found;
		System.out.println(file_server.objects[obstacle_counter].coordinates_array[0].x);
		
		obstacle_counter++;
		
		
				
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

	/**
	 * 
	 */
	public CentralStation() {
		robots = new Robot[10];
		robots_positions = new Coordinates[10];
		behavior_patterns = new String[7];
		behavior_patterns[0] = "search";
		behavior_patterns[1] = "follow_wall";
		behavior_patterns[2] = "spiral";
		behavior_patterns[3] = "stop";
		behavior_patterns[4] = "turn_right";
		behavior_patterns[5] = "turn_left";
		behavior_patterns[6] = "found";
		file_server = new FileServer(central_station);
		
	}

	/**
	 * 
	 */
	public void done_mapping() {
	}
};
