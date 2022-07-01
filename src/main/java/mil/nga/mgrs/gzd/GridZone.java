package mil.nga.mgrs.gzd;

import java.util.ArrayList;
import java.util.List;

import mil.nga.grid.GridUtils;
import mil.nga.grid.features.Bounds;
import mil.nga.grid.features.Line;
import mil.nga.grid.features.Point;
import mil.nga.mgrs.MGRSUtils;
import mil.nga.mgrs.features.GridLine;
import mil.nga.mgrs.grid.GridType;
import mil.nga.mgrs.utm.Hemisphere;
import mil.nga.mgrs.utm.UTM;

/**
 * Grid Zone
 * 
 * @author wnewman
 * @author osbornb
 */
public class GridZone {

	/**
	 * Longitudinal strip
	 */
	private LongitudinalStrip strip;

	/**
	 * Latitude band
	 */
	private LatitudeBand band;

	/**
	 * Bounds
	 */
	private Bounds bounds;

	/**
	 * Constructor
	 * 
	 * @param strip
	 *            longitudinal strip
	 * @param band
	 *            latitude band
	 */
	public GridZone(LongitudinalStrip strip, LatitudeBand band) {
		this.strip = strip;
		this.band = band;
		this.bounds = Bounds.degrees(strip.getWest(), band.getSouth(),
				strip.getEast(), band.getNorth());
	}

	/**
	 * Get the longitudinal strip
	 * 
	 * @return longitudinal strip
	 */
	public LongitudinalStrip getStrip() {
		return strip;
	}

	/**
	 * Get the latitude band
	 * 
	 * @return latitude band
	 */
	public LatitudeBand getBand() {
		return band;
	}

	/**
	 * Get the zone number
	 * 
	 * @return zone number
	 */
	public int getNumber() {
		return strip.getNumber();
	}

	/**
	 * Get the band letter
	 * 
	 * @return band letter
	 */
	public char getLetter() {
		return band.getLetter();
	}

	/**
	 * Get the hemisphere
	 * 
	 * @return hemisphere
	 */
	public Hemisphere getHemisphere() {
		return band.getHemisphere();
	}

	/**
	 * Get the bounds
	 * 
	 * @return bounds
	 */
	public Bounds getBounds() {
		return bounds;
	}

	/**
	 * Get the label name
	 * 
	 * @return name
	 */
	public String getName() {
		return MGRSUtils.getLabelName(getNumber(), getLetter());
	}

	/**
	 * Is the provided bounds within the zone bounds
	 * 
	 * @param bounds
	 *            bounds
	 * @return true if within bounds
	 */
	public boolean isWithin(Bounds bounds) {
		bounds = bounds.toUnit(this.bounds.getUnit());
		return this.bounds.getSouth() <= bounds.getNorth()
				&& this.bounds.getNorth() >= bounds.getSouth()
				&& this.bounds.getWest() <= bounds.getEast()
				&& this.bounds.getEast() >= bounds.getWest();
	}

	/**
	 * Get the longitudinal strip expansion, number of additional neighbors to
	 * iterate over in combination with this strip
	 * 
	 * @return longitudinal strip neighbor iteration expansion
	 */
	public int getStripExpand() {
		return strip.getExpand();
	}

	/**
	 * Get the grid zone lines
	 * 
	 * @param gridType
	 *            grid type
	 * @return lines
	 */
	public List<GridLine> getLines(GridType gridType) {
		return getLines(bounds, gridType);
	}

	/**
	 * Get the grid zone lines
	 * 
	 * @param tileBounds
	 *            tile bounds
	 * @param gridType
	 *            grid type
	 * @return lines
	 */
	public List<GridLine> getLines(Bounds tileBounds, GridType gridType) {

		List<GridLine> lines = null;

		if (gridType == GridType.GZD) {
			// if precision is 0, draw the zone bounds
			lines = new ArrayList<>();
			for (Line line : bounds.getLines()) {
				lines.add(GridLine.line(line, GridType.GZD));
			}
		} else {

			Bounds drawBounds = getDrawBounds(tileBounds, gridType);

			if (drawBounds != null) {

				lines = new ArrayList<>();

				int precision = gridType.getPrecision();
				int zoneNumber = getNumber();
				Hemisphere hemisphere = getHemisphere();
				double minLon = bounds.getMinLongitude();
				double maxLon = bounds.getMaxLongitude();

				for (double easting = drawBounds
						.getMinLongitude(); easting < drawBounds
								.getMaxLongitude(); easting += precision) {

					GridType eastingPrecision = GridType.getPrecision(easting);

					for (double northing = drawBounds
							.getMinLatitude(); northing < drawBounds
									.getMaxLatitude(); northing += precision) {

						GridType northingPrecision = GridType
								.getPrecision(northing);

						Point southwest = UTM.point(zoneNumber, hemisphere,
								easting, northing);
						Point northwest = UTM.point(zoneNumber, hemisphere,
								easting, northing + precision);
						Point southeast = UTM.point(zoneNumber, hemisphere,
								easting + precision, northing);

						// For points outside the tile grid longitude bounds,
						// get a bound just outside the bounds
						if (precision > 1) {
							if (southwest.getLongitude() < minLon) {
								southwest = getWestBoundsPoint(easting,
										northing, southwest, southeast);
							} else if (southeast.getLongitude() > maxLon) {
								southeast = getEastBoundsPoint(easting,
										northing, southwest, southeast);
							}
						}

						// Vertical line
						lines.add(GridLine.line(southwest, northwest,
								eastingPrecision));

						// Horizontal line
						lines.add(GridLine.line(southwest, southeast,
								northingPrecision));

					}
				}

			}

		}

		return lines;
	}

