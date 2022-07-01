package mil.nga.mgrs;

import java.text.ParseException;
import java.util.List;

import org.junit.Test;

import mil.nga.grid.features.Point;
import mil.nga.grid.tile.GridTile;
import mil.nga.grid.tile.Pixel;
import mil.nga.grid.tile.PixelRange;
import mil.nga.mgrs.features.GridLine;
import mil.nga.mgrs.grid.Grid;
import mil.nga.mgrs.grid.GridLabel;
import mil.nga.mgrs.grid.GridType;
import mil.nga.mgrs.grid.Grids;
import mil.nga.mgrs.grid.ZoomGrids;
import mil.nga.mgrs.gzd.GridRange;
import mil.nga.mgrs.gzd.GridZone;
import mil.nga.mgrs.gzd.GridZones;
import mil.nga.mgrs.utm.UTM;

/**
 * README example tests
 * 
 * @author osbornb
 */
public class ReadmeTest {

	/**
	 * Test MGRS coordinates
	 * 
	 * @throws ParseException
	 *             upon failure to parse
	 */
	@Test
	public void testCoordinates() throws ParseException {

		MGRS mgrs = MGRS.parse("33XVG74594359");
		Point point = mgrs.toPoint();
		Point pointMeters = point.toMeters();
		UTM utm = mgrs.toUTM();
		String utmCoordinate = utm.toString();
		Point point2 = utm.toPoint();

		MGRS mgrs2 = MGRS.parse("33X VG 74596 43594");

		double latitude = 63.98862388;
		double longitude = 29.06755082;
		Point point3 = Point.point(longitude, latitude);
		MGRS mgrs3 = MGRS.from(point3);
		String mgrsCoordinate = mgrs3.toString();
		String mgrsGZD = mgrs3.coordinate(GridType.GZD);
		String mgrs100k = mgrs3.coordinate(GridType.HUNDRED_KILOMETER);
		String mgrs10k = mgrs3.coordinate(GridType.TEN_KILOMETER);
		String mgrs1k = mgrs3.coordinate(GridType.KILOMETER);
		String mgrs100m = mgrs3.coordinate(GridType.HUNDRED_METER);
		String mgrs10m = mgrs3.coordinate(GridType.TEN_METER);
		String mgrs1m = mgrs3.coordinate(GridType.METER);

		UTM utm2 = UTM.from(point3);
		MGRS mgrs4 = utm2.toMGRS();

		UTM utm3 = UTM.parse("18 N 585628 4511322");
		MGRS mgrs5 = utm3.toMGRS();

	}

	/**
	 * Test draw tile template logic
	 */
	@Test
	public void testDrawTile() {
		testDrawTile(GridTile.tile(512, 512, 8, 12, 5));
	}

	/**
	 * Test draw tile template logic
	 * 
	 * @param tile
	 *            grid tile
	 */
	private static void testDrawTile(GridTile tile) {

		// GridTile tile = ...;

		Grids grids = Grids.create();

		ZoomGrids zoomGrids = grids.getGrids(tile.getZoom());
		if (zoomGrids.hasGrids()) {

			GridRange gridRange = GridZones.getGridRange(tile.getBounds());

			for (Grid grid : zoomGrids) {

				// draw this grid for each zone
				for (GridZone zone : gridRange) {

					List<GridLine> lines = grid.getLines(tile, zone);
					if (lines != null) {
						PixelRange pixelRange = zone.getBounds()
								.getPixelRange(tile);
						for (GridLine line : lines) {
							Pixel pixel1 = line.getPoint1().getPixel(tile);
							Pixel pixel2 = line.getPoint2().getPixel(tile);
							// Draw line
						}
					}

					List<GridLabel> labels = grid.getLabels(tile, zone);
					if (labels != null) {
						for (GridLabel label : labels) {
							PixelRange pixelRange = label.getBounds()
									.getPixelRange(tile);
							Pixel centerPixel = label.getCenter()
									.getPixel(tile);
							// Draw label
						}
					}

				}
			}
		}

	}

}
