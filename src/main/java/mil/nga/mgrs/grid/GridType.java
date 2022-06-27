package mil.nga.mgrs.grid;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

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

	/**
	 * Get the precision of the value in meters based upon trailing 0's
	 * 
	 * @param value
	 *            value in meters
	 * @return precision grid type
	 */
	public static GridType getPrecision(double value) {
		GridType precision = null;
		if (value % HUNDRED_KILOMETER.precision == 0) {
			precision = HUNDRED_KILOMETER;
		} else if (value % TEN_KILOMETER.precision == 0) {
			precision = TEN_KILOMETER;
		} else if (value % KILOMETER.precision == 0) {
			precision = KILOMETER;
		} else if (value % HUNDRED_METER.precision == 0) {
			precision = HUNDRED_METER;
		} else if (value % TEN_METER.precision == 0) {
			precision = TEN_METER;
		} else {
			precision = METER;
		}
		return precision;
	}

	/**
	 * Get the less precise (larger precision value) grid types
	 * 
	 * @param type
	 *            grid type
	 * @return grid types less precise
	 */
	public static Set<GridType> lessPrecise(GridType type) {
		GridType[] types = Arrays.copyOfRange(GridType.values(), 0,
				type.ordinal());
		return new LinkedHashSet<>(Arrays.asList(types));
	}

	/**
	 * Get the more precise (smaller precision value) grid types
	 * 
	 * @param type
	 *            grid type
	 * @return grid types more precise
	 */
	public static Set<GridType> morePrecise(GridType type) {
		GridType[] values = GridType.values();
		GridType[] types = Arrays.copyOfRange(values, type.ordinal() + 1,
				values.length);
		return new LinkedHashSet<>(Arrays.asList(types));
	}

}
