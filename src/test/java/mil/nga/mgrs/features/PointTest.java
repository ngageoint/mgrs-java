package mil.nga.mgrs.features;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Point test
 * 
 * @author osbornb
 *
 */
public class PointTest {

	/**
	 * Test unit conversions
	 */
	@Test
	public void testUnit() {

		Point point = Point.degrees(-112.500003, 21.943049);
		assertEquals(Unit.DEGREE, point.getUnit());
		assertEquals(-112.500003, point.getLongitude(), 0.0);
		assertEquals(21.943049, point.getLatitude(), 0.0);

		Point point2 = point.toMeters();
		assertEquals(Unit.METER, point2.getUnit());
		assertEquals(-12523443.048201751, point2.getLongitude(), 0.0);
		assertEquals(2504688.958883909, point2.getLatitude(), 0.0);

		Point point3 = point2.toDegrees();
		assertEquals(Unit.DEGREE, point3.getUnit());
		assertEquals(-112.500003, point3.getLongitude(), 0.0000000000001);
		assertEquals(21.943049, point3.getLatitude(), 0.0000000000001);

	}

}
