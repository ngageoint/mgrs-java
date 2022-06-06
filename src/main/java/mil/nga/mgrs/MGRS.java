package mil.nga.mgrs;

import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mil.nga.mgrs.features.Point;
import mil.nga.mgrs.grid.GridType;
import mil.nga.mgrs.gzd.GridZones;
import mil.nga.mgrs.utm.Hemisphere;
import mil.nga.mgrs.utm.UTM;

/**
 * Military Grid Reference System Coordinate
 * 
 * @author wnewman
 * @author osbornb
 */
public class MGRS {

	/**
	 * 100km grid square column (‘e’) letters repeat every third zone
	 */
	private static final String[] columnLetters = new String[] { "ABCDEFGH",
			"JKLMNPQR", "STUVWXYZ" };

	/**
	 * 100km grid square row (‘n’) letters repeat every other zone
	 */
	private static final String[] rowLetters = new String[] {
			"ABCDEFGHJKLMNPQRSTUV", "FGHJKLMNPQRSTUVABCDE" };

	/**
	 * MGRS string pattern
	 */
	private static final Pattern mgrsPattern = Pattern
			.compile("^(\\d{1,2})([^ABIOYZabioyz])([A-Za-z]{2})([0-9][0-9]+$)");

	/**
	 * Zone number
	 */
	private int zone;

	/**
	 * Band letter
	 */
	private char band;

	/**
	 * Column letter
	 */
	private char column;

	/**
	 * Row letter
	 */
	private char row;

	/**
	 * Easting
	 */
	private long easting;

	/**
	 * Northing
	 */
	private long northing;

	/**
	 * Create
	 * 
	 * @param zone
	 *            zone number
	 * @param band
	 *            band letter
	 * @param column
	 *            column letter
	 * @param row
	 *            row letter
	 * @param easting
	 *            easting
	 * @param northing
	 *            northing
	 * @return MGRS
	 */
	public static MGRS create(int zone, char band, char column, char row,
			long easting, long northing) {
		return new MGRS(zone, band, column, row, easting, northing);
	}

	/**
	 * Create
	 * 
	 * @param zone
	 *            zone number
	 * @param band
	 *            band letter
	 * @param easting
	 *            easting
	 * @param northing
	 *            northing
	 * @return MGRS
	 */
	public static MGRS create(int zone, char band, long easting,
			long northing) {
		return new MGRS(zone, band, easting, northing);
	}

	/**
	 * Constructor
	 * 
	 * @param zone
	 *            zone number
	 * @param band
	 *            band letter
	 * @param column
	 *            column letter
	 * @param row
	 *            row letter
	 * @param easting
	 *            easting
	 * @param northing
	 *            northing
	 */
	public MGRS(int zone, char band, char column, char row, long easting,
			long northing) {
		this.zone = zone;
		this.band = band;
		this.column = column;
		this.row = row;
		this.easting = easting;
		this.northing = northing;
	}

	/**
	 * Constructor
	 * 
	 * @param zone
	 *            zone number
	 * @param band
	 *            band letter
	 * @param easting
	 *            easting
	 * @param northing
	 *            northing
	 */
	public MGRS(int zone, char band, long easting, long northing) {
		this(zone, band, getColumnLetter(zone, easting),
				getRowLetter(zone, northing), easting, northing);
	}

	/**
	 * Get the zone number
	 * 
	 * @return zone number
	 */
	public int getZone() {
		return zone;
	}

	/**
	 * Get the band letter
	 * 
	 * @return band letter
	 */
	public char getBand() {
		return band;
	}

	/**
	 * Get the column letter
	 * 
	 * @return column letter
	 */
	public char getColumn() {
		return column;
	}

	/**
	 * Get the row letter
	 * 
	 * @return row letter
	 */
	public char getRow() {
		return row;
	}

	/**
	 * Get the easting
	 * 
	 * @return easting
	 */
	public long getEasting() {
		return easting;
	}

	/**
	 * Get the northing
	 * 
	 * @return northing
	 */
	public long getNorthing() {
		return northing;
	}

	/**
	 * Get the hemisphere
	 * 
	 * @return hemisphere
	 */
	public Hemisphere getHemisphere() {
		return Hemisphere.fromBandLetter(band);
	}

