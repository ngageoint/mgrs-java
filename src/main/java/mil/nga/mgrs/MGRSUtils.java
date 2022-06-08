package mil.nga.mgrs;

import mil.nga.mgrs.features.Bounds;
import mil.nga.mgrs.features.Line;
import mil.nga.mgrs.features.Point;
import mil.nga.mgrs.features.Unit;
import mil.nga.mgrs.tile.Pixel;

/**
 * Military Grid Reference System utilities
 * 
 * @author wnewman
 * @author osbornb
 */
public class MGRSUtils {

	/**
	 * Get the pixel where the point fits into the bounds
	 * 
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @param bounds
	 *            bounds
	 * @param point
	 *            point
	 * @return pixel
	 */
	public static Pixel getPixel(int width, int height, Bounds bounds,
			Point point) {

		point = point.toMeters();
		bounds = bounds.toMeters();

		float x = getXPixel(width, bounds, point.getLongitude());
		float y = getYPixel(height, bounds, point.getLatitude());
		return new Pixel(x, y);
	}

	/**
	 * Get the X pixel for where the longitude in meters fits into the bounds
	 *
	 * @param width
	 *            width
	 * @param bounds
	 *            bounds
	 * @param longitude
	 *            longitude in meters
	 * @return x pixel
	 */
	public static float getXPixel(int width, Bounds bounds, double longitude) {

		bounds = bounds.toMeters();

		double boxWidth = bounds.getMaxLongitude() - bounds.getMinLongitude();
		double offset = longitude - bounds.getMinLongitude();
		double percentage = offset / boxWidth;
		float pixel = (float) (percentage * width);

		return pixel;
	}

	/**
	 * Get the Y pixel for where the latitude in meters fits into the bounds
	 *
	 * @param height
	 *            height
	 * @param bounds
	 *            bounds
	 * @param latitude
	 *            latitude
	 * @return y pixel
	 */
	public static float getYPixel(int height, Bounds bounds, double latitude) {

		bounds = bounds.toMeters();

		double boxHeight = bounds.getMaxLatitude() - bounds.getMinLatitude();
		double offset = bounds.getMaxLatitude() - latitude;
		double percentage = offset / boxHeight;
		float pixel = (float) (percentage * height);

		return pixel;
	}

