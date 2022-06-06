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

	}

	/**
	 * Test parsing a MGRS string value
	 * 
	 * @throws ParseException
	 *             upon failure to parse
	 */
	@Test
	public void testCoordinate() throws ParseException {

		double latitude = 63.98863;
		double longitude = 29.06757;

		Point point = Point.create(longitude, latitude);

		MGRS mgrs = point.toMGRS();
		String mgrsValue = "35VPL0115697387";
		assertEquals(mgrsValue, mgrs.toString());
		assertEquals(mgrsValue, mgrs.coordinate());

		double latitude2 = 12.40;
		double longitude2 = 53.51;

		Point point2 = Point.create(longitude2, latitude2);
		MGRS mgrs2 = point2.toMGRS();
		String mgrsValue2 = "39PYP7290672069";
		assertEquals(mgrsValue2, mgrs2.toString());
		assertEquals(mgrsValue2, mgrs2.coordinate());

		assertEquals("39P", mgrs2.coordinate(GridType.GZD));

		String hundredKilometer = mgrs2.coordinate(GridType.HUNDRED_KILOMETER);
		assertEquals("39PYP", hundredKilometer);
		assertEquals(hundredKilometer, mgrs2.coordinate(0));

		String tenKilometer = mgrs2.coordinate(GridType.TEN_KILOMETER);
		assertEquals("39PYP77", tenKilometer);
		assertEquals(tenKilometer, mgrs2.coordinate(1));

		String kilometer = mgrs2.coordinate(GridType.KILOMETER);
		assertEquals("39PYP7272", kilometer);
		assertEquals(kilometer, mgrs2.coordinate(2));

		String hundredMeter = mgrs2.coordinate(GridType.HUNDRED_METER);
		assertEquals("39PYP729720", hundredMeter);
		assertEquals(hundredMeter, mgrs2.coordinate(3));

		String tenMeter = mgrs2.coordinate(GridType.TEN_METER);
		assertEquals("39PYP72907206", tenMeter);
		assertEquals(tenMeter, mgrs2.coordinate(4));

		assertEquals(mgrsValue2, mgrs2.coordinate(GridType.METER));
		assertEquals(mgrsValue2, mgrs2.coordinate(5));

	}

}
