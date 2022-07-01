package mil.nga.mgrs.features;

import mil.nga.grid.features.Line;
import mil.nga.grid.features.Point;
import mil.nga.mgrs.grid.GridType;

/**
 * Line between two points
 * 
 * @author wnewman
 * @author osbornb
 */
public class GridLine extends Line {

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
	public static GridLine line(Point point1, Point point2) {
		return new GridLine(point1, point2);
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
	public static GridLine line(Point point1, Point point2, GridType gridType) {
		return new GridLine(point1, point2, gridType);
	}

	/**
	 * Create a line
	 * 
	 * @param line
	 *            line to copy
	 * @return line
	 */
	public static GridLine line(Line line) {
		return new GridLine(line);
	}

	/**
	 * Create a line
	 * 
	 * @param line
	 *            line to copy
	 * @param gridType
	 *            line grid type
	 * @return line
	 */
	public static GridLine line(Line line, GridType gridType) {
		return new GridLine(line, gridType);
	}

	/**
	 * Copy a line
	 * 
	 * @param line
	 *            line to copy
	 * @return line
	 */
	public static GridLine line(GridLine line) {
		return new GridLine(line);
	}

	/**
	 * Constructor
	 * 
	 * @param point1
	 *            first point
	 * @param point2
	 *            second point
	 */
	public GridLine(Point point1, Point point2) {
		super(point1, point2);
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
	public GridLine(Point point1, Point point2, GridType gridType) {
		this(point1, point2);
		this.gridType = gridType;
	}

	/**
	 * Constructor
	 * 
	 * @param line
	 *            line to copy
	 */
	public GridLine(Line line) {
		super(line);
	}

	/**
	 * Constructor
	 * 
	 * @param line
	 *            line to copy
	 * @param gridType
	 *            line grid type
	 */
	public GridLine(Line line, GridType gridType) {
		this(line);
		this.gridType = gridType;
	}

	/**
	 * Copy Constructor
	 * 
	 * @param line
	 *            line to copy
	 */
	public GridLine(GridLine line) {
		this(line, line.getGridType());
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
	 * Copy the line
	 * 
	 * @return line copy
	 */
	public GridLine copy() {
		return new GridLine(this);
	}

}
