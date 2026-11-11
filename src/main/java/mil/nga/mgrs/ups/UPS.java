package mil.nga.mgrs.ups;

import mil.nga.grid.features.Point;

/**
 * Universal Polar Stereographic (UPS) on WGS84, used by MGRS north of 84 N
 * and south of 80 S (bands Y and Z in the north, A and B in the south).
 * <p>
 * Polar stereographic with scale 0.994 at the pole and a false easting and
 * northing of 2,000,000 m (NGA.STND.0037). The forward and inverse use the
 * conformal latitude as in Karney (2011), "Transverse Mercator with an
 * accuracy of a few nanometers", so they agree to well below a millimeter.
 *
 * @since 2.2.0
 */
public class UPS {

	/**
	 * Scale at the pole
	 */
	public static final double K0 = 0.994;

	/**
	 * False easting and northing (meters)
	 */
	public static final double FALSE_EASTING_NORTHING = 2000000;

	/**
	 * MGRS UPS band letters
	 */
	public static final String BAND_LETTERS = "ABYZ";

	/**
	 * 100km column letters for bands A, B, Y and Z
	 */
	private static final String[] COLUMN_LETTERS = { "JKLPQRSTUXYZ",
			"ABCFGHJKLPQR", "RSTUXYZ", "ABCFGHJ" };

	/**
	 * 100km row letters for the south (A, B) and north (Y, Z)
	 */
	private static final String[] ROW_LETTERS = { "ABCDEFGHJKLMNPQRSTUVWXYZ",
			"ABCDEFGHJKLMNP" };

	/**
	 * First 100km column index (easting / 100km) of bands A, B, Y and Z
	 */
	private static final int[] COLUMN_START = { 8, 20, 13, 20 };

	/**
	 * First 100km row index (northing / 100km) in the south and north
	 */
	private static final int[] ROW_START = { 8, 13 };

	private static final double A = 6378137.0;
	private static final double F = 1 / 298.257223563;
	private static final double E = Math.sqrt(F * (2 - F));
	private static final double C = Math.sqrt(
			Math.pow(1 + E, 1 + E) * Math.pow(1 - E, 1 - E));

	/**
	 * Check if a latitude is covered by UPS in MGRS (at or north of 84 N, or
	 * south of 80 S)
	 *
	 * @param latitude
	 *            latitude in degrees
	 * @return true if UPS
	 */
	public static boolean isPolar(double latitude) {
		return latitude >= 84 || latitude < -80;
	}

	/**
	 * Check if a band letter is a UPS band (A, B, Y or Z)
	 *
	 * @param band
	 *            band letter
	 * @return true if UPS
	 */
	public static boolean isBand(char band) {
		return BAND_LETTERS.indexOf(Character.toUpperCase(band)) >= 0;
	}

	/**
	 * Convert latitude and longitude to UPS easting and northing
	 *
	 * @param latitude
	 *            latitude in degrees
	 * @param longitude
	 *            longitude in degrees
	 * @return easting and northing
	 */
	public static double[] forward(double latitude, double longitude) {
		boolean north = latitude >= 0;
		double phi = Math.toRadians(Math.abs(latitude));
		double lam = Math.toRadians(longitude);
		double tauPrime = conformalTau(Math.tan(phi));
		// t = tan(pi/4 - chi/2) = sqrt(1 + tau'^2) - tau', without cancellation
		double t = Double.isInfinite(tauPrime) ? 0
				: 1 / (Math.sqrt(1 + tauPrime * tauPrime) + tauPrime);
		double rho = 2 * A * K0 * t / C;
		double easting = FALSE_EASTING_NORTHING + rho * Math.sin(lam);
		double northing = north
				? FALSE_EASTING_NORTHING - rho * Math.cos(lam)
				: FALSE_EASTING_NORTHING + rho * Math.cos(lam);
		return new double[] { easting, northing };
	}

	/**
	 * Convert UPS easting and northing to a point
	 *
	 * @param north
	 *            true for the north polar region
	 * @param easting
	 *            easting in meters
	 * @param northing
	 *            northing in meters
	 * @return point in degrees
	 */
	public static Point inverse(boolean north, double easting,
			double northing) {
		double x = easting - FALSE_EASTING_NORTHING;
		double y = north ? FALSE_EASTING_NORTHING - northing
				: northing - FALSE_EASTING_NORTHING;
		double rho = Math.hypot(x, y);
		double latitude = 90;
		double longitude = 0;
		if (rho > 0) {
			double t = rho * C / (2 * A * K0);
			// tau' = tan(chi) = (1 - t^2) / (2 t)
			double tau = tauFromConformal((1 - t * t) / (2 * t));
			latitude = Math.toDegrees(Math.atan(tau));
			longitude = Math.toDegrees(Math.atan2(x, y));
		}
		if (!north) {
			latitude = -latitude;
		}
		return Point.degrees(longitude, latitude);
	}

