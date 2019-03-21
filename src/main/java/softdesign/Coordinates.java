//--------------------------------------------------------
//Code generated by Papyrus Java
//--------------------------------------------------------

package main.java.softdesign;

/************************************************************/
/**
* 
*/
public class Coordinates {
	/**
	 * 
	 */
	public double x;
	/**
	 * 
	 */
	public double y;

	/**
	 * 
	 * @param x 
	 * @param y 
	 */
	public Coordinates(double x, double y) {
		//flooring coordinates by intervals of 0.5 to correspond to implemented grid pattern size
		this.x = Math.floor(x * 2) / 2.0;
		this.y = Math.floor(y * 2) / 2.0;
	}
	public Coordinates(Coordinates copy) {
		this.x = copy.x;
		this.y = copy.y;
	}
};