	/**
	 * Get the tile bounds from the XYZ tile coordinates and zoom level
	 *
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param zoom
	 *            zoom level
	 * @return bounds
	 */
	public static Bounds getBounds(int x, int y, int zoom) {

		int tilesPerSide = tilesPerSide(zoom);
		double tileSize = tileSize(tilesPerSide);

		double minLon = (-1 * MGRSConstants.WEB_MERCATOR_HALF_WORLD_WIDTH)
				+ (x * tileSize);
		double minLat = MGRSConstants.WEB_MERCATOR_HALF_WORLD_WIDTH
				- ((y + 1) * tileSize);
		double maxLon = (-1 * MGRSConstants.WEB_MERCATOR_HALF_WORLD_WIDTH)
				+ ((x + 1) * tileSize);
		double maxLat = MGRSConstants.WEB_MERCATOR_HALF_WORLD_WIDTH
				- (y * tileSize);

		return Bounds.meters(minLon, minLat, maxLon, maxLat);
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
	 * Get the zoom level of the bounds using the shortest bounds side length
	 * 
	 * @param bounds
	 *            bounds
	 * @return zoom level
	 */
	public static double getZoomLevel(Bounds bounds) {
		bounds = bounds.toMeters();
		double tileSize = Math.min(bounds.getWidth(), bounds.getHeight());
		double tilesPerSide = 2 * MGRSConstants.WEB_MERCATOR_HALF_WORLD_WIDTH
				/ tileSize;
		return Math.log(tilesPerSide) / Math.log(2);
	}

	/**
	 * Convert a coordinate from a unit to another unit
	 * 
	 * @param fromUnit
	 *            unit of provided coordinate
	 * @param longitude
	 *            longitude
	 * @param latitude
	 *            latitude
	 * @param toUnit
	 *            desired unit
	 * @return point in unit
	 */
	public static Point toUnit(Unit fromUnit, double longitude, double latitude,
			Unit toUnit) {
		Point point = null;
		if (fromUnit == toUnit) {
			point = Point.create(longitude, latitude, toUnit);
		} else {
			point = toUnit(longitude, latitude, toUnit);
		}
		return point;
	}

	/**
	 * Convert a coordinate to the unit, assumes the coordinate is in the
	 * opposite unit
	 * 
	 * @param longitude
	 *            longitude
	 * @param latitude
	 *            latitude
	 * @param unit
	 *            desired unit
	 * @return point in unit
	 */
	public static Point toUnit(double longitude, double latitude, Unit unit) {
		Point point = null;
		switch (unit) {
		case DEGREE:
			point = toDegrees(longitude, latitude);
			break;
		case METER:
			point = toMeters(longitude, latitude);
			break;
		default:
			throw new IllegalArgumentException("Unsupported unit: " + unit);
		}
		return point;
	}

	/**
	 * Convert a WGS84 coordinate to a point in meters
	 * 
	 * @param longitude
	 *            WGS84 longitude
	 * @param latitude
	 *            WGS84 latitude
	 * @return point in meters
	 */
	public static Point toMeters(double longitude, double latitude) {
		double lon = longitude * MGRSConstants.WEB_MERCATOR_HALF_WORLD_WIDTH
				/ 180;
		double lat = Math.log(Math.tan((90 + latitude) * Math.PI / 360))
				/ (Math.PI / 180);
		lat = lat * MGRSConstants.WEB_MERCATOR_HALF_WORLD_WIDTH / 180;
		return Point.meters(lon, lat);
	}

	/**
	 * Convert a coordinate in meters to a WGS84 point
	 * 
	 * @param longitude
	 *            longitude in meters
	 * @param latitude
	 *            latitude in meters
	 * @return WGS84 coordinate
	 */
	public static Point toDegrees(double longitude, double latitude) {
		double lon = longitude * 180
				/ MGRSConstants.WEB_MERCATOR_HALF_WORLD_WIDTH;
		double lat = latitude * 180
				/ MGRSConstants.WEB_MERCATOR_HALF_WORLD_WIDTH;
		lat = Math.atan(Math.exp(lat * (Math.PI / 180))) / Math.PI * 360 - 90;
		return Point.degrees(lon, lat);
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

	/**
	 * Get the point intersection between two lines
	 * 
	 * @param line1
	 *            first line
	 * @param line2
	 *            second line
	 * @return intersection point or null if no intersection
	 */
	public static Point intersection(Line line1, Line line2) {
		return intsersection(line1.getPoint1(), line1.getPoint2(),
				line2.getPoint1(), line2.getPoint2());
	}

	/**
	 * Get the point intersection between end points of two lines
	 * 
	 * @param line1Point1
	 *            first point of the first line
	 * @param line1Point2
	 *            second point of the first line
	 * @param line2Point1
	 *            first point of the second line
	 * @param line2Point2
	 *            second point of the second line
	 * @return intersection point or null if no intersection
	 */
	public static Point intsersection(Point line1Point1, Point line1Point2,
			Point line2Point1, Point line2Point2) {

		Unit unit = line1Point1.getUnit();

		line1Point1 = line1Point1.toMeters();
		line1Point2 = line1Point2.toMeters();
		line2Point1 = line2Point1.toMeters();
		line2Point2 = line2Point2.toMeters();

		Point intersection = null;

		double a1 = line1Point2.getLatitude() - line1Point1.getLatitude();
		double b1 = line1Point1.getLongitude() - line1Point2.getLongitude();
		double c1 = a1 * (line1Point1.getLongitude())
				+ b1 * (line1Point1.getLatitude());

		double a2 = line2Point2.getLatitude() - line2Point1.getLatitude();
		double b2 = line2Point1.getLongitude() - line2Point2.getLongitude();
		double c2 = a2 * (line2Point1.getLongitude())
				+ b2 * (line2Point1.getLatitude());

		double determinant = a1 * b2 - a2 * b1;

		if (determinant != 0) {
			double x = (b2 * c1 - b1 * c2) / determinant;
			double y = (a1 * c2 - a2 * c1) / determinant;
			intersection = Point.meters(x, y).toUnit(unit);
		}

		return intersection;
	}

}
