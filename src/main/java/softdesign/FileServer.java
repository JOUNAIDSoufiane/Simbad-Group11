//--------------------------------------------------------
//Code generated by Papyrus Java
//--------------------------------------------------------

package main.java.softdesign;


/************************************************************/
/**
* 
*/
public class FileServer {
	/**
	 * 
	 */
	public Coordinates[] unvisited = new Coordinates[10201];
	/**
	 * 
	 */
	public Coordinates[] blocked;
	/**
	 * 
	 */
	public Object[] objects;

	/**
	 * 
	 * @param coordinates 
	 */
	public void update_blocked(Coordinates coordinates) {
	}
	
	/**
	 * 
	 * @param coordinates
	 */
	public void remove_coordinates(Coordinates coordinates) {
		
		if (coordinates.x > 0)
			coordinates.x *= -1;
		if (coordinates.y > 0)
			coordinates.y *= -1;
		
		//replace already visited coordinate in array with coordinate 99,99
		int location = (int) (((25 + coordinates.x) / 0.5 * 101) + (25 + coordinates.y) / 0.5); 
		unvisited[location].x = 99;
		unvisited[location].y = 99;
				
	}
	
	/**
	 * 
	 * @param central_station 
	 */
	public FileServer(CentralStation central_station) {
		
		//initialize unvisited array with all possible coordinates
		int count = 0;
		double x = -25;
		while (x <= 25) {
			double y = -25;
			while (y <= 25) {
				unvisited[count] = new Coordinates(x,y);
				y += 0.5;
				count++;
			}
			x += 0.5;
		}
	}
};
