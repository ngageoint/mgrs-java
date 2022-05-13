package mil.nga.mgrs.gzd;

import java.util.ArrayList;
import java.util.List;

import mil.nga.mgrs.MGRSTile;
import mil.nga.mgrs.MGRSUtils;
import mil.nga.mgrs.features.Line;
import mil.nga.mgrs.features.Pixel;
import mil.nga.mgrs.features.PixelRange;
import mil.nga.mgrs.features.Point;
import mil.nga.mgrs.features.Unit;

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
	 * Unit
	 */
	private Unit unit;

	/**
	 * Create bounds
	 * 
	 * @param west
	 *            western longitude
	 * @param south
	 *            southern latitude
	 * @param east
	 *            eastern longitude
	 * @param north
	 *            northern latitude
	 * @param unit
	 *            unit
	 * @return bounds
	 */
	public static Bounds bounds(double west, double south, double east,
			double north, Unit unit) {
		return new Bounds(west, south, east, north, unit);
	}

	/**
	 * Create bounds in degrees
	 * 
	 * @param west
	 *            western longitude
	 * @param south
	 *            southern latitude
	 * @param east
	 *            eastern longitude
	 * @param north
	 *            northern latitude
	 * @return bounds
	 */
	public static Bounds degrees(double west, double south, double east,
			double north) {
		return bounds(west, south, east, north, Unit.DEGREE);
	}

	/**
	 * Create bounds in degrees
	 * 
	 * @param west
	 *            western longitude
	 * @param south
	 *            southern latitude
	 * @param east
	 *            eastern longitude
	 * @param north
	 *            northern latitude
	 * @return bounds
	 */
	public static Bounds meters(double west, double south, double east,
			double north) {
		return bounds(west, south, east, north, Unit.METER);
	}

	/**
	 * Create bounds
	 * 
	 * @param southwest
	 *            southwest corner
	 * @param northeast
	 *            northeast corner
	 * @return bounds
	 */
	public static Bounds bounds(Point southwest, Point northeast) {
		return new Bounds(southwest, northeast);
	}

	/**
	 * Create bounds, in {@link Unit#DEGREE} units
	 * 
	 * @param strip
	 *            longitudinal strip
	 * @param band
	 *            latitude band
	 * @return bounds
	 */
	public static Bounds bounds(LongitudinalStrip strip, LatitudeBand band) {
		return new Bounds(strip, band);
	}

	/**
	 * Create bounds, in {@link Unit#DEGREE} units
	 * 
	 * @param bounds
	 *            bounds array: [west, south, east, north] or [minLon, minLat,
	 *            maxLon, maxLat]
	 * @return bounds
	 */
	public static Bounds bounds(double[] bounds) {
		return new Bounds(bounds);
	}

	/**
	 * Create bounds
	 * 
	 * @param bounds
	 *            bounds array: [west, south, east, north] or [minLon, minLat,
	 *            maxLon, maxLat]
	 * @param unit
	 *            unit
	 * @return bounds
	 */
	public static Bounds bounds(double[] bounds, Unit unit) {
		return new Bounds(bounds, unit);
	}

	/**
	 * Create bounds in degrees
	 * 
	 * @param bounds
	 *            bounds degrees array: [west, south, east, north] or [minLon,
	 *            minLat, maxLon, maxLat]
	 * @return bounds
	 */
	public static Bounds degrees(double[] bounds) {
		return bounds(bounds, Unit.DEGREE);
	}

	/**
	 * Create bounds in meters
	 * 
	 * @param bounds
	 *            bounds meters array: [west, south, east, north] or [minLon,
	 *            minLat, maxLon, maxLat]
	 * @return bounds
	 */
	public static Bounds meters(double[] bounds) {
		return bounds(bounds, Unit.METER);
	}

	/**
	 * Constructor, in {@link Unit#DEGREE} units
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
		this(west, south, east, north, Unit.DEGREE);
	}

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
	 * @param unit
	 *            unit
	 */
	public Bounds(double west, double south, double east, double north,
			Unit unit) {
		this.west = west;
		this.south = south;
		this.east = east;
		this.north = north;
		this.unit = unit;
	}

	/**
	 * Constructor
	 * 
	 * @param southwest
	 *            southwest corner
	 * @param northeast
	 *            northeast corner
	 */
	public Bounds(Point southwest, Point northeast) {
		this(southwest.getLongitude(), southwest.getLatitude(),
				northeast.getLongitude(), northeast.getLatitude(),
				southwest.getUnit());

		if (!isUnit(northeast.getUnit())) {
			throw new IllegalArgumentException(
					"Points are in different units. southwest: " + unit
							+ ", northeast: " + northeast.getUnit());
		}
	}

	/**
	 * Constructor, in {@link Unit#DEGREE} units
	 * 
	 * @param strip
	 *            longitudinal strip
	 * @param band
	 *            latitude band
	 */
	public Bounds(LongitudinalStrip strip, LatitudeBand band) {
		this(strip.getWest(), band.getSouth(), strip.getEast(), band.getNorth(),
				Unit.DEGREE);
	}

	/**
	 * Constructor, in {@link Unit#DEGREE} units
	 * 
	 * @param bounds
	 *            bounds array: [west, south, east, north] or [minLon, minLat,
	 *            maxLon, maxLat]
	 */
	public Bounds(double[] bounds) {
		this(bounds, Unit.DEGREE);
	}

	/**
	 * Constructor
	 * 
	 * @param bounds
	 *            bounds array: [west, south, east, north] or [minLon, minLat,
	 *            maxLon, maxLat]
	 * @param unit
	 *            unit
	 */
	public Bounds(double[] bounds, Unit unit) {
		this(bounds[0], bounds[1], bounds[2], bounds[3], unit);
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
	 * Are bounds in degrees
	 * 
	 * @return true if degrees
	 */
	public boolean isDegrees() {
		return isUnit(Unit.DEGREE);
	}

	/**
	 * Are bounds in meters
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
	 * @return bounds in units, same bounds if equal units
	 */
	public Bounds toUnit(Unit unit) {
		Bounds bounds = null;
		if (isUnit(unit)) {
			bounds = this;
		} else {
			Point southwest = getSouthwest().toUnit(unit);
			Point northeast = getNortheast().toUnit(unit);
			bounds = new Bounds(southwest, northeast);
		}
		return bounds;
	}

	/**
	 * Convert to degrees
	 * 
	 * @return bounds in degrees, same bounds if already in degrees
	 */
	public Bounds toDegrees() {
		return toUnit(Unit.DEGREE);
	}

	/**
	 * Convert to meters
	 * 
	 * @return bounds in meters, same bounds if already in meters
	 */
	public Bounds toMeters() {
		return toUnit(Unit.METER);
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
		return getCenter().getLatitude();
	}

	/**
	 * Get the center coordinate
	 * 
	 * @return center coordinate
	 */
	public Point getCenter() {

		double centerLongitude = getCenterLongitude();

		Point northPoint = null;
		Point southPoint = null;
		switch (unit) {
		case DEGREE:
			northPoint = Point.degreesToMeters(centerLongitude, north);
			southPoint = Point.degreesToMeters(centerLongitude, south);
			break;
		case METER:
			northPoint = Point.meters(centerLongitude, north);
			southPoint = Point.meters(centerLongitude, south);
			break;
		default:
			throw new IllegalArgumentException("Unsupported unit: " + unit);
		}

		double centerX = northPoint.getLongitude();
		double centerY = southPoint.getLatitude()
				+ (0.5 * (northPoint.getLatitude() - southPoint.getLatitude()));

		Point point = Point.meters(centerX, centerY);
		if (unit == Unit.DEGREE) {
			point.toDegrees();
		}

		return point;
	}

	/**
	 * Get the southwest coordinate
	 * 
	 * @return southwest coordinate
	 */
	public Point getSouthwest() {
		return Point.point(getWest(), getSouth(), unit);
	}

	/**
	 * Get the northwest coordinate
	 * 
	 * @return northwest coordinate
	 */
	public Point getNorthwest() {
		return Point.point(getWest(), getNorth(), unit);
	}

	/**
	 * Get the southeast coordinate
	 * 
	 * @return southeast coordinate
	 */
	public Point getSoutheast() {
		return Point.point(getEast(), getSouth(), unit);
	}

	/**
	 * Get the northeast coordinate
	 * 
	 * @return northeast coordinate
	 */
	public Point getNortheast() {
		return Point.point(getEast(), getNorth(), unit);
	}

	/**
	 * Create a new bounds as the union between this bounds and the provided
	 * 
	 * @param bounds
	 *            bounds
	 * @return bounds
	 */
	public Bounds union(Bounds bounds) {

		bounds = bounds.toUnit(unit);

		double west = Math.min(getWest(), bounds.getWest());
		double south = Math.min(getSouth(), bounds.getSouth());
		double east = Math.max(getEast(), bounds.getEast());
		double north = Math.max(getNorth(), bounds.getNorth());

		return new Bounds(west, south, east, north, unit);
	}

	/**
	 * Get the pixel range where the bounds fit into the provided bounds
	 * 
	 * @param tile
	 *            tile
	 * @param bounds
	 *            bounds
	 * @return pixel range
	 */
	public PixelRange getPixelRange(MGRSTile tile, Bounds bounds) {
		return getPixelRange(tile.getWidth(), tile.getHeight(), bounds);
	}

	/**
	 * Get the pixel range where the bounds fit into the provided bounds
	 * 
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @param bounds
	 *            bounds
	 * @return pixel range
	 */
	public PixelRange getPixelRange(int width, int height, Bounds bounds) {
		bounds = bounds.toMeters();
		Pixel topLeft = MGRSUtils.getPixel(width, height, bounds,
				getNorthwest());
		Pixel bottomRight = MGRSUtils.getPixel(width, height, bounds,
				getSoutheast());
		return new PixelRange(topLeft, bottomRight);
	}

	/**
	 * Get the four line bounds in meters
	 * 
	 * @return lines
	 */
	public List<Line> getLines() {

		Point southwest = getSouthwest();
		Point northwest = getNorthwest();
		Point northeast = getNortheast();
		Point southeast = getSoutheast();

		List<Line> lines = new ArrayList<>();
		lines.add(Line.line(southwest, northwest));
		lines.add(Line.line(northwest, northeast));
		lines.add(Line.line(northeast, southeast));
		lines.add(Line.line(southeast, southwest));

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
