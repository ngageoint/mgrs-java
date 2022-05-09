package mil.nga.mgrs;

import mil.nga.mgrs.wgs84.LatLng;

/**
 * Created by wnewman on 11/21/16.
 */
public class GeoUtility {

    private static final int NUM_100K_SETS = 6;

    /**
     * The column letters (for easting) of the lower left value, per
     * set.
     */
    private static final String SET_ORIGIN_COLUMN_LETTERS = "AJSAJS";

    /**
     * The row letters (for northing) of the lower left value, per
     * set.
     */
    private static final String SET_ORIGIN_ROW_LETTERS = "AFAFAF";

    private static final int A = 65; // A
    private static final int I = 73; // I
    private static final int O = 79; // O
    private static final int V = 86; // V
    private static final int Z = 90; // Z

    private static double a = 6378137.0; // WGS-84 geoidal semi-major axis of earth in meters

    private static double EQUATORIAL_RADIUS = 6378137.0;
    private static double ECC_SQUARED = 0.006694380023;
    private static double ECC_PRIME_SQUARED = ECC_SQUARED / (1 - ECC_SQUARED);
    private static double EASTING_OFFSET  = 500000.0;   // (meters)

    // scale factor of central meridian
    private static double k0 = 0.9996;

    private static double e = 8.1819190842622e-2;  // eccentricity
    private static double asq = a * a;
    private static double esq = e * e;

    private static double b = Math.sqrt(asq * (1 - esq)); // WGS-84 geoidal semi-minor axis of earth in meters

    private static double degreeToRadian(double degrees) {
        return Math.PI * degrees / 180.0;
    }

    private static double radianToDegree(double radians)  {
        return 180.0 * radians / Math.PI;
    }

