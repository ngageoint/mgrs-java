package mil.nga.mgrs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

		assertTrue(MGRS.isMGRS(mgrsValue));
		MGRS mgrs = MGRS.parse(mgrsValue);
		assertEquals(GridType.TEN_METER, MGRS.precision(mgrsValue));
		assertEquals(4, MGRS.accuracy(mgrsValue));
		assertEquals(mgrsValue, mgrs.coordinate(GridType.TEN_METER));
		assertEquals(mgrsValue, mgrs.coordinate(4));
		assertEquals(GridType.TEN_METER, mgrs.precision());
		assertEquals(4, mgrs.accuracy());

		UTM utm = mgrs.toUTM();
		assertEquals(utmValue, utm.toString());

		mgrsValue = "33X VG 74596 43594";
		utmValue = "33 N 474596 8643594";

		assertTrue(MGRS.isMGRS(mgrsValue));
		mgrs = MGRS.parse(mgrsValue);
		assertEquals(GridType.METER, MGRS.precision(mgrsValue));
		assertEquals(5, MGRS.accuracy(mgrsValue));
		assertEquals(mgrsValue.replaceAll("\\s", ""), mgrs.toString());

		utm = mgrs.toUTM();
		assertEquals(utmValue, utm.toString());

		assertTrue(UTM.isUTM(utmValue));
		utm = UTM.parse(utmValue);
		assertEquals(utmValue, utm.toString());

		mgrs = utm.toMGRS();
		assertEquals(mgrsValue.replaceAll("\\s", ""), mgrs.toString());

		utmValue = "33 N 474596.26 8643594.54";

		assertTrue(UTM.isUTM(utmValue));
		utm = UTM.parse(utmValue);
		assertEquals(utmValue, utm.toString());

		mgrs = utm.toMGRS();
		assertEquals(mgrsValue.replaceAll("\\s", ""), mgrs.toString());

		mgrsValue = "33X";
		assertTrue(MGRS.isMGRS(mgrsValue));
		mgrs = MGRS.parse(mgrsValue);
		assertEquals(GridType.GZD, MGRS.precision(mgrsValue));
		assertEquals(0, MGRS.accuracy(mgrsValue));
		assertEquals(33, mgrs.getZone());
		assertEquals('X', mgrs.getBand());
		assertEquals('T', mgrs.getColumn());
		assertEquals('V', mgrs.getRow());
		assertEquals("TV", mgrs.getColumnRowId());
		assertEquals(93363, mgrs.getEasting());
		assertEquals(99233, mgrs.getNorthing());
		assertEquals("33XTV9336399233", mgrs.coordinate());
		Point point = mgrs.toPoint();
		assertEquals(9.0, point.getLongitude(), 0.0001);
		assertEquals(72.0, point.getLatitude(), 0.0001);
		assertEquals(GridType.METER, mgrs.precision());
		assertEquals(5, mgrs.accuracy());

		mgrsValue = "33XVG";
		assertTrue(MGRS.isMGRS(mgrsValue));
		mgrs = MGRS.parse(mgrsValue);
		assertEquals(GridType.HUNDRED_KILOMETER, MGRS.precision(mgrsValue));
		assertEquals(0, MGRS.accuracy(mgrsValue));
		assertEquals(33, mgrs.getZone());
		assertEquals('X', mgrs.getBand());
		assertEquals('V', mgrs.getColumn());
		assertEquals('G', mgrs.getRow());
		assertEquals("VG", mgrs.getColumnRowId());
		assertEquals(0, mgrs.getEasting());
		assertEquals(0, mgrs.getNorthing());
		assertEquals(mgrsValue, mgrs.coordinate(GridType.HUNDRED_KILOMETER));
		assertEquals("33XVG0000000000", mgrs.coordinate());
		point = mgrs.toPoint();
		assertEquals(10.8756458, point.getLongitude(), 0.0001);
		assertEquals(77.4454720, point.getLatitude(), 0.0001);
		assertEquals(GridType.HUNDRED_KILOMETER, mgrs.precision());
		assertEquals(0, mgrs.accuracy());

		mgrsValue = "33XVG74";
		assertTrue(MGRS.isMGRS(mgrsValue));
		mgrs = MGRS.parse(mgrsValue);
		assertEquals(GridType.TEN_KILOMETER, MGRS.precision(mgrsValue));
		assertEquals(1, MGRS.accuracy(mgrsValue));
		assertEquals(33, mgrs.getZone());
		assertEquals('X', mgrs.getBand());
		assertEquals('V', mgrs.getColumn());
		assertEquals('G', mgrs.getRow());
		assertEquals("VG", mgrs.getColumnRowId());
		assertEquals(70000, mgrs.getEasting());
		assertEquals(40000, mgrs.getNorthing());
		assertEquals(mgrsValue, mgrs.coordinate(GridType.TEN_KILOMETER));
		assertEquals("33XVG7000040000", mgrs.coordinate());
		point = mgrs.toPoint();
		assertEquals(13.7248758, point.getLongitude(), 0.0001);
		assertEquals(77.8324735, point.getLatitude(), 0.0001);
		assertEquals(GridType.TEN_KILOMETER, mgrs.precision());
		assertEquals(1, mgrs.accuracy());

		mgrsValue = "33XVG7443";
		assertTrue(MGRS.isMGRS(mgrsValue));
		mgrs = MGRS.parse(mgrsValue);
		assertEquals(GridType.KILOMETER, MGRS.precision(mgrsValue));
		assertEquals(2, MGRS.accuracy(mgrsValue));
		assertEquals(33, mgrs.getZone());
		assertEquals('X', mgrs.getBand());
		assertEquals('V', mgrs.getColumn());
		assertEquals('G', mgrs.getRow());
		assertEquals("VG", mgrs.getColumnRowId());
		assertEquals(74000, mgrs.getEasting());
		assertEquals(43000, mgrs.getNorthing());
		assertEquals(mgrsValue, mgrs.coordinate(GridType.KILOMETER));
		assertEquals("33XVG7400043000", mgrs.coordinate());
		point = mgrs.toPoint();
		assertEquals(13.8924385, point.getLongitude(), 0.0001);
		assertEquals(77.8600782, point.getLatitude(), 0.0001);
		assertEquals(GridType.KILOMETER, mgrs.precision());
		assertEquals(2, mgrs.accuracy());

		mgrsValue = "33XVG745435";
		assertTrue(MGRS.isMGRS(mgrsValue));
		mgrs = MGRS.parse(mgrsValue);
		assertEquals(GridType.HUNDRED_METER, MGRS.precision(mgrsValue));
		assertEquals(3, MGRS.accuracy(mgrsValue));
		assertEquals(33, mgrs.getZone());
		assertEquals('X', mgrs.getBand());
		assertEquals('V', mgrs.getColumn());
		assertEquals('G', mgrs.getRow());
		assertEquals("VG", mgrs.getColumnRowId());
		assertEquals(74500, mgrs.getEasting());
		assertEquals(43500, mgrs.getNorthing());
		assertEquals(mgrsValue, mgrs.coordinate(GridType.HUNDRED_METER));
		assertEquals("33XVG7450043500", mgrs.coordinate());
		point = mgrs.toPoint();
		assertEquals(13.9133378, point.getLongitude(), 0.0001);
		assertEquals(77.8646415, point.getLatitude(), 0.0001);
		assertEquals(GridType.HUNDRED_METER, mgrs.precision());
		assertEquals(3, mgrs.accuracy());

		mgrsValue = "33XVG74594359";
		assertTrue(MGRS.isMGRS(mgrsValue));
		mgrs = MGRS.parse(mgrsValue);
		assertEquals(GridType.TEN_METER, MGRS.precision(mgrsValue));
		assertEquals(4, MGRS.accuracy(mgrsValue));
		assertEquals(33, mgrs.getZone());
		assertEquals('X', mgrs.getBand());
		assertEquals('V', mgrs.getColumn());
		assertEquals('G', mgrs.getRow());
		assertEquals("VG", mgrs.getColumnRowId());
		assertEquals(74590, mgrs.getEasting());
		assertEquals(43590, mgrs.getNorthing());
		assertEquals(mgrsValue, mgrs.coordinate(GridType.TEN_METER));
		assertEquals("33XVG7459043590", mgrs.coordinate());
		point = mgrs.toPoint();
		assertEquals(13.9171014, point.getLongitude(), 0.0001);
		assertEquals(77.8654628, point.getLatitude(), 0.0001);
		assertEquals(GridType.TEN_METER, mgrs.precision());
		assertEquals(4, mgrs.accuracy());

		mgrsValue = "33XVG7459743593";
		assertTrue(MGRS.isMGRS(mgrsValue));
		mgrs = MGRS.parse(mgrsValue);
		assertEquals(GridType.METER, MGRS.precision(mgrsValue));
		assertEquals(5, MGRS.accuracy(mgrsValue));
		assertEquals(33, mgrs.getZone());
		assertEquals('X', mgrs.getBand());
		assertEquals('V', mgrs.getColumn());
		assertEquals('G', mgrs.getRow());
		assertEquals("VG", mgrs.getColumnRowId());
		assertEquals(74597, mgrs.getEasting());
		assertEquals(43593, mgrs.getNorthing());
		assertEquals(mgrsValue, mgrs.coordinate(GridType.METER));
		assertEquals("33XVG7459743593", mgrs.coordinate());
		point = mgrs.toPoint();
		assertEquals(13.9173973, point.getLongitude(), 0.0001);
		assertEquals(77.8654908, point.getLatitude(), 0.0001);
		assertEquals(GridType.METER, mgrs.precision());
		assertEquals(5, mgrs.accuracy());

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
	 * @throws ParseException
	 *             upon failure to parse
	 */
	private void testCoordinate(double longitude, double latitude, String value)
			throws ParseException {
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
	 * @throws ParseException
	 *             upon failure to parse
	 */
	private void testCoordinateMeters(double longitude, double latitude,
			String value) throws ParseException {
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
	 * @throws ParseException
	 *             upon failure to parse
	 */
	private void testCoordinate(Point point, String value)
			throws ParseException {

		MGRS mgrs = point.toMGRS();
		assertEquals(value, mgrs.toString());
		assertEquals(value, mgrs.coordinate());

		String gzd = mgrs.coordinate(GridType.GZD);
		assertEquals(accuracyValue(value, -1), gzd);
		assertTrue(MGRS.isMGRS(gzd));
		MGRS gzdMGRS = MGRS.parse(gzd);
		assertEquals(GridType.GZD, MGRS.precision(gzd));
		assertEquals(0, MGRS.accuracy(gzd));
		assertEquals(gzd, gzdMGRS.coordinate(GridType.GZD));

		String hundredKilometer = mgrs.coordinate(GridType.HUNDRED_KILOMETER);
		assertEquals(accuracyValue(value, 0), hundredKilometer);
		assertEquals(hundredKilometer, mgrs.coordinate(0));
		assertTrue(MGRS.isMGRS(hundredKilometer));
		MGRS hundredKilometerMGRS = MGRS.parse(hundredKilometer);
		assertEquals(GridType.HUNDRED_KILOMETER,
				MGRS.precision(hundredKilometer));
		assertEquals(0, MGRS.accuracy(hundredKilometer));
		assertEquals(hundredKilometer,
				hundredKilometerMGRS.coordinate(GridType.HUNDRED_KILOMETER));
		assertEquals(0, hundredKilometerMGRS.getEasting());
		assertEquals(0, hundredKilometerMGRS.getNorthing());
		assertEquals(GridType.HUNDRED_KILOMETER,
				hundredKilometerMGRS.precision());
		assertEquals(0, hundredKilometerMGRS.accuracy());

		String tenKilometer = mgrs.coordinate(GridType.TEN_KILOMETER);
		assertEquals(accuracyValue(value, 1), tenKilometer);
		assertEquals(tenKilometer, mgrs.coordinate(1));
		assertTrue(MGRS.isMGRS(tenKilometer));
		MGRS tenKilometerMGRS = MGRS.parse(tenKilometer);
		assertEquals(GridType.TEN_KILOMETER, MGRS.precision(tenKilometer));
		assertEquals(1, MGRS.accuracy(tenKilometer));
		assertEquals(tenKilometer,
				tenKilometerMGRS.coordinate(GridType.TEN_KILOMETER));
		assertEquals(getEasting(tenKilometer, 1),
				tenKilometerMGRS.getEasting());
		assertEquals(getNorthing(tenKilometer, 1),
				tenKilometerMGRS.getNorthing());
		assertEquals(GridType.TEN_KILOMETER, tenKilometerMGRS.precision());
		assertEquals(1, tenKilometerMGRS.accuracy());

		String kilometer = mgrs.coordinate(GridType.KILOMETER);
		assertEquals(accuracyValue(value, 2), kilometer);
		assertEquals(kilometer, mgrs.coordinate(2));
		assertTrue(MGRS.isMGRS(kilometer));
		MGRS kilometerMGRS = MGRS.parse(kilometer);
		assertEquals(GridType.KILOMETER, MGRS.precision(kilometer));
		assertEquals(2, MGRS.accuracy(kilometer));
		assertEquals(kilometer, kilometerMGRS.coordinate(GridType.KILOMETER));
		assertEquals(getEasting(kilometer, 2), kilometerMGRS.getEasting());
		assertEquals(getNorthing(kilometer, 2), kilometerMGRS.getNorthing());
		assertEquals(GridType.KILOMETER, kilometerMGRS.precision());
		assertEquals(2, kilometerMGRS.accuracy());

		String hundredMeter = mgrs.coordinate(GridType.HUNDRED_METER);
		assertEquals(accuracyValue(value, 3), hundredMeter);
		assertEquals(hundredMeter, mgrs.coordinate(3));
		assertTrue(MGRS.isMGRS(hundredMeter));
		MGRS hundredMeterMGRS = MGRS.parse(hundredMeter);
		assertEquals(GridType.HUNDRED_METER, MGRS.precision(hundredMeter));
		assertEquals(3, MGRS.accuracy(hundredMeter));
		assertEquals(hundredMeter,
				hundredMeterMGRS.coordinate(GridType.HUNDRED_METER));
		assertEquals(getEasting(hundredMeter, 3),
				hundredMeterMGRS.getEasting());
		assertEquals(getNorthing(hundredMeter, 3),
				hundredMeterMGRS.getNorthing());
		assertEquals(GridType.HUNDRED_METER, hundredMeterMGRS.precision());
		assertEquals(3, hundredMeterMGRS.accuracy());

		String tenMeter = mgrs.coordinate(GridType.TEN_METER);
		assertEquals(accuracyValue(value, 4), tenMeter);
		assertEquals(tenMeter, mgrs.coordinate(4));
		assertTrue(MGRS.isMGRS(tenMeter));
		MGRS tenMeterMGRS = MGRS.parse(tenMeter);
		assertEquals(GridType.TEN_METER, MGRS.precision(tenMeter));
		assertEquals(4, MGRS.accuracy(tenMeter));
		assertEquals(tenMeter, tenMeterMGRS.coordinate(GridType.TEN_METER));
		assertEquals(getEasting(tenMeter, 4), tenMeterMGRS.getEasting());
		assertEquals(getNorthing(tenMeter, 4), tenMeterMGRS.getNorthing());
		assertEquals(GridType.TEN_METER, tenMeterMGRS.precision());
		assertEquals(4, tenMeterMGRS.accuracy());

		String meter = mgrs.coordinate(GridType.METER);
		assertEquals(meter, value);
		assertEquals(accuracyValue(value, 5), meter);
		assertEquals(meter, mgrs.coordinate(5));
		assertTrue(MGRS.isMGRS(meter));
		MGRS meterMGRS = MGRS.parse(meter);
		assertEquals(GridType.METER, MGRS.precision(meter));
		assertEquals(5, MGRS.accuracy(meter));
		assertEquals(meter, meterMGRS.coordinate(GridType.METER));
		assertEquals(getEasting(meter, 5), meterMGRS.getEasting());
		assertEquals(getNorthing(meter, 5), meterMGRS.getNorthing());
		assertEquals(GridType.METER, meterMGRS.precision());
		assertEquals(5, meterMGRS.accuracy());

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

	/**
	 * Get the easting of the MGRS value in the accuracy
	 * 
	 * @param value
	 *            MGRS value
	 * @param accuracy
	 *            accuracy digits
	 * @return easting
	 */
	private long getEasting(String value, int accuracy) {
		return padAccuracy(value.substring(value.length() - 2 * accuracy,
				value.length() - accuracy), accuracy);
	}

	/**
	 * Get the northing of the MGRS value in the accuracy
	 * 
	 * @param value
	 *            MGRS value
	 * @param accuracy
	 *            accuracy digits
	 * @return northing
	 */
	private long getNorthing(String value, int accuracy) {
		return padAccuracy(value.substring(value.length() - accuracy),
				accuracy);
	}

	/**
	 * Pad the value with the accuracy and parse as a long
	 * 
	 * @param value
	 *            MGRS value
	 * @param accuracy
	 *            accuracy digits
	 * @return long value
	 */
	private long padAccuracy(String value, int accuracy) {
		for (int i = accuracy; i < 5; i++) {
			value += "0";
		}
		return Long.parseLong(value);
	}

}
