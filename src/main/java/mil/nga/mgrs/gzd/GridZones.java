package mil.nga.mgrs.gzd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.nga.mgrs.MGRSConstants;
import mil.nga.mgrs.MGRSUtils;
import mil.nga.mgrs.features.Bounds;

/**
 * Grid Zones, Longitudinal Strips, and Latitude Bands
 * 
 * @author wnewman
 * @author osbornb
 */
public class GridZones {

	/**
	 * Longitudinal Strips
	 */
	public static final Map<Integer, LongitudinalStrip> strips = new HashMap<>();

	/**
	 * Latitude Bands
	 */
	public static final Map<Character, LatitudeBand> bands = new HashMap<>();

	/**
	 * Grid Zones
	 */
	public static final Map<Integer, Map<Character, GridZone>> gridZones = new HashMap<>();

	static {

		// Create longitudinal strips
		ZoneNumberRange numberRange = new ZoneNumberRange();
		for (int zoneNumber : numberRange) {
			double longitude = MGRSConstants.MIN_LON
					+ ((zoneNumber - 1) * MGRSConstants.ZONE_WIDTH);
			LongitudinalStrip strip = new LongitudinalStrip(zoneNumber,
					longitude, longitude + MGRSConstants.ZONE_WIDTH);
			strips.put(strip.getNumber(), strip);
		}

		// Create latitude bands
		double latitude = MGRSConstants.MIN_LAT;
		BandLetterRange letterRange = new BandLetterRange();
		for (char bandLetter : letterRange) {
			double min = latitude;
			if (bandLetter == MGRSConstants.MAX_BAND_LETTER) {
				latitude += MGRSConstants.MAX_BAND_HEIGHT;
			} else {
				latitude += MGRSConstants.BAND_HEIGHT;
			}
			bands.put(bandLetter, new LatitudeBand(bandLetter, min, latitude));
		}

		// Create grid zones
		for (LongitudinalStrip strip : strips.values()) {

			int zoneNumber = strip.getNumber();

			Map<Character, GridZone> stripGridZones = new HashMap<>();
			for (LatitudeBand band : bands.values()) {

				char bandLetter = band.getLetter();

				LongitudinalStrip gridZoneStrip = strip;

				if (isSvalbard(zoneNumber, bandLetter)) {
					gridZoneStrip = getSvalbardStrip(strip);
				} else if (isNorway(zoneNumber, bandLetter)) {
					gridZoneStrip = getNorwayStrip(strip);
				}

				if (gridZoneStrip != null) {
					stripGridZones.put(bandLetter,
							new GridZone(gridZoneStrip, band));
				}

			}
			gridZones.put(zoneNumber, stripGridZones);
		}

	}

	/**
	 * Get the longitudinal strip by zone number
	 * 
	 * @param zoneNumber
	 *            zone number
	 * @return longitudinal strip
	 */
	public static LongitudinalStrip getLongitudinalStrip(int zoneNumber) {
		MGRSUtils.validateZoneNumber(zoneNumber);
		return strips.get(zoneNumber);
	}

	/**
	 * Get the west longitude in degrees of the zone number
	 * 
	 * @param zoneNumber
	 *            zone number
	 * @return longitude in degrees
	 */
	public static double getWestLongitude(int zoneNumber) {
		return getLongitudinalStrip(zoneNumber).getWest();
	}

	/**
	 * Get the east longitude in degrees of the zone number
	 * 
	 * @param zoneNumber
	 *            zone number
	 * @return longitude in degrees
	 */
	public static double getEastLongitude(int zoneNumber) {
		return getLongitudinalStrip(zoneNumber).getEast();
	}

	/**
	 * Get the latitude band by band letter
	 * 
	 * @param bandLetter
	 *            band letter
	 * @return latitude band
	 */
	public static LatitudeBand getLatitudeBand(char bandLetter) {
		MGRSUtils.validateBandLetter(bandLetter);
		return bands.get(bandLetter);
	}

	/**
	 * Get the south latitude in degrees of the band letter
	 * 
	 * @param bandLetter
	 *            band letter
	 * @return latitude in degrees
	 */
	public static double getSouthLatitude(char bandLetter) {
		return getLatitudeBand(bandLetter).getSouth();
	}

	/**
	 * Get the north latitude in degrees of the band letter
	 * 
	 * @param bandLetter
	 *            band letter
	 * @return latitude in degrees
	 */
	public static double getNorthLatitude(char bandLetter) {
		return getLatitudeBand(bandLetter).getNorth();
	}

