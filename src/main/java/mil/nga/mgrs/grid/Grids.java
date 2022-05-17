package mil.nga.mgrs.grid;

import java.util.Iterator;
import java.util.List;

/**
 * Zoom Level Matching Grids
 * 
 * @author osbornb
 */
public class Grids implements Iterable<Grid> {

	/**
	 * Zoom level
	 */
	private int zoom;

	/**
	 * Grids
	 */
	private List<Grid> grids;

	/**
	 * Constructor
	 * 
	 * @param zoom
	 *            zoom level
	 * @param grids
	 *            grids
	 */
	public Grids(int zoom, List<Grid> grids) {
		this.zoom = zoom;
		this.grids = grids;
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
	public List<Grid> getGrids() {
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
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Grid> iterator() {
		return grids.iterator();
	}

}
