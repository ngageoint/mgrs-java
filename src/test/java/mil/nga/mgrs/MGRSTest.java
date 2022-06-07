package mil.nga.mgrs;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

import mil.nga.mgrs.features.Point;
import mil.nga.mgrs.grid.GridType;
import mil.nga.mgrs.utm.UTM;

/**
 * MGRS Test
 * 
 * @author osbornb
 */
public class MGRSTest {

	/**
	 * Test parsing a MGRS string value
	 * 
	 * @throws ParseException
	 *             upon failure to parse
	 */
	@Test
	public void testParse() throws ParseException {

		String mgrsValue = "33XVG74594359";
		String utmValue = "33 N 474590 8643590";

		MGRS mgrs = MGRS.parse(mgrsValue);
		assertEquals(mgrsValue, mgrs.coordinate(GridType.TEN_METER));
		assertEquals(mgrsValue, mgrs.coordinate(4));

		UTM utm = mgrs.toUTM();
		assertEquals(utmValue, utm.toString());

		mgrsValue = "33X VG 74596 43594";
		utmValue = "33 N 474596 8643594";

		mgrs = MGRS.parse(mgrsValue);
		assertEquals(mgrsValue.replaceAll("\\s", ""), mgrs.toString());

		utm = mgrs.toUTM();
		assertEquals(utmValue, utm.toString());

		utm = UTM.parse(utmValue);
		assertEquals(utmValue, utm.toString());

		mgrs = utm.toMGRS();
		assertEquals(mgrsValue.replaceAll("\\s", ""), mgrs.toString());

		utmValue = "33 N 474596.26 8643594.54";

		utm = UTM.parse(utmValue);
		assertEquals(utmValue, utm.toString());

		mgrs = utm.toMGRS();
		assertEquals(mgrsValue.replaceAll("\\s", ""), mgrs.toString());

	}

	/**
	 * Test parsing a MGRS string value
	 * 
	 * @throws ParseException
	 *             upon failure to parse
	 */
	@Test
	public void testCoordinate() throws ParseException {

		testCoordinate(29.06757, 63.98863, "35VPL0115697387");
		testCoordinateMeters(3235787.09, 9346877.48, "35VPL0115697387");

		testCoordinate(53.51, 12.40, "39PYP7290672069");
		testCoordinateMeters(5956705.95, 1391265.16, "39PYP7290672069");

		testCoordinate(-157.916861, 21.309444, "4QFJ1234056781");
		testCoordinateMeters(-17579224.55, 2428814.96, "4QFJ1234056781");

	}

	/**
	 * Test the WGS84 coordinate with expected MGSR coordinate
	 * 
	 * @param longitude
	 *            longitude in degrees
	 * @param latitude
	 *            latitude in degrees
	 * @param value
	 *            MGRS value
	 */
	private void testCoordinate(double longitude, double latitude,
			String value) {
		Point point = Point.create(longitude, latitude);
		testCoordinate(point, value);
		testCoordinate(point.toMeters(), value);
	}

	/**
	 * Test the WGS84 coordinate with expected MGSR coordinate
	 * 
	 * @param longitude
	 *            longitude in degrees
	 * @param latitude
	 *            latitude in degrees
	 * @param value
	 *            MGRS value
	 */
	private void testCoordinateMeters(double longitude, double latitude,
			String value) {
		Point point = Point.meters(longitude, latitude);
		testCoordinate(point, value);
		testCoordinate(point.toDegrees(), value);
	}

	/**
	 * Test the coordinate with expected MGSR coordinate
	 * 
	 * @param point
	 *            point
	 * @param value
	 *            MGRS value
	 */
	private void testCoordinate(Point point, String value) {

		MGRS mgrs = point.toMGRS();
		assertEquals(value, mgrs.toString());
		assertEquals(value, mgrs.coordinate());

		assertEquals(accuracyValue(value, -1), mgrs.coordinate(GridType.GZD));

		String hundredKilometer = mgrs.coordinate(GridType.HUNDRED_KILOMETER);
		assertEquals(accuracyValue(value, 0), hundredKilometer);
		assertEquals(hundredKilometer, mgrs.coordinate(0));

		String tenKilometer = mgrs.coordinate(GridType.TEN_KILOMETER);
		assertEquals(accuracyValue(value, 1), tenKilometer);
		assertEquals(tenKilometer, mgrs.coordinate(1));

		String kilometer = mgrs.coordinate(GridType.KILOMETER);
		assertEquals(accuracyValue(value, 2), kilometer);
		assertEquals(kilometer, mgrs.coordinate(2));

		String hundredMeter = mgrs.coordinate(GridType.HUNDRED_METER);
		assertEquals(accuracyValue(value, 3), hundredMeter);
		assertEquals(hundredMeter, mgrs.coordinate(3));

		String tenMeter = mgrs.coordinate(GridType.TEN_METER);
		assertEquals(accuracyValue(value, 4), tenMeter);
		assertEquals(tenMeter, mgrs.coordinate(4));

		String meter = mgrs.coordinate(GridType.METER);
		assertEquals(meter, value);
		assertEquals(accuracyValue(value, 5), meter);
		assertEquals(meter, mgrs.coordinate(5));

	}

	/**
	 * Get the MGRS value in the accuracy digits
	 * 
	 * @param value
	 *            MGRS value
	 * @param accuracy
	 *            accuracy digits (-1 for GZD)
	 * @return MGRS in accuracy
	 */
	private String accuracyValue(String value, int accuracy) {

		int gzdLength = value.length() % 2 == 1 ? 3 : 2;
		String accuracyValue = value.substring(0, gzdLength);

		if (accuracy >= 0) {

			accuracyValue += value.substring(gzdLength, gzdLength + 2);

			if (accuracy > 0) {

				String eastingNorthing = value
						.substring(accuracyValue.length());
				int currentAccuracy = eastingNorthing.length() / 2;
				String easting = eastingNorthing.substring(0, currentAccuracy);
				String northing = eastingNorthing.substring(currentAccuracy);

				accuracyValue += easting.substring(0, accuracy);
				accuracyValue += northing.substring(0, accuracy);

			}

		}

		return accuracyValue;
	}

}
