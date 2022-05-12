package mil.nga.mgrs.gzd;

import java.util.Iterator;

import mil.nga.mgrs.MGRSConstants;
import mil.nga.mgrs.MGRSUtils;

/**
 * Band Letter Range
 * 
 * @author osbornb
 */
public class BandLetterRange implements Iterable<Character> {

	/**
	 * Southern band letter
	 */
	private char south;

	/**
	 * Northern band letter
	 */
	private char north;

	/**
	 * Constructor, full range
	 */
	public BandLetterRange() {
		this(MGRSConstants.MIN_BAND_LETTER, MGRSConstants.MAX_BAND_LETTER);
	}

	/**
	 * Constructor
	 * 
	 * @param south
	 *            southern band letter
	 * @param north
	 *            northern band letter
	 */
	public BandLetterRange(char south, char north) {
		this.south = south;
		this.north = north;
	}

	/**
	 * Get the southern band letter
	 * 
	 * @return southern band letter
	 */
	public char getSouth() {
		return south;
	}

	/**
	 * Set the southern band letter
	 * 
	 * @param south
	 *            southern band letter
	 */
	public void setSouth(char south) {
		this.south = south;
	}

	/**
	 * Get the northern band letter
	 * 
	 * @return northern band letter
	 */
	public char getNorth() {
		return north;
	}

	/**
	 * Set the northern band letter
	 * 
	 * @param north
	 *            northern band letter
	 */
	public void setNorth(char north) {
		this.north = north;
	}

	/**
	 * Get the southern latitude
	 * 
	 * @return latitude
	 */
	public double getSouthLatitude() {
		return GridZones.getLatitudeBand(south).getSouth();
	}

	/**
	 * Get the northern latitude
	 * 
	 * @return latitude
	 */
	public double getNorthLatitude() {
		return GridZones.getLatitudeBand(north).getNorth();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Character> iterator() {
		return new Iterator<Character>() {

			/**
			 * Band letter
			 */
			private char letter = south;

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean hasNext() {
				return letter <= north;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Character next() {
				char value = letter;
				letter = MGRSUtils.nextBandLetter(letter);
				return value;
			}

		};
	}

}
