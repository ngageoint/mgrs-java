package mil.nga.mgrs.gzd;

/**
 * Grid enumeration
 * 
 * @author wnewman
 * @author osbornb
 */
public enum Grid {

	/**
	 * Ten Meter
	 */
	TEN_METER(10, 18, Integer.MAX_VALUE),

	/**
	 * Hundred Meter
	 */
	HUNDRED_METER(100, 15, 17),

	/**
	 * Kilometer
	 */
	KILOMETER(1000, 12, 14),

	/**
	 * Ten Kilometer
	 */
	TEN_KILOMETER(10000, 9, 11),

	/**
	 * Hundred Kilometer
	 */
	HUNDRED_KILOMETER(100000, 5, Integer.MAX_VALUE),

	/**
	 * Grid Zone Designator
	 */
	GZD(0, 0, Integer.MAX_VALUE);

	/**
	 * Grid precision in meters
	 */
	public int precision;

	/**
	 * Minimum zoom level
	 */
	private int minZoom;

	/**
	 * Maximum zoom level
	 */
	private int maxZoom;

	/**
	 * Constructor
	 * 
	 * @param precision
	 *            precision in meters
	 * @param minZoom
	 *            minimum zoom level
	 * @param maxZoom
	 *            maximum zoom level
	 */
	private Grid(int precision, int minZoom, int maxZoom) {
		this.precision = precision;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
	}

	/**
	 * Get the precision in meters
	 * 
	 * @return precision meters
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * Get the minimum zoom level
	 * 
	 * @return minimum zoom level
	 */
	public int getMinZoom() {
		return minZoom;
	}

	/**
	 * Get the maximum zoom level
	 * 
	 * @return maximum zoom level
	 */
	public int getMaxZoom() {
		return maxZoom;
	}

	/**
	 * Is the zoom level within the grid zoom range
	 * 
	 * @param zoom
	 *            zoom level
	 * @return true if within range
	 */
	public boolean isWithin(int zoom) {
		return zoom >= minZoom && zoom <= maxZoom;
	}

}
