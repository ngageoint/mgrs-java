package mil.nga.mgrs.utm;

import mil.nga.grid.features.Point;
import mil.nga.mgrs.MGRSConstants;

/**
 * Hemisphere enumeration
 * 
 * @author wnewman
 * @author osbornb
 */
public enum Hemisphere {

	/**
	 * Northern hemisphere
	 */
	NORTH,

	/**
	 * Southern hemisphere
	 */
	SOUTH;

	/**
	 * Get the hemisphere of the band letter
	 * 
	 * @param letter
	 *            band letter
	 * @return hemisphere
	 */
	public static Hemisphere fromBandLetter(char letter) {
		return letter < MGRSConstants.BAND_LETTER_NORTH ? Hemisphere.SOUTH
				: Hemisphere.NORTH;
	}

	/**
	 * Get the hemisphere for the latitude
	 * 
	 * @param latitude
	 *            latitude
	 * @return hemisphere
	 */
	public static Hemisphere fromLatitude(double latitude) {
		return latitude >= 0 ? Hemisphere.NORTH : Hemisphere.SOUTH;
	}

	/**
	 * Get the hemisphere for the point
	 * 
	 * @param point
	 *            point
	 * @return hemisphere
	 */
	public static Hemisphere from(Point point) {
		return fromLatitude(point.getLatitude());
	}

}