package mil.nga.mgrs.grid;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * Zoom Level Matching Grids
 * 
 * @author osbornb
 */
public class ZoomGrids implements Iterable<Grid> {

	/**
	 * Zoom level
	 */
	private final int zoom;

	/**
	 * Grids
	 */
	private final TreeSet<Grid> grids = new TreeSet<>();

	/**
	 * Constructor
	 * 
	 * @param zoom
	 *            zoom level
	 */
	public ZoomGrids(int zoom) {
		this.zoom = zoom;
	}

	/**
	 * Get the zoom level
	 * 
	 * @return zoom level
	 */
	public int getZoom() {
		return zoom;
	}

	/**
	 * Get the grids within the zoom level
	 * 
	 * @return grids
	 */
	public TreeSet<Grid> getGrids() {
		return grids;
	}

	/**
	 * Get the number of grids
	 * 
	 * @return number of grids
	 */
	public int numGrids() {
		return grids.size();
	}

	/**
	 * Determine if the zoom level has grids
	 * 
	 * @return true if has grids
	 */
	public boolean hasGrids() {
		return !grids.isEmpty();
	}

	/**
	 * Add a grid
	 * 
	 * @param grid
	 *            grid
	 * @return true if added
	 */
	public boolean addGrid(Grid grid) {
		return grids.add(grid);
	}

	/**
	 * Remove the grid
	 * 
	 * @param grid
	 *            grid
	 * @return true if removed
	 */
	public boolean removeGrid(Grid grid) {
		return grids.remove(grid);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Grid> iterator() {
		return grids.iterator();
	}

}
