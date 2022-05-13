package mil.nga.mgrs;

import mil.nga.mgrs.features.Pixel;
import mil.nga.mgrs.features.Point;
import mil.nga.mgrs.gzd.Bounds;

/**
 * Created by wnewman on 1/5/17.
 */
public class MGRSTile {

	private int width;
	private int height;

	private Bounds bounds;
	private Bounds webMercatorBounds;

	public MGRSTile(int width, int height, int x, int y, int zoom) {
		this.width = width;
		this.height = height;

		bounds = MGRSUtils.getBounds(x, y, zoom);
		webMercatorBounds = MGRSUtils.getWebMercatorBounds(x, y,
				zoom);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Bounds getBounds() {
		return bounds;
	}

	public Bounds getWebMercatorBounds() {
		return webMercatorBounds;
	}

	public Pixel getPixel(Point point) {
		return MGRSUtils.getPixel(width, height, webMercatorBounds, point);
	}

	public float getPointXPixel(double x) {
		return MGRSUtils.getXPixel(width, webMercatorBounds, x);
	}

	public float getPointYPixel(double y) {
		return MGRSUtils.getYPixel(height, webMercatorBounds, y);
	}

}
