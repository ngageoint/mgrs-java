package mil.nga.mgrs;

import java.util.List;

import org.junit.Test;

import mil.nga.mgrs.gzd.GridZone;
import mil.nga.mgrs.gzd.GridZones;

public class GZDZonesTest {

	@Test
	public void temp() {
		List<GridZone> temp = GridZones.gridZones;

		for (double longitude = -180.0; longitude <= 180.0; longitude += 1.0) {
			System.out.println("west " + longitude + " - "
					+ GridZones.getZoneNumber(longitude, false));
			System.out.println("east " + longitude + " - "
					+ GridZones.getZoneNumber(longitude, true));
		}

		for (double latitude = -80.0; latitude <= 84.0; latitude += 1.0) {
			System.out.println("south " + latitude + " - "
					+ GridZones.getBandLetter(latitude, false));
			System.out.println("north " + latitude + " - "
					+ GridZones.getBandLetter(latitude, true));
		}

	}

}
