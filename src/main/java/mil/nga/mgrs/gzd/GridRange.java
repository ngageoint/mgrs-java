package mil.nga.mgrs.gzd;

/**
 * Grid Range
 * 
 * @author osbornb
 */
public class GridRange {

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

		return new Bounds(west, south, east, north);
	}

}
