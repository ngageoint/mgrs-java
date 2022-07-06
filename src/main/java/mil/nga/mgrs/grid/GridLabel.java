package mil.nga.mgrs.grid;

import mil.nga.grid.Label;
import mil.nga.grid.features.Bounds;
import mil.nga.grid.features.Point;
import mil.nga.mgrs.MGRS;

/**
 * MGRS Grid Label
 * 
 * @author wnewman
 * @author osbornb
 */
public class GridLabel extends Label {

	/**
	 * Grid type
	 */
	private GridType gridType;

	/**
	 * MGRS coordinate
	 */
	private MGRS coordinate;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            name
	 * @param center
	 *            center point
	 * @param bounds
	 *            bounds
	 * @param gridType
	 *            grid type
	 * @param coordinate
	 *            MGRS coordinate
	 */
	public GridLabel(String name, Point center, Bounds bounds,
			GridType gridType, MGRS coordinate) {
		super(name, center, bounds);
		this.gridType = gridType;
		this.coordinate = coordinate;
	}

	/**
	 * Get the grid type
	 * 
	 * @return grid type
	 */
	public GridType getGridType() {
		return gridType;
	}

	/**
	 * Set the grid type
	 * 
	 * @param gridType
	 *            grid type
	 */
	public void setGridType(GridType gridType) {
		this.gridType = gridType;
	}

	/**
	 * Get the MGRS coordinate
	 * 
	 * @return MGRS coordinate
	 */
	public MGRS getCoordinate() {
		return coordinate;
	}

	/**
	 * Set the MGRS coordinate
	 * 
	 * @param coordinate
	 *            MGRS coordinate
	 */
	public void setCoordinate(MGRS coordinate) {
		this.coordinate = coordinate;
	}

}
