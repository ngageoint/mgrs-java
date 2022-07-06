package mil.nga.mgrs.grid;

import mil.nga.grid.BaseZoomGrids;

/**
 * Zoom Level Matching Grids
 * 
 * @author osbornb
 */
public class ZoomGrids extends BaseZoomGrids<Grid> {

	/**
	 * Constructor
	 * 
	 * @param zoom
	 *            zoom level
	 */
	public ZoomGrids(int zoom) {
		super(zoom);
	}

	/**
	 * Get the grid type precision
	 * 
	 * @return grid type precision
	 */
	public GridType getPrecision() {
		GridType type = null;
		if (hasGrids()) {
			type = grids.first().getType();
		}
		return type;
	}

}
