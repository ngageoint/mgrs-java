package mil.nga.mgrs.gzd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wnewman on 11/17/16.
 */
public class GZDZones {

	public static final Map<Integer, GZDZone> longitudeGZDZones = new HashMap<>() {
		{
			put(1, new GZDZone(-180.0, -174.0));
			put(2, new GZDZone(-174.0, -168.0));
			put(3, new GZDZone(-168.0, -162.0));
			put(4, new GZDZone(-162.0, -156.0));
			put(5, new GZDZone(-156.0, -150.0));
			put(6, new GZDZone(-150.0, -144.0));
			put(7, new GZDZone(-144.0, -138.0));
			put(8, new GZDZone(-138.0, -132.0));
			put(9, new GZDZone(-132.0, -126.0));
			put(10, new GZDZone(-126.0, -120.0));
			put(11, new GZDZone(-120.0, -114.0));
			put(12, new GZDZone(-114.0, -108.0));
			put(13, new GZDZone(-108.0, -102.0));
			put(14, new GZDZone(-102.0, -96.0));
			put(15, new GZDZone(-96.0, -90.0));
			put(16, new GZDZone(-90.0, -84.0));
			put(17, new GZDZone(-84.0, -78.0));
			put(18, new GZDZone(-78.0, -72.0));
			put(19, new GZDZone(-72.0, -66.0));
			put(20, new GZDZone(-66.0, -60.0));
			put(21, new GZDZone(-60.0, -54.0));
			put(22, new GZDZone(-54.0, -48.0));
			put(23, new GZDZone(-48.0, -42.0));
			put(24, new GZDZone(-42.0, -36.0));
			put(25, new GZDZone(-36.0, -30.0));
			put(26, new GZDZone(-30.0, -24.0));
			put(27, new GZDZone(-24.0, -18.0));
			put(28, new GZDZone(-18.0, -12.0));
			put(29, new GZDZone(-12.0, -6.0));
			put(30, new GZDZone(-6.0, 0.0));
			put(31, new GZDZone(0.0, 6.0));
			put(32, new GZDZone(6.0, 12.0));
			put(33, new GZDZone(12.0, 18.0));
			put(34, new GZDZone(18.0, 24.0));
			put(35, new GZDZone(24.0, 30.0));
			put(36, new GZDZone(30.0, 36.0));
			put(37, new GZDZone(36.0, 42.0));
			put(38, new GZDZone(42.0, 48.0));
			put(39, new GZDZone(48.0, 54.0));
			put(40, new GZDZone(54.0, 60.0));
			put(41, new GZDZone(60.0, 66.0));
			put(42, new GZDZone(66.0, 72.0));
			put(43, new GZDZone(72.0, 78.0));
			put(44, new GZDZone(78.0, 84.0));
			put(45, new GZDZone(84.0, 90.0));
			put(46, new GZDZone(90.0, 96.0));
			put(47, new GZDZone(96.0, 102.0));
			put(48, new GZDZone(102.0, 108.0));
			put(49, new GZDZone(108.0, 114.0));
			put(50, new GZDZone(114.0, 120.0));
			put(51, new GZDZone(120.0, 126.0));
			put(52, new GZDZone(126.0, 132.0));
			put(53, new GZDZone(132.0, 138.0));
			put(54, new GZDZone(138.0, 144.0));
			put(55, new GZDZone(144.0, 150.0));
			put(56, new GZDZone(150.0, 156.0));
			put(57, new GZDZone(156.0, 162.0));
			put(58, new GZDZone(162.0, 168.0));
			put(59, new GZDZone(168.0, 174.0));
			put(60, new GZDZone(174.0, 180.0));
		}
	};

	public static final Map<Character, GZDZone> latitudeGZDZones = new HashMap<>() {
		{
			put('C', new GZDZone(-80.0, -72.0));
			put('D', new GZDZone(-72.0, -64.0));
			put('E', new GZDZone(-64.0, -56.0));
			put('F', new GZDZone(-56.0, -48.0));
			put('G', new GZDZone(-48.0, -40.0));
			put('H', new GZDZone(-40.0, -32.0));
			put('J', new GZDZone(-32.0, -24.0));
			put('K', new GZDZone(-24.0, -16.0));
			put('L', new GZDZone(-16.0, -8.0));
			put('M', new GZDZone(-8.0, 0.0));
			put('N', new GZDZone(0.0, 8.0));
			put('P', new GZDZone(8.0, 16.0));
			put('Q', new GZDZone(16.0, 24.0));
			put('R', new GZDZone(24.0, 32.0));
			put('S', new GZDZone(32.0, 40.0));
			put('T', new GZDZone(40.0, 48.0));
			put('U', new GZDZone(48.0, 56.0));
			put('V', new GZDZone(56.0, 64.0));
			put('W', new GZDZone(64.0, 72.0));
			put('X', new GZDZone(72.0, 84.0));
		}
	};

	public static final List<GridZoneDesignator> gridZones = new ArrayList<GridZoneDesignator>() {
		{
			for (Map.Entry<Character, GZDZone> latitudeZone : latitudeGZDZones
					.entrySet()) {
				for (Map.Entry<Integer, GZDZone> longitudeZone : longitudeGZDZones
						.entrySet()) {
					double[] bbox = new double[] {
							longitudeZone.getValue().getMin(),
							latitudeZone.getValue().getMin(),
							longitudeZone.getValue().getMax(),
							latitudeZone.getValue().getMax() };
					add(new GridZoneDesignator(latitudeZone.getKey(),
							longitudeZone.getKey(), bbox));
				}
			}
		}
	};

	public static List<GridZoneDesignator> zonesWithin(double[] bbox) {
		List<GridZoneDesignator> zones = new ArrayList<>();
		for (GridZoneDesignator zone : gridZones) {
			if (zone.within(bbox)) {
				zones.add(zone);
			}
		}

		return zones;
	}
}
