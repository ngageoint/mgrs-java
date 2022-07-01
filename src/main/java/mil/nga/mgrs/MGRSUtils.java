package mil.nga.mgrs;

import mil.nga.grid.GridUtils;

/**
 * Military Grid Reference System utilities
 * 
 * @author wnewman
 * @author osbornb
 */
public class MGRSUtils {

	/**
	 * Validate the zone number
	 * 
	 * @param number
	 *            zone number
	 */
	public static void validateZoneNumber(int number) {
		if (number < MGRSConstants.MIN_ZONE_NUMBER
				|| number > MGRSConstants.MAX_ZONE_NUMBER) {
			throw new IllegalArgumentException("Illegal zone number (expected "
					+ MGRSConstants.MIN_ZONE_NUMBER + " - "
					+ MGRSConstants.MAX_ZONE_NUMBER + "): " + number);
		}
	}

	/**
	 * Validate the band letter
	 * 
	 * @param letter
	 *            band letter
	 */
	public static void validateBandLetter(char letter) {
		if (letter < MGRSConstants.MIN_BAND_LETTER
				|| letter > MGRSConstants.MAX_BAND_LETTER
				|| GridUtils.isOmittedBandLetter(letter)) {
			throw new IllegalArgumentException(
					"Illegal band letter (CDEFGHJKLMNPQRSTUVWX): " + letter);
		}
	}

	/**
	 * Get the next band letter
	 * 
	 * @param letter
	 *            band letter
	 * @return next band letter, 'Y' ({@link MGRSConstants#MAX_BAND_LETTER} + 1)
	 *         if no next bands
	 */
	public static char nextBandLetter(char letter) {
		MGRSUtils.validateBandLetter(letter);
		letter++;
		if (GridUtils.isOmittedBandLetter(letter)) {
			letter++;
		}
		return letter;
	}

	/**
	 * Get the previous band letter
	 * 
	 * @param letter
	 *            band letter
	 * @return previous band letter, 'B' ({@link MGRSConstants#MIN_BAND_LETTER}
	 *         - 1) if no previous bands
	 */
	public static char previousBandLetter(char letter) {
		MGRSUtils.validateBandLetter(letter);
		letter--;
		if (GridUtils.isOmittedBandLetter(letter)) {
			letter--;
		}
		return letter;
	}

	/**
	 * Get the label name
	 * 
	 * @param zoneNumber
	 *            zone number
	 * @param bandLetter
	 *            band letter
	 * @return name
	 */
	public static String getLabelName(int zoneNumber, char bandLetter) {
		return String.valueOf(zoneNumber) + bandLetter;
	}

}
