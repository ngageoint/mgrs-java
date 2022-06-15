package mil.nga.mgrs.gzd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	 * Constructor, full range
	 */
	public GridRange() {
		this.zoneNumberRange = new ZoneNumberRange();
		this.bandLetterRange = new BandLetterRange();
	}

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
			 * Minimum zone number
			 */
			private final int minZoneNumber = zoneNumberRange.getWest();

			/**
			 * Maximum zone number
			 */
			private final int maxZoneNumber = zoneNumberRange.getEast();

			/**
			 * Minimum band letter
			 */
			private final char minBandLetter = bandLetterRange.getSouth();

			/**
			 * Minimum band letter
			 */
			private final char maxBandLetter = bandLetterRange.getNorth();

			/**
			 * Zone number
			 */
			private int zoneNumber = minZoneNumber;

			/**
			 * Band letter
			 */
			private char bandLetter = minBandLetter;

			/**
			 * Grid zone
			 */
			private GridZone gridZone = null;

			/**
			 * Additional special case grid zones
			 */
			private List<GridZone> additional = new ArrayList<>();

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean hasNext() {

				while (gridZone == null && zoneNumber <= maxZoneNumber) {

					gridZone = GridZones.getGridZone(zoneNumber, bandLetter);

					// Handle special case grid gaps (Svalbard)
					if (gridZone == null) {

						// Retrieve the western grid if on the left edge
						if (zoneNumber == minZoneNumber) {
							additional.add(GridZones.getGridZone(zoneNumber - 1,
									bandLetter));
						}

						// Expand to the eastern grid if on the right edge
						if (zoneNumber == maxZoneNumber) {
							additional.add(GridZones.getGridZone(zoneNumber + 1,
									bandLetter));
						}

					} else {

						// Handle special case grid zone expansions (Norway)
						int expand = gridZone.getStripExpand();
						if (expand != 0) {
							if (expand > 0) {
								for (int expandZone = zoneNumber
										+ expand; expandZone > zoneNumber; expandZone--) {
									if (expandZone > maxZoneNumber) {
										additional.add(GridZones.getGridZone(
												expandZone, bandLetter));
									} else {
										break;
									}
								}
							} else {
								for (int expandZone = zoneNumber
										+ expand; expandZone < zoneNumber; expandZone++) {
									if (expandZone < minZoneNumber) {
										additional.add(GridZones.getGridZone(
												expandZone, bandLetter));
									} else {
										break;
									}
								}
							}
						}

					}

					bandLetter = MGRSUtils.nextBandLetter(bandLetter);
					if (bandLetter > maxBandLetter) {
						zoneNumber++;
						bandLetter = minBandLetter;
					}

				}

				if (gridZone == null && !additional.isEmpty()) {
					gridZone = additional.remove(0);
				}

				return gridZone != null;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GridZone next() {
				GridZone next = gridZone;
				gridZone = null;
				return next;
			}

		};
	}

}
