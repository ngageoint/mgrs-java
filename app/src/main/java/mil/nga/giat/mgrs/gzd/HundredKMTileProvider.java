package mil.nga.giat.mgrs.gzd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v4.util.Pair;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import mil.nga.giat.mgrs.GeoUtility;
import mil.nga.giat.mgrs.Mercator;
import mil.nga.giat.mgrs.R;
import mil.nga.giat.mgrs.TileBoundingBoxUtils;
import mil.nga.giat.mgrs.wgs84.Line;
import mil.nga.giat.mgrs.wgs84.Point;

/**
 * Created by wnewman on 11/17/16.
 */
public class HundredKMTileProvider implements TileProvider {

    private static int TEXT_SIZE = 8;

    /**
     * Half the world distance in either direction
     */
    public static double WEB_MERCATOR_HALF_WORLD_WIDTH = 20037508.342789244;

    private static int ONE_HUNDRED_THOUSAND_METERS = 100000;
    private static int CENTER_EASTING = 500000;

    private Context context;
    private int tileWidth;
    private int tileHeight;

    public HundredKMTileProvider(Context context) {
        this.context = context;

        tileWidth = context.getResources().getInteger(R.integer.tile_width);
        tileHeight = context.getResources().getInteger(R.integer.tile_height);
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {

        if (zoom < 5) {
            return null;
        }

        Bitmap bitmap = drawTile(x, y, zoom);

        byte[] bytes = null;
        try {
            bytes = toBytes(bitmap);
        } catch (IOException e) {
            // uhhhh
            Log.e("FOO", "UHH", e);
        }

        Tile tile = new Tile(tileWidth, tileHeight, bytes);

        return tile;
    }

    private Bitmap drawTile(int x, int y, int zoom) {

        Bitmap bitmap = Bitmap.createBitmap(tileWidth, tileHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        double[] boundingBox = getBoundingBox(x, y, zoom);
        double[] webMercatorBoundingBox = getWebMercatorBoundingBox(x, y, zoom);

        Map<Character, Pair<Double, Double>> latitudeZones = GZDZones.latitudeGZDZonesForBBOX(boundingBox);
        Map<Integer, Pair<Double, Double>> longitudeZones = GZDZones.longitudeGZDZonesForBBOX(boundingBox);

        for (Map.Entry<Character, Pair<Double, Double>> latitiudeGZDZone : latitudeZones.entrySet()) {
//            if (!latitiudeGZDZone.getKey().equals('T')) continue;

            for (Map.Entry<Integer, Pair<Double, Double>> longitudeGZDZone : longitudeZones.entrySet()) {
//                if (!longitudeGZDZone.getKey().equals(11)) continue;

                if (x == 341 && y == 423 && zoom == 11) {
                    Log.i("foo", "ya");
                }

                longitudeLinesForGZDZone(latitiudeGZDZone, longitudeGZDZone, webMercatorBoundingBox, canvas);
                latitudeLinesForGZDZone(latitiudeGZDZone, longitudeGZDZone, webMercatorBoundingBox, canvas);
            }
        }

        return bitmap;
    }

    private void longitudeLinesForGZDZone(Map.Entry<Character, Pair<Double, Double>> latitiudeGZDZone, Map.Entry<Integer, Pair<Double, Double>> longitudeGZDZone, double[] bbox, Canvas canvas) {
        if (latitiudeGZDZone.getKey() > 'M') {
            linesForNorthernGZDZone(latitiudeGZDZone, longitudeGZDZone, bbox, canvas);
        } else {
            linesForSouthernGZDZone(latitiudeGZDZone, longitudeGZDZone, bbox, canvas);
        }
    }

    private void linesForNorthernGZDZone(Map.Entry<Character, Pair<Double, Double>> latitiudeGZDZone, Map.Entry<Integer, Pair<Double, Double>> longitudeGZDZone, double[] bbox, Canvas canvas) {
        double minLat = latitiudeGZDZone.getValue().first;
        double maxLat = latitiudeGZDZone.getValue().second;

        double minLon = longitudeGZDZone.getValue().first;
        double maxLon = longitudeGZDZone.getValue().second;

        int zoneNumber = longitudeGZDZone.getKey();
        Character zoneLetter = latitiudeGZDZone.getKey();

        GeoUtility.UTM lowerLeftUTM = GeoUtility.latLngToUtm(minLat, minLon);
        double lowerLeftEasting = (Math.floor(lowerLeftUTM.easting / ONE_HUNDRED_THOUSAND_METERS) * ONE_HUNDRED_THOUSAND_METERS);
        double lowerLeftNorthing = (Math.floor(lowerLeftUTM.northing / ONE_HUNDRED_THOUSAND_METERS) * ONE_HUNDRED_THOUSAND_METERS);

        GeoUtility.UTM upperRightUTM = GeoUtility.latLngToUtm(maxLat, maxLon);
        double endEasting = (CENTER_EASTING - upperRightUTM.easting) + CENTER_EASTING;
        endEasting = (Math.ceil(endEasting / ONE_HUNDRED_THOUSAND_METERS) * ONE_HUNDRED_THOUSAND_METERS);
        double endNorthing = (Math.floor(upperRightUTM.northing / ONE_HUNDRED_THOUSAND_METERS) * ONE_HUNDRED_THOUSAND_METERS);

        double easting = lowerLeftEasting;
        while (easting <= endEasting) {
            double newEasting = easting + ONE_HUNDRED_THOUSAND_METERS;
            double northing = lowerLeftNorthing;
            while (northing <= endNorthing) {
                double newNorthing = northing + ONE_HUNDRED_THOUSAND_METERS;

                LatLng latLng1 = GeoUtility.utmToLatLng(zoneNumber, lowerLeftUTM.zoneLetter, easting, northing);
                LatLng latLng2 = GeoUtility.utmToLatLng(zoneNumber, upperRightUTM.zoneLetter, easting, newNorthing);

                if (latLng1.longitude > minLon && latLng1.longitude < maxLon) {
                    Point p1 = new Point(latLng1);
                    Point p2 = new Point(latLng2);
                    Line line = new Line(p1, p2);
                    line = horizontalIntersection(new Line(p1, p2), new Point(new LatLng(0, minLon)), new Point(new LatLng(0, maxLon)));
                    drawLine(bbox, canvas, line);
                }

                northing = newNorthing;
            }

            easting = newEasting;
        }

//        lowerLeftNorthing = (Math.ceil(lowerLeftUTM.northing / ONE_HUNDRED_THOUSAND_METERS)  * ONE_HUNDRED_THOUSAND_METERS);
//        easting = lowerLeftUTM.easting;
//        endEasting = 2 * CENTER_EASTING - easting;
//        while (easting <= upperRightUTM.easting) {
//            double newEasting = easting + ONE_HUNDRED_THOUSAND_METERS;
//            double northing = lowerLeftNorthing;
//            while (northing <= endNorthing) {
//                double newNorthing = northing + ONE_HUNDRED_THOUSAND_METERS;
//
//                LatLng latLng1 = GeoUtility.utmToLatLng(zoneNumber, lowerLeftUTM.zoneLetter, easting, northing);
//                LatLng latLng2 = GeoUtility.utmToLatLng(zoneNumber, upperRightUTM.zoneLetter, newEasting, northing);
//                Point p1 = new Point(latLng1);
//                Point p2 = new Point(latLng2);
//                Line line = new Line(p1, p2);
//                drawLine(bbox, canvas, line);
//
//                northing = newNorthing;
//            }
//
//            easting = newEasting;
//        }
    }

    private void linesForSouthernGZDZone(Map.Entry<Character, Pair<Double, Double>> latitiudeGZDZone, Map.Entry<Integer, Pair<Double, Double>> longitudeGZDZone, double[] bbox, Canvas canvas) {
        double minLat = latitiudeGZDZone.getValue().first;
        double maxLat = latitiudeGZDZone.getValue().second;

        double minLon = longitudeGZDZone.getValue().first;
        double maxLon = longitudeGZDZone.getValue().second;

        int zoneNumber = longitudeGZDZone.getKey();

        GeoUtility.UTM upperLeftUTM = GeoUtility.latLngToUtm(maxLat, minLon);
        double upperLeftEasting = (Math.ceil(upperLeftUTM.easting / ONE_HUNDRED_THOUSAND_METERS) * ONE_HUNDRED_THOUSAND_METERS);
        double upperLeftNorthing = (Math.ceil(upperLeftUTM.northing / ONE_HUNDRED_THOUSAND_METERS + 1)  * ONE_HUNDRED_THOUSAND_METERS);
        if (latitiudeGZDZone.getKey() == 'M') {
            upperLeftNorthing = 10000000.0;
            upperLeftUTM.zoneLetter = 'M';
        }

        GeoUtility.UTM lowerRightUTM = GeoUtility.latLngToUtm(minLat, maxLon);
        double lowerRightEasting = CENTER_EASTING * 2 - upperLeftEasting;
        lowerRightEasting = (Math.floor(lowerRightEasting / ONE_HUNDRED_THOUSAND_METERS) * ONE_HUNDRED_THOUSAND_METERS);
        double lowerRightNorthing = (Math.ceil(lowerRightUTM.northing / ONE_HUNDRED_THOUSAND_METERS) * ONE_HUNDRED_THOUSAND_METERS);

        for (double easting = upperLeftEasting; easting <= lowerRightEasting; easting += ONE_HUNDRED_THOUSAND_METERS) {
            double northing = upperLeftNorthing;
            while (northing >= lowerRightNorthing) {
                double newNorthing = northing - ONE_HUNDRED_THOUSAND_METERS;

                LatLng latLng1 = GeoUtility.utmToLatLng(zoneNumber, upperLeftUTM.zoneLetter, easting, northing);
                LatLng latLng2 = GeoUtility.utmToLatLng(zoneNumber, lowerRightUTM.zoneLetter, easting, newNorthing);

                if (latLng1.longitude > minLon && latLng1.longitude < maxLon) {
                    Point p1 = new Point(latLng1);
                    Point p2 = new Point(latLng2);
                    Line line = new Line(p1, p2);

                    line = horizontalIntersection(line, new Point(new LatLng(0, minLon)), new Point(new LatLng(0, maxLon)));
                    drawLine(bbox, canvas, line);
                }

                northing = newNorthing;
            }
        }

//        upperLeftNorthing = (Math.floor(upperLeftUTM.northing / ONE_HUNDRED_THOUSAND_METERS + 1)  * ONE_HUNDRED_THOUSAND_METERS);
//        if (latitiudeGZDZone.getKey() == 'M') {
//            upperLeftNorthing = 10000000.0;
//            upperLeftUTM.zoneLetter = 'M';
//        }
//        upperLeftEasting = (Math.floor(upperLeftUTM.easting / ONE_HUNDRED_THOUSAND_METERS) * ONE_HUNDRED_THOUSAND_METERS);
//        for (double northing = upperLeftNorthing; northing >= lowerRightNorthing; northing -= ONE_HUNDRED_THOUSAND_METERS) {
//            double easting = upperLeftEasting;
//            while (easting <= lowerRightEasting) {
//                double newEasting = easting + ONE_HUNDRED_THOUSAND_METERS;
//                LatLng latLng1 = GeoUtility.utmToLatLng(zoneNumber, upperLeftUTM.zoneLetter, easting, northing);
//                LatLng latLng2 = GeoUtility.utmToLatLng(zoneNumber, lowerRightUTM.zoneLetter, newEasting, northing);
//                Point p1 = new Point(latLng1);
//                Point p2 = new Point(latLng2);
//                Line line = new Line(p1, p2);
//
//                drawLine(bbox, canvas, line);
//                easting = newEasting;
//            }
//        }
    }


    private void latitudeLinesForGZDZone(Map.Entry<Character, Pair<Double, Double>> latitiudeGZDZone, Map.Entry<Integer, Pair<Double, Double>> longitudeGZDZone, double[] bbox, Canvas canvas) {

        Character zoneLetter = latitiudeGZDZone.getKey();
        Integer zoneNumber = longitudeGZDZone.getKey();

        Double minLat = latitiudeGZDZone.getValue().first;
        Double maxLat = latitiudeGZDZone.getValue().second;

        Double minLon = longitudeGZDZone.getValue().first;
        Double maxLon = longitudeGZDZone.getValue().second;

        Double centerLongitude = Math.abs((maxLon - minLon) / 2) + minLon;
        GeoUtility.UTM utm = GeoUtility.latLngToUtm(minLat, centerLongitude);
        double northing = Math.ceil(utm.northing / ONE_HUNDRED_THOUSAND_METERS) * ONE_HUNDRED_THOUSAND_METERS;


        double maxNorthing;
        if (zoneLetter.equals('M')) {
            maxNorthing = 10000000.0;
        } else {
            maxNorthing = GeoUtility.latLngToUtm(maxLat, centerLongitude).northing;
        }

        GeoUtility.UTM lowerLeftUTM = GeoUtility.latLngToUtm(minLat, minLon);
        double lowerLeftEasting = (Math.floor(lowerLeftUTM.easting / ONE_HUNDRED_THOUSAND_METERS) * ONE_HUNDRED_THOUSAND_METERS);
        double lowerLeftNorthing = (Math.floor(lowerLeftUTM.northing / ONE_HUNDRED_THOUSAND_METERS) * ONE_HUNDRED_THOUSAND_METERS);

        GeoUtility.UTM upperRightUTM = GeoUtility.latLngToUtm(maxLat, maxLon);
        double endEasting = (CENTER_EASTING - upperRightUTM.easting) + CENTER_EASTING;
        endEasting = (Math.ceil(endEasting / ONE_HUNDRED_THOUSAND_METERS) * ONE_HUNDRED_THOUSAND_METERS);
        double endNorthing = (Math.floor(upperRightUTM.northing / ONE_HUNDRED_THOUSAND_METERS) * ONE_HUNDRED_THOUSAND_METERS);
        do {
            LatLng latLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, utm.easting, northing);
            double easting = GeoUtility.latLngToUtm(latLng.latitude, minLon).easting;
            double maxEasting = (2 * utm.easting) - easting;
            do {
                double newEasting = easting + ONE_HUNDRED_THOUSAND_METERS < maxEasting ? easting + ONE_HUNDRED_THOUSAND_METERS : maxEasting;

                LatLng latLng1 = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, northing);
                LatLng latLng2 = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, newEasting, northing);
                Line line = new Line(new Point(latLng1), new Point(latLng2));
                drawLine(bbox, canvas, line);

                easting = newEasting;
            } while (easting < maxEasting);

            northing += ONE_HUNDRED_THOUSAND_METERS;
        } while (northing < maxNorthing);

//        do {
//            // go left
//            double easting = CENTER_EASTING;
//            LatLng latLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, utm.easting, northing);
//            double minEasting = GeoUtility.latLngToUtm(latLng.latitude, minLon).easting;
//            do {
//                double newEasting = easting - ONE_HUNDRED_THOUSAND_METERS > minEasting ? easting - ONE_HUNDRED_THOUSAND_METERS : minEasting;
//
//                LatLng latLng1 = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, northing);
//                LatLng latLng2 = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, newEasting, northing);
//                Line line = new Line(new Point(latLng1), new Point(latLng2));
//                drawLine(bbox, canvas, line);
//
//                easting = newEasting;
//            } while (easting > minEasting);
//
//            // go right
//            easting = CENTER_EASTING;
//            double maxEasting = (2 * utm.easting) - minEasting;
//            do {
//                double newEasting = easting + ONE_HUNDRED_THOUSAND_METERS < maxEasting ? easting + ONE_HUNDRED_THOUSAND_METERS : maxEasting;
//
//                LatLng latLng1 = GeoUtility.utmToLatLng(utm.zoneNumber, utm.zoneLetter, easting, northing);
//                LatLng latLng2 = GeoUtility.utmToLatLng(utm.zoneNumber, utm.zoneLetter, newEasting, northing);
//                Line line = new Line(new Point(latLng1), new Point(latLng2));
//                drawLine(bbox, canvas, line);
//
//                easting = newEasting;
//            } while (easting < maxEasting);
//
//            northing += ONE_HUNDRED_THOUSAND_METERS;
//        } while (northing < maxNorthing);
    }

    /**
     * Draw the shape on the canvas
     *
     * @param boundingBox
     * @param canvas
     */
    private void drawLine(double[] boundingBox, Canvas canvas, Line line) {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(76, 175, 80));

        Path linePath = new Path();
        addPolyline(boundingBox, linePath, line);
        canvas.drawPath(linePath, paint);
    }

    private void drawId(String id, LatLng centerLatLng, double minLon, double maxLon, double[] boundingBox, Canvas canvas) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(76, 175, 80));
        paint.setTextSize(TEXT_SIZE * context.getResources().getDisplayMetrics().density);

        // Determine the text bounds
        Rect textBounds = new Rect();
        paint.getTextBounds(id, 0, id.length(), textBounds);
        float textWidth = textBounds.right - textBounds.left;

        double[] meters = degreesToMeters(new LatLng(centerLatLng.latitude, centerLatLng.longitude));
        float x = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox, meters[0]);
        float y = TileBoundingBoxUtils.getYPixel(tileHeight, boundingBox, meters[1]);

        double[] minMeters = degreesToMeters(new LatLng(centerLatLng.latitude, minLon));
        double[] maxMeters = degreesToMeters(new LatLng(centerLatLng.latitude, maxLon));

        float minX = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox, minMeters[0]);
        float maxX = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox, maxMeters[0]);
        float zoneWidth = maxX - minX;
        if (zoneWidth > textWidth + 10) {
            // Draw the text
            canvas.drawText(id, x - textBounds.exactCenterX(), y - textBounds.exactCenterY(), paint);
        }
    }

    /**
     * Add the polyline to the path
     *
     * @param boundingBox
     * @param path
     * @param path
     * @param line
     */
    private void addPolyline(double[] boundingBox, Path path, mil.nga.giat.mgrs.wgs84.Line line) {

        float x = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox, line.p1.x);
        float y = TileBoundingBoxUtils.getYPixel(tileHeight, boundingBox, line.p1.y);
        path.moveTo(x, y);

        x = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox, line.p2.x);
        y = TileBoundingBoxUtils.getYPixel(tileHeight, boundingBox, line.p2.y);
        path.lineTo(x, y);
    }

    /**
     * Compress the bitmap to a byte array
     *
     * @param bitmap
     * @return
     * @throws IOException
     */
    public static byte[] toBytes(Bitmap bitmap) throws IOException {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
        int quality = 100;

        byte[] bytes = null;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            bitmap.compress(format, quality, byteStream);
            bytes = byteStream.toByteArray();
        } finally {
            byteStream.close();
        }

        return bytes;
    }

    /**
     * Get the tile bounding box from the Google Maps API tile
     * coordinates and zoom level
     *
     * @param x
     *            x coordinate
     * @param y
     *            y coordinate
     * @param zoom
     *            zoom level
     * @return bounding box
     */
    public static double[] getBoundingBox(long x, long y, int zoom) {

        double[] bbox  = new double[]{
                tileToLongitue(x, zoom),
                tileToLatitude(y + 1, zoom),
                tileToLongitue(x + 1, zoom),
                tileToLatitude(y, zoom),
        };

        return bbox;
    }

    private static double tileToLongitue(long x, long zoom) {
        return (x / Math.pow(2, zoom) * 360 - 180);
    }

    private static double tileToLatitude(long y, long zoom) {
        double n = Math.PI - 2 * Math.PI * y / Math.pow(2, zoom);
        return (180 / Math.PI * Math.atan(0.5 * (Math.exp(n) - Math.exp(-n))));
    }

    /**
     * Get the Web Mercator tile bounding box from the Google Maps API tile
     * coordinates and zoom level
     *
     * @param x
     *            x coordinate
     * @param y
     *            y coordinate
     * @param zoom
     *            zoom level
     * @return bounding box
     */
    public static double[] getWebMercatorBoundingBox(long x, long y, int zoom) {

        int tilesPerSide = tilesPerSide(zoom);
        double tileSize = tileSize(tilesPerSide);

        double minLon = (-1 * WEB_MERCATOR_HALF_WORLD_WIDTH) + (x * tileSize);
        double maxLon = (-1 * WEB_MERCATOR_HALF_WORLD_WIDTH) + ((x + 1) * tileSize);
        double minLat = WEB_MERCATOR_HALF_WORLD_WIDTH - ((y + 1) * tileSize);
        double maxLat = WEB_MERCATOR_HALF_WORLD_WIDTH - (y * tileSize);

        return new double[] {minLon, minLat, maxLon, maxLat};
    }

    /**
     * Get the tiles per side, width and height, at the zoom level
     *
     * @param zoom
     *            zoom level
     * @return tiles per side
     */
    public static int tilesPerSide(int zoom) {
        return (int) Math.pow(2, zoom);
    }


    /**
     * Get the tile size in meters
     *
     * @param tilesPerSide
     *            tiles per side
     * @return tile size
     */
    public static double tileSize(int tilesPerSide) {
        return (2 * WEB_MERCATOR_HALF_WORLD_WIDTH) / tilesPerSide;
    }

    private static double[] degreesToMeters(LatLng latLng) {
        double x = latLng.longitude * WEB_MERCATOR_HALF_WORLD_WIDTH / 180;
        double y = Math.log(Math.tan((90 + latLng.latitude) * Math.PI / 360)) / (Math.PI / 180);
        y = y * WEB_MERCATOR_HALF_WORLD_WIDTH / 180;
        double[] foo =  new double[] {x, y};

        Mercator m = new Mercator();
        double[] meters = m.merc(latLng.longitude, latLng.latitude);
        return meters;
    }

    private static double[] metersToDegrees(double[] meters) {
        double x = meters[0] *  180 / WEB_MERCATOR_HALF_WORLD_WIDTH;
        double y = Math.atan(Math.exp(meters[1] * Math.PI / 20037508.34)) * 360 / Math.PI - 90;

        return new double[] {x, y};
    }

    private static Point intersect(Line line1, Line line2) {
        Point pt1 = line1.p1;
        Point pt2 = line1.p2;
        Point pt3 = line2.p1;
        Point pt4 = line2.p2;

        double x1 = pt1.x;
        double y1 = pt1.y;
        double x2 = pt2.x;
        double y2 = pt2.y;
        double x3 = pt3.x;
        double y3 = pt3.y;
        double x4 = pt4.x;
        double y4 = pt4.y;

        double denominator = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (denominator == 0) {
            return null;
        }

        double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denominator;
        double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denominator;
        double x = x1 + ua * (x2 - x1);
        double y = y1 + ua * (y2 - y1);

        return new Point(x, y);
    }

    private static Line horizontalIntersection(Line line, Point left, Point right) {
        Point p2 = line.p2;

        if (line.p2.x > right.x) {
            double slope = (line.p1.y - line.p2.y) / (line.p1.x - line.p2.x);
            double y = line.p1.y + (slope * (right.x - line.p1.x));
            p2 = new Point(right.x, y);
        }

        if (line.p2.x < left.x) {
            double slope = (line.p1.y - line.p2.y) / (line.p1.x - line.p2.x);
            double y = line.p1.y + (slope * (left.x - line.p1.x));
            p2 = new Point(left.x, y);
        }

        return new Line(line.p1, p2);
    }

}
