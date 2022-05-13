package mil.nga.mgrs;

import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mil.nga.mgrs.features.Point;
import mil.nga.mgrs.utm.Hemisphere;
import mil.nga.mgrs.utm.UTM;

/**
 * Created by wnewman on 12/21/17.
 */

public class MGRS {

	/*
	 * Latitude bands C..X 8° each, covering 80°S to 84°N
	 */
	private static final String latBands = "CDEFGHJKLMNPQRSTUVWXX"; // X is
																	// repeated
																	// for
																	// 80-84°N

	/*
	 * 100km grid square column (‘e’) letters repeat every third zone
	 */
	private static final String[] e100kLetters = new String[] { "ABCDEFGH",
			"JKLMNPQR", "STUVWXYZ" };

	/*
	 * 100km grid square row (‘n’) letters repeat every other zone
	 */
	private static final String[] n100kLetters = new String[] {
			"ABCDEFGHJKLMNPQRSTUV", "FGHJKLMNPQRSTUVABCDE" };

	private static final Pattern mgrsPattern = Pattern
			.compile("^(\\d{1,2})([^ABIOYZabioyz])([A-Za-z]{2})([0-9][0-9]+$)");

	private Integer zone;
	private Character band;
	private Character e100k;
	private Character n100k;
	private Long easting;
	private Long northing;

	public MGRS(Integer zone, Character band, Character e100k, Character n100k,
			Long easting, Long northing) {
		this.zone = zone;
		this.band = band;
		this.e100k = e100k;
		this.n100k = n100k;
		this.easting = easting;
		this.northing = northing;
	}

	public Integer getZone() {
		return zone;
	}

	public Character getBand() {
		return band;
	}

	public Character getE100k() {
		return e100k;
	}

	public Character getN100k() {
		return n100k;
	}

	public Long getEasting() {
		return easting;
	}

	public Long getNorthing() {
		return northing;
	}

	public String format(int accuracy) {
		String easting = String.format(Locale.getDefault(), "%05d",
				this.easting);
		String northing = String.format(Locale.getDefault(), "%05d",
				this.northing);

		return zone.toString() + band + e100k + n100k
				+ easting.substring(0, accuracy)
				+ northing.substring(0, accuracy);
	}

	/**
	 * Return whether the given string is valid MGRS string
	 *
	 * @param mgrs
	 *            potential MGRS string.
	 * @return true if MGRS string is valid, false otherwise.
	 */
	public static boolean isMGRS(String mgrs) {
		return mgrsPattern.matcher(mgrs).matches();
	}

	/**
	 * Encodes a latitude/longitude as MGRS string.
	 *
	 * @param latLng
	 *            LatLng An object literal latitude and longitude
	 * @return MGRS mgrs.
	 */
	public static MGRS from(Point latLng) {

		latLng = latLng.toDegrees();

		UTM utm = UTM.from(latLng);

		// grid zones are 8° tall, 0°N is 10th band
		char band = latBands
				.charAt((int) Math.floor(latLng.getLatitude() / 8 + 10)); // latitude
																			// band

		// columns in zone 1 are A-H, zone 2 J-R, zone 3 S-Z, then repeating
		// every 3rd zone
		int column = (int) Math.floor(utm.getEasting() / 100000);
		Character e100k = e100kLetters[(utm.getZoneNumber() - 1) % 3]
				.charAt(column - 1); // col-1 since 1*100e3 -> A (index 0),
										// 2*100e3 -> B (index 1), etc.

		// rows in even zones are A-V, in odd zones are F-E
		int row = (int) Math.floor(utm.getNorthing() / 100000) % 20;
		Character n100k = n100kLetters[(utm.getZoneNumber() - 1) % 2]
				.charAt(row);

		// truncate easting/northing to within 100km grid square
		Long easting = Math.round(utm.getEasting() % 100000);
		Long northing = Math.round(utm.getNorthing() % 100000);

		return new MGRS(utm.getZoneNumber(), band, e100k, n100k, easting,
				northing);
	}

	public static MGRS parse(String mgrs) throws ParseException {
		Matcher matcher = mgrsPattern.matcher(mgrs);
		if (!matcher.matches()) {
			throw new ParseException("Invalid MGRS", 0);
		}

		int zone = Integer.parseInt(matcher.group(1));
		char band = matcher.group(2).toUpperCase().charAt(0);
		Character e100k = matcher.group(3).toUpperCase().charAt(0);
		Character n100k = matcher.group(3).toUpperCase().charAt(1);

		String numericLocation = matcher.group(4);
		int precision = numericLocation.length() / 2;
		String[] numericLocations = { numericLocation.substring(0, precision),
				numericLocation.substring(precision) };

		// parse easting & northing
		double multiplier = Math.pow(10.0, 5 - precision);
		Long easting = new Double(
				Double.parseDouble(numericLocations[0]) * multiplier)
						.longValue();
		Long northing = new Double(
				Double.parseDouble(numericLocations[1]) * multiplier)
						.longValue();

		return new MGRS(zone, band, e100k, n100k, easting, northing);
	}

	public UTM utm() {
		// get easting specified by e100k
		double col = e100kLetters[(zone - 1) % 3].indexOf(e100k) + 1; // index+1
																		// since
																		// A
																		// (index
																		// 0) ->
																		// 1*100e3,
																		// B
																		// (index
																		// 1) ->
																		// 2*100e3,
																		// etc.
		double e100kNum = col * 100000; // e100k in meters

		// get northing specified by n100k
		double row = n100kLetters[(zone - 1) % 2].indexOf(n100k);
		double n100kNum = row * 100000; // n100k in meters

		// get latitude of (bottom of) band
		double latBand = (latBands.indexOf(band) - 10) * 8;

		// northing of bottom of band, extended to include entirety of
		// bottommost 100km square
		// (100km square boundaries are aligned with 100km UTM northing
		// intervals)

		double nBand = Math.floor(
				UTM.from(Point.degrees(0, latBand)).getNorthing() / 100000)
				* 100000;

		// 100km grid square row letters repeat every 2,000km north; add enough
		// 2,000km blocks to get
		// into required band
		double n2M = 0; // northing of 2,000km block
		while (n2M + n100kNum + northing < nBand) {
			n2M += 2000000;
		}

		Hemisphere hemisphere = Hemisphere.fromBandLetter(band);

		return new UTM(zone, hemisphere, e100kNum + easting,
				n2M + n100kNum + northing);
	}

	/**
	 * Get the two letter 100k designator for a given UTM easting, northing and
	 * zone number value.
	 *
	 * @param easting
	 *            easting
	 * @param northing
	 *            northing
	 * @param zoneNumber
	 *            zone number
	 * @return the two letter 100k designator for the given UTM location.
	 */
	public static String get100KId(double easting, double northing,
			Integer zoneNumber) {

		// columns in zone 1 are A-H, zone 2 J-R, zone 3 S-Z, then repeating
		// every 3rd zone
		int column = (int) Math.floor(easting / 100000);
		Character e100k = e100kLetters[(zoneNumber - 1) % 3].charAt(column - 1); // col-1
																					// since
																					// 1*100e3
																					// ->
																					// A
																					// (index
																					// 0),
																					// 2*100e3
																					// ->
																					// B
																					// (index
																					// 1),
																					// etc.

		// rows in even zones are A-V, in odd zones are F-E
		int row = (int) Math.floor(northing / 100000) % 20;
		Character n100k = n100kLetters[(zoneNumber - 1) % 2].charAt(row);

		return e100k.toString() + n100k.toString();
	}
}
