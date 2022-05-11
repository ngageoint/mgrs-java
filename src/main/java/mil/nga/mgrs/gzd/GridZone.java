package mil.nga.mgrs.gzd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mil.nga.mgrs.Label;
import mil.nga.mgrs.MGRS;
import mil.nga.mgrs.features.LatLng;
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
	public Collection<Line> getLines(Grid grid) {
		return getLines(grid.getPrecision());
	}

	/**
	 * Get the grid zone lines
	 * 
	 * @param precision
	 *            precision in meters
	 * @return lines
	 */
	public Collection<Line> getLines(int precision) {
		return getLines(bounds, precision);
	}

	/**
	 * Get the grid zone lines
	 * 
	 * @param tileBounds
	 *            tile bounds array: [west, south, east, north] or [minLon,
	 *            minLat, maxLon, maxLat]
	 * @param grid
	 *            grid
	 * @return lines
	 */
	public Collection<Line> getLines(double[] tileBounds, Grid grid) {
		return getLines(tileBounds, grid.getPrecision());
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
	public Collection<Line> getLines(Bounds tileBounds, Grid grid) {
		return getLines(tileBounds, grid.getPrecision());
	}

	/**
	 * Get the grid zone lines
	 * 
	 * @param tileBounds
	 *            tile bounds array: [west, south, east, north] or [minLon,
	 *            minLat, maxLon, maxLat]
	 * @param precision
	 *            precision in meters
	 * @return lines
	 */
	public Collection<Line> getLines(double[] tileBounds, int precision) {
		return getLines(new Bounds(tileBounds), precision);
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
	public Collection<Line> getLines(Bounds tileBounds, int precision) {

		Collection<Line> lines = new ArrayList<>();

		if (precision == 0) {
			// if precision is 0, draw the zone bounds
			lines.addAll(bounds.getLines());
		} else {
			Collection<Line> longitudeLines = longitudeLines(tileBounds,
					precision);
			lines.addAll(longitudeLines);

			Collection<Line> latitudeLines = latitudeLines(tileBounds,
					precision);
			lines.addAll(latitudeLines);
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
	public Collection<Label> getLabels(Grid grid) {
		return getLabels(grid.getPrecision());
	}

	/**
	 * Get the grid zone labels
	 * 
	 * @param precision
	 *            precision in meters
	 * @return labels
	 */
	public Collection<Label> getLabels(int precision) {
		return getLabels(bounds, precision);
	}

	/**
	 * Get the grid zone labels
	 * 
	 * @param tileBounds
	 *            tile bounds array: [west, south, east, north] or [minLon,
	 *            minLat, maxLon, maxLat]
	 * @param grid
	 *            grid
	 * @return labels
	 */
	public Collection<Label> getLabels(double[] tileBounds, Grid grid) {
		return getLabels(tileBounds, grid.getPrecision());
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
	public Collection<Label> getLabels(Bounds tileBounds, Grid grid) {
		return getLabels(tileBounds, grid.getPrecision());
	}

	/**
	 * Get the grid zone labels
	 * 
	 * @param tileBounds
	 *            tile bounds array: [west, south, east, north] or [minLon,
	 *            minLat, maxLon, maxLat]
	 * @param precision
	 *            precision in meters
	 * @return labels
	 */
	public Collection<Label> getLabels(double[] tileBounds, int precision) {
		return getLabels(new Bounds(tileBounds), precision);
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
	public Collection<Label> getLabels(Bounds tileBounds, int precision) {

		int zoneNumber = getNumber();
		char bandLetter = getLetter();
		Hemisphere hemisphere = getHemisphere();

		Collection<Label> labels = new ArrayList<>();

		if (precision == 0) {
			// if precision is 0, draw the GZD name
			Point center = bounds.getCenterPoint();

			Point zoneLowerLeft = bounds.getSouthwestPoint();
			Point zoneUpperRight = bounds.getNortheastPoint();
			double[] zoneBoundingBox = new double[] { zoneLowerLeft.x,
					zoneLowerLeft.y, zoneUpperRight.x, zoneUpperRight.y };

			String name = Integer.toString(zoneNumber) + bandLetter;
			Label gzdLabel = new Label(name, center, zoneBoundingBox,
					bandLetter, zoneNumber);
			labels.add(gzdLabel);

			return labels;
		}

		Double minLat = Math.max(tileBounds.getSouth(), bounds.getSouth());
		Double maxLat = Math.min(tileBounds.getNorth(), bounds.getNorth());

		Double minLon = Math.max(tileBounds.getWest(), bounds.getWest());
		Double maxLon = Math.min(tileBounds.getEast(), bounds.getEast());

		UTM lowerLeftUTM = UTM.from(new LatLng(minLat, minLon), zoneNumber,
				hemisphere);
		double lowerEasting = (Math.floor(lowerLeftUTM.getEasting() / precision)
				* precision) - precision;
		double lowerNorthing = (Math
				.ceil(lowerLeftUTM.getNorthing() / precision) * precision);

		UTM upperRightUTM = UTM.from(new LatLng(maxLat, maxLon), zoneNumber,
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
				Label label = getLabel(precision, easting, northing);
				labels.add(label);

				easting = newEasting;
			}

			northing = newNorthing;
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

		UTM lowerLeftUTM = UTM.from(
				new LatLng(bounds.getSouth(), bounds.getWest()), zoneNumber,
				hemisphere);
		UTM upperRightUTM = UTM.from(
				new LatLng(bounds.getNorth(), bounds.getEast()), zoneNumber,
				hemisphere);

		double newNorthing = northing - precision;
		double centerNorthing = northing - (precision / 2);

		double newEasting = easting + precision;
		double centerEasting = easting + (precision / 2);

		if (newNorthing < lowerLeftUTM.getNorthing()) {
			LatLng currentLatLng = LatLng.from(new UTM(zoneNumber, hemisphere,
					centerEasting, lowerLeftUTM.getNorthing()));
			UTM utm = UTM.from(
					new LatLng(bounds.getSouth(), currentLatLng.longitude),
					zoneNumber, hemisphere);
			centerNorthing = ((northing - lowerLeftUTM.getNorthing()) / 2)
					+ lowerLeftUTM.getNorthing();
			newNorthing = utm.getNorthing();
		} else if (northing > upperRightUTM.getNorthing()) {
			LatLng currentLatLng = LatLng.from(new UTM(zoneNumber, hemisphere,
					centerEasting, upperRightUTM.getNorthing()));
			UTM utm = UTM.from(
					new LatLng(bounds.getNorth(), currentLatLng.longitude),
					zoneNumber, hemisphere);
			centerNorthing = ((upperRightUTM.getNorthing() - newNorthing) / 2)
					+ newNorthing;
			northing = utm.getNorthing();
		}

		if (easting < lowerLeftUTM.getEasting()) {
			LatLng currentLatLng = LatLng.from(new UTM(zoneNumber, hemisphere,
					newEasting, centerNorthing));
			UTM utm = UTM.from(
					new LatLng(currentLatLng.latitude, bounds.getWest()),
					zoneNumber, hemisphere);
			centerEasting = utm.getEasting()
					+ ((newEasting - utm.getEasting()) / 2);
			easting = utm.getEasting();
		} else if (newEasting > upperRightUTM.getEasting()) {
			LatLng currentLatLng = LatLng.from(
					new UTM(zoneNumber, hemisphere, easting, centerNorthing));
			UTM utm = UTM.from(
					new LatLng(currentLatLng.latitude, bounds.getEast()),
					zoneNumber, hemisphere);
			centerEasting = easting + ((utm.getEasting() - easting) / 2);
			newEasting = utm.getEasting();
		}

		String id = MGRS.get100KId(centerEasting, centerNorthing, zoneNumber);
		LatLng centerLatLng = LatLng.from(
				new UTM(zoneNumber, hemisphere, centerEasting, centerNorthing));
		Point center = centerLatLng.toPoint();

		LatLng l1 = LatLng
				.from(new UTM(zoneNumber, hemisphere, easting, newNorthing));
		LatLng l2 = LatLng
				.from(new UTM(zoneNumber, hemisphere, easting, northing));
		LatLng l3 = LatLng
				.from(new UTM(zoneNumber, hemisphere, newEasting, northing));
		LatLng l4 = LatLng
				.from(new UTM(zoneNumber, hemisphere, newEasting, newNorthing));

		double minLatitude = Math.max(l1.latitude, l4.latitude);
		double maxLatitude = Math.min(l2.latitude, l3.latitude);

		double minLongitude = Math.max(l1.longitude, l2.longitude);
		double maxLongitude = Math.min(l3.longitude, l4.longitude);

		Point minPoint = LatLng.toPoint(minLatitude, minLongitude);
		Point maxPoint = LatLng.toPoint(maxLatitude, maxLongitude);

		return new Label(id, center,
				new double[] { minPoint.x, minPoint.y, maxPoint.x, maxPoint.y },
				getLetter(), zoneNumber);
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
	private Collection<Line> longitudeLines(Bounds tileBounds, int precision) {
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
	private Collection<Line> northernLongitudeLines(Bounds tileBounds,
			int precision) {

		int zoneNumber = getNumber();
		Hemisphere hemisphere = getHemisphere();

		List<Line> lines = new ArrayList<>();

		Double minLat = Math.max(tileBounds.getSouth(), bounds.getSouth());
		Double maxLat = Math.min(tileBounds.getNorth(), bounds.getNorth());

		Double minLon = Math.max(tileBounds.getWest(), bounds.getWest());
		Double maxLon = Math.min(tileBounds.getEast(), bounds.getEast());

		UTM lowerLeftUTM = UTM.from(new LatLng(minLat, minLon), zoneNumber,
				hemisphere);
		double lowerLeftEasting = (Math
				.floor(lowerLeftUTM.getEasting() / precision) * precision);
		double lowerLeftNorthing = (Math
				.floor(lowerLeftUTM.getNorthing() / precision) * precision);

		UTM upperRightUTM = UTM.from(new LatLng(maxLat, maxLon), zoneNumber,
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

				LatLng latLng1 = LatLng.from(
						new UTM(zoneNumber, hemisphere, easting, northing));
				LatLng latLng2 = LatLng.from(
						new UTM(zoneNumber, hemisphere, easting, newNorthing));
				Line line = new Line(latLng1, latLng2);
				lines.add(line);

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
	private Collection<Line> southernLongitudeLines(Bounds tileBounds,
			int precision) {

		int zoneNumber = getNumber();
		Hemisphere hemisphere = getHemisphere();

		Collection<Line> lines = new ArrayList<>();

		Double minLat = Math.max(tileBounds.getSouth(), bounds.getSouth());
		Double maxLat = Math.min(tileBounds.getNorth(), bounds.getNorth());

		Double minLon = Math.max(tileBounds.getWest(), bounds.getWest());
		Double maxLon = Math.min(tileBounds.getEast(), bounds.getEast());

		UTM upperLeftUTM = UTM.from(new LatLng(maxLat, minLon), zoneNumber,
				hemisphere);
		double upperLeftEasting = (Math
				.floor(upperLeftUTM.getEasting() / precision) * precision);
		double upperLeftNorthing = (Math
				.ceil(upperLeftUTM.getNorthing() / precision + 1) * precision);
		if (getLetter() == 'M') {
			upperLeftNorthing = 10000000.0;
			upperLeftUTM = new UTM(upperLeftUTM.getZoneNumber(),
					Hemisphere.SOUTH, upperLeftUTM.getEasting(),
					upperLeftUTM.getNorthing());
		}

		UTM lowerRightUTM = UTM.from(new LatLng(minLat, maxLon), zoneNumber,
				hemisphere);
		double lowerRightEasting = (Math
				.ceil(lowerRightUTM.getEasting() / precision) * precision);
		double lowerRightNorthing = (Math
				.floor(lowerRightUTM.getNorthing() / precision) * precision);

		for (double easting = upperLeftEasting; easting <= lowerRightEasting; easting += precision) {
			double northing = upperLeftNorthing;
			while (northing >= lowerRightNorthing) {
				double newNorthing = northing - precision;

				LatLng latLng1 = LatLng.from(new UTM(zoneNumber,
						upperLeftUTM.getHemisphere(), easting, northing));
				LatLng latLng2 = LatLng.from(new UTM(zoneNumber,
						lowerRightUTM.getHemisphere(), easting, newNorthing));
				Line line = new Line(latLng1, latLng2);
				lines.add(line);

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
	private Collection<Line> latitudeLines(Bounds tileBounds, int precision) {

		int zoneNumber = getNumber();
		Hemisphere hemisphere = getHemisphere();

		Collection<Line> lines = new ArrayList<>();

		Double minLat = Math.max(tileBounds.getSouth(), bounds.getSouth());
		Double maxLat = Math.min(tileBounds.getNorth(), bounds.getNorth());

		Double minLon = Math.max(tileBounds.getWest(), bounds.getWest());
		Double maxLon = Math.min(tileBounds.getEast(), bounds.getEast());

		UTM lowerLeftUTM = UTM.from(new LatLng(minLat, minLon), zoneNumber,
				hemisphere);
		double lowerEasting = (Math.floor(lowerLeftUTM.getEasting() / precision)
				* precision) - precision;
		double lowerNorthing = (Math
				.floor(lowerLeftUTM.getNorthing() / precision) * precision);

		UTM upperRightUTM = UTM.from(new LatLng(maxLat, maxLon), zoneNumber,
				hemisphere);
		double upperEasting = (Math.ceil(upperRightUTM.getEasting() / precision)
				* precision) + precision;
		double upperNorthing = (Math
				.ceil(upperRightUTM.getNorthing() / precision) * precision)
				+ precision;
		if (getLetter() == 'M') {
			upperNorthing = 10000000.0;
		}

		double northing = lowerNorthing;
		while (northing < upperNorthing) {
			double easting = lowerEasting;
			double newNorthing = northing + precision;
			while (easting < upperEasting) {
				double newEasting = easting + precision;
				LatLng latLng1 = LatLng.from(
						new UTM(zoneNumber, hemisphere, easting, northing));
				LatLng latLng2 = LatLng.from(
						new UTM(zoneNumber, hemisphere, newEasting, northing));
				Line line = new Line(latLng1, latLng2);
				lines.add(line);

				easting = newEasting;
			}

			northing = newNorthing;
		}

		return lines;
	}

}
