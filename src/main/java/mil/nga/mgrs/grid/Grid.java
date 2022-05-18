package mil.nga.mgrs.grid;

import java.util.List;

import mil.nga.mgrs.features.Bounds;
import mil.nga.mgrs.features.Line;
import mil.nga.mgrs.gzd.GridZone;
import mil.nga.mgrs.tile.MGRSTile;

/**
 * Grid
 * 
 * @author wnewman
 * @author osbornb
 */
public class Grid implements Comparable<Grid> {

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
	 * Grid labeler
	 */
	private Labeler labeler;

	/**
	 * Constructor
	 * 
	 * @param type
	 *            grid type
	 * @param minZoom
	 *            minimum zoom level
	 */
	protected Grid(GridType type, int minZoom) {
		this(type, minZoom, null, null);
	}

	/**
	 * Constructor
	 * 
	 * @param type
	 *            grid type
	 * @param minZoom
	 *            minimum zoom level
	 * @param labeler
	 *            grid labeler
	 */
	protected Grid(GridType type, int minZoom, Labeler labeler) {
		this(type, minZoom, null, labeler);
	}

	/**
	 * Constructor
	 * 
	 * @param type
	 *            grid type
	 * @param minZoom
	 *            minimum zoom level
	 * @param maxZoom
	 *            maximum zoom level
	 */
	protected Grid(GridType type, int minZoom, Integer maxZoom) {
		this(type, minZoom, maxZoom, null);
	}

	/**
	 * Constructor
	 * 
	 * @param type
	 *            grid type
	 * @param minZoom
	 *            minimum zoom level
	 * @param maxZoom
	 *            maximum zoom level
	 * @param labeler
	 *            grid labeler
	 */
	protected Grid(GridType type, int minZoom, Integer maxZoom,
			Labeler labeler) {
		this(type, true, minZoom, maxZoom, labeler);
	}

	/**
	 * Constructor
	 * 
	 * @param type
	 *            grid type
	 * @param enabled
	 *            enabled grid
	 * @param minZoom
	 *            minimum zoom level
	 * @param maxZoom
	 *            maximum zoom level
	 */
	protected Grid(GridType type, boolean enabled, int minZoom,
			Integer maxZoom) {
		this(type, enabled, minZoom, maxZoom, null);
	}

	/**
	 * Constructor
	 * 
	 * @param type
	 *            grid type
	 * @param enabled
	 *            enabled grid
	 * @param minZoom
	 *            minimum zoom level
	 * @param maxZoom
	 *            maximum zoom level
	 * @param labeler
	 *            grid labeler
	 */
	protected Grid(GridType type, boolean enabled, int minZoom, Integer maxZoom,
			Labeler labeler) {
		this.type = type;
		this.enabled = enabled;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
		this.labeler = labeler;
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
		return getLines(tile.getBounds(), zone);
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
		return zone.getLines(tileBounds, getPrecision());
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
			labels = labeler.getLabels(tileBounds, getPrecision(), zone);
		}
		return labels;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Grid other) {
		return other.getPrecisionCompare() - getPrecisionCompare();
	}

	/**
	 * Get the precision in meters
	 * 
	 * @return precision meters
	 */
	public int getPrecisionCompare() {
		int precision = getPrecision();
		if (precision <= 0) {
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
