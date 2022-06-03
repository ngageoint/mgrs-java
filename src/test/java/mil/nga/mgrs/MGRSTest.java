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
		assertEquals("35VPL0115797388", mgrs.toString());

	}

}
