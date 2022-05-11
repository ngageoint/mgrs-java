package mil.nga.mgrs.gzd;

import mil.nga.mgrs.utm.Hemisphere;

/**
 * Latitude (horizontal) band
 * 
 * @author osbornb
 */
public class LatitudeBand {

	/**
	 * Band letter
	 */
	private char letter;

	/**
	 * Southern latitude
	 */
	private double south;

	/**
	 * Northern latitude
	 */
	private double north;

	/**
	 * Hemisphere
	 */
	private Hemisphere hemisphere;

	/**
	 * Constructor
	 * 
	 * @param letter
	 *            band letter
	 * @param south
	 *            southern latitude
	 * @param north
	 *            northern latitude
	 */
	public LatitudeBand(char letter, double south, double north) {
		this.letter = letter;
		this.south = south;
		this.north = north;
		this.hemisphere = letter < 'N' ? Hemisphere.SOUTH : Hemisphere.NORTH;
	}

	/**
	 * Get the band letter
	 * 
	 * @return band letter
	 */
	public char getLetter() {
		return letter;
	}

	/**
	 * Get the southern latitude
	 * 
	 * @return southern latitude
	 */
	public double getSouth() {
		return south;
	}

	/**
	 * Get the northern latitude
	 * 
	 * @return northern latitude
	 */
	public double getNorth() {
		return north;
	}

	/**
	 * Get the hemisphere
	 * 
	 * @return hemisphere
	 */
	public Hemisphere getHemisphere() {
		return hemisphere;
	}

}