    public static LatLng utmToLatLng(int zone, char letter, double easting, double northing) {
        double latitude;
        double longitude;

        double north = northing;
        if (letter < 'N') {
            // Remove 10,000,000 meter offset used for southern hemisphere
            north -= 10000000.0;
        }

        latitude = (north/6366197.724/0.9996+(1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)-0.006739496742*Math.sin(north/6366197.724/0.9996)*Math.cos(north/6366197.724/0.9996)*(Math.atan(Math.cos(Math.atan(( Math.exp((easting - 500000) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting - 500000) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3))-Math.exp(-(easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*( 1 -  0.006739496742*Math.pow((easting - 500000) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3)))/2/Math.cos((north-0.9996*6399593.625*(north/6366197.724/0.9996-0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996 )/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996)))*Math.tan((north-0.9996*6399593.625*(north/6366197.724/0.9996 - 0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996 )*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996))-north/6366197.724/0.9996)*3/2)*(Math.atan(Math.cos(Math.atan((Math.exp((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3))-Math.exp(-(easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3)))/2/Math.cos((north-0.9996*6399593.625*(north/6366197.724/0.9996-0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996)))*Math.tan((north-0.9996*6399593.625*(north/6366197.724/0.9996-0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996))-north/6366197.724/0.9996))*180/Math.PI;
        latitude = Math.round(latitude * 10000000);
        latitude = latitude/10000000;

        longitude = Math.atan((Math.exp((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3))-Math.exp(-(easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3)))/2/Math.cos((north-0.9996*6399593.625*( north/6366197.724/0.9996-0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2* north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3)) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996))*180/Math.PI+zone*6-183;
        longitude = Math.round(longitude * 10000000);
        longitude = longitude/10000000;

        return new LatLng(latitude, longitude);
    }

    public static UTM latLngToUtm(double latitude, double longitude) {
        int zone = (int) Math.floor(longitude / 6 + 31);
        return latLngToUtm(latitude, longitude, zone);
    }

    public static UTM LLtoUTM(double latitude, double longitude, Integer zone) {

        char letter = 'Z';
        if ((84 >= latitude) && (latitude >= 72)) {
            letter = 'X';
        }
        else if ((72 > latitude) && (latitude >= 64)) {
            letter = 'W';
        }
        else if ((64 > latitude) && (latitude >= 56)) {
            letter = 'V';
        }
        else if ((56 > latitude) && (latitude >= 48)) {
            letter = 'U';
        }
        else if ((48 > latitude) && (latitude >= 40)) {
            letter = 'T';
        }
        else if ((40 > latitude) && (latitude >= 32)) {
            letter = 'S';
        }
        else if ((32 > latitude) && (latitude >= 24)) {
            letter = 'R';
        }
        else if ((24 > latitude) && (latitude >= 16)) {
            letter = 'Q';
        }
        else if ((16 > latitude) && (latitude >= 8)) {
            letter = 'P';
        }
        else if ((8 > latitude) && (latitude >= 0)) {
            letter = 'N';
        }
        else if ((0 > latitude) && (latitude >= -8)) {
            letter = 'M';
        }
        else if ((-8 > latitude) && (latitude >= -16)) {
            letter = 'L';
        }
        else if ((-16 > latitude) && (latitude >= -24)) {
            letter = 'K';
        }
        else if ((-24 > latitude) && (latitude >= -32)) {
            letter = 'J';
        }
        else if ((-32 > latitude) && (latitude >= -40)) {
            letter = 'H';
        }
        else if ((-40 > latitude) && (latitude >= -48)) {
            letter = 'G';
        }
        else if ((-48 > latitude) && (latitude >= -56)) {
            letter = 'F';
        }
        else if ((-56 > latitude) && (latitude >= -64)) {
            letter = 'E';
        }
        else if ((-64 > latitude) && (latitude >= -72)) {
            letter = 'D';
        }
        else if ((-72 > latitude) && (latitude >= -80)) {
            letter = 'C';
        }


        // Make sure the longitude is between -180.00 .. 179.99..
        // Convert values on 0-360 range to this range.
        double latRad = latitude * Math.PI / 180;
        double lonRad = longitude * Math.PI / 180;

        // user-supplied zone number will force coordinates to be computed in a particular zone
        if (zone == null) {
            zone = (int) Math.floor(longitude / 6 + 31);
        }

        double lonOrigin = (zone - 1) * 6 - 180 + 3;  // +3 puts origin in middle of zone
        double lonOriginRad = lonOrigin * Math.PI / 180;

        // compute the UTM Zone from the latitude and longitude
//        String UTMZone = zone + "" + UTMLetterDesignator(lat) + " ";

        double N = EQUATORIAL_RADIUS / Math.sqrt(1 - ECC_SQUARED *
                Math.sin(latRad) * Math.sin(latRad));
        double T = Math.tan(latRad) * Math.tan(latRad);
        double C = ECC_PRIME_SQUARED * Math.cos(latRad) * Math.cos(latRad);
        double A = Math.cos(latRad) * (lonRad - lonOriginRad);

        // Note that the term Mo drops out of the "M" equation, because phi
        // (latitude crossing the central meridian, lambda0, at the origin of the
        //  x,y coordinates), is equal to zero for UTM.
        double M = EQUATORIAL_RADIUS * (( 1 - ECC_SQUARED / 4
                - 3 * (ECC_SQUARED * ECC_SQUARED) / 64
                - 5 * (ECC_SQUARED * ECC_SQUARED * ECC_SQUARED) / 256) * latRad
                - ( 3 * ECC_SQUARED / 8 + 3 * ECC_SQUARED * ECC_SQUARED / 32
                + 45 * ECC_SQUARED * ECC_SQUARED * ECC_SQUARED / 1024)
                * Math.sin(2 * latRad) + (15 * ECC_SQUARED * ECC_SQUARED / 256
                + 45 * ECC_SQUARED * ECC_SQUARED * ECC_SQUARED / 1024) * Math.sin(4 * latRad)
                - (35 * ECC_SQUARED * ECC_SQUARED * ECC_SQUARED / 3072) * Math.sin(6 * latRad));

        double UTMEasting = (k0 * N * (A + (1 - T + C) * (A * A * A) / 6
                + (5 - 18 * T + T * T + 72 * C - 58 * ECC_PRIME_SQUARED )
                * (A * A * A * A * A) / 120)
                + EASTING_OFFSET);

        double UTMNorthing = (k0 * (M + N * Math.tan(latRad) * ( (A * A) / 2 + (5 - T + 9
                * C + 4 * C * C ) * (A * A * A * A) / 24
                + (61 - 58 * T + T * T + 600 * C - 330 * ECC_PRIME_SQUARED )
                * (A * A * A * A * A * A) / 720)));

        return new UTM(UTMNorthing, UTMEasting, zone, letter);
    }

    public static UTM latLngToUtm(double latitude, double longitude, int zone) {

        char letter = 'Z';
        if ((84 >= latitude) && (latitude >= 72)) {
            letter = 'X';
        }
        else if ((72 > latitude) && (latitude >= 64)) {
            letter = 'W';
        }
        else if ((64 > latitude) && (latitude >= 56)) {
            letter = 'V';
        }
        else if ((56 > latitude) && (latitude >= 48)) {
            letter = 'U';
        }
        else if ((48 > latitude) && (latitude >= 40)) {
            letter = 'T';
        }
        else if ((40 > latitude) && (latitude >= 32)) {
            letter = 'S';
        }
        else if ((32 > latitude) && (latitude >= 24)) {
            letter = 'R';
        }
        else if ((24 > latitude) && (latitude >= 16)) {
            letter = 'Q';
        }
        else if ((16 > latitude) && (latitude >= 8)) {
            letter = 'P';
        }
        else if ((8 > latitude) && (latitude >= 0)) {
            letter = 'N';
        }
        else if ((0 > latitude) && (latitude >= -8)) {
            letter = 'M';
        }
        else if ((-8 > latitude) && (latitude >= -16)) {
            letter = 'L';
        }
        else if ((-16 > latitude) && (latitude >= -24)) {
            letter = 'K';
        }
        else if ((-24 > latitude) && (latitude >= -32)) {
            letter = 'J';
        }
        else if ((-32 > latitude) && (latitude >= -40)) {
            letter = 'H';
        }
        else if ((-40 > latitude) && (latitude >= -48)) {
            letter = 'G';
        }
        else if ((-48 > latitude) && (latitude >= -56)) {
            letter = 'F';
        }
        else if ((-56 > latitude) && (latitude >= -64)) {
            letter = 'E';
        }
        else if ((-64 > latitude) && (latitude >= -72)) {
            letter = 'D';
        }
        else if ((-72 > latitude) && (latitude >= -80)) {
            letter = 'C';
        }

        double easting=0.5*Math.log((1+Math.cos(latitude*Math.PI/180)*Math.sin(longitude*Math.PI/180-(6*zone-183)*Math.PI/180))/(1-Math.cos(latitude*Math.PI/180)*Math.sin(longitude*Math.PI/180-(6*zone-183)*Math.PI/180)))*0.9996*6399593.62/Math.pow((1+Math.pow(0.0820944379, 2)*Math.pow(Math.cos(latitude*Math.PI/180), 2)), 0.5)*(1+ Math.pow(0.0820944379,2)/2*Math.pow((0.5*Math.log((1+Math.cos(latitude*Math.PI/180)*Math.sin(longitude*Math.PI/180-(6*zone-183)*Math.PI/180))/(1-Math.cos(latitude*Math.PI/180)*Math.sin(longitude*Math.PI/180-(6*zone-183)*Math.PI/180)))),2)*Math.pow(Math.cos(latitude*Math.PI/180),2)/3)+500000;
        easting=Math.round(easting*100)*0.01;
        double northing = (Math.atan(Math.tan(latitude*Math.PI/180)/Math.cos((longitude*Math.PI/180-(6*zone -183)*Math.PI/180)))-latitude*Math.PI/180)*0.9996*6399593.625/Math.sqrt(1+0.006739496742*Math.pow(Math.cos(latitude*Math.PI/180),2))*(1+0.006739496742/2*Math.pow(0.5*Math.log((1+Math.cos(latitude*Math.PI/180)*Math.sin((longitude*Math.PI/180-(6*zone -183)*Math.PI/180)))/(1-Math.cos(latitude*Math.PI/180)*Math.sin((longitude*Math.PI/180-(6*zone -183)*Math.PI/180)))),2)*Math.pow(Math.cos(latitude*Math.PI/180),2))+0.9996*6399593.625*(latitude*Math.PI/180-0.005054622556*(latitude*Math.PI/180+Math.sin(2*latitude*Math.PI/180)/2)+4.258201531e-05*(3*(latitude*Math.PI/180+Math.sin(2*latitude*Math.PI/180)/2)+Math.sin(2*latitude*Math.PI/180)*Math.pow(Math.cos(latitude*Math.PI/180),2))/4-1.674057895e-07*(5*(3*(latitude*Math.PI/180+Math.sin(2*latitude*Math.PI/180)/2)+Math.sin(2*latitude*Math.PI/180)*Math.pow(Math.cos(latitude*Math.PI/180),2))/4+Math.sin(2*latitude*Math.PI/180)*Math.pow(Math.cos(latitude*Math.PI/180),2)*Math.pow(Math.cos(latitude*Math.PI/180),2))/3);

        if (latitude < 0.0)
            northing = northing + 10000000;

        northing=Math.round(northing*100)*0.01;

        return new UTM(northing, easting, zone, letter);
    }

    /**
     * Get the two letter 100k designator for a given UTM easting,
     * northing and zone number value.
     *
     * @param easting easting
     * @param northing northing
     * @param zoneNumber zone number
     * @return the two letter 100k designator for the given UTM location.
     */
    public static String get100KId(double easting, double northing, Integer zoneNumber) {
        int set = get100kSetForZone(zoneNumber);
        int setColumn = (int) Math.floor(easting / 100000);
        int setRow = (int) Math.floor(northing / 100000) % 20;
        return getLetter100kID(setColumn, setRow, set);
    }


    /**
     * Given a UTM zone number, figure out the MGRS 100K set it is in.
     *
     * @param zoneNumber An UTM zone number.
     * @return the 100k set the UTM zone is in.
     */
    private static int get100kSetForZone(Integer zoneNumber) {
        int setParm = zoneNumber % NUM_100K_SETS;
        if (setParm == 0) {
            setParm = NUM_100K_SETS;
        }

        return setParm;
    }

    /**
     * Get the two-letter MGRS 100k designator given information
     * translated from the UTM northing, easting and zone number.
     *
     * @param column the column index as it relates to the MGRS
     *        100k set spreadsheet, created from the UTM easting.
     *        Values are 1-8.
     * @param row the row index as it relates to the MGRS 100k set
     *        spreadsheet, created from the UTM northing value. Values
     *        are from 0-19.
     * @param parm the set block, as it relates to the MGRS 100k set
     *        spreadsheet, created from the UTM zone. Values are from
     *        1-60.
     * @return two letter MGRS 100k code.
     */
    private static String getLetter100kID(int column, int row, int parm) {

        parm = parm % NUM_100K_SETS;
        if (parm == 0) {
            parm = NUM_100K_SETS;
        }

        // colOrigin and rowOrigin are the letters at the origin of the set
        int index = parm - 1;
        char colOrigin = SET_ORIGIN_COLUMN_LETTERS.charAt(index);
        char rowOrigin = SET_ORIGIN_ROW_LETTERS.charAt(index);

        // colInt and rowInt are the letters to build to return
        int colInt = colOrigin + column - 1;
        int rowInt = rowOrigin + row;
        boolean rollover = false;

        if (colInt > Z) {
            colInt = colInt - Z + A - 1;
            rollover = true;
        }

        if (colInt == I || (colOrigin < I && colInt > I) || ((colInt > I || colOrigin < I) && rollover)) {
            colInt++;
        }

        if (colInt == O || (colOrigin < O && colInt > O) || ((colInt > O || colOrigin < O) && rollover)) {
            colInt++;

            if (colInt == I) {
                colInt++;
            }
        }

        if (colInt > Z) {
            colInt = colInt - Z + A - 1;
        }

        if (rowInt > V) {
            rowInt = rowInt - V + A - 1;
            rollover = true;
        }
        else {
            rollover = false;
        }

        if (((rowInt == I) || ((rowOrigin < I) && (rowInt > I))) || (((rowInt > I) || (rowOrigin < I)) && rollover)) {
            rowInt++;
        }

        if (((rowInt == O) || ((rowOrigin < O) && (rowInt > O))) || (((rowInt > O) || (rowOrigin < O)) && rollover)) {
            rowInt++;

            if (rowInt == I) {
                rowInt++;
            }
        }

        if (rowInt > V) {
            rowInt = rowInt - V + A - 1;
        }


        String twoLetter = "" + (char) colInt + (char) rowInt;
        return twoLetter;
    }

    /**
     * Encodes a UTM location as MGRS string.
     *
     * @param latLng coordinate
     * @param accuracy Accuracy in digits (1-5).
     * @return MGRS string for the given UTM location.
     */
    public static String latLngToMGRS(LatLng latLng, int accuracy) {
        UTM utm = latLngToUtm(latLng.latitude, latLng.longitude);

        String easting = String.format("%05d", Math.round(utm.easting));
        String northing =  String.format("%05d", Math.round(utm.northing % 100000.0));

        int column = (int) Math.floor(utm.easting / 100000);
        int row = (int) Math.floor(utm.northing / 100000) % 20;

        return "" + utm.zoneNumber + utm.zoneLetter + getLetter100kID(column, row, utm.zoneNumber) + easting.substring(0, accuracy) + northing.substring(0, accuracy);
    }

    public static class UTM {
        public double northing;
        public double easting;
        public int zoneNumber;
        public char zoneLetter;

        public UTM(double northing, double easting, int zoneNumber, char zoneLetter) {
            this.northing = northing;
            this.easting = easting;
            this.zoneNumber = zoneNumber;
            this.zoneLetter = zoneLetter;
        }
    }
}
