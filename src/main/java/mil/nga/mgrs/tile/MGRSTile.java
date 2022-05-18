package mil.nga.mgrs.tile;

import mil.nga.mgrs.MGRSUtils;
import mil.nga.mgrs.features.Bounds;
import mil.nga.mgrs.features.Point;
import mil.nga.mgrs.features.Unit;

/**
 * Military Grid Reference System Tile
 * 
 * @author wnewman
 * @author osbornb
 */
public class MGRSTile {

	/**
	 * Tile width
	 */
	private int width;

	/**
	 * Tile height
	 */
	private int height;

	/**
	 * X coordinate
	 */
	private int x;

	/**
	 * Y coordinate
	 */
	private int y;

	/**
	 * Zoom level
	 */
	private int zoom;

	/**
	 * Bounds
	 */
	private Bounds bounds;

	/**
	 * Create a tile
	 * 
	 * @param width
	 *            tile width
	 * @param height
	 *            tile height
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param zoom
	 *            zoom level
	 * @return tile
	 */
	public static MGRSTile create(int width, int height, int x, int y,
			int zoom) {
		return new MGRSTile(width, height, x, y, zoom);
	}

	/**
	 * Constructor
	 * 
	 * @param width
	 *            tile width
	 * @param height
	 *            tile height
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param zoom
	 *            zoom level
	 */
	public MGRSTile(int width, int height, int x, int y, int zoom) {
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
		this.zoom = zoom;
		this.bounds = MGRSUtils.getBounds(x, y, zoom);
	}

	/**
	 * Get the tile width
	 * 
	 * @return tile width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the tile height
	 * 
	 * @return tile height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get the x coordinate
	 * 
	 * @return x coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the y coordinate
	 * 
	 * @return y coordinate
	 */
	public int getY() {
		return y;
	}

	/**
	 * Get the zoom level
	 * 
	 * @return zoom level
	 */
	public int getZoom() {
		return zoom;
	}

	/**
	 * Get the tile bounds
	 * 
	 * @return bounds
	 */
	public Bounds getBounds() {
		return bounds;
	}

	/**
	 * Get the bounds in the units
	 * 
	 * @param unit
	 *            units
	 * @return bounds in units
	 */
	public Bounds getBounds(Unit unit) {
		return bounds.toUnit(unit);
	}

	/**
	 * Get the bounds in degrees
	 * 
	 * @return bounds in degrees
	 */
	public Bounds getBoundsDegrees() {
		return getBounds(Unit.DEGREE);
	}

	/**
	 * Get the bounds in meters
	 * 
	 * @return bounds in meters
	 */
	public Bounds getBoundsMeters() {
		return getBounds(Unit.METER);
	}

	/**
	 * Get the point pixel location in the tile
	 * 
	 * @param point
	 *            point
	 * @return pixel
	 */
	public Pixel getPixel(Point point) {
		return MGRSUtils.getPixel(width, height, bounds, point);
	}

	/**
	 * Get the longitude in meters x pixel location in the tile
	 * 
	 * @param longitude
	 *            longitude in meters
	 * @return x pixel
	 */
	public float getXPixel(double longitude) {
		return MGRSUtils.getXPixel(width, bounds, longitude);
	}

	/**
	 * Get the latitude (in meters) y pixel location in the tile
	 * 
	 * @param latitude
	 *            latitude in meters
	 * @return y pixel
	 */
	public float getYPixel(double latitude) {
		return MGRSUtils.getYPixel(height, bounds, latitude);
	}

}