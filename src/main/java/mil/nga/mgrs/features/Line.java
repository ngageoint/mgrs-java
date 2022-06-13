package mil.nga.mgrs.features;

import mil.nga.mgrs.grid.GridType;

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
	private Point point1;

	/**
	 * Point 2
	 */
	private Point point2;

	/**
	 * Grid type the line represents if any
	 */
	private GridType gridType;

	/**
	 * Create a line
	 * 
	 * @param point1
	 *            first point
	 * @param point2
	 *            second point
	 * @return line
	 */
	public static Line line(Point point1, Point point2) {
		return new Line(point1, point2);
	}

	/**
	 * Create a line
	 * 
	 * @param point1
	 *            first point
	 * @param point2
	 *            second point
	 * @param gridType
	 *            line grid type
	 * @return line
	 */
	public static Line line(Point point1, Point point2, GridType gridType) {
		return new Line(point1, point2, gridType);
	}

	/**
	 * Constructor
	 * 
	 * @param point1
	 *            first point
	 * @param point2
	 *            second point
	 */
	public Line(Point point1, Point point2) {
		setPoints(point1, point2);
	}

	/**
	 * Constructor
	 * 
	 * @param point1
	 *            first point
	 * @param point2
	 *            second point
	 * @param gridType
	 *            line grid type
	 */
	public Line(Point point1, Point point2, GridType gridType) {
		this(point1, point2);
		this.gridType = gridType;
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
		validateUnits();
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
		validateUnits();
	}

	/**
	 * Set the points
	 * 
	 * @param point1
	 *            first point
	 * @param point2
	 *            second point
	 */
	public void setPoints(Point point1, Point point2) {
		this.point1 = point1;
		this.point2 = point2;
		validateUnits();
	}

	/**
	 * Get the unit
	 * 
	 * @return unit
	 */
	public Unit getUnit() {
		return point1.getUnit();
	}

	/**
	 * Is in the provided unit type
	 * 
	 * @param unit
	 *            unit
	 * @return true if in the unit
	 */
	public boolean isUnit(Unit unit) {
		return point1.isUnit(unit);
	}

	/**
	 * Is this line in degrees
	 * 
	 * @return true if degrees
	 */
	public boolean isDegrees() {
		return point1.isDegrees();
	}

	/**
	 * Is this line in meters
	 * 
	 * @return true if meters
	 */
	public boolean isMeters() {
		return point1.isMeters();
	}

	/**
	 * Convert to the unit
	 * 
	 * @param unit
	 *            unit
	 * @return point in units, same point if equal units
	 */
	public Line toUnit(Unit unit) {
		Line line = null;
		if (isUnit(unit)) {
			line = this;
		} else {
			line = new Line(point1.toUnit(unit), point2.toUnit(unit), gridType);
		}
		return line;
	}

	/**
	 * Convert to degrees
	 * 
	 * @return line in degrees, same line if already in degrees
	 */
	public Line toDegrees() {
		return toUnit(Unit.DEGREE);
	}

	/**
	 * Convert to meters
	 * 
	 * @return line in meters, same line if already in meters
	 */
	public Line toMeters() {
		return toUnit(Unit.METER);
	}

	/**
	 * Get the line grid type
	 * 
	 * @return grid type
	 */
	public GridType getGridType() {
		return gridType;
	}

	/**
	 * Check if the line has a grid type
	 * 
	 * @return true if has grid type
	 */
	public boolean hasGridType() {
		return gridType != null;
	}

	/**
	 * Set the line grid type
	 * 
	 * @param gridType
	 *            grid type
	 */
	public void setGridType(GridType gridType) {
		this.gridType = gridType;
	}

	/**
	 * Validate units are the same
	 */
	private void validateUnits() {
		if (!point1.isUnit(point2.getUnit())) {
			throw new IllegalArgumentException(
					"Points are in different units. point1: " + point1.getUnit()
							+ ", point2: " + point2.getUnit());
		}
	}

}
