package mil.nga.mgrs;

import mil.nga.grid.GridConstants;

/**
 * Military Grid Reference System Constants
 * 
 * @author osbornb
 */
public class MGRSConstants {

	/**
	 * Minimum longitude
	 */
	public static final double MIN_LON = GridConstants.MIN_LON;

	/**
	 * Maximum longitude
	 */
	public static final double MAX_LON = GridConstants.MAX_LON;

	/**
	 * Minimum latitude
	 */
	public static final double MIN_LAT = -80.0;

	/**
	 * Maximum latitude
	 */
	public static final double MAX_LAT = 84.0;

	/**
	 * Minimum grid zone number
	 */
	public static final int MIN_ZONE_NUMBER = 1;

	/**
	 * Maximum grid zone number
	 */
	public static final int MAX_ZONE_NUMBER = 60;

	/**
	 * Grid zone width
	 */
	public static final double ZONE_WIDTH = 6.0;

	/**
	 * Minimum grid band letter
	 */
	public static final char MIN_BAND_LETTER = 'C';

	/**
	 * Maximum grid band letter
	 */
	public static final char MAX_BAND_LETTER = 'X';

	/**
	 * Number of bands
	 */
	public static final int NUM_BANDS = 20;

	/**
	 * Grid band height for all by but the {@link #MAX_BAND_LETTER}
	 */
	public static final double BAND_HEIGHT = 8.0;

	/**
	 * Grid band height for the {@link #MAX_BAND_LETTER}
	 */
	public static final double MAX_BAND_HEIGHT = 12.0;

	/**
	 * Last southern hemisphere band letter
	 */
	public static final char BAND_LETTER_SOUTH = 'M';

	/**
	 * First northern hemisphere band letter
	 */
	public static final char BAND_LETTER_NORTH = 'N';

	/**
	 * Min zone number in Svalbard grid zones
	 */
	public static final int MIN_SVALBARD_ZONE_NUMBER = 31;

	/**
	 * Max zone number in Svalbard grid zones
	 */
	public static final int MAX_SVALBARD_ZONE_NUMBER = 37;

	/**
	 * Band letter in Svalbard grid zones
	 */
	public static final char SVALBARD_BAND_LETTER = MAX_BAND_LETTER;

	/**
	 * Min zone number in Norway grid zones
	 */
	public static final int MIN_NORWAY_ZONE_NUMBER = 31;

	/**
	 * Max zone number in Norway grid zones
	 */
	public static final int MAX_NORWAY_ZONE_NUMBER = 32;

	/**
	 * Band letter in Norway grid zones
	 */
	public static final char NORWAY_BAND_LETTER = 'V';

}
