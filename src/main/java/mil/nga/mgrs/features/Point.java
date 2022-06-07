package mil.nga.mgrs.features;

import java.text.ParseException;

import mil.nga.mgrs.MGRS;
import mil.nga.mgrs.MGRSUtils;
import mil.nga.mgrs.gzd.GridZones;
import mil.nga.mgrs.tile.MGRSTile;
import mil.nga.mgrs.tile.Pixel;
import mil.nga.mgrs.utm.Hemisphere;
import mil.nga.mgrs.utm.UTM;

/**
 * Point
 * 
 * @author wnewman
 * @author osbornb
 */
public class Point {

	/**
	 * Latitude
	 */
	private double latitude;

	/**
	 * Longitude
	 */
	private double longitude;

	/**
	 * Unit
	 */
	private Unit unit;

	/**
	 * Create a point with default degree unit
	 * 
	 * @param longitude
	 *            longitude
	 * @param latitude
	 *            latitude
	 * @return point
	 */
	public static Point create(double longitude, double latitude) {
		return degrees(longitude, latitude);
	}

	/**
	 * Create a point
	 * 
	 * @param longitude
	 *            longitude
	 * @param latitude
	 *            latitude
	 * @param unit
	 *            unit
	 * @return point
	 */
	public static Point create(double longitude, double latitude, Unit unit) {
		return new Point(longitude, latitude, unit);
	}

	/**
	 * Create a point in degrees
	 * 
	 * @param longitude
	 *            longitude in degrees
	 * @param latitude
	 *            latitude in degrees
	 * @return point in degrees
	 */
	public static Point degrees(double longitude, double latitude) {
		return create(longitude, latitude, Unit.DEGREE);
	}

	/**
	 * Create a point in meters
	 * 
	 * @param longitude
	 *            longitude in meters
	 * @param latitude
	 *            latitude in meters
	 * @return point in meters
	 */
	public static Point meters(double longitude, double latitude) {
		return create(longitude, latitude, Unit.METER);
	}

	/**
	 * Create a point from a coordinate in a unit to another unit
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
		return MGRSUtils.toUnit(fromUnit, longitude, latitude, toUnit);
	}

	/**
	 * Create a point from a coordinate in an opposite unit to another unit
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
		return MGRSUtils.toUnit(longitude, latitude, unit);
	}

	/**
	 * Create a point converting the degrees coordinate to meters
	 * 
	 * @param longitude
	 *            longitude in degrees
	 * @param latitude
	 *            latitude in degrees
	 * @return point in meters
	 */
	public static Point degreesToMeters(double longitude, double latitude) {
		return toUnit(Unit.DEGREE, longitude, latitude, Unit.METER);
	}

	/**
	 * Create a point converting the meters coordinate to degrees
	 * 
	 * @param longitude
	 *            longitude in meters
	 * @param latitude
	 *            latitude in meters
	 * @return point in degrees
	 */
	public static Point metersToDegrees(double longitude, double latitude) {
		return toUnit(Unit.METER, longitude, latitude, Unit.DEGREE);
	}

	/**
	 * Create a point from UTM values
	 * 
	 * @param zoneNumber
	 *            zone number
	 * @param hemisphere
	 *            hemisphere
	 * @param easting
	 *            easting
	 * @param northing
	 *            northing
	 * @return point
	 */
	public static Point create(int zoneNumber, Hemisphere hemisphere,
			double easting, double northing) {
		return UTM.create(zoneNumber, hemisphere, easting, northing).toPoint();
	}

	/**
	 * Constructor, in {@link Unit#DEGREE} units
	 * 
	 * @param longitude
	 *            longitude
	 * @param latitude
	 *            latitude
	 */
	public Point(double longitude, double latitude) {
		this(longitude, latitude, Unit.DEGREE);
	}

