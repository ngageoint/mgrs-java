package mil.nga.mgrs.features;

/**
 * Point in meters
 * 
 * @author wnewman
 * @author osbornb
 */
public class Point {

	/**
	 * X value
	 */
	public double x;

	/**
	 * Y value
	 */
	public double y;

	/**
	 * Constructor
	 * 
	 * @param x
	 *            x value
	 * @param y
	 *            y value
	 */
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Get the x value
	 * 
	 * @return x value
	 */
	public double getX() {
		return x;
	}

	/**
	 * Set the x value
	 * 
	 * @param x
	 *            x value
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Get the y value
	 * 
	 * @return y value
	 */
	public double getY() {
		return y;
	}

	/**
	 * Set the y value
	 * 
	 * @param y
	 *            y value
	 */
	public void setY(double y) {
		this.y = y;
	}

}
