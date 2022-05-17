package mil.nga.mgrs.gzd;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import mil.nga.mgrs.MGRSConstants;
import mil.nga.mgrs.MGRSUtils;

/**
 * Grid Zones Test
 * 
 * @author osbornb
 */
public class GridZonesTest {

	/**
	 * Test zone numbers
	 */
	@Test
	public void testZoneNumbers() {

		int zoneNumber = MGRSConstants.MIN_ZONE_NUMBER;
		for (double longitude = MGRSConstants.MIN_LON; longitude <= MGRSConstants.MAX_LON; longitude += MGRSConstants.ZONE_WIDTH) {

			int west = (longitude > MGRSConstants.MIN_LON
					&& longitude < MGRSConstants.MAX_LON) ? zoneNumber - 1
							: zoneNumber;
			int east = zoneNumber;

			if (longitude < MGRSConstants.MAX_LON) {
				assertEquals(east,
						(int) Math.floor(longitude / 6 + 31));
			}

			assertEquals(west, GridZones.getZoneNumber(longitude, false));
			assertEquals(east, GridZones.getZoneNumber(longitude, true));
			assertEquals(east, GridZones.getZoneNumber(longitude));

			if (zoneNumber < MGRSConstants.MAX_ZONE_NUMBER) {
				zoneNumber++;
			}

		}

	}

	/**
	 * Test band letters
	 */
	@Test
	public void testBandLetters() {

		char bandLetter = MGRSConstants.MIN_BAND_LETTER;
		for (double latitude = MGRSConstants.MIN_LAT; latitude <= MGRSConstants.MAX_LAT; latitude += (latitude < 80.0
				? MGRSConstants.BAND_HEIGHT
				: 4.0)) {

			char south = (latitude > MGRSConstants.MIN_LAT && latitude < 80.0)
					? MGRSUtils.previousBandLetter(bandLetter)
					: bandLetter;
			char north = bandLetter;
			
			assertEquals(north,
					BandLetterRangeTest.BAND_LETTERS
							.charAt((int) Math.floor(latitude / 8 + 10)));

			assertEquals(south, GridZones.getBandLetter(latitude, false));
			assertEquals(north, GridZones.getBandLetter(latitude, true));
			assertEquals(north, GridZones.getBandLetter(latitude));

			if (bandLetter < MGRSConstants.MAX_BAND_LETTER) {
				bandLetter = MGRSUtils.nextBandLetter(bandLetter);
			}

		}

	}

}
