package mil.nga.mgrs.gzd;

import java.util.ArrayList;
import java.util.List;

import mil.nga.mgrs.Label;
import mil.nga.mgrs.MGRS;
import mil.nga.mgrs.MGRSConstants;
import mil.nga.mgrs.MGRSUtils;
import mil.nga.mgrs.features.Line;
import mil.nga.mgrs.features.Point;
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
		this.bounds = new Bounds(strip, band);
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
		return this.bounds.getSouth() <= bounds.getNorth()
				&& this.bounds.getNorth() >= bounds.getSouth()
				&& this.bounds.getWest() <= bounds.getEast()
				&& this.bounds.getEast() >= bounds.getWest();
	}

	/**
	 * Get the grid zone lines
	 * 
	 * @param grid
	 *            grid
	 * @return lines
	 */
	public List<Line> getLines(Grid grid) {
		return getLines(grid.getPrecision());
	}

	/**
	 * Get the grid zone lines
	 * 
	 * @param precision
	 *            precision in meters
	 * @return lines
	 */
	public List<Line> getLines(int precision) {
		return getLines(bounds, precision);
	}

	/**
	 * Get the grid zone lines
	 * 
	 * @param tileBounds
	 *            tile bounds
	 * @param grid
	 *            grid
	 * @return lines
	 */
	public List<Line> getLines(Bounds tileBounds, Grid grid) {
		return getLines(tileBounds, grid.getPrecision());
	}

	/**
	 * Get the grid zone lines
	 * 
	 * @param tileBounds
	 *            tile bounds
	 * @param precision
	 *            precision in meters
	 * @return lines
	 */
	public List<Line> getLines(Bounds tileBounds, int precision) {

		List<Line> lines = new ArrayList<>();

		if (precision == 0) {
			// if precision is 0, draw the zone bounds
			lines.addAll(bounds.getLines());
		} else {
			tileBounds = tileBounds.toDegrees();
			lines.addAll(longitudeLines(tileBounds, precision));
			lines.addAll(latitudeLines(tileBounds, precision));
		}

		return lines;
	}

	/**
	 * Get the grid zone labels
	 * 
	 * @param grid
	 *            grid
	 * @return labels
	 */
	public List<Label> getLabels(Grid grid) {
		return getLabels(grid.getPrecision());
	}

	/**
	 * Get the grid zone labels
	 * 
	 * @param precision
	 *            precision in meters
	 * @return labels
	 */
	public List<Label> getLabels(int precision) {
		return getLabels(bounds, precision);
	}

	/**
	 * Get the grid zone labels
	 * 
	 * @param tileBounds
	 *            tile bounds
	 * @param grid
	 *            grid
	 * @return labels
	 */
	public List<Label> getLabels(Bounds tileBounds, Grid grid) {
		return getLabels(tileBounds, grid.getPrecision());
	}

	/**
	 * Get the grid zone labels
	 * 
	 * @param tileBounds
	 *            tile bounds
	 * @param precision
	 *            precision in meters
	 * @return labels
	 */
	public List<Label> getLabels(Bounds tileBounds, int precision) {

		int zoneNumber = getNumber();
		char bandLetter = getLetter();
		Hemisphere hemisphere = getHemisphere();

		List<Label> labels = new ArrayList<>();

		if (precision == 0) {
			// if precision is 0, draw the GZD name
			labels.add(new Label(getName(), bounds.getCenter(), bounds,
					zoneNumber, bandLetter));
		} else {

			tileBounds = tileBounds.toDegrees();

			tileBounds = tileBounds.overlap(bounds);

			UTM lowerLeftUTM = UTM.from(tileBounds.getSouthwest(), zoneNumber,
					hemisphere);
			double lowerEasting = (Math
					.floor(lowerLeftUTM.getEasting() / precision) * precision)
					- precision;
			double lowerNorthing = (Math
					.ceil(lowerLeftUTM.getNorthing() / precision) * precision);

			UTM upperRightUTM = UTM.from(tileBounds.getNortheast(), zoneNumber,
					hemisphere);
			double upperEasting = (Math
					.floor(upperRightUTM.getEasting() / precision) * precision)
					+ precision;
			double upperNorthing = (Math
					.ceil(upperRightUTM.getNorthing() / precision) * precision)
					+ precision;

			double northing = lowerNorthing;
			while (northing < upperNorthing) {
				double easting = lowerEasting;
				double newNorthing = northing + precision;
				while (easting < upperEasting) {
					double newEasting = easting + precision;

					// Draw cell name
					labels.add(getLabel(precision, easting, northing));

					easting = newEasting;
				}

				northing = newNorthing;
			}
		}

		return labels;
	}

	/**
	 * Get the grid zone label
	 * 
	 * @param precision
	 *            precision in meters
	 * @param easting
	 *            easting
	 * @param northing
	 *            northing
	 * @return labels
	 */
	private Label getLabel(int precision, double easting, double northing) {

		int zoneNumber = getNumber();
		Hemisphere hemisphere = getHemisphere();

		UTM lowerLeftUTM = UTM.from(bounds.getSouthwest(), zoneNumber,
				hemisphere);
		UTM upperRightUTM = UTM.from(bounds.getNortheast(), zoneNumber,
				hemisphere);

		double newNorthing = northing - precision;
		double centerNorthing = northing - (precision / 2);

		double newEasting = easting + precision;
		double centerEasting = easting + (precision / 2);

		if (newNorthing < lowerLeftUTM.getNorthing()) {
			Point currentLatLng = Point.from(new UTM(zoneNumber, hemisphere,
					centerEasting, lowerLeftUTM.getNorthing()));
			UTM utm = UTM.from(Point.degrees(currentLatLng.getLongitude(),
					bounds.getSouth()), zoneNumber, hemisphere);
			centerNorthing = ((northing - lowerLeftUTM.getNorthing()) / 2)
					+ lowerLeftUTM.getNorthing();
			newNorthing = utm.getNorthing();
		} else if (northing > upperRightUTM.getNorthing()) {
			Point currentLatLng = Point.from(new UTM(zoneNumber, hemisphere,
					centerEasting, upperRightUTM.getNorthing()));
			UTM utm = UTM.from(Point.degrees(currentLatLng.getLongitude(),
					bounds.getNorth()), zoneNumber, hemisphere);
			centerNorthing = ((upperRightUTM.getNorthing() - newNorthing) / 2)
					+ newNorthing;
			northing = utm.getNorthing();
		}

		if (easting < lowerLeftUTM.getEasting()) {
			Point currentLatLng = Point.from(new UTM(zoneNumber, hemisphere,
					newEasting, centerNorthing));
			UTM utm = UTM.from(
					Point.degrees(bounds.getWest(),
							currentLatLng.getLatitude()),
					zoneNumber, hemisphere);
			centerEasting = utm.getEasting()
					+ ((newEasting - utm.getEasting()) / 2);
			easting = utm.getEasting();
		} else if (newEasting > upperRightUTM.getEasting()) {
			Point currentLatLng = Point.from(
					new UTM(zoneNumber, hemisphere, easting, centerNorthing));
			UTM utm = UTM.from(
					Point.degrees(bounds.getEast(),
							currentLatLng.getLatitude()),
					zoneNumber, hemisphere);
			centerEasting = easting + ((utm.getEasting() - easting) / 2);
			newEasting = utm.getEasting();
		}

		String id = MGRS.get100KId(centerEasting, centerNorthing, zoneNumber);
		Point center = Point.from(
				new UTM(zoneNumber, hemisphere, centerEasting, centerNorthing));

		Point l1 = Point
				.from(new UTM(zoneNumber, hemisphere, easting, newNorthing));
		Point l2 = Point
				.from(new UTM(zoneNumber, hemisphere, easting, northing));
		Point l3 = Point
				.from(new UTM(zoneNumber, hemisphere, newEasting, northing));
		Point l4 = Point
				.from(new UTM(zoneNumber, hemisphere, newEasting, newNorthing));

		double minLatitude = Math.max(l1.getLatitude(), l4.getLatitude());
		double maxLatitude = Math.min(l2.getLatitude(), l3.getLatitude());

		double minLongitude = Math.max(l1.getLongitude(), l2.getLongitude());
		double maxLongitude = Math.min(l3.getLongitude(), l4.getLongitude());

		Bounds bounds = Bounds.degrees(minLongitude, minLatitude, maxLongitude,
				maxLatitude);

		return new Label(id, center, bounds, zoneNumber, getLetter());
	}

	/**
	 * Get the grid zone longitude lines
	 * 
	 * @param tileBounds
	 *            tile bounds
	 * @param precision
	 *            precision in meters
	 * @return lines
	 */
	private List<Line> longitudeLines(Bounds tileBounds, int precision) {
		if (getHemisphere() == Hemisphere.NORTH) {
			return northernLongitudeLines(tileBounds, precision);
		} else {
			return southernLongitudeLines(tileBounds, precision);
		}
	}

	/**
	 * Get the grid zone northern longitude lines
	 * 
	 * @param tileBounds
	 *            tile bounds
	 * @param precision
	 *            precision in meters
	 * @return lines
	 */
	private List<Line> northernLongitudeLines(Bounds tileBounds,
			int precision) {

		int zoneNumber = getNumber();
		Hemisphere hemisphere = getHemisphere();

		List<Line> lines = new ArrayList<>();

		tileBounds = tileBounds.overlap(bounds);

		UTM lowerLeftUTM = UTM.from(tileBounds.getSouthwest(), zoneNumber,
				hemisphere);
		double lowerLeftEasting = (Math
				.floor(lowerLeftUTM.getEasting() / precision) * precision);
		double lowerLeftNorthing = (Math
				.floor(lowerLeftUTM.getNorthing() / precision) * precision);

		UTM upperRightUTM = UTM.from(tileBounds.getNortheast(), zoneNumber,
				hemisphere);
		double endEasting = (Math.ceil(upperRightUTM.getEasting() / precision)
				* precision);
		double endNorthing = (Math.ceil(upperRightUTM.getNorthing() / precision)
				* precision);

		double easting = lowerLeftEasting;
		while (easting <= endEasting) {
			double newEasting = easting + precision;
			double northing = lowerLeftNorthing;
			while (northing <= endNorthing) {
				double newNorthing = northing + precision;

				Point latLng1 = Point.from(
						new UTM(zoneNumber, hemisphere, easting, northing));
				Point latLng2 = Point.from(
						new UTM(zoneNumber, hemisphere, easting, newNorthing));
				lines.add(Line.line(latLng1, latLng2));

				northing = newNorthing;
			}

			easting = newEasting;
		}

		return lines;
	}

	/**
	 * Get the grid zone southern longitude lines
	 * 
	 * @param tileBounds
	 *            tile bounds
	 * @param precision
	 *            precision in meters
	 * @return lines
	 */
	private List<Line> southernLongitudeLines(Bounds tileBounds,
			int precision) {

		int zoneNumber = getNumber();
		Hemisphere hemisphere = getHemisphere();

		List<Line> lines = new ArrayList<>();

		tileBounds = tileBounds.overlap(bounds);

		UTM upperLeftUTM = UTM.from(tileBounds.getNorthwest(), zoneNumber,
				hemisphere);
		double upperLeftEasting = (Math
				.floor(upperLeftUTM.getEasting() / precision) * precision);
		double upperLeftNorthing = (Math
				.ceil(upperLeftUTM.getNorthing() / precision + 1) * precision);
		if (getLetter() == MGRSConstants.BAND_LETTER_SOUTH) {
			upperLeftNorthing = 10000000.0;
			upperLeftUTM = new UTM(upperLeftUTM.getZoneNumber(),
					Hemisphere.SOUTH, upperLeftUTM.getEasting(),
					upperLeftUTM.getNorthing());
		}

		UTM lowerRightUTM = UTM.from(tileBounds.getSoutheast(), zoneNumber,
				hemisphere);
		double lowerRightEasting = (Math
				.ceil(lowerRightUTM.getEasting() / precision) * precision);
		double lowerRightNorthing = (Math
				.floor(lowerRightUTM.getNorthing() / precision) * precision);

		for (double easting = upperLeftEasting; easting <= lowerRightEasting; easting += precision) {
			double northing = upperLeftNorthing;
			while (northing >= lowerRightNorthing) {
				double newNorthing = northing - precision;

				Point latLng1 = Point.from(new UTM(zoneNumber,
						upperLeftUTM.getHemisphere(), easting, northing));
				Point latLng2 = Point.from(new UTM(zoneNumber,
						lowerRightUTM.getHemisphere(), easting, newNorthing));
				lines.add(Line.line(latLng1, latLng2));

				northing = newNorthing;
			}
		}

		return lines;
	}

	/**
	 * Get the grid zone latitude lines
	 * 
	 * @param tileBounds
	 *            tile bounds
	 * @param precision
	 *            precision in meters
	 * @return lines
	 */
	private List<Line> latitudeLines(Bounds tileBounds, int precision) {

		int zoneNumber = getNumber();
		Hemisphere hemisphere = getHemisphere();

		List<Line> lines = new ArrayList<>();

		tileBounds = tileBounds.overlap(bounds);

		UTM lowerLeftUTM = UTM.from(tileBounds.getSouthwest(), zoneNumber,
				hemisphere);
		double lowerEasting = (Math.floor(lowerLeftUTM.getEasting() / precision)
				* precision) - precision;
		double lowerNorthing = (Math
				.floor(lowerLeftUTM.getNorthing() / precision) * precision);

		UTM upperRightUTM = UTM.from(tileBounds.getNortheast(), zoneNumber,
				hemisphere);
		double upperEasting = (Math.ceil(upperRightUTM.getEasting() / precision)
				* precision) + precision;
		double upperNorthing = (Math
				.ceil(upperRightUTM.getNorthing() / precision) * precision)
				+ precision;
		if (getLetter() == MGRSConstants.BAND_LETTER_SOUTH) {
			upperNorthing = 10000000.0;
		}

		double northing = lowerNorthing;
		while (northing < upperNorthing) {
			double easting = lowerEasting;
			double newNorthing = northing + precision;
			while (easting < upperEasting) {
				double newEasting = easting + precision;
				Point latLng1 = Point.from(
						new UTM(zoneNumber, hemisphere, easting, northing));
				Point latLng2 = Point.from(
						new UTM(zoneNumber, hemisphere, newEasting, northing));
				lines.add(Line.line(latLng1, latLng2));

				easting = newEasting;
			}

			northing = newNorthing;
		}

		return lines;
	}

}
