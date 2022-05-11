package mil.nga.mgrs.gzd;

import java.util.ArrayList;
import java.util.Collection;

import mil.nga.mgrs.features.LatLng;
import mil.nga.mgrs.features.Line;
import mil.nga.mgrs.features.Point;

/**
 * Zone Bounds
 * 
 * @author osbornb
 */
public class Bounds {

	/**
	 * Western longitude
	 */
	private double west;

	/**
	 * Southern latitude
	 */
	private double south;

	/**
	 * Eastern longitude
	 */
	private double east;

	/**
	 * Northern latitude
	 */
	private double north;

	/**
	 * Constructor
	 * 
	 * @param west
	 *            western longitude
	 * @param south
	 *            southern latitude
	 * @param east
	 *            eastern longitude
	 * @param north
	 *            northern latitude
	 */
	public Bounds(double west, double south, double east, double north) {
		this.west = west;
		this.south = south;
		this.east = east;
		this.north = north;
	}

	/**
	 * Constructor
	 * 
	 * @param strip
	 *            longitudinal strip
	 * @param band
	 *            latitude band
	 */
	public Bounds(LongitudinalStrip strip, LatitudeBand band) {
		this(strip.getWest(), band.getSouth(), strip.getEast(),
				band.getNorth());
	}

	/**
	 * Constructor
	 * 
	 * @param bounds
	 *            bounds array: [west, south, east, north]
	 */
	public Bounds(double[] bounds) {
		this(bounds[0], bounds[1], bounds[2], bounds[3]);
	}

	/**
	 * Get the western longitude
	 * 
	 * @return western longitude
	 */
	public double getWest() {
		return west;
	}

	/**
	 * Set the western longitude
	 * 
	 * @param west
	 *            western longitude
	 */
	public void setWest(double west) {
		this.west = west;
	}

	/**
	 * Get the southern latitude
	 * 
	 * @return southern latitude
	 */
	public double getSouth() {
		return south;
	}

	/**
	 * Set the southern latitude
	 * 
	 * @param south
	 *            southern latitude
	 */
	public void setSouth(double south) {
		this.south = south;
	}

	/**
	 * Get the eastern longitude
	 * 
	 * @return eastern longitude
	 */
	public double getEast() {
		return east;
	}

	/**
	 * Set the eastern longitude
	 * 
	 * @param east
	 *            eastern longitude
	 */
	public void setEast(double east) {
		this.east = east;
	}

	/**
	 * Get the northern latitude
	 * 
	 * @return northern latitude
	 */
	public double getNorth() {
		return north;
	}

	/**
	 * Set the northern latitude
	 * 
	 * @param north
	 *            northern latitude
	 */
	public void setNorth(double north) {
		this.north = north;
	}

	/**
	 * Get the western longitude
	 * 
	 * @return western longitude
	 */
	public double getMinLongitude() {
		return getWest();
	}

	/**
	 * Set the western longitude
	 * 
	 * @param west
	 *            western longitude
	 */
	public void setMinLongitude(double west) {
		setWest(west);
	}

	/**
	 * Get the southern latitude
	 * 
	 * @return southern latitude
	 */
	public double getMinLatitude() {
		return getSouth();
	}

	/**
	 * Set the southern latitude
	 * 
	 * @param south
	 *            southern latitude
	 */
	public void setMinLatitude(double south) {
		setSouth(south);
	}

	/**
	 * Get the eastern longitude
	 * 
	 * @return eastern longitude
	 */
	public double getMaxLongitude() {
		return getEast();
	}

	/**
	 * Set the eastern longitude
	 * 
	 * @param east
	 *            eastern longitude
	 */
	public void setMaxLongitude(double east) {
		setEast(east);
	}

	/**
	 * Get the northern latitude
	 * 
	 * @return northern latitude
	 */
	public double getMaxLatitude() {
		return getNorth();
	}

	/**
	 * Set the northern latitude
	 * 
	 * @param north
	 *            northern latitude
	 */
	public void setMaxLatitude(double north) {
		setNorth(north);
	}

	/**
	 * Get the center longitude
	 * 
	 * @return center longitude
	 */
	public double getCenterLongitude() {
		return ((east - west) / 2.0) + west;
	}

	/**
	 * Get the center latitude
	 * 
	 * @return center latitude
	 */
	public double getCenterLatitude() {
		return ((north - south) / 2.0) + south;
	}

	/**
	 * Get the center coordinate
	 * 
	 * @return center coordinate
	 */
	public LatLng getCenter() {
		return new LatLng(getCenterLatitude(), getCenterLongitude());
	}

	/**
	 * Get the center point in meters
	 * 
	 * @return center point in meters
	 */
	public Point getCenterPoint() {
		return getCenter().toPoint();
	}

	/**
	 * Get the southwest coordinate
	 * 
	 * @return southwest coordinate
	 */
	public LatLng getSouthwest() {
		return new LatLng(getSouth(), getWest());
	}

	/**
	 * Get the southwest point in meters
	 * 
	 * @return southwest point in meters
	 */
	public Point getSouthwestPoint() {
		return getSouthwest().toPoint();
	}

	/**
	 * Get the northwest coordinate
	 * 
	 * @return northwest coordinate
	 */
	public LatLng getNorthwest() {
		return new LatLng(getNorth(), getWest());
	}

	/**
	 * Get the northwest point in meters
	 * 
	 * @return northwest point in meters
	 */
	public Point getNorthwestPoint() {
		return getNorthwest().toPoint();
	}

	/**
	 * Get the southeast coordinate
	 * 
	 * @return southeast coordinate
	 */
	public LatLng getSoutheast() {
		return new LatLng(getSouth(), getEast());
	}

	/**
	 * Get the southeast point in meters
	 * 
	 * @return southeast point in meters
	 */
	public Point getSoutheastPoint() {
		return getSoutheast().toPoint();
	}

	/**
	 * Get the northeast coordinate
	 * 
	 * @return northeast coordinate
	 */
	public LatLng getNortheast() {
		return new LatLng(getNorth(), getEast());
	}

	/**
	 * Get the northeast point in meters
	 * 
	 * @return northeast point in meters
	 */
	public Point getNortheastPoint() {
		return getNortheast().toPoint();
	}

	/**
	 * Get the four line bounds in meters
	 * 
	 * @return lines
	 */
	public Collection<Line> getLines() {

		Point southwest = getSouthwestPoint();
		Point northwest = getNorthwestPoint();
		Point northeast = getNortheastPoint();
		Point southeast = getSoutheastPoint();

		Collection<Line> lines = new ArrayList<>();
		lines.add(new Line(southwest, northwest));
		lines.add(new Line(northwest, northeast));
		lines.add(new Line(northeast, southeast));
		lines.add(new Line(southeast, southwest));

		return lines;
	}

	/**
	 * Get a grid range of the bounds
	 * 
	 * @return grid range
	 */
	public GridRange getGridRange() {
		return GridZones.getGridRange(this);
	}

	/**
	 * Get a zone number range between the western and eastern longitudes
	 * 
	 * @return zone number range
	 */
	public ZoneNumberRange getZoneNumberRange() {
		return GridZones.getZoneNumberRange(this);
	}

	/**
	 * Get a band letter range between the southern and northern latitudes
	 * 
	 * @return band letter range
	 */
	public BandLetterRange getBandLetterRange() {
		return GridZones.getBandLetterRange(this);
	}

}
