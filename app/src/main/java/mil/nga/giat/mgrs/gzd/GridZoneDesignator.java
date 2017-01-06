package mil.nga.giat.mgrs.gzd;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mil.nga.giat.mgrs.GeoUtility;
import mil.nga.giat.mgrs.Label;
import mil.nga.giat.mgrs.LatLng;
import mil.nga.giat.mgrs.wgs84.Line;
import mil.nga.giat.mgrs.wgs84.Point;

/**
 * Created by wnewman on 12/23/16.
 */
public class GridZoneDesignator {

    private static int CENTER_EASTING = 500000;

    private Character zoneLetter;
    private Integer zoneNumber;
    private double[] zoneBounds;

    public GridZoneDesignator(Character zoneLetter, Integer zoneNumber, double[] zoneBounds) {
        this.zoneLetter = zoneLetter;
        this.zoneNumber = zoneNumber;
        this.zoneBounds = zoneBounds;
    }

    public Character zoneLetter() {
        return zoneLetter;
    }

    public Integer zoneNumber() {
        return zoneNumber;
    }

    public double[] zoneBounds() {
        return zoneBounds;
    }

    public boolean within(double[] bbox) {
        if ((zoneBounds[1] <= bbox[3] && zoneBounds[3] >= bbox[1]) && (zoneBounds[0] <= bbox[2]) && (zoneBounds[2] >= bbox[0])) {
            return true;
        }

        return false;
    }

    public Collection<Line> lines(double[] boundingBox, int precision) {
        Collection<Line> lines = new ArrayList<>();
//        if (!zoneLetter().equals('H')) return lines;
//        if (!zoneNumber().equals(35)) return lines;

        if (precision == 0) {
            // if precision is 0, draw the zone bounds
            lines.add(new Line(new Point(new LatLng(zoneBounds[1], zoneBounds[0])), new Point(new LatLng(zoneBounds[3], zoneBounds[0]))));
            lines.add(new Line(new Point(new LatLng(zoneBounds[3], zoneBounds[0])), new Point(new LatLng(zoneBounds[3], zoneBounds[2]))));
            lines.add(new Line(new Point(new LatLng(zoneBounds[3], zoneBounds[2])), new Point(new LatLng(zoneBounds[1], zoneBounds[2]))));
            lines.add(new Line(new Point(new LatLng(zoneBounds[1], zoneBounds[2])), new Point(new LatLng(zoneBounds[1], zoneBounds[0]))));
        } else {
            Collection<Line> longitudeLines = longitudeLinesForGZDZone(boundingBox, precision);
            lines.addAll(longitudeLines);

            Collection<Line> latitudeLines = latitudeLinesForGZDZone(boundingBox, precision);
            lines.addAll(latitudeLines);
        }

        return lines;
    }

    public Collection<Label> labels(double[] bbox, int precision) {
        Collection<Label> labels = new ArrayList<>();

        if (precision == 0) {
            // if precision is 0, draw the GZD name
            double centerLon = ((zoneBounds[2] - zoneBounds[0]) / 2.0) + zoneBounds[0];
            double centerLat = ((zoneBounds[3] - zoneBounds[1]) / 2.0) + zoneBounds[1];
            Point center = new Point(new LatLng(centerLat, centerLon));

            Point zoneLowerLeft = new Point(new LatLng(zoneBounds[1], zoneBounds[0]));
            Point zoneUpperRight = new Point(new LatLng(zoneBounds[3], zoneBounds[2]));
            double[] zoneBoundingBox = new double[]{zoneLowerLeft.x, zoneLowerLeft.y, zoneUpperRight.x, zoneUpperRight.y};

            String name = zoneNumber.toString() + zoneLetter;
            Label gzdLabel = new Label(name, center, zoneBoundingBox, zoneLetter, zoneNumber);
            labels.add(gzdLabel);

            return labels;
        }

        Double minLat = Math.max(bbox[1], zoneBounds[1]);
        Double maxLat = Math.min(bbox[3], zoneBounds[3]);

        Double minLon = Math.max(bbox[0], zoneBounds[0]);
        Double maxLon = Math.min(bbox[2], zoneBounds[2]);

        GeoUtility.UTM lowerLeftUTM = GeoUtility.latLngToUtm(minLat, minLon, zoneNumber);
        double lowerEasting = (Math.floor(lowerLeftUTM.easting / precision) * precision) - precision;
        double lowerNorthing = (Math.ceil(lowerLeftUTM.northing / precision) * precision);

        GeoUtility.UTM upperRightUTM = GeoUtility.latLngToUtm(maxLat, maxLon, zoneNumber);
        double upperEasting = (Math.floor(upperRightUTM.easting / precision) * precision) + precision;
        double upperNorthing = (Math.ceil(upperRightUTM.northing / precision) * precision) + precision;
        if (zoneLetter.equals('M')) {
            upperNorthing = 10000000.0;
        }

        double northing = lowerNorthing;
        while (northing < upperNorthing) {
            double easting = lowerEasting;
            double newNorthing = northing + precision;
            while (easting < upperEasting) {
                double newEasting = easting + precision;

                // Draw cell name
                Label label = getLabel(precision, easting, northing);
                labels.add(label);

                easting = newEasting;
            }

            northing = newNorthing;
        }

        return labels;
    }

