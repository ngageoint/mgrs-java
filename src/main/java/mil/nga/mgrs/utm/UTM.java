package mil.nga.mgrs.utm;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mil.nga.mgrs.MGRS;
import mil.nga.mgrs.features.Point;

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
	private int zone;

	/**
	 * Hemisphere
	 */
	private Hemisphere hemisphere;

	/**
	 * Easting
	 */
	private double easting;

	/**
	 * Northing
	 */
	private double northing;

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
		return Point.from(this);
	}

	/**
	 * Convert to a MGRS coordinate
	 * 
	 * @return MGRS
	 */
	public MGRS toMGRS() {
		return toPoint().toMGRS();
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
		value.append(hemisphere == Hemisphere.NORTH ? "N" : "S");
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
		Hemisphere hemisphere = matcher.group(2).equalsIgnoreCase("N")
				? Hemisphere.NORTH
				: Hemisphere.SOUTH;
		double easting = Double.parseDouble(matcher.group(3));
		double northing = Double.parseDouble(matcher.group(4));

		return UTM.create(zone, hemisphere, easting, northing);
	}

	/**
	 * Create from a coordinate
	 * 
	 * @param latLng
	 *            coordinate
	 * @return UTM
	 */
	public static UTM from(Point latLng) {
		return from(latLng, latLng.getZoneNumber());
	}

	/**
	 * Create from a coordinate and zone number
	 * 
	 * @param latLng
	 *            coordinate
	 * @param zone
	 *            zone number
	 * @return UTM
	 */
	public static UTM from(Point latLng, int zone) {
		return from(latLng, zone, latLng.getHemisphere());
	}

	/**
	 * Create from a coordinate, zone number, and hemisphere
	 * 
	 * @param latLng
	 *            coordinate
	 * @param zone
	 *            zone number
	 * @param hemisphere
	 *            hemisphere
	 * @return UTM
	 */
	public static UTM from(Point latLng, int zone, Hemisphere hemisphere) {

		latLng = latLng.toDegrees();

		double latitude = latLng.getLatitude();
		double longitude = latLng.getLongitude();

		// @formatter:off
        double easting = 0.5 * Math.log((1+Math.cos(latitude*Math.PI/180)*Math.sin(longitude*Math.PI/180-(6*zone-183)*Math.PI/180))/(1-Math.cos(latitude*Math.PI/180)*Math.sin(longitude*Math.PI/180-(6*zone-183)*Math.PI/180)))*0.9996*6399593.62/Math.pow((1+Math.pow(0.0820944379, 2)*Math.pow(Math.cos(latitude*Math.PI/180), 2)), 0.5)*(1+ Math.pow(0.0820944379,2)/2*Math.pow((0.5*Math.log((1+Math.cos(latitude*Math.PI/180)*Math.sin(longitude*Math.PI/180-(6*zone-183)*Math.PI/180))/(1-Math.cos(latitude*Math.PI/180)*Math.sin(longitude*Math.PI/180-(6*zone-183)*Math.PI/180)))),2)*Math.pow(Math.cos(latitude*Math.PI/180),2)/3)+500000;
		// @formatter:on
		easting = Math.round(easting * 100) * 0.01;

		// @formatter:off
        double northing = (Math.atan(Math.tan(latitude*Math.PI/180)/Math.cos((longitude*Math.PI/180-(6*zone -183)*Math.PI/180)))-latitude*Math.PI/180)*0.9996*6399593.625/Math.sqrt(1+0.006739496742*Math.pow(Math.cos(latitude*Math.PI/180),2))*(1+0.006739496742/2*Math.pow(0.5*Math.log((1+Math.cos(latitude*Math.PI/180)*Math.sin((longitude*Math.PI/180-(6*zone -183)*Math.PI/180)))/(1-Math.cos(latitude*Math.PI/180)*Math.sin((longitude*Math.PI/180-(6*zone -183)*Math.PI/180)))),2)*Math.pow(Math.cos(latitude*Math.PI/180),2))+0.9996*6399593.625*(latitude*Math.PI/180-0.005054622556*(latitude*Math.PI/180+Math.sin(2*latitude*Math.PI/180)/2)+4.258201531e-05*(3*(latitude*Math.PI/180+Math.sin(2*latitude*Math.PI/180)/2)+Math.sin(2*latitude*Math.PI/180)*Math.pow(Math.cos(latitude*Math.PI/180),2))/4-1.674057895e-07*(5*(3*(latitude*Math.PI/180+Math.sin(2*latitude*Math.PI/180)/2)+Math.sin(2*latitude*Math.PI/180)*Math.pow(Math.cos(latitude*Math.PI/180),2))/4+Math.sin(2*latitude*Math.PI/180)*Math.pow(Math.cos(latitude*Math.PI/180),2)*Math.pow(Math.cos(latitude*Math.PI/180),2))/3);
		// @formatter:on

		if (hemisphere == Hemisphere.SOUTH) {
			northing = northing + 10000000;
		}

		northing = Math.round(northing * 100) * 0.01;

		return UTM.create(zone, hemisphere, easting, northing);
	}

}