	/**
	 * Get a point west of the horizontal bounds at one meter precision
	 * 
	 * @param easting
	 *            easting value
	 * @param northing
	 *            northing value
	 * @param west
	 *            west point
	 * @param east
	 *            east point
	 * @return higher precision point
	 */
	private Point getWestBoundsPoint(double easting, double northing,
			Point west, Point east) {
		return getBoundsPoint(easting, northing, west, east, false);
	}

	/**
	 * Get a point east of the horizontal bounds at one meter precision
	 * 
	 * @param easting
	 *            easting value
	 * @param northing
	 *            northing value
	 * @param west
	 *            west point
	 * @param east
	 *            east point
	 * @return higher precision point
	 */
	private Point getEastBoundsPoint(double easting, double northing,
			Point west, Point east) {
		return getBoundsPoint(easting, northing, west, east, true);
	}

	/**
	 * Get a point outside of the horizontal bounds at one meter precision
	 * 
	 * @param easting
	 *            easting value
	 * @param northing
	 *            northing value
	 * @param west
	 *            west point
	 * @param east
	 *            east point
	 * @param eastern
	 *            true if east of the eastern bounds, false if west of the
	 *            western bounds
	 * @return higher precision point
	 */
	private Point getBoundsPoint(double easting, double northing, Point west,
			Point east, boolean eastern) {

		Line line = GridLine.line(west, east);

		Line boundsLine;
		if (eastern) {
			boundsLine = bounds.getEastLine();
		} else {
			boundsLine = bounds.getWestLine();
		}

		int zoneNumber = getNumber();
		Hemisphere hemisphere = getHemisphere();

		// Intersection between the horizontal line and vertical bounds line
		Point intersection = GridUtils.intersection(line, boundsLine);

		// Intersection easting
		UTM intersectionUTM = UTM.from(intersection, zoneNumber, hemisphere);
		double intersectionEasting = intersectionUTM.getEasting();

		// One meter precision just outside the bounds
		double boundsEasting = intersectionEasting - easting;
		if (eastern) {
			boundsEasting = Math.ceil(boundsEasting);
		} else {
			boundsEasting = Math.floor(boundsEasting);
		}

		// Higher precision point just outside of the bounds
		Point boundsPoint = UTM.point(zoneNumber, hemisphere,
				easting + boundsEasting, northing);

		return boundsPoint;
	}

	/**
	 * Get the draw bounds of easting and northing in meters
	 * 
	 * @param tileBounds
	 *            tile bounds
	 * @param gridType
	 *            grid type
	 * @return draw bounds or null
	 */
	public Bounds getDrawBounds(Bounds tileBounds, GridType gridType) {

		Bounds drawBounds = null;

		tileBounds = tileBounds.toDegrees().overlap(bounds);

		if (!tileBounds.isEmpty()) {

			int zoneNumber = getNumber();
			Hemisphere hemisphere = getHemisphere();

			UTM upperLeftUTM = UTM.from(tileBounds.getNorthwest(), zoneNumber,
					hemisphere);
			UTM lowerLeftUTM = UTM.from(tileBounds.getSouthwest(), zoneNumber,
					hemisphere);
			UTM lowerRightUTM = UTM.from(tileBounds.getSoutheast(), zoneNumber,
					hemisphere);
			UTM upperRightUTM = UTM.from(tileBounds.getNortheast(), zoneNumber,
					hemisphere);

			int precision = gridType.getPrecision();
			double leftEasting = Math.floor(Math.min(upperLeftUTM.getEasting(),
					lowerLeftUTM.getEasting()) / precision) * precision;
			double lowerNorthing = Math
					.floor(Math.min(lowerLeftUTM.getNorthing(),
							lowerRightUTM.getNorthing()) / precision)
					* precision;
			double rightEasting = Math
					.ceil(Math.max(lowerRightUTM.getEasting(),
							upperRightUTM.getEasting()) / precision)
					* precision;
			double upperNorthing = Math
					.ceil(Math.max(upperRightUTM.getNorthing(),
							upperLeftUTM.getNorthing()) / precision)
					* precision;

			drawBounds = Bounds.meters(leftEasting, lowerNorthing, rightEasting,
					upperNorthing);

		}

		return drawBounds;
	}

}