    private Label getLabel(int precision, double easting, double northing) {
        double n = northing;
        double e = easting;

        GeoUtility.UTM lowerLeftUTM = GeoUtility.latLngToUtm(zoneBounds[1], zoneBounds[0], zoneNumber);
        GeoUtility.UTM lowerRightUTM = GeoUtility.latLngToUtm(zoneBounds[3], zoneBounds[0], zoneNumber);

        GeoUtility.UTM upperRightUTM = GeoUtility.latLngToUtm(zoneBounds[3], zoneBounds[2], zoneNumber);
        GeoUtility.UTM upperLeftUTM = GeoUtility.latLngToUtm(zoneBounds[3], zoneBounds[0], zoneNumber);

        double newNorthing = northing - precision;
        double centerNorthing = northing - (precision / 2);

        double newEasting = easting + precision;
        double centerEasting = easting + (precision / 2);

        if (newNorthing < lowerLeftUTM.northing) {
            LatLng currentLatLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, centerEasting, lowerLeftUTM.northing);
            GeoUtility.UTM utm = GeoUtility.latLngToUtm(zoneBounds[1], currentLatLng.longitude, zoneNumber);
            centerNorthing = ((northing - lowerLeftUTM.northing) / 2) + lowerLeftUTM.northing;
            newNorthing = utm.northing;
        } else if (northing > upperRightUTM.northing) {
            LatLng currentLatLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, centerEasting, upperRightUTM.northing);
            GeoUtility.UTM utm = GeoUtility.latLngToUtm(zoneBounds[3], currentLatLng.longitude, zoneNumber);
            centerNorthing = ((upperRightUTM.northing - newNorthing) / 2) + newNorthing;
            northing = utm.northing;
        }

        if (easting < lowerLeftUTM.easting) {
            String id = GeoUtility.get100KId(centerEasting, centerNorthing, zoneNumber);
            if (id.equals("KR")) {
                Log.i("northing", northing + "");
            }

            LatLng currentLatLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, newEasting, centerNorthing);
            GeoUtility.UTM utm = GeoUtility.latLngToUtm(currentLatLng.latitude, zoneBounds[0], zoneNumber);
            centerEasting = utm.easting + ((newEasting - utm.easting) / 2);
            easting = utm.easting;
        } else if (newEasting > upperRightUTM.easting) {
            String id = GeoUtility.get100KId(centerEasting, centerNorthing, zoneNumber);
            if (id.equals("GA")) {
                Log.i("", northing + "");
            }

            LatLng currentLatLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, centerNorthing);
            GeoUtility.UTM utm = GeoUtility.latLngToUtm(currentLatLng.latitude, zoneBounds[2], zoneNumber);
            centerEasting = easting + ((utm.easting - easting) / 2);
            newEasting = utm.easting;
        }

        String id = GeoUtility.get100KId(centerEasting, centerNorthing, zoneNumber);
        if (id.equals("LE")) {
            Log.i("", "");
        }

        LatLng centerLatLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, centerEasting, centerNorthing);
        Point center = new Point(centerLatLng);

        LatLng l1 = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, newNorthing);
        LatLng l2 = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, northing);
        LatLng l3 = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, newEasting, northing);
        LatLng l4 = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, newEasting, newNorthing);

        double minLatitude = Math.max(l1.latitude, l4.latitude);
        double maxLatitude = Math.min(l2.latitude,  l3.latitude);

        double minLongitude = Math.max(l1.longitude, l2.longitude);
        double maxLongitude = Math.min(l3.longitude, l4.longitude);

        Point minPoint = new Point(new LatLng(minLatitude, minLongitude));
        Point maxPoint = new Point(new LatLng(maxLatitude, maxLongitude));

        LatLng minZone = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, newNorthing);
        Point min = new mil.nga.giat.mgrs.wgs84.Point(minZone);

        LatLng maxZone = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, newEasting, northing);
        Point max = new mil.nga.giat.mgrs.wgs84.Point(maxZone);

        return new Label(id, center, new double[] {minPoint.x, minPoint.y, maxPoint.x, maxPoint.y}, zoneLetter, zoneNumber);
    }


    private Collection<Line> longitudeLinesForGZDZone(double[] boundingBox, int precision) {
        if (zoneLetter() > 'M') {
            return linesForNorthernGZDZone(boundingBox, precision);
        } else {
            return linesForSouthernGZDZone(boundingBox, precision);
        }
    }

    private Collection<Line> linesForNorthernGZDZone(double[] boundingBox, int precision) {
        List<Line> lines = new ArrayList<>();

        Double minLat = Math.max(boundingBox[1], zoneBounds[1]);
        Double maxLat = Math.min(boundingBox[3], zoneBounds[3]);

        Double minLon = Math.max(boundingBox[0], zoneBounds[0]);
        Double maxLon = Math.min(boundingBox[2], zoneBounds[2]);

        GeoUtility.UTM lowerLeftUTM = GeoUtility.latLngToUtm(minLat, minLon, zoneNumber);
        double lowerLeftEasting = (Math.floor(lowerLeftUTM.easting / precision) * precision);
        double lowerLeftNorthing = (Math.floor(lowerLeftUTM.northing / precision) * precision);

        GeoUtility.UTM upperRightUTM = GeoUtility.latLngToUtm(maxLat, maxLon, zoneNumber);
        double endEasting = (Math.ceil(upperRightUTM.easting / precision) * precision);
        double endNorthing = (Math.ceil(upperRightUTM.northing / precision) * precision);

        double easting = lowerLeftEasting;
        while (easting <= endEasting) {
            double newEasting = easting + precision;
            double northing = lowerLeftNorthing;
            while (northing <= endNorthing) {
                double newNorthing = northing + precision;

                LatLng latLng1 = GeoUtility.utmToLatLng(zoneNumber, lowerLeftUTM.zoneLetter, easting, northing);
                LatLng latLng2 = GeoUtility.utmToLatLng(zoneNumber, upperRightUTM.zoneLetter, easting, newNorthing);
                Line line = new Line(new Point(latLng1), new Point(latLng2));
                lines.add(line);

                northing = newNorthing;
            }

            easting = newEasting;
        }

        return lines;
    }

    private Collection<Line> linesForSouthernGZDZone(double[] boundingBox, int precision) {
        Collection<Line> lines = new ArrayList<>();

        Double minLat = Math.max(boundingBox[1], zoneBounds[1]);
        Double maxLat = Math.min(boundingBox[3], zoneBounds[3]);

        Double minLon = Math.max(boundingBox[0], zoneBounds[0]);
        Double maxLon = Math.min(boundingBox[2], zoneBounds[2]);

        GeoUtility.UTM upperLeftUTM = GeoUtility.latLngToUtm(maxLat, minLon, zoneNumber);
        double upperLeftEasting = (Math.floor(upperLeftUTM.easting / precision) * precision);
        double upperLeftNorthing = (Math.ceil(upperLeftUTM.northing / precision + 1) * precision);
        if (zoneLetter == 'M') {
            upperLeftNorthing = 10000000.0;
            upperLeftUTM.zoneLetter = 'M';
        }

        GeoUtility.UTM lowerRightUTM = GeoUtility.latLngToUtm(minLat, maxLon, zoneNumber);
        double lowerRightEasting = (Math.ceil(lowerRightUTM.easting / precision) * precision);
        double lowerRightNorthing = (Math.floor(lowerRightUTM.northing / precision) * precision);

        for (double easting = upperLeftEasting; easting <= lowerRightEasting; easting += precision) {
            double northing = upperLeftNorthing;
            while (northing >= lowerRightNorthing) {
                double newNorthing = northing - precision;

                LatLng latLng1 = GeoUtility.utmToLatLng(zoneNumber, upperLeftUTM.zoneLetter, easting, northing);
                LatLng latLng2 = GeoUtility.utmToLatLng(zoneNumber, lowerRightUTM.zoneLetter, easting, newNorthing);
                Line line = new Line(new Point(latLng1), new Point(latLng2));
                lines.add(line);

                northing = newNorthing;
            }
        }

        return lines;
    }

    private Collection<Line> latitudeLinesForGZDZone(double[] boundingBox, int precision) {
        Collection<Line> lines = new ArrayList<>();

        Double minLat = Math.max(boundingBox[1], zoneBounds[1]);
        Double maxLat = Math.min(boundingBox[3], zoneBounds[3]);

        Double minLon = Math.max(boundingBox[0], zoneBounds[0]);
        Double maxLon = Math.min(boundingBox[2], zoneBounds[2]);

        GeoUtility.UTM lowerLeftUTM = GeoUtility.latLngToUtm(minLat, minLon, zoneNumber);
        double lowerEasting = (Math.floor(lowerLeftUTM.easting / precision) * precision) - precision;
        double lowerNorthing = (Math.floor(lowerLeftUTM.northing / precision) * precision);

        GeoUtility.UTM upperRightUTM = GeoUtility.latLngToUtm(maxLat, maxLon, zoneNumber);
        double upperEasting = (Math.ceil(upperRightUTM.easting / precision) * precision) + precision;
        double upperNorthing = (Math.ceil(upperRightUTM.northing / precision) * precision) + precision;
        if (zoneLetter.equals('M')) {
            upperNorthing = 10000000.0;
        }

        double northing = lowerNorthing;
        while (northing < upperNorthing) {
            double easting = lowerEasting;
            double newNorthing = northing + precision;
            while (easting < upperEasting) {
                double newEasting = easting + precision;
                LatLng latLng1 = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, northing);
                LatLng latLng2 = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, newEasting, northing);
                Line line = new Line(new Point(latLng1), new Point(latLng2));
                lines.add(line);

                easting = newEasting;
            }

            northing = newNorthing;
        }

        return lines;
    }
}
