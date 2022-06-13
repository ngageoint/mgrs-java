package mil.nga.mgrs.grid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.nga.mgrs.color.Color;
import mil.nga.mgrs.features.Bounds;
import mil.nga.mgrs.features.Line;
import mil.nga.mgrs.gzd.GridZone;
import mil.nga.mgrs.property.MGRSProperties;
import mil.nga.mgrs.property.PropertyConstants;
import mil.nga.mgrs.tile.MGRSTile;

/**
 * Grid
 * 
 * @author wnewman
 * @author osbornb
 */
public class Grid implements Comparable<Grid> {

	/**
	 * Default line width
	 */
	public static final double DEFAULT_WIDTH = MGRSProperties
			.getDoubleProperty(PropertyConstants.GRID, PropertyConstants.WIDTH);

	/**
	 * Grid type
	 */
	private final GridType type;

	/**
	 * Enabled grid
	 */
	private boolean enabled;

	/**
	 * Minimum zoom level
	 */
	private int minZoom;

	/**
	 * Maximum zoom level
	 */
	private Integer maxZoom;

	/**
	 * Minimum zoom level override for drawing grid lines
	 */
	private Integer linesMinZoom;

	/**
	 * Maximum zoom level override for drawing grid lines
	 */
	private Integer linesMaxZoom;

	/**
	 * Grid line styles
	 */
	private Map<GridType, GridStyle> styles = new HashMap<>();

	/**
	 * Grid labeler
	 */
	private Labeler labeler;

	/**
	 * Constructor
	 * 
	 * @param type
	 *            grid type
	 */
	protected Grid(GridType type) {
		this.type = type;
		styles.put(type, new GridStyle());
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
	 * Is the grid enabled
	 * 
	 * @return enabled flag
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Set the enabled flag
	 * 
	 * @param enabled
	 *            enabled flag
	 */
	protected void setEnabled(boolean enabled) {
		this.enabled = enabled;
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
	 * Set the minimum zoom level
	 * 
	 * @param minZoom
	 *            minimum zoom level
	 */
	protected void setMinZoom(int minZoom) {
		this.minZoom = minZoom;
	}

	/**
	 * Get the maximum zoom level
	 * 
	 * @return maximum zoom level
	 */
	public Integer getMaxZoom() {
		return maxZoom;
	}

	/**
	 * Has a maximum zoom level
	 * 
	 * @return true if has a maximum, false if unbounded
	 */
	public boolean hasMaxZoom() {
		return maxZoom != null;
	}

	/**
	 * Set the maximum zoom level
	 * 
	 * @param maxZoom
	 *            maximum zoom level
	 */
	protected void setMaxZoom(Integer maxZoom) {
		this.maxZoom = maxZoom;
	}

	/**
	 * Is the zoom level within the grid zoom range
	 * 
	 * @param zoom
	 *            zoom level
	 * @return true if within range
	 */
	public boolean isWithin(int zoom) {
		return zoom >= minZoom && (maxZoom == null || zoom <= maxZoom);
	}

	/**
	 * Get the minimum zoom level for drawing grid lines
	 * 
	 * @return minimum zoom level
	 */
	public int getLinesMinZoom() {
		return linesMinZoom != null ? linesMinZoom : getMinZoom();
	}

	/**
	 * Has a minimum zoom level override for drawing grid lines
	 * 
	 * @return true if has a minimum, false if not overridden
	 */
	public boolean hasLinesMinZoom() {
		return linesMinZoom != null;
	}

	/**
	 * Set the minimum level override for drawing grid lines
	 * 
	 * @param linesMinZoom
	 *            minimum zoom level or null to remove
	 */
	public void setLinesMinZoom(Integer linesMinZoom) {
		this.linesMinZoom = linesMinZoom;
	}

	/**
	 * Get the maximum zoom level for drawing grid lines
	 * 
	 * @return maximum zoom level
	 */
	public Integer getLinesMaxZoom() {
		return linesMaxZoom != null ? linesMaxZoom : getMaxZoom();
	}

	/**
	 * Has a maximum zoom level override for drawing grid lines
	 * 
	 * @return true if has a maximum, false if not overridden
	 */
	public boolean hasLinesMaxZoom() {
		return linesMaxZoom != null;
	}

	/**
	 * Set the maximum level override for drawing grid lines
	 * 
	 * @param linesMaxZoom
	 *            maximum zoom level or null to remove
	 */
	public void setLinesMaxZoom(Integer linesMaxZoom) {
		this.linesMaxZoom = linesMaxZoom;
	}

	/**
	 * Is the zoom level within the grid lines zoom range
	 * 
	 * @param zoom
	 *            zoom level
	 * @return true if within range
	 */
	public boolean isLinesWithin(int zoom) {
		return (linesMinZoom == null || zoom >= linesMinZoom)
				&& (linesMaxZoom == null || zoom <= linesMaxZoom);
	}

	/**
	 * Get the grid line style
	 * 
	 * @return grid line style
	 */
	public GridStyle getStyle() {
		return styles.get(type);
	}

	/**
	 * Set the grid line style
	 * 
	 * @param style
	 *            grid line style
	 */
	public void setStyle(GridStyle style) {
		if (style == null) {
			style = new GridStyle();
		}
		styles.put(type, style);
	}

	/**
	 * Get the grid line color
	 * 
	 * @return grid line color
	 */
	public Color getColor() {
		return getStyle().getColor();
	}

	/**
	 * Set the grid line color
	 * 
	 * @param color
	 *            grid line color
	 */
	public void setColor(Color color) {
		getStyle().setColor(color);
	}

	/**
	 * Get the grid line width
	 * 
	 * @return grid line width
	 */
	public double getWidth() {
		return getStyle().getWidth();
	}

	/**
	 * Set the grid line width
	 * 
	 * @param width
	 *            grid line width
	 */
	public void setWidth(double width) {
		getStyle().setWidth(width);
	}

	/**
	 * Get the grid type precision line style for the grid type
	 * 
	 * @param gridType
	 *            grid type
	 * @return grid type line style
	 */
	public GridStyle getStyle(GridType gridType) {
		return styles.get(gridType);
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
		if (style == null) {
			style = new GridStyle();
		}
		styles.put(gridType, style);
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
	public Labeler getLabeler() {
		return labeler;
	}

	/**
	 * Has a grid labeler
	 * 
	 * @return true if has a grid labeler
	 */
	public boolean hasLabeler() {
		return labeler != null;
	}

	/**
	 * Set the grid labeler
	 * 
	 * @param labeler
	 *            grid labeler
	 */
	protected void setLabeler(Labeler labeler) {
		this.labeler = labeler;
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
	public List<Line> getLines(MGRSTile tile, GridZone zone) {
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
	public List<Line> getLines(int zoom, Bounds tileBounds, GridZone zone) {
		List<Line> lines = null;
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
	public List<Line> getLines(Bounds tileBounds, GridZone zone) {
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
	public List<Label> getLabels(MGRSTile tile, GridZone zone) {
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
	public List<Label> getLabels(int zoom, Bounds tileBounds, GridZone zone) {
		List<Label> labels = null;
		if (hasLabeler() && labeler.isEnabled() && labeler.isWithin(zoom)) {
			labels = labeler.getLabels(tileBounds, type, zone);
		}
		return labels;
	}

	/**
	 * Get the label grid zone edge buffer
	 * 
	 * @return label buffer (greater than or equal to 0.0 and less than 0.5)
	 */
	public double getLabelBuffer() {
		return hasLabeler() ? labeler.getBuffer() : 0.0;
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