	/**
	 * Get the zones within the bounds
	 * 
	 * @param bounds
	 *            bounds
	 * @return grid zones
	 */
	public static List<GridZone> getZones(Bounds bounds) {

		List<GridZone> zones = new ArrayList<>();

		GridRange gridRange = getGridRange(bounds);
		for (GridZone zone : gridRange) {
			zones.add(zone);
		}

		return zones;
	}

	/**
	 * Get the grid zone by zone number and band letter
	 * 
	 * @param zoneNumber
	 *            zone number
	 * @param bandLetter
	 *            band letter
	 * @return grid zone
	 */
	public static GridZone getGridZone(int zoneNumber, char bandLetter) {
		MGRSUtils.validateZoneNumber(zoneNumber);
		MGRSUtils.validateBandLetter(bandLetter);
		return gridZones.get(zoneNumber).get(bandLetter);
	}

	/**
	 * Get a grid range from the bounds
	 * 
	 * @param bounds
	 *            bounds
	 * @return grid range
	 */
	public static GridRange getGridRange(Bounds bounds) {
		bounds = bounds.toDegrees();
		ZoneNumberRange zoneNumberRange = getZoneNumberRange(bounds);
		BandLetterRange bandLetterRange = getBandLetterRange(bounds);
		return new GridRange(zoneNumberRange, bandLetterRange);
	}

	/**
	 * Get a zone number range between the western and eastern bounds
	 * 
	 * @param bounds
	 *            bounds
	 * @return zone number range
	 */
	public static ZoneNumberRange getZoneNumberRange(Bounds bounds) {
		bounds = bounds.toDegrees();
		return getZoneNumberRange(bounds.getWest(), bounds.getEast());
	}

	/**
	 * Get a zone number range between the western and eastern longitudes
	 * 
	 * @param west
	 *            western longitude in degrees
	 * @param east
	 *            eastern longitude in degrees
	 * @return zone number range
	 */
	public static ZoneNumberRange getZoneNumberRange(double west, double east) {
		int westZone = getZoneNumber(west, false);
		int eastZone = getZoneNumber(east, true);
		return new ZoneNumberRange(westZone, eastZone);
	}

	/**
	 * Get the zone number of the longitude (degrees between
	 * {@link MGRSConstants#MIN_LON} and {@link MGRSConstants#MAX_LON}). Eastern
	 * zone number on borders.
	 * 
	 * @param longitude
	 *            longitude in degrees
	 * @return zone number
	 */
	public static int getZoneNumber(double longitude) {
		return getZoneNumber(longitude, true);
	}

	/**
	 * Get the zone number of the longitude (degrees between
	 * {@link MGRSConstants#MIN_LON} and {@link MGRSConstants#MAX_LON})
	 * 
	 * @param longitude
	 *            longitude in degrees
	 * @param eastern
	 *            true for eastern number on edges, false for western
	 * @return zone number
	 */
	public static int getZoneNumber(double longitude, boolean eastern) {

		// Normalize the longitude if needed
		if (longitude < MGRSConstants.MIN_LON
				|| longitude > MGRSConstants.MAX_LON) {
			longitude = (longitude - MGRSConstants.MIN_LON)
					% (2 * MGRSConstants.MAX_LON) + MGRSConstants.MIN_LON;
		}

		// Determine the zone
		double zoneValue = (longitude - MGRSConstants.MIN_LON)
				/ MGRSConstants.ZONE_WIDTH;
		int zoneNumber = 1 + (int) zoneValue;

		// Handle western edge cases and 180.0
		if (!eastern) {
			if (zoneNumber > 1 && zoneValue % 1.0 == 0.0) {
				zoneNumber--;
			}
		} else if (zoneNumber > MGRSConstants.MAX_ZONE_NUMBER) {
			zoneNumber--;
		}

		return zoneNumber;
	}

	/**
	 * Get a band letter range between the southern and northern bounds
	 * 
	 * @param bounds
	 *            bounds
	 * @return band letter range
	 */
	public static BandLetterRange getBandLetterRange(Bounds bounds) {
		bounds = bounds.toDegrees();
		return getBandLetterRange(bounds.getSouth(), bounds.getNorth());
	}

	/**
	 * Get a band letter range between the southern and northern latitudes in
	 * degrees
	 * 
	 * @param south
	 *            southern latitude in degrees
	 * @param north
	 *            northern latitude in degrees
	 * @return band letter range
	 */
	public static BandLetterRange getBandLetterRange(double south,
			double north) {
		char southLetter = getBandLetter(south, false);
		char northLetter = getBandLetter(north, true);
		return new BandLetterRange(southLetter, northLetter);
	}

