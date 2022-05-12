package mil.nga.mgrs.gzd;

import java.util.Iterator;

import mil.nga.mgrs.MGRSConstants;

/**
 * Zone Number Range
 * 
 * @author osbornb
 */
public class ZoneNumberRange implements Iterable<Integer> {

	/**
	 * Western zone number
	 */
	private int west;

	/**
	 * Eastern zone number
	 */
	private int east;

	/**
	 * Constructor, full range
	 */
	public ZoneNumberRange() {
		this(MGRSConstants.MIN_ZONE_NUMBER, MGRSConstants.MAX_ZONE_NUMBER);
	}

	/**
	 * Constructor
	 * 
	 * @param west
	 *            western zone number
	 * @param east
	 *            eastern zone number
	 */
	public ZoneNumberRange(int west, int east) {
		this.west = west;
		this.east = east;
	}

	/**
	 * Get the western zone number
	 * 
	 * @return western zone number
	 */
	public int getWest() {
		return west;
	}

	/**
	 * Set the western zone number
	 * 
	 * @param west
	 *            western zone number
	 */
	public void setWest(int west) {
		this.west = west;
	}

	/**
	 * Get the eastern zone number
	 * 
	 * @return eastern zone number
	 */
	public int getEast() {
		return east;
	}

	/**
	 * Set the eastern zone number
	 * 
	 * @param east
	 *            eastern zone number
	 */
	public void setEast(int east) {
		this.east = east;
	}

	/**
	 * Get the western longitude
	 * 
	 * @return longitude
	 */
	public double getWestLongitude() {
		return GridZones.getLongitudinalStrip(west).getWest();
	}

	/**
	 * Get the eastern longitude
	 * 
	 * @return longitude
	 */
	public double getEastLongitude() {
		return GridZones.getLongitudinalStrip(east).getEast();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {

			/**
			 * Zone number
			 */
			private int number = west;

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean hasNext() {
				return number <= east;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Integer next() {
				return number++;
			}

		};
	}

}
