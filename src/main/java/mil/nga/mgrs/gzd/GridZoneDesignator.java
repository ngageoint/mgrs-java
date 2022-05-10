package mil.nga.mgrs.gzd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mil.nga.mgrs.Label;
import mil.nga.mgrs.MGRS;
import mil.nga.mgrs.utm.UTM;
import mil.nga.mgrs.wgs84.LatLng;
import mil.nga.mgrs.wgs84.Line;
import mil.nga.mgrs.wgs84.Point;

/**
 * Created by wnewman on 12/23/16.
 */
public class GridZoneDesignator {

    private Character zoneLetter;
    private UTM.Hemisphere hemisphere;
    private Integer zoneNumber;
    private double[] zoneBounds;

    public GridZoneDesignator(Character zoneLetter, Integer zoneNumber, double[] zoneBounds) {
        this.zoneLetter = zoneLetter;
        this.hemisphere = zoneLetter < 'N' ? UTM.Hemisphere.SOUTH : UTM.Hemisphere.NORTH;
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

        UTM lowerLeftUTM = UTM.from(new LatLng(minLat, minLon), zoneNumber, hemisphere);
        double lowerEasting = (Math.floor(lowerLeftUTM.getEasting() / precision) * precision) - precision;
        double lowerNorthing = (Math.ceil(lowerLeftUTM.getNorthing() / precision) * precision);

        UTM upperRightUTM = UTM.from(new LatLng(maxLat, maxLon), zoneNumber, hemisphere);
        double upperEasting = (Math.floor(upperRightUTM.getEasting() / precision) * precision) + precision;
        double upperNorthing = (Math.ceil(upperRightUTM.getNorthing() / precision) * precision) + precision;

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
        UTM lowerLeftUTM = UTM.from(new LatLng(zoneBounds[1], zoneBounds[0]), zoneNumber, hemisphere);
        UTM upperRightUTM = UTM.from(new LatLng(zoneBounds[3], zoneBounds[2]), zoneNumber, hemisphere);

        double newNorthing = northing - precision;
        double centerNorthing = northing - (precision / 2);

        double newEasting = easting + precision;
        double centerEasting = easting + (precision / 2);

        if (newNorthing < lowerLeftUTM.getNorthing()) {
            LatLng currentLatLng = LatLng.from(new UTM(zoneNumber, hemisphere, centerEasting, lowerLeftUTM.getNorthing()));
            UTM utm = UTM.from(new LatLng(zoneBounds[1], currentLatLng.longitude), zoneNumber, hemisphere);
            centerNorthing = ((northing - lowerLeftUTM.getNorthing()) / 2) + lowerLeftUTM.getNorthing();
            newNorthing = utm.getNorthing();
        } else if (northing > upperRightUTM.getNorthing()) {
            LatLng currentLatLng = LatLng.from(new UTM(zoneNumber, hemisphere, centerEasting, upperRightUTM.getNorthing()));
            UTM utm = UTM.from(new LatLng(zoneBounds[3], currentLatLng.longitude), zoneNumber, hemisphere);
            centerNorthing = ((upperRightUTM.getNorthing() - newNorthing) / 2) + newNorthing;
            northing = utm.getNorthing();
        }

        if (easting < lowerLeftUTM.getEasting()) {
            LatLng currentLatLng = LatLng.from(new UTM(zoneNumber, hemisphere, newEasting, centerNorthing));
            UTM utm = UTM.from(new LatLng(currentLatLng.latitude, zoneBounds[0]), zoneNumber, hemisphere);
            centerEasting = utm.getEasting() + ((newEasting - utm.getEasting()) / 2);
            easting = utm.getEasting();
        } else if (newEasting > upperRightUTM.getEasting()) {
            LatLng currentLatLng = LatLng.from(new UTM(zoneNumber, hemisphere, easting, centerNorthing));
            UTM utm = UTM.from(new LatLng(currentLatLng.latitude, zoneBounds[2]), zoneNumber, hemisphere);
            centerEasting = easting + ((utm.getEasting() - easting) / 2);
            newEasting = utm.getEasting();
        }

        String id = MGRS.get100KId(centerEasting, centerNorthing, zoneNumber);
        LatLng centerLatLng = LatLng.from(new UTM(zoneNumber, hemisphere, centerEasting, centerNorthing));
        Point center = new Point(centerLatLng);

        LatLng l1 = LatLng.from(new UTM(zoneNumber, hemisphere, easting, newNorthing));
        LatLng l2 = LatLng.from(new UTM(zoneNumber, hemisphere, easting, northing));
        LatLng l3 = LatLng.from(new UTM(zoneNumber, hemisphere, newEasting, northing));
        LatLng l4 = LatLng.from(new UTM(zoneNumber, hemisphere, newEasting, newNorthing));

        double minLatitude = Math.max(l1.latitude, l4.latitude);
        double maxLatitude = Math.min(l2.latitude,  l3.latitude);

        double minLongitude = Math.max(l1.longitude, l2.longitude);
        double maxLongitude = Math.min(l3.longitude, l4.longitude);

        Point minPoint = new Point(new LatLng(minLatitude, minLongitude));
        Point maxPoint = new Point(new LatLng(maxLatitude, maxLongitude));

        return new Label(id, center, new double[] {minPoint.x, minPoint.y, maxPoint.x, maxPoint.y}, zoneLetter, zoneNumber);
    }


