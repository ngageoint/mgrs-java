package mil.nga.mgrs.grid;

import mil.nga.grid.Label;
import mil.nga.grid.features.Bounds;
import mil.nga.grid.features.Point;
import mil.nga.mgrs.MGRSUtils;

/**
 * MGRS Grid Label
 * 
 * @author wnewman
 * @author osbornb
 */
public class GridLabel extends Label {

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
	public GridLabel(Point center, Bounds bounds, int zoneNumber,
			char bandLetter) {
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
	public GridLabel(String name, Point center, Bounds bounds, int zoneNumber,
			char bandLetter) {
		super(name, center, bounds);
		this.zoneNumber = zoneNumber;
		this.bandLetter = bandLetter;
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
