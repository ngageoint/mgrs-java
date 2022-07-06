package mil.nga.mgrs.grid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.nga.color.Color;
import mil.nga.grid.BaseGrid;
import mil.nga.grid.GridStyle;
import mil.nga.grid.features.Bounds;
import mil.nga.grid.property.PropertyConstants;
import mil.nga.grid.tile.GridTile;
import mil.nga.mgrs.features.GridLine;
import mil.nga.mgrs.gzd.GridZone;
import mil.nga.mgrs.property.MGRSProperties;

/**
 * Grid
 * 
 * @author wnewman
 * @author osbornb
 */
public class Grid extends BaseGrid implements Comparable<Grid> {

	/**
	 * Default line width
	 */
	public static final double DEFAULT_WIDTH = MGRSProperties.getInstance()
			.getDoubleProperty(PropertyConstants.GRID, PropertyConstants.WIDTH);

	/**
	 * Grid type
	 */
	private final GridType type;

	/**
	 * Grid line styles
	 */
	private Map<GridType, GridStyle> styles = new HashMap<>();

	/**
	 * Constructor
	 * 
	 * @param type
	 *            grid type
	 */
	protected Grid(GridType type) {
		this.type = type;
	}

	/**
	 * Get the grid type
	 * 
	 * @return grid type
	 */
	public GridType getType() {
		return type;
	}

	/**
	 * Is the provided grid type
	 * 
	 * @param type
	 *            grid type
	 * @return true if the type
	 */
	public boolean isType(GridType type) {
		return this.type == type;
	}

	/**
	 * Get the precision in meters
	 * 
	 * @return precision meters
	 */
	public int getPrecision() {
		return type.getPrecision();
	}

	/**
	 * Get the grid type precision line style for the grid type
	 * 
	 * @param gridType
	 *            grid type
	 * @return grid type line style
	 */
	public GridStyle getStyle(GridType gridType) {
		GridStyle style = null;
		if (gridType == type) {
			style = getStyle();
		} else {
			style = styles.get(gridType);
		}
		return style;
	}

	/**
	 * Get the grid type line style for the grid type or create it
	 * 
	 * @param gridType
	 *            grid type
	 * @return grid type line style
	 */
	private GridStyle getOrCreateStyle(GridType gridType) {
		GridStyle style = getStyle(gridType);
		if (style == null) {
			style = new GridStyle();
			setStyle(gridType, style);
		}
		return style;
	}

	/**
	 * Set the grid type precision line style
	 * 
	 * @param gridType
	 *            grid type
	 * @param style
	 *            grid line style
	 */
	public void setStyle(GridType gridType, GridStyle style) {
		if (gridType.getPrecision() < getPrecision()) {
			throw new IllegalArgumentException(
					"Grid can not define a style for a higher precision grid type. Type: "
							+ type + ", Style Type: " + gridType);
		}
		if (gridType == type) {
			setStyle(style);
		} else {
			styles.put(gridType, style != null ? style : new GridStyle());
		}
	}

	/**
	 * Clear the propagated grid type precision styles
	 */
	public void clearPrecisionStyles() {
		styles.clear();
	}

	/**
	 * Get the grid type precision line color
	 * 
	 * @param gridType
	 *            grid type
	 * @return grid type line color
	 */
	public Color getColor(GridType gridType) {
		Color color = null;
		GridStyle style = getStyle(gridType);
		if (style != null) {
			color = style.getColor();
		}
		if (color == null) {
			color = getColor();
		}
		return color;
	}

	/**
	 * Set the grid type precision line color
	 * 
	 * @param gridType
	 *            grid type
	 * @param color
	 *            grid line color
	 */
	public void setColor(GridType gridType, Color color) {
		getOrCreateStyle(gridType).setColor(color);
	}

	/**
	 * Get the grid type precision line width
	 * 
	 * @param gridType
	 *            grid type
	 * @return grid type line width
	 */
	public double getWidth(GridType gridType) {
		double width = 0;
		GridStyle style = getStyle(gridType);
		if (style != null) {
			width = style.getWidth();
		}
		if (width == 0) {
			width = getWidth();
		}
		return width;
	}

	/**
	 * Set the grid type precision line width
	 * 
	 * @param gridType
	 *            grid type
	 * @param width
	 *            grid line width
	 */
	public void setWidth(GridType gridType, double width) {
		getOrCreateStyle(gridType).setWidth(width);
	}

	/**
	 * Get the grid labeler
	 * 
	 * @return grid labeler
	 */
	public GridLabeler getLabeler() {
		return (GridLabeler) super.getLabeler();
	}

	/**
	 * Set the grid labeler
	 * 
	 * @param labeler
	 *            grid labeler
	 */
	protected void setLabeler(GridLabeler labeler) {
		super.setLabeler(labeler);
	}

	/**
	 * Get the lines for the tile and zone
	 * 
	 * @param tile
	 *            tile
	 * @param zone
	 *            grid zone
	 * @return lines
	 */
	public List<GridLine> getLines(GridTile tile, GridZone zone) {
		return getLines(tile.getZoom(), tile.getBounds(), zone);
	}

	/**
	 * Get the lines for the zoom, tile bounds, and zone
	 * 
	 * @param zoom
	 *            zoom level
	 * @param tileBounds
	 *            tile bounds
	 * @param zone
	 *            grid zone
	 * @return lines
	 */
	public List<GridLine> getLines(int zoom, Bounds tileBounds, GridZone zone) {
		List<GridLine> lines = null;
		if (isLinesWithin(zoom)) {
			lines = getLines(tileBounds, zone);
		}
		return lines;
	}

	/**
	 * Get the lines for the tile bounds and zone
	 * 
	 * @param tileBounds
	 *            tile bounds
	 * @param zone
	 *            grid zone
	 * @return lines
	 */
	public List<GridLine> getLines(Bounds tileBounds, GridZone zone) {
		return zone.getLines(tileBounds, type);
	}

	/**
	 * Get the labels for the tile and zone
	 * 
	 * @param tile
	 *            tile
	 * @param zone
	 *            grid zone
	 * @return labels
	 */
	public List<GridLabel> getLabels(GridTile tile, GridZone zone) {
		return getLabels(tile.getZoom(), tile.getBounds(), zone);
	}

	/**
	 * Get the labels for the zoom, tile bounds, and zone
	 * 
	 * @param zoom
	 *            zoom level
	 * @param tileBounds
	 *            tile bounds
	 * @param zone
	 *            grid zone
	 * @return labels
	 */
	public List<GridLabel> getLabels(int zoom, Bounds tileBounds,
			GridZone zone) {
		List<GridLabel> labels = null;
		if (isLabelerWithin(zoom)) {
			labels = getLabeler().getLabels(tileBounds, type, zone);
		}
		return labels;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Grid other) {
		return getPrecisionCompare() - other.getPrecisionCompare();
	}

	/**
	 * Get the precision in meters
	 * 
	 * @return precision meters
	 */
	public int getPrecisionCompare() {
		int precision = getPrecision();
		if (precision <= GridType.GZD.getPrecision()) {
			precision = Integer.MAX_VALUE;
		}
		return precision;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Grid other = (Grid) obj;
		if (type != other.type)
			return false;
		return true;
	}

}
