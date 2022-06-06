package mil.nga.mgrs.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Grid Type Test
 * 
 * @author osbornb
 */
public class GridTypeTest {

	/**
	 * Test precisions
	 */
	@Test
	public void testPrecision() {

		assertEquals(0, GridType.GZD.getPrecision());
		assertEquals(100000, GridType.HUNDRED_KILOMETER.getPrecision());
		assertEquals(10000, GridType.TEN_KILOMETER.getPrecision());
		assertEquals(1000, GridType.KILOMETER.getPrecision());
		assertEquals(100, GridType.HUNDRED_METER.getPrecision());
		assertEquals(10, GridType.TEN_METER.getPrecision());
		assertEquals(1, GridType.METER.getPrecision());

	}

	/**
	 * Test digit accuracies
	 */
	@Test
	public void testAccuracy() {

		assertEquals(0, GridType.GZD.getAccuracy());

		assertEquals(GridType.HUNDRED_KILOMETER, GridType.withAccuracy(0));
		assertEquals(0, GridType.HUNDRED_KILOMETER.getAccuracy());

		assertEquals(GridType.TEN_KILOMETER, GridType.withAccuracy(1));
		assertEquals(1, GridType.TEN_KILOMETER.getAccuracy());

		assertEquals(GridType.KILOMETER, GridType.withAccuracy(2));
		assertEquals(2, GridType.KILOMETER.getAccuracy());

		assertEquals(GridType.HUNDRED_METER, GridType.withAccuracy(3));
		assertEquals(3, GridType.HUNDRED_METER.getAccuracy());

		assertEquals(GridType.TEN_METER, GridType.withAccuracy(4));
		assertEquals(4, GridType.TEN_METER.getAccuracy());

		assertEquals(GridType.METER, GridType.withAccuracy(5));
		assertEquals(5, GridType.METER.getAccuracy());

	}

}
