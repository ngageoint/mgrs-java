package mil.nga.mgrs.gzd;

import mil.nga.grid.Hemisphere;
import mil.nga.mgrs.MGRSUtils;

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
		setLetter(letter);
		this.south = south;
		this.north = north;
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
	 * Set the band letter
	 * 
	 * @param letter
	 *            band letter
	 */
	public void setLetter(char letter) {
		this.letter = letter;
		this.hemisphere = MGRSUtils.getHemisphere(letter);
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
	 * Set the southern latitude
	 * 
	 * @param south
	 *            southern latitude
	 */
	public void setSouth(double south) {
		this.south = south;
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
	 * Set the northern latitude
	 * 
	 * @param north
	 *            northern latitude
	 */
	public void setNorth(double north) {
		this.north = north;
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
