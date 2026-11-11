package mil.nga.mgrs.utm;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mil.nga.grid.GridConstants;
import mil.nga.grid.Hemisphere;
import mil.nga.grid.features.Point;
import mil.nga.mgrs.MGRS;
import mil.nga.mgrs.gzd.GridZones;

/**
 * Universal Transverse Mercator Projection
 * 
 * @author wnewman
 * @author osbornb
 */
public class UTM {

	/**
	 * Zone number
	 */
	private final int zone;

	/**
	 * Hemisphere
	 */
	private final Hemisphere hemisphere;

	/**
	 * Easting
	 */
	private final double easting;

	/**
	 * Northing
	 */
	private final double northing;

	/**
	 * UTM string pattern
	 */
	private static final Pattern utmPattern = Pattern.compile(
			"^(\\d{1,2})\\s*([N|S])\\s*(\\d+\\.?\\d*)\\s*(\\d+\\.?\\d*)$",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Create
	 * 
	 * @param zone
	 *            zone number
	 * @param hemisphere
	 *            hemisphere
	 * @param easting
	 *            easting
	 * @param northing
	 *            northing
	 * @return UTM
	 */
	public static UTM create(int zone, Hemisphere hemisphere, double easting,
			double northing) {
		return new UTM(zone, hemisphere, easting, northing);
	}

	/**
	 * Create a point from the UTM attributes
	 * 
	 * @param zone
	 *            zone number
	 * @param hemisphere
	 *            hemisphere
	 * @param easting
	 *            easting
	 * @param northing
	 *            northing
	 * @return point
	 */
	public static Point point(int zone, Hemisphere hemisphere, double easting,
			double northing) {
		return create(zone, hemisphere, easting, northing).toPoint();
	}

	/**
	 * Constructor
	 * 
	 * @param zone
	 *            zone number
	 * @param hemisphere
	 *            hemisphere
	 * @param easting
	 *            easting
	 * @param northing
	 *            northing
	 */
	public UTM(int zone, Hemisphere hemisphere, double easting,
			double northing) {
		this.zone = zone;
		this.hemisphere = hemisphere;
		this.easting = easting;
		this.northing = northing;
	}

	/**
	 * Get the zone number
	 * 
	 * @return zone number
	 */
	public int getZone() {
		return zone;
	}

	/**
	 * Get the hemisphere
	 * 
	 * @return hemisphere
	 */
	public Hemisphere getHemisphere() {
		return hemisphere;
	}

	/**
	 * Get the easting
	 * 
	 * @return easting
	 */
	public double getEasting() {
		return easting;
	}

	/**
	 * Get the northing
	 * 
	 * @return northing
	 */
	public double getNorthing() {
		return northing;
	}

	/**
	 * Convert to a point
	 * 
	 * @return point
	 */
	public Point toPoint() {

		double north = northing;
		if (hemisphere == Hemisphere.SOUTH) {
			// Remove 10,000,000 meter offset used for southern hemisphere
			north -= 10000000.0;
		}

		// Karney (2011), "Transverse Mercator with an accuracy of a few
		// nanometers", eqs 15-22: Krueger series to order n^6
		double eta = (easting - 500000) / (K0 * A);
		double xi = north / (K0 * A);

		double xiPrime = xi;
		double etaPrime = eta;
		for (int j = 1; j <= 6; j++) {
			xiPrime -= BETA[j] * Math.sin(2 * j * xi) * Math.cosh(2 * j * eta);
			etaPrime -= BETA[j] * Math.cos(2 * j * xi) * Math.sinh(2 * j * eta);
		}

		double sinhEtaPrime = Math.sinh(etaPrime);
		double sinXiPrime = Math.sin(xiPrime);
		double cosXiPrime = Math.cos(xiPrime);
		double tauPrime = sinXiPrime / Math.sqrt(
				sinhEtaPrime * sinhEtaPrime + cosXiPrime * cosXiPrime);

		// Newton iteration for tau = tan(latitude)
		double tau = tauPrime;
		for (int i = 0; i < 10; i++) {
			double tauIPrime = conformalTau(tau);
			double delta = (tauPrime - tauIPrime)
					/ Math.sqrt(1 + tauIPrime * tauIPrime)
					* (1 + (1 - E * E) * tau * tau)
					/ ((1 - E * E) * Math.sqrt(1 + tau * tau));
			tau += delta;
			if (!(Math.abs(delta) > 1e-12 * Math.max(1, Math.abs(tau)))) {
				break;
			}
		}

		double latitude = Math.toDegrees(Math.atan(tau));
		double longitude = Math.toDegrees(Math.atan2(sinhEtaPrime, cosXiPrime))
				+ zone * 6 - 183;

		return Point.degrees(longitude, latitude);
	}

	/**
	 * Convert to a MGRS coordinate
	 * 
	 * @return MGRS
	 */
	public MGRS toMGRS() {
		return MGRS.from(toPoint());
	}

	/**
	 * Format to a UTM string
	 * 
	 * @return UTM string
	 */
	public String format() {

		StringBuilder value = new StringBuilder();

		DecimalFormat formatter = new DecimalFormat("0.##");

		value.append(String.format("%02d", zone));
		value.append(" ");
		value.append(hemisphere == Hemisphere.NORTH ? GridConstants.NORTH_CHAR
				: GridConstants.SOUTH_CHAR);
		value.append(" ");
		value.append(formatter.format(easting));
		value.append(" ");
		value.append(formatter.format(northing));

		return value.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return format();
	}

	/**
	 * Return whether the given string is valid UTM string
	 *
	 * @param utm
	 *            potential UTM string
	 * @return true if UTM string is valid, false otherwise
	 */
	public static boolean isUTM(String utm) {
		return utmPattern.matcher(utm).matches();
	}

	/**
	 * Parse a UTM value (Zone N|S Easting Northing)
	 * 
	 * @param utm
	 *            UTM value
	 * @return UTM
	 * @throws ParseException
	 *             upon failure to parse UTM value
	 */
	public static UTM parse(String utm) throws ParseException {
		Matcher matcher = utmPattern.matcher(utm);
		if (!matcher.matches()) {
			throw new ParseException("Invalid UTM: " + utm, 0);
		}

		int zone = Integer.parseInt(matcher.group(1));
		Hemisphere hemisphere = matcher.group(2).equalsIgnoreCase(
				GridConstants.NORTH_CHAR) ? Hemisphere.NORTH : Hemisphere.SOUTH;
		double easting = Double.parseDouble(matcher.group(3));
		double northing = Double.parseDouble(matcher.group(4));

		return UTM.create(zone, hemisphere, easting, northing);
	}

	/**
	 * Create from a point
	 * 
	 * @param point
	 *            point
	 * @return UTM
	 */
	public static UTM from(Point point) {
		return from(point, GridZones.getZoneNumber(point));
	}

	/**
	 * Create from a point and zone number
	 * 
	 * @param point
	 *            point
	 * @param zone
	 *            zone number
	 * @return UTM
	 */
	public static UTM from(Point point, int zone) {
		return from(point, zone, Hemisphere.from(point));
	}

	/**
	 * Create from a coordinate, zone number, and hemisphere
	 * 
	 * @param point
	 *            coordinate
	 * @param zone
	 *            zone number
	 * @param hemisphere
	 *            hemisphere
	 * @return UTM
	 */
	public static UTM from(Point point, int zone, Hemisphere hemisphere) {

		point = point.toDegrees();

		double latitude = point.getLatitude();
		double lambda = point.getLongitude() - (zone * 6 - 183);
		if (lambda < -180 || lambda >= 180) {
			lambda = ((lambda + 180) % 360 + 360) % 360 - 180;
		}

		// Karney (2011), "Transverse Mercator with an accuracy of a few
		// nanometers", eqs 7-14: Krueger series to order n^6
		double phi = Math.toRadians(latitude);
		double lam = Math.toRadians(lambda);

		double tauPrime = conformalTau(Math.tan(phi));
		double cosLambda = Math.cos(lam);
		double xiPrime = Math.atan2(tauPrime, cosLambda);
		double etaPrime = asinh(Math.sin(lam)
				/ Math.sqrt(tauPrime * tauPrime + cosLambda * cosLambda));

		double xi = xiPrime;
		double eta = etaPrime;
		for (int j = 1; j <= 6; j++) {
			xi += ALPHA[j] * Math.sin(2 * j * xiPrime)
					* Math.cosh(2 * j * etaPrime);
			eta += ALPHA[j] * Math.cos(2 * j * xiPrime)
					* Math.sinh(2 * j * etaPrime);
		}

		double easting = K0 * A * eta + 500000;
		double northing = K0 * A * xi;

		if (hemisphere == Hemisphere.SOUTH) {
			northing = northing + 10000000;
		}

		// Round to 1 micrometer so exact grid values survive a round trip
		// through latitude and longitude (floating point noise is ~1e-9 m)
		// before MGRS truncates them.
		return UTM.create(zone, hemisphere, roundMicrometer(easting),
				roundMicrometer(northing));
	}

	/**
	 * Round a meter value to the nearest micrometer
	 *
	 * @param value
	 *            meters
	 * @return rounded meters
	 */
	private static double roundMicrometer(double value) {
		return Math.round(value * 1e6) / 1e6;
	}

	/**
	 * UTM central meridian scale factor
	 */
	private static final double K0 = 0.9996;

	/**
	 * WGS84 flattening
	 */
	private static final double F = 1 / 298.257223563;

	/**
	 * WGS84 third flattening
	 */
	private static final double N = F / (2 - F);

	/**
	 * WGS84 eccentricity
	 */
	private static final double E = Math.sqrt(F * (2 - F));

	/**
	 * WGS84 rectifying radius, Karney (2011) eq 14
	 */
	private static final double A = 6378137.0 / (1 + N)
			* (1 + N * N / 4 + Math.pow(N, 4) / 64 + Math.pow(N, 6) / 256);

	/**
	 * Krueger series coefficients (one based), Karney (2011) eq 35
	 */
	private static final double[] ALPHA = { 0,
			N / 2 - 2. / 3 * Math.pow(N, 2) + 5. / 16 * Math.pow(N, 3)
					+ 41. / 180 * Math.pow(N, 4) - 127. / 288 * Math.pow(N, 5)
					+ 7891. / 37800 * Math.pow(N, 6),
			13. / 48 * Math.pow(N, 2) - 3. / 5 * Math.pow(N, 3)
					+ 557. / 1440 * Math.pow(N, 4) + 281. / 630 * Math.pow(N, 5)
					- 1983433. / 1935360 * Math.pow(N, 6),
			61. / 240 * Math.pow(N, 3) - 103. / 140 * Math.pow(N, 4)
					+ 15061. / 26880 * Math.pow(N, 5)
					+ 167603. / 181440 * Math.pow(N, 6),
			49561. / 161280 * Math.pow(N, 4) - 179. / 168 * Math.pow(N, 5)
					+ 6601661. / 7257600 * Math.pow(N, 6),
			34729. / 80640 * Math.pow(N, 5)
					- 3418889. / 1995840 * Math.pow(N, 6),
			212378941. / 319334400 * Math.pow(N, 6) };

	/**
	 * Inverse Krueger series coefficients (one based), Karney (2011) eq 36
	 */
	private static final double[] BETA = { 0,
			N / 2 - 2. / 3 * Math.pow(N, 2) + 37. / 96 * Math.pow(N, 3)
					- 1. / 360 * Math.pow(N, 4) - 81. / 512 * Math.pow(N, 5)
					+ 96199. / 604800 * Math.pow(N, 6),
			1. / 48 * Math.pow(N, 2) + 1. / 15 * Math.pow(N, 3)
					- 437. / 1440 * Math.pow(N, 4) + 46. / 105 * Math.pow(N, 5)
					- 1118711. / 3870720 * Math.pow(N, 6),
			17. / 480 * Math.pow(N, 3) - 37. / 840 * Math.pow(N, 4)
					- 209. / 4480 * Math.pow(N, 5)
					+ 5569. / 90720 * Math.pow(N, 6),
			4397. / 161280 * Math.pow(N, 4) - 11. / 504 * Math.pow(N, 5)
					- 830251. / 7257600 * Math.pow(N, 6),
			4583. / 161280 * Math.pow(N, 5)
					- 108847. / 3991680 * Math.pow(N, 6),
			20648693. / 638668800 * Math.pow(N, 6) };

	/**
	 * Tangent of the conformal latitude, Karney (2011) eqs 7 and 9
	 *
	 * @param tau
	 *            tangent of the latitude
	 * @return tangent of the conformal latitude
	 */
	private static double conformalTau(double tau) {
		double sigma = Math.sinh(E * atanh(E * tau / Math.sqrt(1 + tau * tau)));
		return tau * Math.sqrt(1 + sigma * sigma)
				- sigma * Math.sqrt(1 + tau * tau);
	}

	/**
	 * Inverse hyperbolic sine
	 *
	 * @param x
	 *            value
	 * @return asinh(x)
	 */
	private static double asinh(double x) {
		double y = Math.abs(x);
		y = Math.log1p(y + y * y / (1 + Math.sqrt(1 + y * y)));
		return x < 0 ? -y : y;
	}

	/**
	 * Inverse hyperbolic tangent
	 *
	 * @param x
	 *            value
	 * @return atanh(x)
	 */
	private static double atanh(double x) {
		double y = Math.abs(x);
		y = Math.log1p(2 * y / (1 - y)) / 2;
		return x < 0 ? -y : y;
	}

}