	/**
	 * Get the MGRS coordinate with one meter precision
	 * 
	 * @return MGRS coordinate
	 */
	public String coordinate() {
		return coordinate(GridType.METER);
	}

	/**
	 * Get the MGRS coordinate with specified grid precision
	 * 
	 * @param type
	 *            grid type precision
	 * @return MGRS coordinate
	 */
	public String coordinate(GridType type) {

		StringBuilder mgrs = new StringBuilder();

		if (type != null) {

			mgrs.append(zone);
			mgrs.append(band);

			if (type != GridType.GZD) {

				mgrs.append(column);
				mgrs.append(row);

				if (type != GridType.HUNDRED_KILOMETER) {

					int accuracy = 5 - (int) Math.log10(type.getPrecision());

					String easting = String.format(Locale.getDefault(), "%05d",
							this.easting);
					String northing = String.format(Locale.getDefault(), "%05d",
							this.northing);

					mgrs.append(easting.substring(0, accuracy));
					mgrs.append(northing.substring(0, accuracy));
				}

			}

		}

		return mgrs.toString();
	}

	/**
	 * Get the two letter column and row 100k designator
	 *
	 * @return the two letter column and row 100k designator
	 */
	public String getColumnRowId() {
		return String.valueOf(column) + row;
	}

	/**
	 * Convert to a point
	 * 
	 * @return point
	 */
	public Point toPoint() {
		return Point.from(this);
	}

	/**
	 * Convert to UTM coordinate
	 * 
	 * @return UTM
	 */
	public UTM toUTM() {

		double easting = getUTMEasting();
		double northing = getUTMNorthing();
		Hemisphere hemisphere = getHemisphere();

		return UTM.create(zone, hemisphere, easting, northing);
	}

	/**
	 * Get the UTM easting
	 * 
	 * @return UTM easting
	 */
	public double getUTMEasting() {

		// get easting specified by e100k
		String columnLetters = getColumnLetters(zone);
		int columnIndex = columnLetters.indexOf(column) + 1;
		// index+1 since A (index 0) -> 1*100e3, B (index 1) -> 2*100e3, etc.
		double e100kNum = columnIndex * 100000.0; // e100k in meters

		return e100kNum + easting;
	}

	/**
	 * Get the UTM northing
	 * 
	 * @return UTM northing
	 */
	public double getUTMNorthing() {

		// get northing specified by n100k
		String rowLetters = getRowLetters(zone);
		int rowIndex = rowLetters.indexOf(row);
		double n100kNum = rowIndex * 100000.0; // n100k in meters

		// get latitude of (bottom of) band
		double latBand = GridZones.getSouthLatitude(band);

		// northing of bottom of band, extended to include entirety of
		// bottommost 100km square
		// (100km square boundaries are aligned with 100km UTM northing
		// intervals)

		double latBandNorthing = Point.degrees(0, latBand).toUTM()
				.getNorthing();
		double nBand = Math.floor(latBandNorthing / 100000) * 100000;

		// 100km grid square row letters repeat every 2,000km north; add enough
		// 2,000km blocks to get
		// into required band
		double n2M = 0; // northing of 2,000km block
		while (n2M + n100kNum + northing < nBand) {
			n2M += 2000000;
		}

		return n2M + n100kNum + northing;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return coordinate();
	}

	/**
	 * Return whether the given string is valid MGRS string
	 *
	 * @param mgrs
	 *            potential MGRS string
	 * @return true if MGRS string is valid, false otherwise
	 */
	public static boolean isMGRS(String mgrs) {
		return mgrsPattern.matcher(mgrs).matches();
	}

	/**
	 * Encodes a point as a MGRS string
	 *
	 * @param latLng
	 *            LatLng An object literal latitude and longitude
	 * @return MGRS
	 */
	public static MGRS from(Point latLng) {

		latLng = latLng.toDegrees();

		UTM utm = latLng.toUTM();

		char bandLetter = latLng.getBandLetter();

		char columnLetter = getColumnLetter(utm);

		char rowLetter = getRowLetter(utm);

		// truncate easting/northing to within 100km grid square
		long easting = Math.round(utm.getEasting() % 100000);
		long northing = Math.round(utm.getNorthing() % 100000);

		return MGRS.create(utm.getZoneNumber(), bandLetter, columnLetter,
				rowLetter, easting, northing);
	}

