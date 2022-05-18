package mil.nga.mgrs.grid;

/**
 * Grid type enumeration
 * 
 * @author wnewman
 * @author osbornb
 */
public enum GridType {

	/**
	 * Grid Zone Designator
	 */
	GZD(0),

	/**
	 * Hundred Kilometer
	 */
	HUNDRED_KILOMETER(100000),

	/**
	 * Ten Kilometer
	 */
	TEN_KILOMETER(10000),

	/**
	 * Kilometer
	 */
	KILOMETER(1000),

	/**
	 * Hundred Meter
	 */
	HUNDRED_METER(100),

	/**
	 * Ten Meter
	 */
	TEN_METER(10);

	/**
	 * Grid precision in meters
	 */
	private int precision;

	/**
	 * Constructor
	 * 
	 * @param precision
	 *            precision in meters
	 */
	private GridType(int precision) {
		this.precision = precision;
	}

	/**
	 * Get the precision in meters
	 * 
	 * @return precision meters
	 */
	public int getPrecision() {
		return precision;
	}

}
