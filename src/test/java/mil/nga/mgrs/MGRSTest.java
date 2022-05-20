package mil.nga.mgrs;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

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
		assertEquals(mgrsValue, mgrs.format(4));

		UTM utm = mgrs.getUTM();
		assertEquals(utmValue, utm.toString());

		mgrsValue = "33X VG 74596 43594";
		utmValue = "33 N 474596 8643594";

		mgrs = MGRS.parse(mgrsValue);
		assertEquals(mgrsValue.replaceAll("\\s", ""), mgrs.toString());

		utm = mgrs.getUTM();
		assertEquals(utmValue, utm.toString());

	}

}
