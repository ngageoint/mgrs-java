package mil.nga.mgrs.utm;

import java.text.ParseException;

import mil.nga.mgrs.MGRS;
import mil.nga.mgrs.features.LatLng;

/**
 * Universal Transverse Mercator Projection
 * 
 * @author wnewman
 */
public class UTM {

	/**
	 * Zone number
	 */
	private int zoneNumber;

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
	 * Constructor
	 * 
	 * @param zoneNumber
	 *            zone number
	 * @param hemisphere
	 *            hemisphere
	 * @param easting
	 *            easting
	 * @param northing
	 *            northing
	 */
	public UTM(int zoneNumber, Hemisphere hemisphere, double easting,
			double northing) {
		this.zoneNumber = zoneNumber;
		this.hemisphere = hemisphere;
		this.easting = easting;
		this.northing = northing;
	}

	/**
	 * Get the zone number
	 * 
	 * @return zone number
	 */
	public int getZoneNumber() {
		return zoneNumber;
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
	 * Parse a MGRS value
	 * 
	 * @param mgrs
	 *            MGRS value
	 * @return UTM
	 * @throws ParseException
	 *             upon failure to parse MGRS value
	 */
	public static UTM parse(String mgrs) throws ParseException {
		return MGRS.parse(mgrs).utm();
	}

	/**
	 * Create from a coordinate
	 * 
	 * @param latLng
	 *            coordinate
	 * @return UTM
	 */
	public static UTM from(LatLng latLng) {
		int zone = (int) Math.floor(latLng.longitude / 6 + 31);
		return from(latLng, zone);
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
	public static UTM from(LatLng latLng, int zone) {
		Hemisphere hemisphere = latLng.latitude >= 0 ? Hemisphere.NORTH
				: Hemisphere.SOUTH;
		return from(latLng, zone, hemisphere);
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
	public static UTM from(LatLng latLng, int zone, Hemisphere hemisphere) {
		double latitude = latLng.latitude;
		double longitude = latLng.longitude;

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

		return new UTM(zone, hemisphere, easting, northing);
	}

}
