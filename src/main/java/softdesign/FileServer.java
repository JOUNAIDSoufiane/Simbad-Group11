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
	private static FileServer file_server = new FileServer();
	/**
	 * 
	 */
	private Coordinates[] unvisited = new Coordinates[10201];
	/**
	 * 
	 */
	public Coordinates[] blocked = new Coordinates[10000];
	/**
	 * 
	 */
	public Object[] objects = new Object[10000];

	/**
	 * 
	 * 
	 */
	public static FileServer getinstance() {
		return file_server;
	}
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
		//replace already visited coordinate in array with coordinate 99,99
		int location = (int) (((25 + coordinates.x) / 0.5 * 101) + (25 + coordinates.y) / 0.5); 

		unvisited[location].x = 99;
		unvisited[location].y = 99;
	}
	
	/**
	 * 
	 * @param central_station 
	 */
	public FileServer() {
		
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
