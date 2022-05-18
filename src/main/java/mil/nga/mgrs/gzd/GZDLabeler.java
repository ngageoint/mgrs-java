package mil.nga.mgrs.gzd;

import java.util.ArrayList;
import java.util.List;

import mil.nga.mgrs.features.Bounds;
import mil.nga.mgrs.grid.Label;
import mil.nga.mgrs.grid.Labeler;

/**
 * Grid Zone Designator labeler
 * 
 * @author osbornb
 */
public class GZDLabeler extends Labeler {

	/**
	 * Constructor
	 * 
	 * @param minZoom
	 *            minimum zoom
	 */
	public GZDLabeler(int minZoom) {
		super(minZoom);
	}

	/**
	 * Constructor
	 * 
	 * @param minZoom
	 *            minimum zoom
	 * @param maxZoom
	 *            maximum zoom
	 */
	public GZDLabeler(int minZoom, Integer maxZoom) {
		super(minZoom, maxZoom);
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
	public GZDLabeler(boolean enabled, int minZoom, Integer maxZoom) {
		super(enabled, minZoom, maxZoom);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Label> getLabels(Bounds tileBounds, int precision,
			GridZone zone) {
		List<Label> labels = new ArrayList<>();
		Bounds bounds = zone.getBounds();
		labels.add(new Label(zone.getName(), bounds.getCenter(), bounds,
				zone.getNumber(), zone.getLetter()));
		return labels;
	}

}
