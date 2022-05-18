package mil.nga.mgrs.grid;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import mil.nga.mgrs.MGRSConstants;
import mil.nga.mgrs.gzd.GZDLabeler;

/**
 * Grids
 * 
 * @author osbornb
 */
public class Grids {

	/**
	 * Grids
	 */
	private Map<GridType, Grid> grids = new HashMap<>();

	/**
	 * Map between zoom levels and grids
	 */
	private TreeMap<Integer, ZoomGrids> zoomGrids = new TreeMap<>();

	/**
	 * Create with all grid types enabled
	 * 
	 * @return grids
	 */
	public static Grids create() {
		return new Grids();
	}

	/**
	 * Create with grids to enable
	 * 
	 * @param types
	 *            grid types to enable
	 * @return grids
	 */
	public static Grids create(GridType... types) {
		return new Grids(types);
	}

	/**
	 * Create with grids to enable
	 * 
	 * @param types
	 *            grid types to enable
	 * @return grids
	 */
	public static Grids create(Collection<GridType> types) {
		return new Grids(types);
	}

	/**
	 * Create only Grid Zone Designator grids
	 * 
	 * @return grids
	 */
	public static Grids createGZD() {
		return create(GridType.GZD);
	}

	/**
	 * Constructor, all grid types enabled
	 */
	public Grids() {
		createGrids(true);
		createZoomGrids();
	}

	/**
	 * Constructor
	 * 
	 * @param types
	 *            grid types to enable
	 */
	public Grids(GridType... types) {
		this(Arrays.asList(types));
	}

	/**
	 * Constructor
	 * 
	 * @param types
	 *            grid types to enable
	 */
	public Grids(Collection<GridType> types) {

		createGrids(false);

		for (GridType type : types) {
			getGrid(type).setEnabled(true);
		}

		createZoomGrids();
	}

	/**
	 * Create the grids
	 * 
	 * @param enabled
	 *            enable created grids
	 */
	private void createGrids(boolean enabled) {
		createGrid(GridType.GZD, enabled, 0, new GZDLabeler(4));
		createGrid(GridType.HUNDRED_KILOMETER, enabled, 5,
				new ColumnRowLabeler(6));
		createGrid(GridType.TEN_KILOMETER, enabled, 9, 11);
		createGrid(GridType.KILOMETER, enabled, 12, 14);
		createGrid(GridType.HUNDRED_METER, enabled, 15, 17);
		createGrid(GridType.TEN_METER, enabled, 18);
	}

	/**
	 * Create a grid
	 * 
	 * @param type
	 *            grid type
	 * @param enabled
	 *            flag
	 * @param minZoom
	 *            minimum zoom
	 */
	private void createGrid(GridType type, boolean enabled, int minZoom) {
		createGrid(type, enabled, minZoom, null, null);
	}

	/**
	 * Create a grid
	 * 
	 * @param type
	 *            grid type
	 * @param enabled
	 *            flag
	 * @param minZoom
	 *            minimum zoom
	 * @param labeler
	 *            grid labeler
	 */
	private void createGrid(GridType type, boolean enabled, int minZoom,
			Labeler labeler) {
		createGrid(type, enabled, minZoom, null, labeler);
	}

	/**
	 * Create a grid
	 * 
	 * @param type
	 *            grid type
	 * @param enabled
	 *            flag
	 * @param minZoom
	 *            minimum zoom
	 * @param maxZoom
	 *            maximum zoom
	 */
	private void createGrid(GridType type, boolean enabled, int minZoom,
			Integer maxZoom) {
		createGrid(type, enabled, minZoom, maxZoom, null);
	}

	/**
	 * Create a grid
	 * 
	 * @param type
	 *            grid type
	 * @param enabled
	 *            flag
	 * @param minZoom
	 *            minimum zoom
	 * @param maxZoom
	 *            maximum zoom
	 * @param labeler
	 *            grid labeler
	 */
	private void createGrid(GridType type, boolean enabled, int minZoom,
			Integer maxZoom, Labeler labeler) {
		grids.put(type, new Grid(type, enabled, minZoom, maxZoom, labeler));
	}

	/**
	 * Create the zoom level grids
	 */
	private void createZoomGrids() {
		for (int zoom = 0; zoom <= MGRSConstants.MAX_MAP_ZOOM_LEVEL; zoom++) {
			createZoomGrids(zoom);
		}
	}

	/**
	 * Get the grid
	 * 
	 * @param type
	 *            grid type
	 * @return grid
	 */
	public Grid getGrid(GridType type) {
		return grids.get(type);
	}

	/**
	 * Get the grids for the zoom level
	 * 
	 * @param zoom
	 *            zoom level
	 * @return grids
	 */
	public ZoomGrids getGrids(int zoom) {
		ZoomGrids grids = zoomGrids.get(zoom);
		if (grids == null) {
			grids = createZoomGrids(zoom);
		}
		return grids;
	}

