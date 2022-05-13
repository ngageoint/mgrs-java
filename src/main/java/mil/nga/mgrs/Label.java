package mil.nga.mgrs;

import mil.nga.mgrs.features.Point;
import mil.nga.mgrs.gzd.Bounds;

/**
 * MGRS Label
 * 
 * @author wnewman
 * @author osbornb
 */
public class Label {

	/**
	 * Name
	 */
	private String name;

	/**
	 * Center point
	 */
	private Point center;

	/**
	 * Bounds
	 */
	private Bounds bounds;

	/**
	 * Zone number
	 */
	private int zoneNumber;

	/**
	 * Band letter
	 */
	private char bandLetter;

	/**
	 * Constructor
	 * 
	 * @param center
	 *            center point
	 * @param bounds
	 *            bounds
	 * @param zoneNumber
	 *            zone number
	 * @param bandLetter
	 *            band letter
	 */
	public Label(Point center, Bounds bounds, int zoneNumber, char bandLetter) {
		this(MGRSUtils.getLabelName(zoneNumber, bandLetter), center, bounds,
				zoneNumber, bandLetter);
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 *            name
	 * @param center
	 *            center point
	 * @param bounds
	 *            bounds
	 * @param zoneNumber
	 *            zone number
	 * @param bandLetter
	 *            band letter
	 */
	public Label(String name, Point center, Bounds bounds, int zoneNumber,
			char bandLetter) {
		this.name = name;
		this.center = center;
		this.bounds = bounds;
		this.zoneNumber = zoneNumber;
		this.bandLetter = bandLetter;
	}

	/**
	 * Get the name
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name
	 * 
	 * @param name
	 *            name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the center point
	 * 
	 * @return center point
	 */
	public Point getCenter() {
		return center;
	}

	/**
	 * Set the center point
	 * 
	 * @param center
	 *            center point
	 */
	public void setCenter(Point center) {
		this.center = center;
	}

	/**
	 * Get the bounds
	 * 
	 * @return bounds
	 */
	public Bounds getBounds() {
		return bounds;
	}

	/**
	 * Set the bounds
	 * 
	 * @param bounds
	 *            bounds
	 */
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	/**
	 * Get the zone number
	 * 
	 * @return zone number
	 */
	public int getZoneNumber() {
		return zoneNumber;
	}

	/**
	 * Set the zone number
	 * 
	 * @param zoneNumber
	 *            zone number
	 */
	public void setZoneNumber(int zoneNumber) {
		this.zoneNumber = zoneNumber;
	}

	/**
	 * Get the band letter
	 * 
	 * @return band letter
	 */
	public char getBandLetter() {
		return bandLetter;
	}

	/**
	 * Set the band letter
	 * 
	 * @param bandLetter
	 *            band letter
	 */
	public void setBandLetter(char bandLetter) {
		this.bandLetter = bandLetter;
	}

}
