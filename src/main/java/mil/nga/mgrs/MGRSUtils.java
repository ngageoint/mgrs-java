package mil.nga.mgrs;

import mil.nga.mgrs.features.LatLng;
import mil.nga.mgrs.features.Point;

/**
 * Created by wnewman on 11/17/16.
 */
public class MGRSUtils {

	/**
	 * Get the X pixel for where the longitude fits into the bounding box
	 *
	 * @param width
	 *            width
	 * @param boundingBox
	 *            bounding box
	 * @param x
	 *            x
	 * @return x pixel
	 */
	public static float getXPixel(long width, double[] boundingBox, double x) {

		double boxWidth = boundingBox[2] - boundingBox[0];
		double offset = x - boundingBox[0];
		double percentage = offset / boxWidth;
		float pixel = (float) (percentage * width);

		return pixel;
	}

	/**
	 * Get the Y pixel for where the latitude fits into the bounding box
	 *
	 * @param height
	 *            height
	 * @param boundingBox
	 *            bounding box
	 * @param y
	 *            y
	 * @return y pixel
	 */
	public static float getYPixel(long height, double[] boundingBox, double y) {

		double boxHeight = boundingBox[3] - boundingBox[1];
		double offset = boundingBox[3] - y;
		double percentage = offset / boxHeight;
		float pixel = (float) (percentage * height);

		return pixel;
	}

	/**
	 * Get the Web Mercator tile bounding box from the Google Maps API tile
	 * coordinates and zoom level
	 *
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param zoom
	 *            zoom level
	 * @return bounding box
	 */
	public static double[] getWebMercatorBoundingBox(long x, long y, int zoom) {

		int tilesPerSide = tilesPerSide(zoom);
		double tileSize = tileSize(tilesPerSide);

		double minLon = (-1 * MGRSConstants.WEB_MERCATOR_HALF_WORLD_WIDTH)
				+ (x * tileSize);
		double maxLon = (-1 * MGRSConstants.WEB_MERCATOR_HALF_WORLD_WIDTH)
				+ ((x + 1) * tileSize);
		double minLat = MGRSConstants.WEB_MERCATOR_HALF_WORLD_WIDTH
				- ((y + 1) * tileSize);
		double maxLat = MGRSConstants.WEB_MERCATOR_HALF_WORLD_WIDTH
				- (y * tileSize);

		return new double[] { minLon, minLat, maxLon, maxLat };
	}

	/**
	 * Get the tile bounding box from the Google Maps API tile coordinates and
	 * zoom level
	 *
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param zoom
	 *            zoom level
	 * @return bounding box
	 */
	public static double[] getBoundingBox(long x, long y, int zoom) {

		double[] bbox = new double[] { tileToLongitue(x, zoom),
				tileToLatitude(y + 1, zoom), tileToLongitue(x + 1, zoom),
				tileToLatitude(y, zoom), };

		return bbox;
	}

	private static double tileToLongitue(long x, long zoom) {
		return (x / Math.pow(2, zoom) * 360 - 180);
	}

	private static double tileToLatitude(long y, long zoom) {
		double n = Math.PI - 2 * Math.PI * y / Math.pow(2, zoom);
		return (180 / Math.PI * Math.atan(0.5 * (Math.exp(n) - Math.exp(-n))));
	}

	/**
	 * Get the tiles per side, width and height, at the zoom level
	 *
	 * @param zoom
	 *            zoom level
	 * @return tiles per side
	 */
	public static int tilesPerSide(int zoom) {
		return (int) Math.pow(2, zoom);
	}

	/**
	 * Get the tile size in meters
	 *
	 * @param tilesPerSide
	 *            tiles per side
	 * @return tile size
	 */
	public static double tileSize(int tilesPerSide) {
		return (2 * MGRSConstants.WEB_MERCATOR_HALF_WORLD_WIDTH) / tilesPerSide;
	}

	/**
	 * Convert a WGS84 coordinate to a point in meters
	 * 
	 * @param latitude
	 *            WGS84 latitude
	 * @param longitude
	 *            WGS84 longitude
	 * @return point in meters
	 */
	public static Point toPoint(double latitude, double longitude) {
		double x = longitude * MGRSConstants.WEB_MERCATOR_HALF_WORLD_WIDTH
				/ 180;
		double y = Math.log(Math.tan((90 + latitude) * Math.PI / 360))
				/ (Math.PI / 180);
		y = y * MGRSConstants.WEB_MERCATOR_HALF_WORLD_WIDTH / 180;
		return new Point(x, y);
	}

	/**
	 * Convert a point in meters to a WGS84 coordinate
	 * 
	 * @param x
	 *            x value
	 * @param y
	 *            y value
	 * @return WGS84 coordinate
	 */
	public static LatLng toLatLng(double x, double y) {
		double longitude = x * 180
				/ MGRSConstants.WEB_MERCATOR_HALF_WORLD_WIDTH;
		double latitude = y * 180 / MGRSConstants.WEB_MERCATOR_HALF_WORLD_WIDTH;
		latitude = Math.atan(Math.exp(latitude * (Math.PI / 180))) / Math.PI
				* 360 - 90;
		return new LatLng(latitude, longitude);
	}

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
				|| isOmittedBandLetter(letter)) {
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
		if (isOmittedBandLetter(letter)) {
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
		if (isOmittedBandLetter(letter)) {
			letter--;
		}
		return letter;
	}

	/**
	 * The the band letter an omitted letter
	 * {@link MGRSConstants#BAND_LETTER_OMIT_I} or
	 * {@link MGRSConstants#BAND_LETTER_OMIT_O}
	 * 
	 * @param letter
	 *            band letter
	 * @return true if omitted
	 */
	public static boolean isOmittedBandLetter(char letter) {
		return letter == MGRSConstants.BAND_LETTER_OMIT_I
				|| letter == MGRSConstants.BAND_LETTER_OMIT_O;
	}

}
