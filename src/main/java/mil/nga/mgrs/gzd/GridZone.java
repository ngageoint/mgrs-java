package mil.nga.mgrs.gzd;

import java.util.ArrayList;
import java.util.List;

import mil.nga.mgrs.MGRSConstants;
import mil.nga.mgrs.MGRSUtils;
import mil.nga.mgrs.features.Bounds;
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
		this.bounds = Bounds.bounds(strip, band);
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
	 * @param precision
	 *            precision in meters
	 * @return lines
	 */
	public List<Line> getLines(Bounds tileBounds, int precision) {

		List<Line> lines = null;

		if (precision == 0) {
			// if precision is 0, draw the zone bounds
			lines = bounds.getLines();
		} else {
			tileBounds = tileBounds.toDegrees();
			lines = new ArrayList<>();
			lines.addAll(longitudeLines(tileBounds, precision));
			lines.addAll(latitudeLines(tileBounds, precision));
		}

		return lines;
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
		char bandLetter = getLetter();
		Hemisphere hemisphere = getHemisphere();

		List<Line> lines = new ArrayList<>();

		tileBounds = tileBounds.overlap(bounds);

		UTM upperLeftUTM = UTM.from(tileBounds.getNorthwest(), zoneNumber,
				hemisphere);
		double upperLeftEasting = (Math
				.floor(upperLeftUTM.getEasting() / precision) * precision);
		double upperLeftNorthing = (Math
				.ceil(upperLeftUTM.getNorthing() / precision + 1) * precision);
		if (bandLetter == MGRSConstants.BAND_LETTER_SOUTH) {
			upperLeftNorthing = Math.min(10000000.0, upperLeftNorthing);
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

				Point latLng1 = Point.from(
						new UTM(zoneNumber, hemisphere, easting, northing));
				Point latLng2 = Point.from(
						new UTM(zoneNumber, hemisphere, easting, newNorthing));
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
		char bandLetter = getLetter();
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
		if (bandLetter == MGRSConstants.BAND_LETTER_SOUTH) {
			upperNorthing = Math.min(10000000.0, upperNorthing);
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
