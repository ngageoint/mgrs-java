package mil.nga.mgrs.gzd;

import java.util.ArrayList;
import java.util.List;

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
			tileBounds = tileBounds.overlap(bounds);

			if (!tileBounds.isEmpty()) {

				lines = new ArrayList<>();

				int zoneNumber = getNumber();
				Hemisphere hemisphere = getHemisphere();

				UTM upperLeftUTM = UTM.from(tileBounds.getNorthwest(),
						zoneNumber, hemisphere);
				UTM lowerLeftUTM = UTM.from(tileBounds.getSouthwest(),
						zoneNumber, hemisphere);
				UTM lowerRightUTM = UTM.from(tileBounds.getSoutheast(),
						zoneNumber, hemisphere);
				UTM upperRightUTM = UTM.from(tileBounds.getNortheast(),
						zoneNumber, hemisphere);

				double leftEasting = Math
						.floor(Math.min(upperLeftUTM.getEasting(),
								lowerLeftUTM.getEasting()) / precision)
						* precision;
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

				for (double easting = leftEasting; easting <= rightEasting; easting += precision) {
					for (double northing = lowerNorthing; northing <= upperNorthing; northing += precision) {
						Point latLng1 = Point.create(zoneNumber, hemisphere,
								easting, northing);
						Point latLng2 = Point.create(zoneNumber, hemisphere,
								easting, northing + precision);
						Point latLng3 = Point.create(zoneNumber, hemisphere,
								easting + precision, northing);
						lines.add(Line.line(latLng1, latLng2));
						lines.add(Line.line(latLng1, latLng3));
					}
				}

			}

		}

		return lines;
	}

}
