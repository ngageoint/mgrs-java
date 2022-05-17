package mil.nga.mgrs.gzd;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Band Letter Range test
 * 
 * @author osbornb
 */
public class BandLetterRangeTest {

	/**
	 * Band Letters
	 */
	public static final String BAND_LETTERS = "CDEFGHJKLMNPQRSTUVWXX";

	/**
	 * Test the full range
	 */
	@Test
	public void testFullRange() {

		BandLetterRange bandRange = new BandLetterRange();
		for (char bandLetter : bandRange) {
			assertEquals(GridZones.getSouthLatitude(bandLetter),
					(BAND_LETTERS.indexOf(bandLetter) - 10) * 8, 0.0);
		}

	}

}