	/**
	 * Parse a MGRS string
	 * 
	 * @param mgrs
	 *            MGRS string
	 * @return MGRS
	 * @throws ParseException
	 *             upon failure to parse the MGRS string
	 */
	public static MGRS parse(String mgrs) throws ParseException {
		Matcher matcher = mgrsPattern.matcher(mgrs.replaceAll("\\s", ""));
		if (!matcher.matches()) {
			throw new ParseException("Invalid MGRS: " + mgrs, 0);
		}

		int zone = Integer.parseInt(matcher.group(1));
		char band = matcher.group(2).toUpperCase().charAt(0);
		char column = matcher.group(3).toUpperCase().charAt(0);
		char row = matcher.group(3).toUpperCase().charAt(1);

		String numericLocation = matcher.group(4);
		int precision = numericLocation.length() / 2;
		String[] numericLocations = { numericLocation.substring(0, precision),
				numericLocation.substring(precision) };

		// parse easting & northing
		double multiplier = Math.pow(10.0, 5 - precision);
		long easting = (long) (Double.parseDouble(numericLocations[0])
				* multiplier);
		long northing = (long) (Double.parseDouble(numericLocations[1])
				* multiplier);

		return MGRS.create(zone, band, column, row, easting, northing);
	}

	/**
	 * Get the two letter column and row 100k designator for a given UTM
	 * easting, northing and zone number value
	 *
	 * @param easting
	 *            easting
	 * @param northing
	 *            northing
	 * @param zoneNumber
	 *            zone number
	 * @return the two letter column and row 100k designator
	 */
	public static String getColumnRowId(double easting, double northing,
			int zoneNumber) {

		char columnLetter = getColumnLetter(zoneNumber, easting);

		char rowLetter = getRowLetter(zoneNumber, northing);

		return String.valueOf(columnLetter) + rowLetter;
	}

	/**
	 * Get the column letter from the UTM
	 * 
	 * @param utm
	 *            UTM
	 * @return column letter
	 */
	public static char getColumnLetter(UTM utm) {
		return getColumnLetter(utm.getZoneNumber(), utm.getEasting());
	}

	/**
	 * Get the column letter from the zone number and easting
	 * 
	 * @param zoneNumber
	 *            zone number
	 * @param easting
	 *            easting
	 * @return column letter
	 */
	public static char getColumnLetter(int zoneNumber, double easting) {
		// columns in zone 1 are A-H, zone 2 J-R, zone 3 S-Z, then repeating
		// every 3rd zone
		int column = (int) Math.floor(easting / 100000);
		String columnLetters = getColumnLetters(zoneNumber);
		return columnLetters.charAt(column - 1);
	}

	/**
	 * Get the row letter from the UTM
	 * 
	 * @param utm
	 *            UTM
	 * @return row letter
	 */
	public static char getRowLetter(UTM utm) {
		return getRowLetter(utm.getZoneNumber(), utm.getNorthing());
	}

	/**
	 * Get the row letter from the zone number and northing
	 * 
	 * @param zoneNumber
	 *            zone number
	 * @param northing
	 *            northing
	 * @return row letter
	 */
	public static char getRowLetter(int zoneNumber, double northing) {
		// rows in even zones are A-V, in odd zones are F-E
		int row = (int) Math.floor(northing / 100000) % 20;
		String rowLetters = getRowLetters(zoneNumber);
		return rowLetters.charAt(row);
	}

	/**
	 * Get the column letters for the zone number
	 * 
	 * @param zoneNumber
	 *            zone number
	 * @return column letters
	 */
	private static String getColumnLetters(int zoneNumber) {
		return columnLetters[(zoneNumber - 1) % 3];
	}

	/**
	 * Get the row letters for the zone number
	 * 
	 * @param zoneNumber
	 *            zone number
	 * @return row letters
	 */
	private static String getRowLetters(int zoneNumber) {
		return rowLetters[(zoneNumber - 1) % 2];
	}

}
