//--------------------------------------------------------
//Code generated by Papyrus Java
//--------------------------------------------------------

package main.java.softdesign;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.vecmath.Point3d;


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
		
		Object found = new Object(coordinates,objColor); // needs to be added to the file server
		
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
	}

	/**
	 * 
	 */
	public void done_mapping() {
	}
};
