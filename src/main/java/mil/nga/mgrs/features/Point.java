package mil.nga.mgrs.features;

import mil.nga.mgrs.MGRSUtils;

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
	private double x;

	/**
	 * Y value
	 */
	private double y;

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

	/**
	 * Convert to a WGS84 coordinate
	 * 
	 * @return coordinate
	 */
	public LatLng toLatLng() {
		return toLatLng(x, y);
	}

	/**
	 * Convert a point to a WGS84 coordinate
	 * 
	 * @param x
	 *            x value
	 * @param y
	 *            y value
	 * @return coordinate
	 */
	public static LatLng toLatLng(double x, double y) {
		return MGRSUtils.toLatLng(x, y);
	}

}
