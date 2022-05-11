package mil.nga.mgrs.features;

/**
 * WGS84 line between two coordinates
 * 
 * @author wnewman
 * @author osbornb
 */
public class LatLngLine {

	/**
	 * Coordinate 1
	 */
	public LatLng coordinate1;

	/**
	 * Coordinate 2
	 */
	public LatLng coordinate2;

	/**
	 * Constructor
	 * 
	 * @param coordinate1
	 *            first coordinate
	 * @param coordinate2
	 *            second coordinate
	 */
	public LatLngLine(LatLng coordinate1, LatLng coordinate2) {
		this.coordinate1 = coordinate1;
		this.coordinate2 = coordinate2;
	}

	/**
	 * Get the first coordinate
	 * 
	 * @return first coordinate
	 */
	public LatLng getCoordinate1() {
		return coordinate1;
	}

	/**
	 * Set the first coordinate
	 * 
	 * @param coordinate1
	 *            first coordinate
	 */
	public void setCoordinate1(LatLng coordinate1) {
		this.coordinate1 = coordinate1;
	}

	/**
	 * Get the second coordinate
	 * 
	 * @return second coordinate
	 */
	public LatLng getCoordinate2() {
		return coordinate2;
	}

	/**
	 * Set the second coordinate
	 * 
	 * @param coordinate2
	 *            second coordinate
	 */
	public void setCoordinate2(LatLng coordinate2) {
		this.coordinate2 = coordinate2;
	}

}
