package mil.nga.mgrs.grid;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import mil.nga.grid.GridStyle;
import mil.nga.grid.color.Color;
import mil.nga.grid.property.PropertyConstants;
import mil.nga.mgrs.MGRSConstants;
import mil.nga.mgrs.gzd.GZDLabeler;
import mil.nga.mgrs.property.MGRSProperties;

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
	 * Constructor, all grid types enabled per property configurations
	 */
	public Grids() {
		createGrids();
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
	 */
	private void createGrids() {
		createGrids(null);
	}

	/**
	 * Create the grids
	 * 
	 * @param enabled
	 *            enable created grids
	 */
	private void createGrids(Boolean enabled) {

		Boolean propagate = MGRSProperties.getInstance().getBooleanProperty(
				false, PropertyConstants.GRIDS, PropertyConstants.PROPAGATE);
		Map<GridType, GridStyle> styles = null;
		if (propagate != null && propagate) {
			styles = new HashMap<>();
		}

		createGrid(GridType.GZD, styles, enabled, new GZDLabeler());
		createGrid(GridType.HUNDRED_KILOMETER, styles, enabled,
				new MGRSLabeler());
		createGrid(GridType.TEN_KILOMETER, styles, enabled, new MGRSLabeler());
		createGrid(GridType.KILOMETER, styles, enabled, new MGRSLabeler());
		createGrid(GridType.HUNDRED_METER, styles, enabled, new MGRSLabeler());
		createGrid(GridType.TEN_METER, styles, enabled, new MGRSLabeler());
		createGrid(GridType.METER, styles, enabled, new MGRSLabeler());

	}

	/**
	 * Create the grid
	 * 
	 * @param type
	 *            grid type
	 * @param styles
	 *            propagate grid styles
	 * @param enabled
	 *            enable created grids
	 * @param labeler
	 *            grid labeler
	 */
	private void createGrid(GridType type, Map<GridType, GridStyle> styles,
			Boolean enabled, GridLabeler labeler) {

		Grid grid = newGrid(type);

		String gridKey = type.name().toLowerCase();

		if (enabled == null) {
			enabled = MGRSProperties.getInstance().getBooleanProperty(false,
					PropertyConstants.GRIDS, gridKey,
					PropertyConstants.ENABLED);
			if (enabled == null) {
				enabled = true;
			}
		}
		grid.setEnabled(enabled);

		Integer minZoom = MGRSProperties.getInstance().getIntegerProperty(false,
				PropertyConstants.GRIDS, gridKey, PropertyConstants.MIN_ZOOM);
		if (minZoom == null) {
			minZoom = 0;
		}
		grid.setMinZoom(minZoom);

		Integer maxZoom = MGRSProperties.getInstance().getIntegerProperty(false,
				PropertyConstants.GRIDS, gridKey, PropertyConstants.MAX_ZOOM);
		grid.setMaxZoom(maxZoom);

		Integer linesMinZoom = MGRSProperties.getInstance().getIntegerProperty(
				false, PropertyConstants.GRIDS, gridKey,
				PropertyConstants.LINES, PropertyConstants.MIN_ZOOM);
		grid.setLinesMinZoom(linesMinZoom);

		Integer linesMaxZoom = MGRSProperties.getInstance().getIntegerProperty(
				false, PropertyConstants.GRIDS, gridKey,
				PropertyConstants.LINES, PropertyConstants.MAX_ZOOM);
		grid.setLinesMaxZoom(linesMaxZoom);

		String colorProperty = MGRSProperties.getInstance().getProperty(false,
				PropertyConstants.GRIDS, gridKey, PropertyConstants.COLOR);
		Color color = colorProperty != null ? Color.color(colorProperty)
				: Color.black();
		grid.setColor(color);

		Double width = MGRSProperties.getInstance().getDoubleProperty(false,
				PropertyConstants.GRIDS, gridKey, PropertyConstants.WIDTH);
		if (width == null) {
			width = Grid.DEFAULT_WIDTH;
		}
		grid.setWidth(width);

		if (styles != null) {
			styles.put(type, GridStyle.style(color, width));
		}

		loadGridStyles(grid, styles, gridKey);

		if (labeler != null) {
			loadLabeler(labeler, gridKey);
		}
		grid.setLabeler(labeler);

		grids.put(type, grid);
	}

	/**
	 * Load grid styles within a higher precision grid
	 * 
	 * @param grid
	 *            grid
	 * @param styles
	 *            propagate grid styles
	 * @param gridKey
	 *            grid key
	 */
	private void loadGridStyles(Grid grid, Map<GridType, GridStyle> styles,
			String gridKey) {
		int precision = grid.getPrecision();
		if (precision < GridType.HUNDRED_KILOMETER.getPrecision()) {
			loadGridStyle(grid, styles, gridKey, GridType.HUNDRED_KILOMETER);
		}
		if (precision < GridType.TEN_KILOMETER.getPrecision()) {
			loadGridStyle(grid, styles, gridKey, GridType.TEN_KILOMETER);
		}
		if (precision < GridType.KILOMETER.getPrecision()) {
			loadGridStyle(grid, styles, gridKey, GridType.KILOMETER);
		}
		if (precision < GridType.HUNDRED_METER.getPrecision()) {
			loadGridStyle(grid, styles, gridKey, GridType.HUNDRED_METER);
		}
		if (precision < GridType.TEN_METER.getPrecision()) {
			loadGridStyle(grid, styles, gridKey, GridType.TEN_METER);
		}
	}

	/**
	 * Load a grid style within a higher precision grid
	 * 
	 * @param grid
	 *            grid
	 * @param styles
	 *            propagate grid styles
	 * @param gridKey
	 *            grid key
	 * @param gridType
	 *            style grid type
	 */
	private void loadGridStyle(Grid grid, Map<GridType, GridStyle> styles,
			String gridKey, GridType gridType) {

		String gridKey2 = gridType.name().toLowerCase();

		String colorProperty = MGRSProperties.getInstance().getProperty(false,
				PropertyConstants.GRIDS, gridKey, gridKey2,
				PropertyConstants.COLOR);
		Color color = null;
		if (colorProperty != null) {
			color = Color.color(colorProperty);
		}

		Double width = MGRSProperties.getInstance().getDoubleProperty(false,
				PropertyConstants.GRIDS, gridKey, gridKey2,
				PropertyConstants.WIDTH);

		if ((color == null || width == null) && styles != null) {
			GridStyle style = styles.get(gridType);
			if (style != null) {
				if (color == null) {
					Color styleColor = style.getColor();
					if (styleColor != null) {
						color = styleColor.copy();
					}
				}
				if (width == null) {
					width = style.getWidth();
				}
			}
		}

		if (color != null || width != null) {

			if (color == null) {
				color = grid.getColor();
			}

			if (width == null || width == 0) {
				width = grid.getWidth();
			}

			GridStyle style = GridStyle.style(color, width);
			grid.setStyle(gridType, style);

			if (styles != null) {
				styles.put(gridType, style);
			}
		}

	}

	/**
	 * Load the labeler from properties for the grid type
	 * 
	 * @param labeler
	 *            labeler
	 * @param gridKey
	 *            grid property key
	 */
	private void loadLabeler(GridLabeler labeler, String gridKey) {

		Boolean enabled = MGRSProperties.getInstance().getBooleanProperty(false,
				PropertyConstants.GRIDS, gridKey, PropertyConstants.LABELER,
				PropertyConstants.ENABLED);
		labeler.setEnabled(enabled != null && enabled);

		Integer minZoom = MGRSProperties.getInstance().getIntegerProperty(false,
				PropertyConstants.GRIDS, gridKey, PropertyConstants.LABELER,
				PropertyConstants.MIN_ZOOM);
		if (minZoom != null) {
			labeler.setMinZoom(minZoom);
		}

		Integer maxZoom = MGRSProperties.getInstance().getIntegerProperty(false,
				PropertyConstants.GRIDS, gridKey, PropertyConstants.LABELER,
				PropertyConstants.MAX_ZOOM);
		if (maxZoom != null) {
			labeler.setMaxZoom(maxZoom);
		}

		String color = MGRSProperties.getInstance().getProperty(false,
				PropertyConstants.GRIDS, gridKey, PropertyConstants.LABELER,
				PropertyConstants.COLOR);
		if (color != null) {
			labeler.setColor(Color.color(color));
		}

		Double textSize = MGRSProperties.getInstance().getDoubleProperty(false,
				PropertyConstants.GRIDS, gridKey, PropertyConstants.LABELER,
				PropertyConstants.TEXT_SIZE);
		if (textSize != null) {
			labeler.setTextSize(textSize);
		}

		Double buffer = MGRSProperties.getInstance().getDoubleProperty(false,
				PropertyConstants.GRIDS, gridKey, PropertyConstants.LABELER,
				PropertyConstants.BUFFER);
		if (buffer != null) {
			labeler.setBuffer(buffer);
		}

	}

	/**
	 * Create a new grid, override to create a specialized grid
	 * 
	 * @param type
	 *            grid type
	 * @return grid
	 */
	protected Grid newGrid(GridType type) {
		return new Grid(type);
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
		ZoomGrids zoomLevelGrids = newZoomGrids(zoom);
		for (Grid grid : grids.values()) {
			if (grid.isEnabled() && grid.isWithin(zoom)) {
				zoomLevelGrids.addGrid(grid);
			}
		}
		zoomGrids.put(zoom, zoomLevelGrids);
		return zoomLevelGrids;
	}

	/**
	 * Create a new zoom grids, override for specialized grids
	 * 
	 * @param zoom
	 *            zoom level
	 * @return zoom grids
	 */
	protected ZoomGrids newZoomGrids(int zoom) {
		return new ZoomGrids(zoom);
	}

	/**
	 * Get the grid precision for the zoom level
	 * 
	 * @param zoom
	 *            zoom level
	 * @return grid type precision
	 */
	public GridType getPrecision(int zoom) {
		return getGrids(zoom).getPrecision();
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
	 * Is the grid type enabled
	 * 
	 * @param type
	 *            grid type
	 * @return true if enabled
	 */
	public boolean isEnabled(GridType type) {
		return getGrid(type).isEnabled();
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

		if (maxZoom != null && maxZoom < minZoom) {
			throw new IllegalArgumentException("Min zoom '" + minZoom
					+ "' can not be larger than max zoom '" + maxZoom + "'");
		}

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

	/**
	 * Set the grid minimum level override for drawing grid lines
	 * 
	 * @param type
	 *            grid type
	 * @param minZoom
	 *            minimum zoom level or null to remove
	 */
	public void setLinesMinZoom(GridType type, Integer minZoom) {
		getGrid(type).setLinesMinZoom(minZoom);
	}

	/**
	 * Set the grid maximum level override for drawing grid lines
	 * 
	 * @param type
	 *            grid type
	 * @param maxZoom
	 *            maximum zoom level or null to remove
	 */
	public void setLinesMaxZoom(GridType type, Integer maxZoom) {
		getGrid(type).setLinesMaxZoom(maxZoom);
	}

	/**
	 * Set all grid line colors
	 * 
	 * @param color
	 *            grid line color
	 */
	public void setAllColors(Color color) {
		setColor(color, GridType.values());
	}

	/**
	 * Set the grid line color for the grid types
	 * 
	 * @param color
	 *            grid line color
	 * @param types
	 *            grid types
	 */
	public void setColor(Color color, GridType... types) {
		setColor(Arrays.asList(types), color);
	}

	/**
	 * Set the grid line color for the grid types
	 * 
	 * @param types
	 *            grid types
	 * @param color
	 *            grid line color
	 */
	public void setColor(Collection<GridType> types, Color color) {
		for (GridType type : types) {
			setColor(type, color);
		}
	}

	/**
	 * Set the grid line color for the grid type
	 * 
	 * @param type
	 *            grid type
	 * @param color
	 *            grid line color
	 */
	public void setColor(GridType type, Color color) {
		getGrid(type).setColor(color);
	}

	/**
	 * Set all grid line widths
	 * 
	 * @param width
	 *            grid line width
	 */
	public void setAllWidths(double width) {
		setWidth(width, GridType.values());
	}

	/**
	 * Set the grid line width for the grid types
	 * 
	 * @param width
	 *            grid line width
	 * @param types
	 *            grid types
	 */
	public void setWidth(double width, GridType... types) {
		setWidth(Arrays.asList(types), width);
	}

	/**
	 * Set the grid line width for the grid types
	 * 
	 * @param types
	 *            grid types
	 * @param width
	 *            grid line width
	 */
	public void setWidth(Collection<GridType> types, double width) {
		for (GridType type : types) {
			setWidth(type, width);
		}
	}

	/**
	 * Set the grid line width for the grid type
	 * 
	 * @param type
	 *            grid type
	 * @param width
	 *            grid line width
	 */
	public void setWidth(GridType type, double width) {
		getGrid(type).setWidth(width);
	}

	/**
	 * Delete propagated styles
	 */
	public void deletePropagatedStyles() {
		deletePropagatedStyles(GridType.values());
	}

	/**
	 * Delete propagated styles for the grid types
	 * 
	 * @param types
	 *            grid types
	 */
	public void deletePropagatedStyles(GridType... types) {
		deletePropagatedStyles(Arrays.asList(types));
	}

	/**
	 * Delete propagated styles for the grid types
	 * 
	 * @param types
	 *            grid types
	 */
	public void deletePropagatedStyles(Collection<GridType> types) {
		for (GridType type : types) {
			deletePropagatedStyles(type);
		}
	}

	/**
	 * Delete propagated styles for the grid type
	 * 
	 * @param type
	 *            grid type
	 */
	public void deletePropagatedStyles(GridType type) {
		getGrid(type).clearPrecisionStyles();
	}

	/**
	 * Set the grid type precision line color for the grid types
	 * 
	 * @param types
	 *            grid types
	 * @param precisionType
	 *            precision grid type
	 * @param color
	 *            grid line color
	 */
	public void setColor(Collection<GridType> types, GridType precisionType,
			Color color) {
		for (GridType type : types) {
			setColor(type, precisionType, color);
		}
	}

	/**
	 * Set the grid type precision line colors for the grid type
	 * 
	 * @param type
	 *            grid type
	 * @param color
	 *            grid line color
	 * @param precisionTypes
	 *            precision grid types
	 */
	public void setColor(GridType type, Color color,
			GridType... precisionTypes) {
		setColor(type, Arrays.asList(precisionTypes), color);
	}

	/**
	 * Set the grid type precision line colors for the grid type
	 * 
	 * @param type
	 *            grid type
	 * @param precisionTypes
	 *            precision grid types
	 * @param color
	 *            grid line color
	 */
	public void setColor(GridType type, Collection<GridType> precisionTypes,
			Color color) {
		for (GridType precisionType : precisionTypes) {
			setColor(type, precisionType, color);
		}
	}

	/**
	 * Set the grid type precision line color for the grid type
	 * 
	 * @param type
	 *            grid type
	 * @param precisionType
	 *            precision grid type
	 * @param color
	 *            grid line color
	 */
	public void setColor(GridType type, GridType precisionType, Color color) {
		getGrid(type).setColor(precisionType, color);
	}

	/**
	 * Set the grid type precision line width for the grid types
	 * 
	 * @param types
	 *            grid types
	 * @param precisionType
	 *            precision grid type
	 * @param width
	 *            grid line width
	 */
	public void setWidth(Collection<GridType> types, GridType precisionType,
			double width) {
		for (GridType type : types) {
			setWidth(type, precisionType, width);
		}
	}

	/**
	 * Set the grid type precision line widths for the grid type
	 * 
	 * @param type
	 *            grid type
	 * @param width
	 *            grid line width
	 * @param precisionTypes
	 *            precision grid types
	 */
	public void setWidth(GridType type, double width,
			GridType... precisionTypes) {
		setWidth(type, Arrays.asList(precisionTypes), width);
	}

	/**
	 * Set the grid type precision line widths for the grid type
	 * 
	 * @param type
	 *            grid type
	 * @param precisionTypes
	 *            precision grid types
	 * @param width
	 *            grid line width
	 */
	public void setWidth(GridType type, Collection<GridType> precisionTypes,
			double width) {
		for (GridType precisionType : precisionTypes) {
			setWidth(type, precisionType, width);
		}
	}

	/**
	 * Set the grid type precision line width for the grid type
	 * 
	 * @param type
	 *            grid type
	 * @param precisionType
	 *            precision grid type
	 * @param width
	 *            grid line width
	 */
	public void setWidth(GridType type, GridType precisionType, double width) {
		getGrid(type).setWidth(precisionType, width);
	}

	/**
	 * Get the labeler for the grid type
	 * 
	 * @param type
	 *            grid type
	 * @return labeler or null
	 */
	public GridLabeler getLabeler(GridType type) {
		return getGrid(type).getLabeler();
	}

	/**
	 * Has a labeler for the grid type
	 * 
	 * @param type
	 *            grid type
	 * @return true if has labeler
	 */
	public boolean hasLabeler(GridType type) {
		return getGrid(type).hasLabeler();
	}

	/**
	 * Set the labeler for the grid type
	 * 
	 * @param type
	 *            grid type
	 * @param labeler
	 *            labeler
	 */
	public void setLabeler(GridType type, GridLabeler labeler) {
		getGrid(type).setLabeler(labeler);
	}

	/**
	 * Enable all grid labelers
	 */
	public void enableAllLabelers() {
		for (Grid grid : grids.values()) {
			GridLabeler labeler = grid.getLabeler();
			if (labeler != null) {
				labeler.setEnabled(true);
			}
		}
	}

	/**
	 * Disable all grid labelers
	 */
	public void disableAllLabelers() {
		disableLabelers(GridType.values());
	}

	/**
	 * Enable the labelers for the grid types
	 * 
	 * @param types
	 *            grid types
	 */
	public void enableLabelers(GridType... types) {
		enableLabelers(Arrays.asList(types));
	}

	/**
	 * Enable the labelers for the grid types
	 * 
	 * @param types
	 *            grid types
	 */
	public void enableLabelers(Collection<GridType> types) {
		for (GridType type : types) {
			enableLabeler(type);
		}
	}

	/**
	 * Disable the labelers for the grid types
	 * 
	 * @param types
	 *            grid types
	 */
	public void disableLabelers(GridType... types) {
		disableLabelers(Arrays.asList(types));
	}

	/**
	 * Disable the labelers for the grid types
	 * 
	 * @param types
	 *            grid types
	 */
	public void disableLabelers(Collection<GridType> types) {
		for (GridType type : types) {
			disableLabeler(type);
		}
	}

	/**
	 * Is a labeler enabled for the grid type
	 * 
	 * @param type
	 *            grid type
	 * @return true if labeler enabled
	 */
	public boolean isLabelerEnabled(GridType type) {
		GridLabeler labeler = getLabeler(type);
		return labeler != null && labeler.isEnabled();
	}

	/**
	 * Enable the grid type labeler
	 * 
	 * @param type
	 *            grid type
	 */
	public void enableLabeler(GridType type) {
		getRequiredLabeler(type).setEnabled(true);
	}

	/**
	 * Disable the grid type labeler
	 * 
	 * @param type
	 *            grid type
	 */
	public void disableLabeler(GridType type) {
		GridLabeler labeler = getLabeler(type);
		if (labeler != null) {
			labeler.setEnabled(false);
		}
	}

	/**
	 * Get the labeler for the grid type
	 * 
	 * @param type
	 *            grid type
	 * @return labeler or null
	 */
	private GridLabeler getRequiredLabeler(GridType type) {
		GridLabeler labeler = getLabeler(type);
		if (labeler == null) {
			throw new IllegalStateException(
					"Grid type does not have a labeler: " + type);
		}
		return labeler;
	}

	/**
	 * Set the grid minimum zoom
	 * 
	 * @param type
	 *            grid type
	 * @param minZoom
	 *            minimum zoom
	 */
	public void setLabelMinZoom(GridType type, int minZoom) {
		GridLabeler labeler = getRequiredLabeler(type);
		labeler.setMinZoom(minZoom);
		Integer maxZoom = labeler.getMaxZoom();
		if (maxZoom != null && maxZoom < minZoom) {
			labeler.setMaxZoom(minZoom);
		}
	}

	/**
	 * Set the grid maximum zoom
	 * 
	 * @param type
	 *            grid type
	 * @param maxZoom
	 *            maximum zoom
	 */
	public void setLabelMaxZoom(GridType type, Integer maxZoom) {
		GridLabeler labeler = getRequiredLabeler(type);
		labeler.setMaxZoom(maxZoom);
		if (maxZoom != null && labeler.getMinZoom() > maxZoom) {
			labeler.setMinZoom(maxZoom);
		}
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
	public void setLabelZoomRange(GridType type, int minZoom, Integer maxZoom) {
		GridLabeler labeler = getRequiredLabeler(type);
		if (maxZoom != null && maxZoom < minZoom) {
			throw new IllegalArgumentException("Min zoom '" + minZoom
					+ "' can not be larger than max zoom '" + maxZoom + "'");
		}
		labeler.setMinZoom(minZoom);
		labeler.setMaxZoom(maxZoom);
	}

	/**
	 * Set all label grid zone edge buffers
	 * 
	 * @param buffer
	 *            label buffer (greater than or equal to 0.0 and less than 0.5)
	 */
	public void setAllLabelBuffers(double buffer) {
		for (Grid grid : grids.values()) {
			GridLabeler labeler = grid.getLabeler();
			if (labeler != null) {
				labeler.setBuffer(buffer);
			}
		}
	}

	/**
	 * Set the label grid zone edge buffer for the grid types
	 * 
	 * @param buffer
	 *            label buffer (greater than or equal to 0.0 and less than 0.5)
	 * @param types
	 *            grid types
	 */
	public void setLabelBuffer(double buffer, GridType... types) {
		setLabelBuffer(Arrays.asList(types), buffer);
	}

	/**
	 * Set the label grid zone edge buffer for the grid types
	 * 
	 * @param types
	 *            grid types
	 * @param buffer
	 *            label buffer (greater than or equal to 0.0 and less than 0.5)
	 */
	public void setLabelBuffer(Collection<GridType> types, double buffer) {
		for (GridType type : types) {
			setLabelBuffer(type, buffer);
		}
	}

	/**
	 * Get the label grid zone edge buffer
	 * 
	 * @param type
	 *            grid type
	 * @return label buffer (greater than or equal to 0.0 and less than 0.5)
	 */
	public double getLabelBuffer(GridType type) {
		return getGrid(type).getLabelBuffer();
	}

	/**
	 * Set the label grid zone edge buffer
	 * 
	 * @param type
	 *            grid type
	 * @param buffer
	 *            label buffer (greater than or equal to 0.0 and less than 0.5)
	 */
	public void setLabelBuffer(GridType type, double buffer) {
		getRequiredLabeler(type).setBuffer(buffer);
	}

	/**
	 * Set all label colors
	 * 
	 * @param color
	 *            label color
	 */
	public void setAllLabelColors(Color color) {
		for (Grid grid : grids.values()) {
			if (grid.hasLabeler()) {
				setLabelColor(grid.getType(), color);
			}
		}
	}

	/**
	 * Set the label color for the grid types
	 * 
	 * @param color
	 *            label color
	 * @param types
	 *            grid types
	 */
	public void setLabelColor(Color color, GridType... types) {
		setLabelColor(Arrays.asList(types), color);
	}

	/**
	 * Set the label color for the grid types
	 * 
	 * @param types
	 *            grid types
	 * @param color
	 *            label color
	 */
	public void setLabelColor(Collection<GridType> types, Color color) {
		for (GridType type : types) {
			setLabelColor(type, color);
		}
	}

	/**
	 * Set the label color
	 * 
	 * @param type
	 *            grid type
	 * @param color
	 *            label color
	 */
	public void setLabelColor(GridType type, Color color) {
		getRequiredLabeler(type).setColor(color);
	}

	/**
	 * Set all label text sizes
	 * 
	 * @param textSize
	 *            label text size
	 */
	public void setAllLabelTextSizes(double textSize) {
		for (Grid grid : grids.values()) {
			if (grid.hasLabeler()) {
				setLabelTextSize(grid.getType(), textSize);
			}
		}
	}

	/**
	 * Set the label text size for the grid types
	 * 
	 * @param textSize
	 *            label text size
	 * @param types
	 *            grid types
	 */
	public void setLabelTextSize(double textSize, GridType... types) {
		setLabelTextSize(Arrays.asList(types), textSize);
	}

	/**
	 * Set the label text size for the grid types
	 * 
	 * @param types
	 *            grid types
	 * @param textSize
	 *            label text size
	 */
	public void setLabelTextSize(Collection<GridType> types, double textSize) {
		for (GridType type : types) {
			setLabelTextSize(type, textSize);
		}
	}

	/**
	 * Set the label text size
	 * 
	 * @param type
	 *            grid type
	 * @param textSize
	 *            label text size
	 */
	public void setLabelTextSize(GridType type, double textSize) {
		getRequiredLabeler(type).setTextSize(textSize);
	}

}
