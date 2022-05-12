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
	 * Set the zone number
	 * 
	 * @param number
	 *            zone number
	 */
	public void setNumber(int number) {
		this.number = number;
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
	 * Set the western longitude
	 * 
	 * @param west
	 *            western longitude
	 */
	public void setWest(double west) {
		this.west = west;
	}

	/**
	 * Get the eastern longitude
	 * 
	 * @return eastern longitude
	 */
	public double getEast() {
		return east;
	}

	/**
	 * Set the eastern longitude
	 * 
	 * @param east
	 *            eastern longitude
	 */
	public void setEast(double east) {
		this.east = east;
	}

}