	/**
	 * Get the MGRS band, 100km column and row letters, and the easting and
	 * northing within the 100km square (truncated to meters)
	 *
	 * @param latitude
	 *            latitude in degrees
	 * @param longitude
	 *            longitude in degrees
	 * @return band, column and row letters (as chars in a long[3]) followed
	 *         by easting and northing
	 */
	public static long[] toMGRS(double latitude, double longitude) {
		boolean north = latitude >= 0;
		double[] en = forward(latitude, longitude);
		// round to 1 micrometer so exact grid values survive a round trip
		double easting = Math.round(en[0] * 1e6) / 1e6;
		double northing = Math.round(en[1] * 1e6) / 1e6;
		boolean east = easting >= FALSE_EASTING_NORTHING;
		int bandIndex = (north ? 2 : 0) + (east ? 1 : 0);
		int columnIndex = (int) Math.floor(easting / 100000);
		int rowIndex = (int) Math.floor(northing / 100000);
		String columns = COLUMN_LETTERS[bandIndex];
		String rows = ROW_LETTERS[north ? 1 : 0];
		char column = columns.charAt(Math.max(0, Math.min(columns.length() - 1,
				columnIndex - COLUMN_START[bandIndex])));
		char row = rows.charAt(Math.max(0, Math.min(rows.length() - 1,
				rowIndex - ROW_START[north ? 1 : 0])));
		return new long[] { BAND_LETTERS.charAt(bandIndex), column, row,
				(long) Math.floor(easting - columnIndex * 100000.0),
				(long) Math.floor(northing - rowIndex * 100000.0) };
	}

	/**
	 * Check the 100km column and row letters are used in the UPS band
	 *
	 * @param band
	 *            band letter (A, B, Y or Z)
	 * @param column
	 *            column letter
	 * @param row
	 *            row letter
	 * @return true if valid
	 */
	public static boolean isColumnRow(char band, char column, char row) {
		int bandIndex = BAND_LETTERS.indexOf(band);
		return bandIndex >= 0 && COLUMN_LETTERS[bandIndex].indexOf(column) >= 0
				&& ROW_LETTERS[bandIndex >= 2 ? 1 : 0].indexOf(row) >= 0;
	}

	/**
	 * Get the point at the south west corner of a MGRS UPS location
	 *
	 * @param band
	 *            band letter (A, B, Y or Z)
	 * @param column
	 *            column letter
	 * @param row
	 *            row letter
	 * @param easting
	 *            easting within the 100km square
	 * @param northing
	 *            northing within the 100km square
	 * @return point in degrees
	 */
	public static Point toPoint(char band, char column, char row, long easting,
			long northing) {
		int bandIndex = BAND_LETTERS.indexOf(band);
		boolean north = bandIndex >= 2;
		int columnIndex = COLUMN_LETTERS[bandIndex].indexOf(column)
				+ COLUMN_START[bandIndex];
		int rowIndex = ROW_LETTERS[north ? 1 : 0].indexOf(row)
				+ ROW_START[north ? 1 : 0];
		return inverse(north, columnIndex * 100000.0 + easting,
				rowIndex * 100000.0 + northing);
	}

	/**
	 * Tangent of the conformal latitude, Karney (2011) eqs 7 and 9
	 */
	private static double conformalTau(double tau) {
		if (Double.isInfinite(tau)) {
			return tau;
		}
		double sigma = Math.sinh(E * atanh(E * tau / Math.sqrt(1 + tau * tau)));
		return tau * Math.sqrt(1 + sigma * sigma)
				- sigma * Math.sqrt(1 + tau * tau);
	}

	/**
	 * Tangent of the latitude from the tangent of the conformal latitude
	 * (Newton iteration, Karney 2011 eqs 19-21)
	 */
	private static double tauFromConformal(double tauPrime) {
		if (Double.isInfinite(tauPrime)) {
			return tauPrime;
		}
		double e2 = E * E;
		double tau = tauPrime / (1 - e2);
		for (int i = 0; i < 10; i++) {
			double tauI = conformalTau(tau);
			double delta = (tauPrime - tauI) / Math.sqrt(1 + tauI * tauI)
					* (1 + (1 - e2) * tau * tau)
					/ ((1 - e2) * Math.sqrt(1 + tau * tau));
			tau += delta;
			if (!(Math.abs(delta) > 1e-12 * Math.max(1, Math.abs(tau)))) {
				break;
			}
		}
		return tau;
	}

	private static double atanh(double x) {
		double y = Math.abs(x);
		y = Math.log1p(2 * y / (1 - y)) / 2;
		return x < 0 ? -y : y;
	}

}
