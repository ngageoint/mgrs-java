package mil.nga.mgrs.features;

/**
 * Line between two points in meters
 * 
 * @author wnewman
 * @author osbornb
 */
public class Line {

	/**
	 * Point 1
	 */
	public Point point1;

	/**
	 * Point 2
	 */
	public Point point2;

	/**
	 * Constructor
	 * 
	 * @param point1
	 *            first point
	 * @param point2
	 *            second point
	 */
	public Line(Point point1, Point point2) {
		this.point1 = point1;
		this.point2 = point2;
	}

	/**
	 * Constructor
	 * 
	 * @param coordinate1
	 *            first coordinate
	 * @param coordinate2
	 *            second coordinate
	 */
	public Line(LatLng coordinate1, LatLng coordinate2) {
		this.point1 = coordinate1.toPoint();
		this.point2 = coordinate2.toPoint();
	}

	/**
	 * Get the first point
	 * 
	 * @return first point
	 */
	public Point getPoint1() {
		return point1;
	}

	/**
	 * Set the first point
	 * 
	 * @param point1
	 *            first point
	 */
	public void setPoint1(Point point1) {
		this.point1 = point1;
	}

	/**
	 * Get the second point
	 * 
	 * @return second point
	 */
	public Point getPoint2() {
		return point2;
	}

	/**
	 * Set the second point
	 * 
	 * @param point2
	 *            second point
	 */
	public void setPoint2(Point point2) {
		this.point2 = point2;
	}

}
