package mil.nga.giat.mgrs.gzd;

import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wnewman on 11/17/16.
 */
public class GZDZones {

    public static final Map<Integer, Pair<Double, Double>> longitudeGZDZones = new HashMap<Integer,  Pair<Double, Double>>() {{
        put(1, new Pair<>(-180.0, -174.0));
        put(2, new Pair<>(-174.0, -168.0));
        put(3, new Pair<>(-168.0, -162.0));
        put(4, new Pair<>(-162.0, -156.0));
        put(5, new Pair<>(-156.0, -150.0));
        put(6, new Pair<>(-150.0, -144.0));
        put(7, new Pair<>(-144.0, -138.0));
        put(8, new Pair<>(-138.0, -132.0));
        put(9, new Pair<>(-132.0, -126.0));
        put(10, new Pair<>(-126.0, -120.0));
        put(11, new Pair<>(-120.0, -114.0));
        put(12, new Pair<>(-114.0, -108.0));
        put(13, new Pair<>(-108.0, -102.0));
        put(14, new Pair<>(-102.0, -96.0));
        put(15, new Pair<>(-96.0, -90.0));
        put(16, new Pair<>(-90.0, -84.0));
        put(17, new Pair<>(-84.0, -78.0));
        put(18, new Pair<>(-78.0, -72.0));
        put(19, new Pair<>(-72.0, -66.0));
        put(20, new Pair<>(-66.0, -60.0));
        put(21, new Pair<>(-60.0, -54.0));
        put(22, new Pair<>(-54.0, -48.0));
        put(23, new Pair<>(-48.0, -42.0));
        put(24, new Pair<>(-42.0, -36.0));
        put(25, new Pair<>(-36.0, -30.0));
        put(26, new Pair<>(-30.0, -24.0));
        put(27, new Pair<>(-24.0, -18.0));
        put(28, new Pair<>(-18.0, -12.0));
        put(29, new Pair<>(-12.0, -6.0));
        put(30, new Pair<>(-6.0, 0.0));
        put(31, new Pair<>(0.0, 6.0));
        put(32, new Pair<>(6.0, 12.0));
        put(33, new Pair<>(12.0, 18.0));
        put(34, new Pair<>(18.0, 24.0));
        put(35, new Pair<>(24.0, 30.0));
        put(36, new Pair<>(30.0, 36.0));
        put(37, new Pair<>(36.0, 42.0));
        put(38, new Pair<>(42.0, 48.0));
        put(39, new Pair<>(48.0, 54.0));
        put(40, new Pair<>(54.0, 60.0));
        put(41, new Pair<>(60.0, 66.0));
        put(42, new Pair<>(66.0, 72.0));
        put(43, new Pair<>(72.0, 78.0));
        put(44, new Pair<>(78.0, 84.0));
        put(45, new Pair<>(84.0, 90.0));
        put(46, new Pair<>(90.0, 96.0));
        put(47, new Pair<>(96.0, 102.0));
        put(48, new Pair<>(102.0, 108.0));
        put(49, new Pair<>(108.0, 114.0));
        put(50, new Pair<>(114.0, 120.0));
        put(51, new Pair<>(120.0, 126.0));
        put(52, new Pair<>(126.0, 132.0));
        put(53, new Pair<>(132.0, 138.0));
        put(54, new Pair<>(138.0, 144.0));
        put(55, new Pair<>(144.0, 150.0));
        put(56, new Pair<>(150.0, 156.0));
        put(57, new Pair<>(156.0, 162.0));
        put(58, new Pair<>(162.0, 168.0));
        put(59, new Pair<>(168.0, 174.0));
        put(60, new Pair<>(174.0, 180.0));
    }};

    public static final Map<Character, Pair<Double, Double>> latitudeGZDZones = new HashMap<Character,  Pair<Double, Double>>() {{
        put('C', new Pair<>(-80.0, -72.0));
        put('D', new Pair<>(-72.0, -64.0));
        put('E', new Pair<>(-64.0, -56.0));
        put('F', new Pair<>(-56.0, -48.0));
        put('G', new Pair<>(-48.0, -40.0));
        put('H', new Pair<>(-40.0, -32.0));
        put('J', new Pair<>(-32.0, -24.0));
        put('K', new Pair<>(-24.0, -16.0));
        put('L', new Pair<>(-16.0, -8.0));
        put('M', new Pair<>(-8.0, 0.0));
        put('N', new Pair<>(0.0, 8.0));
        put('P', new Pair<>(8.0, 16.0));
        put('Q', new Pair<>(16.0, 24.0));
        put('R', new Pair<>(24.0, 32.0));
        put('S', new Pair<>(32.0, 40.0));
        put('T', new Pair<>(40.0, 48.0));
        put('U', new Pair<>(48.0, 56.0));
        put('V', new Pair<>(56.0, 64.0));
        put('W', new Pair<>(64.0, 72.0));
        put('X', new Pair<>(72.0, 84.0));
    }};

    public static final List<GridZoneDesignator> gridZones = new ArrayList<GridZoneDesignator>() {{
        for (Map.Entry<Character, Pair<Double, Double>> latitudeZone : latitudeGZDZones.entrySet()) {
            for (Map.Entry<Integer, Pair<Double, Double>> longitudeZone : longitudeGZDZones.entrySet()) {
                double[] bbox = new double[] {longitudeZone.getValue().first, latitudeZone.getValue().first, longitudeZone.getValue().second, latitudeZone.getValue().second};
                add(new GridZoneDesignator(latitudeZone.getKey(), longitudeZone.getKey(), bbox));
            }
        }
    }};

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