	/**
	 * Create grids for the zoom level
	 * 
	 * @param zoom
	 *            zoom level
	 * @return grids
	 */
	private ZoomGrids createZoomGrids(int zoom) {
		ZoomGrids zoomLevelGrids = new ZoomGrids(zoom);
		for (Grid grid : grids.values()) {
			if (grid.isEnabled() && grid.isWithin(zoom)) {
				zoomLevelGrids.addGrid(grid);
			}
		}
		zoomGrids.put(zoom, zoomLevelGrids);
		return zoomLevelGrids;
	}

	/**
	 * Set the active grid types
	 * 
	 * @param types
	 *            grid types
	 */
	public void setGrids(GridType... types) {
		setGridTypes(Arrays.asList(types));
	}

	/**
	 * Set the active grids
	 * 
	 * @param grids
	 *            grids
	 */
	public void setGrids(Grid... grids) {
		setGrids(Arrays.asList(grids));
	}

	/**
	 * Set the active grid types
	 * 
	 * @param types
	 *            grid types
	 */
	public void setGridTypes(Collection<GridType> types) {
		Set<GridType> disableTypes = new HashSet<>(
				Arrays.asList(GridType.values()));
		for (GridType gridType : types) {
			enable(gridType);
			disableTypes.remove(gridType);
		}
		disableTypes(disableTypes);
	}

	/**
	 * Set the active grids
	 * 
	 * @param grids
	 *            grids
	 */
	public void setGrids(Collection<Grid> grids) {
		Set<GridType> disableTypes = new HashSet<>(
				Arrays.asList(GridType.values()));
		for (Grid grid : grids) {
			enable(grid);
			disableTypes.remove(grid.getType());
		}
		disableTypes(disableTypes);
	}

	/**
	 * Enable grid types
	 * 
	 * @param types
	 *            grid types
	 */
	public void enableTypes(GridType... types) {
		enableTypes(Arrays.asList(types));
	}

	/**
	 * Enable grids
	 * 
	 * @param grids
	 *            grids
	 */
	public void enableGrids(Grid... grids) {
		enableGrids(Arrays.asList(grids));
	}

	/**
	 * Enable grid types
	 * 
	 * @param types
	 *            grid types
	 */
	public void enableTypes(Collection<GridType> types) {
		for (GridType type : types) {
			enable(type);
		}
	}

	/**
	 * Enable grids
	 * 
	 * @param grids
	 *            grids
	 */
	public void enableGrids(Collection<Grid> grids) {
		for (Grid grid : grids) {
			enable(grid);
		}
	}

	/**
	 * Disable grid types
	 * 
	 * @param types
	 *            grid types
	 */
	public void disableTypes(GridType... types) {
		disableTypes(Arrays.asList(types));
	}

	/**
	 * Disable grids
	 * 
	 * @param grids
	 *            grids
	 */
	public void disableGrids(Grid... grids) {
		disableGrids(Arrays.asList(grids));
	}

	/**
	 * Disable grid types
	 * 
	 * @param types
	 *            grid types
	 */
	public void disableTypes(Collection<GridType> types) {
		for (GridType type : types) {
			disable(type);
		}
	}

	/**
	 * Disable grids
	 * 
	 * @param grids
	 *            grids
	 */
	public void disableGrids(Collection<Grid> grids) {
		for (Grid grid : grids) {
			disable(grid);
		}
	}

	/**
	 * Enable the grid type
	 * 
	 * @param type
	 *            grid type
	 */
	public void enable(GridType type) {
		enable(getGrid(type));
	}

	/**
	 * Enable the grid
	 * 
	 * @param grid
	 *            grid
	 */
	public void enable(Grid grid) {

		if (!grid.isEnabled()) {

			grid.setEnabled(true);

			int minZoom = grid.getMinZoom();
			Integer maxZoom = grid.getMaxZoom();
			if (maxZoom == null) {
				maxZoom = zoomGrids.lastKey();
			}

			for (int zoom = minZoom; zoom <= maxZoom; zoom++) {
				addGrid(grid, zoom);
			}

		}

	}

	/**
	 * Disable the grid type
	 * 
	 * @param type
	 *            grid type
	 */
	public void disable(GridType type) {
		disable(getGrid(type));
	}

	/**
	 * Disable the grid
	 * 
	 * @param grid
	 *            grid
	 */
	public void disable(Grid grid) {

		if (grid.isEnabled()) {

			grid.setEnabled(false);

			int minZoom = grid.getMinZoom();
			Integer maxZoom = grid.getMaxZoom();
			if (maxZoom == null) {
				maxZoom = zoomGrids.lastKey();
			}

			for (int zoom = minZoom; zoom <= maxZoom; zoom++) {
				removeGrid(grid, zoom);
			}

		}

	}

