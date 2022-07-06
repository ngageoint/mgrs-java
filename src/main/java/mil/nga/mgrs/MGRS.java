package mil.nga.mgrs;

import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mil.nga.grid.GridUtils;
import mil.nga.grid.Hemisphere;
import mil.nga.grid.features.Bounds;
import mil.nga.grid.features.Line;
import mil.nga.grid.features.Point;
import mil.nga.mgrs.features.GridLine;
import mil.nga.mgrs.grid.GridType;
import mil.nga.mgrs.gzd.GridZone;
import mil.nga.mgrs.gzd.GridZones;
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
	private static final Pattern mgrsPattern = Pattern.compile(
			"^(\\d{1,2})([C-HJ-NP-X])(?:([A-HJ-NP-Z][A-HJ-NP-V])((\\d{2}){0,5}))?$",
			Pattern.CASE_INSENSITIVE);

	/**
	 * MGRS invalid string pattern (Svalbard)
	 */
	private static final Pattern mgrsInvalidPattern = Pattern
			.compile("^3[246]X.*$", Pattern.CASE_INSENSITIVE);

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
		return MGRSUtils.getHemisphere(band);
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

					mgrs.append(getEastingAndNorthing(type));

				}

			}

		}

		return mgrs.toString();
	}

	/**
	 * Get the easting and northing concatenated value in the grid type
	 * precision
	 * 
	 * @param type
	 *            grid type precision
	 * @return easting and northing value
	 */
	public String getEastingAndNorthing(GridType type) {

		int accuracy = 5 - (int) Math.log10(type.getPrecision());

		String easting = String.format(Locale.getDefault(), "%05d",
				this.easting);
		String northing = String.format(Locale.getDefault(), "%05d",
				this.northing);

		return easting.substring(0, accuracy) + northing.substring(0, accuracy);
	}

	/**
	 * Get the MGRS coordinate with the accuracy number of digits in the easting
	 * and northing values. Accuracy must be inclusively between 0
	 * ({@link GridType#HUNDRED_KILOMETER}) and 5 ({@link GridType#METER}).
	 * 
	 * @param accuracy
	 *            accuracy digits between 0 (inclusive) and 5 (inclusive)
	 * @return MGRS coordinate
	 */
	public String coordinate(int accuracy) {
		return coordinate(GridType.withAccuracy(accuracy));
	}

	/**
	 * Get the MGRS coordinate grid precision
	 * 
	 * @return grid type precision
	 */
	public GridType precision() {
		return GridType.withAccuracy(accuracy());
	}

	/**
	 * Get the MGRS coordinate accuracy number of digits
	 * 
	 * @return accuracy digits
	 */
	public int accuracy() {

		int accuracy = 5;

		for (int accuracyLevel = 10; accuracyLevel <= 100000; accuracyLevel *= 10) {
			if (easting % accuracyLevel != 0 || northing % accuracyLevel != 0) {
				break;
			}
			accuracy--;
		}

		return accuracy;
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
	 * Get the GZD grid zone
	 * 
	 * @return GZD grid zone
	 */
	public GridZone getGridZone() {
		return GridZones.getGridZone(this);
	}

	/**
	 * Convert to a point
	 * 
	 * @return point
	 */
	public Point toPoint() {
		return toUTM().toPoint();
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

		double latBandNorthing = UTM.from(Point.degrees(0, latBand))
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
		mgrs = removeSpaces(mgrs);
		return mgrsPattern.matcher(mgrs).matches()
				&& !mgrsInvalidPattern.matcher(mgrs).matches();
	}

	/**
	 * Removed spaces from the value
	 * 
	 * @param value
	 *            value string
	 * @return value without spaces
	 */
	private static String removeSpaces(String value) {
		return value.replaceAll("\\s", "");
	}

	/**
	 * Encodes a point as a MGRS string
	 *
	 * @param point
	 *            point
	 * @return MGRS
	 */
	public static MGRS from(Point point) {

		point = point.toDegrees();

		UTM utm = UTM.from(point);

		char bandLetter = GridZones.getBandLetter(point.getLatitude());

		char columnLetter = getColumnLetter(utm);

		char rowLetter = getRowLetter(utm);

		// truncate easting/northing to within 100km grid square
		long easting = (long) (utm.getEasting() % 100000);
		long northing = (long) (utm.getNorthing() % 100000);

		return MGRS.create(utm.getZone(), bandLetter, columnLetter, rowLetter,
				easting, northing);
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
		Matcher matcher = mgrsPattern.matcher(removeSpaces(mgrs));
		if (!matcher.matches()) {
			throw new ParseException("Invalid MGRS: " + mgrs, 0);
		}

		int zone = Integer.parseInt(matcher.group(1));
		char band = matcher.group(2).toUpperCase().charAt(0);

		GridZone gridZone = GridZones.getGridZone(zone, band);
		if (gridZone == null) {
			throw new ParseException("Invalid MGRS: " + mgrs, 0);
		}

		MGRS mgrsValue = null;

		String columnRow = matcher.group(3);
		if (columnRow != null) {

			columnRow = columnRow.toUpperCase();
			char column = columnRow.charAt(0);
			char row = columnRow.charAt(1);

			// parse easting & northing
			long easting = 0;
			long northing = 0;
			String location = matcher.group(4);
			if (!location.isEmpty()) {
				int precision = location.length() / 2;
				double multiplier = Math.pow(10.0, 5 - precision);
				easting = (long) (Double.parseDouble(
						location.substring(0, precision)) * multiplier);
				northing = (long) (Double.parseDouble(
						location.substring(precision)) * multiplier);
			}

			mgrsValue = MGRS.create(zone, band, column, row, easting, northing);

			if (location.isEmpty()) {

				Point point = mgrsValue.toPoint().toDegrees();
				Bounds gridBounds = gridZone.getBounds();
				Point gridSouthwest = gridBounds.getSouthwest().toDegrees();

				boolean westBounds = point.getLongitude() < gridSouthwest
						.getLongitude();
				boolean southBounds = point.getLatitude() < gridSouthwest
						.getLatitude();

				if (westBounds || southBounds) {

					if (westBounds && southBounds) {
						Point northeast = MGRS.create(zone, band, column, row,
								GridType.HUNDRED_KILOMETER.getPrecision(),
								GridType.HUNDRED_KILOMETER.getPrecision())
								.toPoint();
						if (gridBounds.contains(northeast)) {
							mgrsValue = from(
									Point.degrees(gridSouthwest.getLongitude(),
											gridSouthwest.getLatitude()));
						}
					} else if (westBounds) {
						Point east = MGRS
								.create(zone, band, column, row,
										GridType.HUNDRED_KILOMETER
												.getPrecision(),
										northing)
								.toPoint();
						if (gridBounds.contains(east)) {
							Point intersection = getWesternBoundsPoint(gridZone,
									point, east);
							mgrsValue = from(intersection);
						}
					} else if (southBounds) {
						Point north = MGRS.create(zone, band, column, row,
								easting,
								GridType.HUNDRED_KILOMETER.getPrecision())
								.toPoint();
						if (gridBounds.contains(north)) {
							Point intersection = getSouthernBoundsPoint(
									gridZone, point, north);
							mgrsValue = from(intersection);
						}
					}

				}

			}

		} else {
			mgrsValue = from(gridZone.getBounds().getSouthwest());
		}

		return mgrsValue;
	}

	/**
	 * Get the point on the western grid zone bounds point between the western
	 * and eastern points
	 * 
	 * @param gridZone
	 *            grid zone
	 * @param west
	 *            western point
	 * @param east
	 *            eastern point
	 * @return western grid bounds point
	 */
	private static Point getWesternBoundsPoint(GridZone gridZone, Point west,
			Point east) {

		UTM eastUTM = UTM.from(east);
		double northing = eastUTM.getNorthing();

		int zoneNumber = gridZone.getNumber();
		Hemisphere hemisphere = gridZone.getHemisphere();

		Line line = GridLine.line(west, east);
		Line boundsLine = gridZone.getBounds().getWestLine();

		Point intersection = GridUtils.intersection(line, boundsLine);

		// Intersection easting
		UTM intersectionUTM = UTM.from(intersection, zoneNumber, hemisphere);
		double intersectionEasting = intersectionUTM.getEasting();

		// One meter precision just inside the bounds
		double boundsEasting = Math.ceil(intersectionEasting);

		// Higher precision point just inside of the bounds
		Point boundsPoint = UTM.point(zoneNumber, hemisphere, boundsEasting,
				northing);

		boundsPoint.setLongitude(boundsLine.getPoint1().getLongitude());

		return boundsPoint;
	}

	/**
	 * Get the point on the southern grid zone bounds point between the southern
	 * and northern points
	 * 
	 * @param gridZone
	 *            grid zone
	 * @param south
	 *            southern point
	 * @param north
	 *            northern point
	 * @return southern grid bounds point
	 */
	private static Point getSouthernBoundsPoint(GridZone gridZone, Point south,
			Point north) {

		UTM northUTM = UTM.from(north);
		double easting = northUTM.getEasting();

		int zoneNumber = gridZone.getNumber();
		Hemisphere hemisphere = gridZone.getHemisphere();

		Line line = GridLine.line(south, north);
		Line boundsLine = gridZone.getBounds().getSouthLine();

		Point intersection = GridUtils.intersection(line, boundsLine);

		// Intersection northing
		UTM intersectionUTM = UTM.from(intersection, zoneNumber, hemisphere);
		double intersectionNorthing = intersectionUTM.getNorthing();

		// One meter precision just inside the bounds
		double boundsNorthing = Math.ceil(intersectionNorthing);

		// Higher precision point just inside of the bounds
		Point boundsPoint = UTM.point(zoneNumber, hemisphere, easting,
				boundsNorthing);

		boundsPoint.setLatitude(boundsLine.getPoint1().getLatitude());

		return boundsPoint;
	}

	/**
	 * Parse the MGRS string for the precision
	 * 
	 * @param mgrs
	 *            MGRS string
	 * @return grid type precision
	 * @throws ParseException
	 *             upon failure to parse the MGRS string
	 */
	public static GridType precision(String mgrs) throws ParseException {
		Matcher matcher = mgrsPattern.matcher(removeSpaces(mgrs));
		if (!matcher.matches()) {
			throw new ParseException("Invalid MGRS: " + mgrs, 0);
		}

		GridType precision = null;

		if (matcher.group(3) != null) {

			String location = matcher.group(4);
			if (!location.isEmpty()) {
				precision = GridType.withAccuracy(location.length() / 2);
			} else {
				precision = GridType.HUNDRED_KILOMETER;
			}

		} else {
			precision = GridType.GZD;
		}

		return precision;
	}

	/**
	 * Get the MGRS coordinate accuracy number of digits
	 * 
	 * @param mgrs
	 *            MGRS string
	 * @return accuracy digits
	 * @throws ParseException
	 *             upon failure to parse the MGRS string
	 */
	public static int accuracy(String mgrs) throws ParseException {
		return precision(mgrs).getAccuracy();
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
		return getColumnLetter(utm.getZone(), utm.getEasting());
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
		return getRowLetter(utm.getZone(), utm.getNorthing());
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
