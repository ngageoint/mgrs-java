package mil.nga.mgrs.grid;

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
	 * Constructor
	 * 
	 * @param type
	 *            grid type
	 * @param minZoom
	 *            minimum zoom level
	 */
	protected Grid(GridType type, int minZoom) {
		this(type, minZoom, null);
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
		this(type, true, minZoom, maxZoom);
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
		this.type = type;
		this.enabled = enabled;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
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