	/**
	 * Set the grid minimum zoom
	 * 
	 * @param type
	 *            grid type
	 * @param minZoom
	 *            minimum zoom
	 */
	public void setMinZoom(GridType type, int minZoom) {
		setMinZoom(getGrid(type), minZoom);
	}

	/**
	 * Set the grid minimum zoom
	 * 
	 * @param grid
	 *            grid
	 * @param minZoom
	 *            minimum zoom
	 */
	public void setMinZoom(Grid grid, int minZoom) {
		Integer maxZoom = grid.getMaxZoom();
		if (maxZoom != null && maxZoom < minZoom) {
			maxZoom = minZoom;
		}
		setZoomRange(grid, minZoom, maxZoom);
	}

	/**
	 * Set the grid maximum zoom
	 * 
	 * @param type
	 *            grid type
	 * @param maxZoom
	 *            maximum zoom
	 */
	public void setMaxZoom(GridType type, Integer maxZoom) {
		setMaxZoom(getGrid(type), maxZoom);
	}

	/**
	 * Set the grid maximum zoom
	 * 
	 * @param grid
	 *            grid
	 * @param maxZoom
	 *            maximum zoom
	 */
	public void setMaxZoom(Grid grid, Integer maxZoom) {
		int minZoom = grid.getMinZoom();
		if (maxZoom != null && minZoom > maxZoom) {
			minZoom = maxZoom;
		}
		setZoomRange(grid, minZoom, maxZoom);
	}

	/**
	 * Set the grid zoom range
	 * 
	 * @param type
	 *            grid type
	 * @param minZoom
	 *            minimum zoom
	 * @param maxZoom
	 *            maximum zoom
	 */
	public void setZoomRange(GridType type, int minZoom, Integer maxZoom) {
		setZoomRange(getGrid(type), minZoom, maxZoom);
	}

	/**
	 * Set the grid zoom range
	 * 
	 * @param grid
	 *            grid
	 * @param minZoom
	 *            minimum zoom
	 * @param maxZoom
	 *            maximum zoom
	 */
	public void setZoomRange(Grid grid, int minZoom, Integer maxZoom) {

		// All grids zoom range
		final int allGridsMin = zoomGrids.firstKey();
		final int allGridsMax = zoomGrids.lastKey();

		// Existing grid zoom range
		int gridMinZoom = grid.getMinZoom();
		Integer gridMaxZoom = grid.getMaxZoom();
		if (gridMaxZoom == null) {
			gridMaxZoom = allGridsMax;
		} else {
			gridMaxZoom = Math.min(gridMaxZoom, allGridsMax);
		}

		grid.setMinZoom(minZoom);
		grid.setMaxZoom(maxZoom);

		minZoom = Math.max(minZoom, allGridsMin);
		if (maxZoom == null) {
			maxZoom = allGridsMax;
		} else {
			maxZoom = Math.min(maxZoom, allGridsMax);
		}

		int minOverlap = Math.max(minZoom, gridMinZoom);
		int maxOverlap = Math.min(maxZoom, gridMaxZoom);

		boolean overlaps = minOverlap <= maxOverlap;

		if (overlaps) {

			int min = Math.min(minZoom, gridMinZoom);
			int max = Math.max(maxZoom, gridMaxZoom);

			for (int zoom = min; zoom <= max; zoom++) {

				if (zoom < minOverlap || zoom > maxOverlap) {

					if (zoom >= minZoom && zoom <= maxZoom) {
						addGrid(grid, zoom);
					} else {
						removeGrid(grid, zoom);
					}

				}

			}
		} else {

			for (int zoom = gridMinZoom; zoom <= gridMaxZoom; zoom++) {
				removeGrid(grid, zoom);
			}

			for (int zoom = minZoom; zoom <= maxZoom; zoom++) {
				addGrid(grid, zoom);
			}

		}

	}

	/**
	 * Add a grid to the zoom level
	 * 
	 * @param grid
	 *            grid
	 * @param zoom
	 *            zoom level
	 */
	private void addGrid(Grid grid, int zoom) {
		ZoomGrids grids = zoomGrids.get(zoom);
		if (grids != null) {
			grids.addGrid(grid);
		}
	}

	/**
	 * Remove a grid from the zoom level
	 * 
	 * @param grid
	 *            grid
	 * @param zoom
	 *            zoom level
	 */
	private void removeGrid(Grid grid, int zoom) {
		ZoomGrids grids = zoomGrids.get(zoom);
		if (grids != null) {
			grids.removeGrid(grid);
		}
	}

}
