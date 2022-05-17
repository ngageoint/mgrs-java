package mil.nga.mgrs.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.nga.mgrs.MGRSConstants;

/**
 * Grid enumeration
 * 
 * @author wnewman
 * @author osbornb
 */
public enum Grid {

	/**
	 * Ten Meter
	 */
	TEN_METER(10, 18, Integer.MAX_VALUE),

	/**
	 * Hundred Meter
	 */
	HUNDRED_METER(100, 15, 17),

	/**
	 * Kilometer
	 */
	KILOMETER(1000, 12, 14),

	/**
	 * Ten Kilometer
	 */
	TEN_KILOMETER(10000, 9, 11),

	/**
	 * Hundred Kilometer
	 */
	HUNDRED_KILOMETER(100000, 5, Integer.MAX_VALUE),

	/**
	 * Grid Zone Designator
	 */
	GZD(0, 0, Integer.MAX_VALUE);

	/**
	 * Grid precision in meters
	 */
	public int precision;

	/**
	 * Minimum zoom level
	 */
	private int minZoom;

	/**
	 * Maximum zoom level
	 */
	private int maxZoom;

	/**
	 * Map between zoom levels and grids
	 */
	private static Map<Integer, Grids> grids = new HashMap<>();

	static {
		// Create zoom level grids
		for (int zoom = 0; zoom <= MGRSConstants.MAX_MAP_ZOOM_LEVEL; zoom++) {
			createGrids(zoom);
		}
	}

	/**
	 * Constructor
	 * 
	 * @param precision
	 *            precision in meters
	 * @param minZoom
	 *            minimum zoom level
	 * @param maxZoom
	 *            maximum zoom level
	 */
	private Grid(int precision, int minZoom, int maxZoom) {
		this.precision = precision;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
	}

	/**
	 * Get the precision in meters
	 * 
	 * @return precision meters
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * Get the minimum zoom level
	 * 
	 * @return minimum zoom level
	 */
	public int getMinZoom() {
		return minZoom;
	}

	/**
	 * Get the maximum zoom level
	 * 
	 * @return maximum zoom level
	 */
	public int getMaxZoom() {
		return maxZoom;
	}

	/**
	 * Is the zoom level within the grid zoom range
	 * 
	 * @param zoom
	 *            zoom level
	 * @return true if within range
	 */
	public boolean isWithin(int zoom) {
		return zoom >= minZoom && zoom <= maxZoom;
	}

	/**
	 * Get the grids for the zoom level
	 * 
	 * @param zoom
	 *            zoom level
	 * @return grids
	 */
	public static Grids getGrids(int zoom) {
		Grids zoomGrids = grids.get(zoom);
		if (zoomGrids == null) {
			zoomGrids = createGrids(zoom);
		}
		return zoomGrids;
	}

	/**
	 * Create grids for the zoom level
	 * 
	 * @param zoom
	 *            zoom level
	 * @return grids
	 */
	private static Grids createGrids(int zoom) {
		List<Grid> gridList = new ArrayList<>();
		for (Grid grid : Grid.values()) {
			if (grid.isWithin(zoom)) {
				gridList.add(grid);
			}
		}
		Grids zoomGrids = new Grids(zoom, gridList);
		grids.put(zoom, zoomGrids);
		return zoomGrids;
	}

}
