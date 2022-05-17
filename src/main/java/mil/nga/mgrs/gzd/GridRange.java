package mil.nga.mgrs.gzd;

import java.util.Iterator;

import mil.nga.mgrs.MGRSUtils;
import mil.nga.mgrs.features.Bounds;

/**
 * Grid Range
 * 
 * @author osbornb
 */
public class GridRange implements Iterable<GridZone> {

	/**
	 * Zone Number Range
	 */
	private ZoneNumberRange zoneNumberRange;

	/**
	 * Band Letter Range
	 */
	private BandLetterRange bandLetterRange;

	/**
	 * Constructor
	 * 
	 * @param zoneNumberRange
	 *            zone number range
	 * @param bandLetterRange
	 *            band letter range
	 */
	public GridRange(ZoneNumberRange zoneNumberRange,
			BandLetterRange bandLetterRange) {
		this.zoneNumberRange = zoneNumberRange;
		this.bandLetterRange = bandLetterRange;
	}

	/**
	 * Get the zone number range
	 * 
	 * @return zone number range
	 */
	public ZoneNumberRange getZoneNumberRange() {
		return zoneNumberRange;
	}

	/**
	 * Set the zone number range
	 * 
	 * @param zoneNumberRange
	 *            zone number range
	 */
	public void setZoneNumberRange(ZoneNumberRange zoneNumberRange) {
		this.zoneNumberRange = zoneNumberRange;
	}

	/**
	 * Get the band letter range
	 * 
	 * @return band letter range
	 */
	public BandLetterRange getBandLetterRange() {
		return bandLetterRange;
	}

	/**
	 * Set the band letter range
	 * 
	 * @param bandLetterRange
	 *            band letter range
	 */
	public void setBandLetterRange(BandLetterRange bandLetterRange) {
		this.bandLetterRange = bandLetterRange;
	}

	/**
	 * Get the grid range bounds
	 * 
	 * @return bounds
	 */
	public Bounds getBounds() {

		double west = zoneNumberRange.getWestLongitude();
		double south = bandLetterRange.getSouthLatitude();
		double east = zoneNumberRange.getEastLongitude();
		double north = bandLetterRange.getNorthLatitude();

		return Bounds.degrees(west, south, east, north);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<GridZone> iterator() {
		return new Iterator<GridZone>() {

			/**
			 * Zone number
			 */
			private int zoneNumber = zoneNumberRange.getWest();

			/**
			 * Band letter
			 */
			private char bandLetter = bandLetterRange.getSouth();

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean hasNext() {
				return zoneNumber <= zoneNumberRange.getEast();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GridZone next() {
				GridZone gridZone = GridZones.getGridZone(zoneNumber,
						bandLetter);
				bandLetter = MGRSUtils.nextBandLetter(bandLetter);
				if (bandLetter > bandLetterRange.getNorth()) {
					zoneNumber++;
					bandLetter = bandLetterRange.getSouth();
				}
				return gridZone;
			}

		};
	}

}
