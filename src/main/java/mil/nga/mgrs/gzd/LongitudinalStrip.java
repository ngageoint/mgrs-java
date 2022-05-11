package mil.nga.mgrs.gzd;

/**
 * Longitudinal (vertical) strip
 * 
 * @author osbornb
 */
public class LongitudinalStrip {

	/**
	 * Zone number
	 */
	private int number;

	/**
	 * Western longitude
	 */
	private double west;

	/**
	 * Eastern longitude
	 */
	private double east;

	/**
	 * Constructor
	 * 
	 * @param number
	 *            zone number
	 * @param west
	 *            western longitude
	 * @param east
	 *            eastern longitude
	 */
	public LongitudinalStrip(int number, double west, double east) {
		this.number = number;
		this.west = west;
		this.east = east;
	}

	/**
	 * Get the zone number
	 * 
	 * @return zone number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Get the western longitude
	 * 
	 * @return western longitude
	 */
	public double getWest() {
		return west;
	}

	/**
	 * Get the eastern longitude
	 * 
	 * @return eastern longitude
	 */
	public double getEast() {
		return east;
	}

}