	/**
	 * Constructor
	 * 
	 * @param longitude
	 *            longitude
	 * @param latitude
	 *            latitude
	 * @param unit
	 *            unit
	 */
	public Point(double longitude, double latitude, Unit unit) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.unit = unit;
	}

	/**
	 * Get the latitude
	 * 
	 * @return latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Set the latitude
	 * 
	 * @param latitude
	 *            latitude
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Get the longitude
	 * 
	 * @return longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Set the longitude
	 * 
	 * @param longitude
	 *            longitude
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Get the unit
	 * 
	 * @return unit
	 */
	public Unit getUnit() {
		return unit;
	}

	/**
	 * Set the unit
	 * 
	 * @param unit
	 *            unit
	 */
	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	/**
	 * Is in the provided unit type
	 * 
	 * @param unit
	 *            unit
	 * @return true if in the unit
	 */
	public boolean isUnit(Unit unit) {
		return this.unit == unit;
	}

	/**
	 * Is this point in degrees
	 * 
	 * @return true if degrees
	 */
	public boolean isDegrees() {
		return isUnit(Unit.DEGREE);
	}

	/**
	 * Is this point in meters
	 * 
	 * @return true if meters
	 */
	public boolean isMeters() {
		return isUnit(Unit.METER);
	}

	/**
	 * Convert to the unit
	 * 
	 * @param unit
	 *            unit
	 * @return point in units, same point if equal units
	 */
	public Point toUnit(Unit unit) {
		Point point = null;
		if (isUnit(unit)) {
			point = this;
		} else {
			point = MGRSUtils.toUnit(this.unit, longitude, latitude, unit);
		}
		return point;
	}

	/**
	 * Convert to degrees
	 * 
	 * @return point in degrees, same point if already in degrees
	 */
	public Point toDegrees() {
		return toUnit(Unit.DEGREE);
	}

	/**
	 * Convert to meters
	 * 
	 * @return point in meters, same point if already in meters
	 */
	public Point toMeters() {
		return toUnit(Unit.METER);
	}

	/**
	 * Convert to a MGRS coordinate
	 * 
	 * @return MGRS
	 */
	public MGRS toMGRS() {
		return MGRS.from(this);
	}

	/**
	 * Convert to UTM coordinate
	 * 
	 * @return UTM
	 */
	public UTM toUTM() {
		return UTM.from(this);
	}

	/**
	 * Get the pixel where the point fits into tile
	 * 
	 * @param tile
	 *            tile
	 * @return pixel
	 */
	public Pixel getPixel(MGRSTile tile) {
		return getPixel(tile.getWidth(), tile.getHeight(), tile.getBounds());
	}

	/**
	 * Get the pixel where the point fits into the bounds
	 * 
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @param bounds
	 *            bounds
	 * @return pixel
	 */
	public Pixel getPixel(int width, int height, Bounds bounds) {
		return MGRSUtils.getPixel(width, height, bounds, this);
	}

	/**
	 * Get the point zone number
	 * 
	 * @return zone number
	 */
	public int getZoneNumber() {
		return GridZones.getZoneNumber(this);
	}

	/**
	 * Get the point band letter
	 * 
	 * @return band letter
	 */
	public char getBandLetter() {
		return GridZones.getBandLetter(toDegrees().getLatitude());
	}

	/**
	 * Get the point hemisphere
	 * 
	 * @return hemisphere
	 */
	public Hemisphere getHemisphere() {
		return Hemisphere.fromLatitude(latitude);
	}

	/**
	 * Create from a Universal Transverse Mercator Projection
	 * 
	 * @param utm
	 *            UTM
	 * @return coordinate
	 */
	public static Point from(UTM utm) {

		double north = utm.getNorthing();
		if (utm.getHemisphere() == Hemisphere.SOUTH) {
			// Remove 10,000,000 meter offset used for southern hemisphere
			north -= 10000000.0;
		}

		int zone = utm.getZone();

		double easting = utm.getEasting();

		// @formatter:off
		double latitude = (north/6366197.724/0.9996+(1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)-0.006739496742*Math.sin(north/6366197.724/0.9996)*Math.cos(north/6366197.724/0.9996)*(Math.atan(Math.cos(Math.atan(( Math.exp((easting - 500000) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting - 500000) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3))-Math.exp(-(easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*( 1 -  0.006739496742*Math.pow((easting - 500000) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3)))/2/Math.cos((north-0.9996*6399593.625*(north/6366197.724/0.9996-0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996 )/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996)))*Math.tan((north-0.9996*6399593.625*(north/6366197.724/0.9996 - 0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996 )*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996))-north/6366197.724/0.9996)*3/2)*(Math.atan(Math.cos(Math.atan((Math.exp((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3))-Math.exp(-(easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3)))/2/Math.cos((north-0.9996*6399593.625*(north/6366197.724/0.9996-0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996)))*Math.tan((north-0.9996*6399593.625*(north/6366197.724/0.9996-0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996))-north/6366197.724/0.9996))*180/Math.PI;
		// @formatter:on
		latitude = Math.round(latitude * 10000000);
		latitude = latitude / 10000000;

		// @formatter:off
		double longitude = Math.atan((Math.exp((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3))-Math.exp(-(easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3)))/2/Math.cos((north-0.9996*6399593.625*( north/6366197.724/0.9996-0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2* north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3)) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996))*180/Math.PI+zone*6-183;
		// @formatter:on
		longitude = Math.round(longitude * 10000000);
		longitude = longitude / 10000000;

		return Point.degrees(longitude, latitude);
	}

	/**
	 * Parse a MGRS value to a coordinate
	 * 
	 * @param mgrs
	 *            MGRS value
	 * @return coordinate
	 */
	public static Point from(MGRS mgrs) {
		return mgrs.toUTM().toPoint();
	}

	/**
	 * Parse a MGRS string value to a coordinate
	 * 
	 * @param mgrs
	 *            MGRS string value
	 * @return coordinate
	 * @throws ParseException
	 *             upon failure to parse the MGRS value
	 */
	public static Point parse(String mgrs) throws ParseException {
		return MGRS.parse(mgrs).toPoint();
	}

}