    private Collection<Line> longitudeLinesForGZDZone(double[] boundingBox, int precision) {
        if (hemisphere == UTM.Hemisphere.NORTH) {
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

        UTM lowerLeftUTM = UTM.from(new LatLng(minLat, minLon), zoneNumber, hemisphere);
        double lowerLeftEasting = (Math.floor(lowerLeftUTM.getEasting() / precision) * precision);
        double lowerLeftNorthing = (Math.floor(lowerLeftUTM.getNorthing() / precision) * precision);

        UTM upperRightUTM = UTM.from(new LatLng(maxLat, maxLon), zoneNumber, hemisphere);
        double endEasting = (Math.ceil(upperRightUTM.getEasting() / precision) * precision);
        double endNorthing = (Math.ceil(upperRightUTM.getNorthing() / precision) * precision);

        double easting = lowerLeftEasting;
        while (easting <= endEasting) {
            double newEasting = easting + precision;
            double northing = lowerLeftNorthing;
            while (northing <= endNorthing) {
                double newNorthing = northing + precision;

                LatLng latLng1 = LatLng.from(new UTM(zoneNumber, hemisphere, easting, northing));
                LatLng latLng2 = LatLng.from(new UTM(zoneNumber, hemisphere, easting, newNorthing));
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

        UTM upperLeftUTM = UTM.from(new LatLng(maxLat, minLon), zoneNumber, hemisphere);
        double upperLeftEasting = (Math.floor(upperLeftUTM.getEasting() / precision) * precision);
        double upperLeftNorthing = (Math.ceil(upperLeftUTM.getNorthing() / precision + 1) * precision);
        if (zoneLetter == 'M') {
            upperLeftNorthing = 10000000.0;
            upperLeftUTM = new UTM(upperLeftUTM.getZoneNumber(), UTM.Hemisphere.SOUTH, upperLeftUTM.getEasting(), upperLeftUTM.getNorthing());
        }

        UTM lowerRightUTM = UTM.from(new LatLng(minLat, maxLon), zoneNumber, hemisphere);
        double lowerRightEasting = (Math.ceil(lowerRightUTM.getEasting() / precision) * precision);
        double lowerRightNorthing = (Math.floor(lowerRightUTM.getNorthing() / precision) * precision);

        for (double easting = upperLeftEasting; easting <= lowerRightEasting; easting += precision) {
            double northing = upperLeftNorthing;
            while (northing >= lowerRightNorthing) {
                double newNorthing = northing - precision;

                LatLng latLng1 = LatLng.from(new UTM(zoneNumber, upperLeftUTM.getHemisphere(), easting, northing));
                LatLng latLng2 = LatLng.from(new UTM(zoneNumber, lowerRightUTM.getHemisphere(), easting, newNorthing));
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

        UTM lowerLeftUTM = UTM.from(new LatLng(minLat, minLon), zoneNumber, hemisphere);
        double lowerEasting = (Math.floor(lowerLeftUTM.getEasting() / precision) * precision) - precision;
        double lowerNorthing = (Math.floor(lowerLeftUTM.getNorthing() / precision) * precision);

        UTM upperRightUTM = UTM.from(new LatLng(maxLat, maxLon), zoneNumber, hemisphere);
        double upperEasting = (Math.ceil(upperRightUTM.getEasting() / precision) * precision) + precision;
        double upperNorthing = (Math.ceil(upperRightUTM.getNorthing() / precision) * precision) + precision;
        if (zoneLetter.equals('M')) {
            upperNorthing = 10000000.0;
        }

        double northing = lowerNorthing;
        while (northing < upperNorthing) {
            double easting = lowerEasting;
            double newNorthing = northing + precision;
            while (easting < upperEasting) {
                double newEasting = easting + precision;
                LatLng latLng1 = LatLng.from(new UTM(zoneNumber, hemisphere, easting, northing));
                LatLng latLng2 = LatLng.from(new UTM(zoneNumber, hemisphere, newEasting, northing));
                Line line = new Line(new Point(latLng1), new Point(latLng2));
                lines.add(line);

                easting = newEasting;
            }

            northing = newNorthing;
        }

        return lines;
    }
}