	/**
	 * Get the band letter of the latitude (degrees between
	 * {@link MGRSConstants#MIN_LAT} and {@link MGRSConstants#MAX_LAT}).
	 * Northern band letter on borders.
	 * 
	 * @param latitude
	 *            latitude in degrees
	 * @return band letter
	 */
	public static char getBandLetter(double latitude) {
		return getBandLetter(latitude, true);
	}

	/**
	 * Get the band letter of the latitude (degrees between
	 * {@link MGRSConstants#MIN_LAT} and {@link MGRSConstants#MAX_LAT})
	 * 
	 * @param latitude
	 *            latitude in degrees
	 * @param northern
	 *            true for northern band on edges, false for southern
	 * @return band letter
	 */
	public static char getBandLetter(double latitude, boolean northern) {

		// Bound the latitude if needed
		if (latitude < MGRSConstants.MIN_LAT) {
			latitude = MGRSConstants.MIN_LAT;
		} else if (latitude > MGRSConstants.MAX_LAT) {
			latitude = MGRSConstants.MAX_LAT;
		}

		double bandValue = (latitude - MGRSConstants.MIN_LAT)
				/ MGRSConstants.BAND_HEIGHT;
		int bands = (int) bandValue;

		// Handle 80.0 to 84.0 and southern edge cases
		if (bands >= MGRSConstants.NUM_BANDS) {
			bands--;
		} else if (!northern && bands > 0 && bandValue % 1.0 == 0.0) {
			bands--;
		}

		// Handle skipped 'I' and 'O' letters
		if (bands > 10) {
			bands += 2;
		} else if (bands > 5) {
			bands++;
		}

		char letter = MGRSConstants.MIN_BAND_LETTER;
		letter += bands;
		return letter;
	}

	/**
	 * Is the zone number and band letter a Svalbard GZD (31X - 37X)
	 * 
	 * @param zoneNumber
	 *            zone number
	 * @param bandLetter
	 *            band letter
	 * @return true if a Svalbard GZD
	 */
	public static boolean isSvalbard(int zoneNumber, char bandLetter) {
		return bandLetter == MGRSConstants.SVALBARD_BAND_LETTER
				&& zoneNumber >= MGRSConstants.MIN_SVALBARD_ZONE_NUMBER
				&& zoneNumber <= MGRSConstants.MAX_SVALBARD_ZONE_NUMBER;
	}

	/**
	 * Get the Svalbard longitudinal strip from the strip
	 * 
	 * @param strip
	 *            longitudinal strip
	 * @return Svalbard strip or null for empty strips
	 */
	private static LongitudinalStrip getSvalbardStrip(LongitudinalStrip strip) {

		LongitudinalStrip svalbardStrip = null;

		int number = strip.getNumber();
		if (number % 2 == 1) {
			double west = strip.getWest();
			double east = strip.getEast();
			double halfWidth = (east - west) / 2.0;
			if (number > 31) {
				west -= halfWidth;
			}
			if (number < 37) {
				east += halfWidth;
			}
			svalbardStrip = new LongitudinalStrip(number, west, east);
		}

		return svalbardStrip;
	}

	/**
	 * Is the zone number and band letter a Norway GZD (31V or 32V)
	 * 
	 * @param zoneNumber
	 *            zone number
	 * @param bandLetter
	 *            band letter
	 * @return true if a Norway GZD
	 */
	private static boolean isNorway(int zoneNumber, char bandLetter) {
		return bandLetter == MGRSConstants.NORWAY_BAND_LETTER
				&& zoneNumber >= MGRSConstants.MIN_NORWAY_ZONE_NUMBER
				&& zoneNumber <= MGRSConstants.MAX_NORWAY_ZONE_NUMBER;
	}

	/**
	 * Get the Norway longitudinal strip from the strip
	 * 
	 * @param strip
	 *            longitudinal strip
	 * @return Norway strip
	 */
	private static LongitudinalStrip getNorwayStrip(LongitudinalStrip strip) {

		int number = strip.getNumber();
		double west = strip.getWest();
		double east = strip.getEast();
		double halfWidth = (east - west) / 2.0;

		int expand = 0;
		if (number == MGRSConstants.MIN_NORWAY_ZONE_NUMBER) {
			east -= halfWidth;
			expand++;
		} else if (number == MGRSConstants.MAX_NORWAY_ZONE_NUMBER) {
			west -= halfWidth;
		}

		return new LongitudinalStrip(number, west, east, expand);
	}

}
