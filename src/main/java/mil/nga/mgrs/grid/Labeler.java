package mil.nga.mgrs.grid;

import java.util.List;

import mil.nga.mgrs.features.Bounds;
import mil.nga.mgrs.gzd.GridZone;

/**
 * Grid Labeler
 * 
 * @author osbornb
 */
public abstract class Labeler {

	/**
	 * Enabled labeler
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
	 * @param minZoom
	 *            minimum zoom
	 */
	public Labeler(int minZoom) {
		this(minZoom, null);
	}

	/**
	 * Constructor
	 * 
	 * @param minZoom
	 *            minimum zoom
	 * @param maxZoom
	 *            maximum zoom
	 */
	public Labeler(int minZoom, Integer maxZoom) {
		this(true, minZoom, maxZoom);
	}

	/**
	 * Constructor
	 * 
	 * @param enabled
	 *            enabled labeler
	 * @param minZoom
	 *            minimum zoom
	 * @param maxZoom
	 *            maximum zoom
	 */
	public Labeler(boolean enabled, int minZoom, Integer maxZoom) {
		this.enabled = enabled;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
	}

	/**
	 * Get labels for the bounds
	 * 
	 * @param tileBounds
	 *            tile bounds
	 * @param precision
	 *            precision in meters
	 * @param zone
	 *            grid zone
	 * @return labels
	 */
	public abstract List<Label> getLabels(Bounds tileBounds, int precision,
			GridZone zone);

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
	public void setEnabled(boolean enabled) {
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
	public void setMinZoom(int minZoom) {
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
	public void setMaxZoom(Integer maxZoom) {
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

}
