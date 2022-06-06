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
	TEN_METER(10),

	/**
	 * Meter
	 */
	METER(1);

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

	/**
	 * Get the Grid Type accuracy number of digits in the easting and northing
	 * values
	 * 
	 * @return accuracy digits
	 */
	public int getAccuracy() {
		return Math.max(ordinal() - 1, 0);
	}

	/**
	 * Get the Grid Type with the accuracy number of digits in the easting and
	 * northing values. Accuracy must be inclusively between 0
	 * ({@link GridType#HUNDRED_KILOMETER}) and 5 ({@link GridType#METER}).
	 * 
	 * @param accuracy
	 *            accuracy digits between 0 (inclusive) and 5 (inclusive)
	 * @return grid type
	 */
	public static GridType withAccuracy(int accuracy) {
		if (accuracy < 0 || accuracy > 5) {
			throw new IllegalArgumentException(
					"Grid Type accuracy digits must be >= 0 and <= 5. accuracy digits: "
							+ accuracy);
		}
		return values()[accuracy + 1];
	}

}
