package mil.nga.giat.mgrs.gzd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
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

// TODO fix names, not sure what I want to do with that yet
// TODO Combine this with GridTileProvider to do all grids

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
            Log.e("FOO", "UHH", e);
        }

        Tile tile = new Tile(tileWidth, tileHeight, bytes);

        return tile;
    }

    private Bitmap drawTile(int x, int y, int zoom) {

        Bitmap bitmap = Bitmap.createBitmap(tileWidth, tileHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        double[] bbox = getBoundingBox(x, y, zoom);
        double[] webMercatorBoundingBox = getWebMercatorBoundingBox(x, y, zoom);

        Map<Character, Pair<Double, Double>> latitudeZones = GZDZones.latitudeGZDZonesForBBOX(bbox);
        Map<Integer, Pair<Double, Double>> longitudeZones = GZDZones.longitudeGZDZonesForBBOX(bbox);

        for (Map.Entry<Character, Pair<Double, Double>> latitiudeGZDZone : latitudeZones.entrySet()) {
//            if (!latitiudeGZDZone.getKey().equals('T')) continue;

            for (Map.Entry<Integer, Pair<Double, Double>> longitudeGZDZone : longitudeZones.entrySet()) {
//                if (!longitudeGZDZone.getKey().equals(11)) continue;

//                if ((x == 5) && (y == 12) && zoom == 5) {
                    longitudeLinesForGZDZone(latitiudeGZDZone, longitudeGZDZone, bbox, webMercatorBoundingBox, canvas);
                    latitudeLinesForGZDZone(latitiudeGZDZone, longitudeGZDZone, bbox, webMercatorBoundingBox, canvas);
//                }

            }
        }

//        // Draw the tile border
//        Paint tileBorderPaint = new Paint();
//        tileBorderPaint.setAntiAlias(true);
//        tileBorderPaint.setStrokeWidth(4);
//        tileBorderPaint.setStyle(Paint.Style.STROKE);
//        tileBorderPaint.setColor(Color.YELLOW);
//        tileBorderPaint.setAlpha(128);
//        canvas.drawRect(0, 0, tileWidth, tileHeight, tileBorderPaint);
//
//        // Draw the tile x,y,z
//        int centerX = (int) (bitmap.getWidth() / 2.0f);
//        int centerY = (int) (bitmap.getHeight() / 2.0f);
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setColor(Color.BLUE);
//        paint.setTextSize(12 * context.getResources().getDisplayMetrics().density);
//
//        // Determine the text bounds
//        String text = "" + x + "," + y + "," + zoom;
//        Rect textBounds = new Rect();
//        paint.getTextBounds(text, 0, text.length(), textBounds);
//
//        // Draw the text
//        canvas.drawText(text, centerX - 200, centerY, paint);

        return bitmap;
    }

    private void longitudeLinesForGZDZone(Map.Entry<Character, Pair<Double, Double>> latitiudeGZDZone, Map.Entry<Integer, Pair<Double, Double>> longitudeGZDZone, double[] bbox, double[] webMercatorBoundingBox, Canvas canvas) {
        if (latitiudeGZDZone.getKey() > 'M') {
            linesForNorthernGZDZone(latitiudeGZDZone, longitudeGZDZone, bbox, webMercatorBoundingBox, canvas);
        } else {
            linesForSouthernGZDZone(latitiudeGZDZone, longitudeGZDZone, bbox, webMercatorBoundingBox, canvas);
        }
    }

    private void linesForNorthernGZDZone(Map.Entry<Character, Pair<Double, Double>> latitiudeGZDZone, Map.Entry<Integer, Pair<Double, Double>> longitudeGZDZone, double[] bbox, double[] webMercatorBoundingBox, Canvas canvas) {
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

        Point zoneLowerLeft = new Point(new LatLng(latitiudeGZDZone.getValue().first, longitudeGZDZone.getValue().first));
        Point zoneUpperRight = new Point(new LatLng(latitiudeGZDZone.getValue().second, longitudeGZDZone.getValue().second));
        double[] zoneBoundingBox = new double[]{zoneLowerLeft.x, zoneLowerLeft.y, zoneUpperRight.x, zoneUpperRight.y};

        double easting = lowerLeftEasting;
        while (easting <= endEasting) {
            double newEasting = easting + ONE_HUNDRED_THOUSAND_METERS;
            double northing = lowerLeftNorthing;
            while (northing <= endNorthing) {
                double newNorthing = northing + ONE_HUNDRED_THOUSAND_METERS;

                LatLng latLng1 = GeoUtility.utmToLatLng(zoneNumber, lowerLeftUTM.zoneLetter, easting, northing);
                LatLng latLng2 = GeoUtility.utmToLatLng(zoneNumber, upperRightUTM.zoneLetter, easting, newNorthing);

                Point p1 = new Point(latLng1);
                Point p2 = new Point(latLng2);
                Line line = new Line(p1, p2);
                drawLine(webMercatorBoundingBox, zoneBoundingBox, canvas, line);

                northing = newNorthing;
            }

            easting = newEasting;
        }
    }

    private void linesForSouthernGZDZone(Map.Entry<Character, Pair<Double, Double>> latitiudeGZDZone, Map.Entry<Integer, Pair<Double, Double>> longitudeGZDZone, double[] bbox, double[] webMercatorBoundingBox, Canvas canvas) {
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

        Point zoneLowerLeft = new Point(new LatLng(latitiudeGZDZone.getValue().first, longitudeGZDZone.getValue().first));
        Point zoneUpperRight = new Point(new LatLng(latitiudeGZDZone.getValue().second, longitudeGZDZone.getValue().second));
        double[] zoneBoundingBox = new double[]{zoneLowerLeft.x, zoneLowerLeft.y, zoneUpperRight.x, zoneUpperRight.y};

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
                    drawLine(webMercatorBoundingBox, zoneBoundingBox, canvas, line);
                }

                northing = newNorthing;
            }
        }
    }


    private void latitudeLinesForGZDZone(Map.Entry<Character, Pair<Double, Double>> latitiudeGZDZone, Map.Entry<Integer, Pair<Double, Double>> longitudeGZDZone, double[] bbox, double[] webMercatorBoundingBox, Canvas canvas) {

        Character zoneLetter = latitiudeGZDZone.getKey();
        Integer zoneNumber = longitudeGZDZone.getKey();

        Double minLat = Math.max(bbox[1], latitiudeGZDZone.getValue().first);
        Double maxLat = Math.min(bbox[3], latitiudeGZDZone.getValue().second);

        Double minLon = Math.max(bbox[0], longitudeGZDZone.getValue().first);
        Double maxLon = Math.min(bbox[2], longitudeGZDZone.getValue().second);

        Point zoneLowerLeft = new Point(new LatLng(latitiudeGZDZone.getValue().first, longitudeGZDZone.getValue().first));
        Point zoneUpperRight = new Point(new LatLng(latitiudeGZDZone.getValue().second, longitudeGZDZone.getValue().second));
        double[] zoneBoundingBox = new double[]{zoneLowerLeft.x, zoneLowerLeft.y, zoneUpperRight.x, zoneUpperRight.y};

        GeoUtility.UTM lowerLeftUTM = GeoUtility.latLngToUtm(minLat, minLon, zoneNumber);
        double lowerEasting = (Math.ceil(lowerLeftUTM.easting / ONE_HUNDRED_THOUSAND_METERS) * ONE_HUNDRED_THOUSAND_METERS) - ONE_HUNDRED_THOUSAND_METERS;
        double lowerNorthing = (Math.ceil(lowerLeftUTM.northing / ONE_HUNDRED_THOUSAND_METERS) * ONE_HUNDRED_THOUSAND_METERS);

        GeoUtility.UTM upperRightUTM = GeoUtility.latLngToUtm(maxLat, maxLon, zoneNumber);
        double upperEasting = (Math.floor(upperRightUTM.easting / ONE_HUNDRED_THOUSAND_METERS) * ONE_HUNDRED_THOUSAND_METERS) + ONE_HUNDRED_THOUSAND_METERS;
        double upperNorthing = (Math.ceil(upperRightUTM.northing / ONE_HUNDRED_THOUSAND_METERS) * ONE_HUNDRED_THOUSAND_METERS) + ONE_HUNDRED_THOUSAND_METERS;
        if (zoneLetter.equals('M')) {
            upperNorthing = 10000000.0;
        }

        double northing = lowerNorthing;
        while (northing < upperNorthing) {
            double easting = lowerEasting;
            double newNorthing = northing + ONE_HUNDRED_THOUSAND_METERS;
            while (easting < upperEasting) {
                double newEasting = easting + ONE_HUNDRED_THOUSAND_METERS;
                LatLng latLng1 = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, northing);
                LatLng latLng2 = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, newEasting, northing);
                Line line = new Line(new Point(latLng1), new Point(latLng2));
                drawLine(webMercatorBoundingBox, zoneBoundingBox, canvas, line);

                // Draw cell name
                drawId(latitiudeGZDZone, longitudeGZDZone, easting, northing, webMercatorBoundingBox, canvas);

                easting = newEasting;
            }

            northing = newNorthing;
        }
    }

    /**
     * Draw the shape on the canvas
     *
     * @param boundingBox
     * @param canvas
     */
    private void drawLine(double[] boundingBox, double[] zoneBoundingBox, Canvas canvas, Line line) {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(76, 175, 80));

        canvas.save();

        float left = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox, zoneBoundingBox[0]);
        float top = TileBoundingBoxUtils.getYPixel(tileHeight, boundingBox, zoneBoundingBox[3]);
        float right = TileBoundingBoxUtils.getXPixel(tileHeight, boundingBox, zoneBoundingBox[2]);
        float bottom = TileBoundingBoxUtils.getYPixel(tileHeight, boundingBox, zoneBoundingBox[1]);
        canvas.clipRect(left, top, right, bottom, Region.Op.INTERSECT);

        Path linePath = new Path();
        addPolyline(boundingBox, linePath, line);
        canvas.drawPath(linePath, paint);

        canvas.restore();
    }

    private void drawId(Map.Entry<Character, Pair<Double, Double>> latitiudeGZDZone, Map.Entry<Integer, Pair<Double, Double>> longitudeGZDZone, double easting, double northing, double[] boundingBox, Canvas canvas) {
        int zoneNumber = longitudeGZDZone.getKey();
        Character zoneLetter = latitiudeGZDZone.getKey();

        GeoUtility.UTM lowerLeftUTM = GeoUtility.latLngToUtm(latitiudeGZDZone.getValue().first, longitudeGZDZone.getValue().first, zoneNumber);
        GeoUtility.UTM upperRightUTM = GeoUtility.latLngToUtm(latitiudeGZDZone.getValue().second, longitudeGZDZone.getValue().second, zoneNumber);

        double newNorthing = northing - ONE_HUNDRED_THOUSAND_METERS;
        double centerNorthing = northing - (ONE_HUNDRED_THOUSAND_METERS / 2);

        double newEasting = easting + ONE_HUNDRED_THOUSAND_METERS;
        double centerEasting = easting + (ONE_HUNDRED_THOUSAND_METERS / 2);

        if (newNorthing < lowerLeftUTM.northing) {
            LatLng currentLatLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, centerEasting, lowerLeftUTM.northing);
            GeoUtility.UTM utm = GeoUtility.latLngToUtm(latitiudeGZDZone.getValue().first, currentLatLng.longitude, zoneNumber);
            centerNorthing = ((northing - lowerLeftUTM.northing) / 2) + lowerLeftUTM.northing;
            newNorthing = utm.northing;
        } else if (northing > upperRightUTM.northing) {
            String id = GeoUtility.get100KId(centerEasting, centerNorthing, zoneNumber);
            if (id.equals("LP")) {
                Log.i("f", "g");
            }

            LatLng currentLatLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, centerEasting, upperRightUTM.northing);
            GeoUtility.UTM utm = GeoUtility.latLngToUtm(latitiudeGZDZone.getValue().second, currentLatLng.longitude, zoneNumber);
            centerNorthing = ((upperRightUTM.northing - newNorthing) / 2) + newNorthing;
            northing = utm.northing;
        }

        if (easting < lowerLeftUTM.easting) {
            LatLng currentLatLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, lowerLeftUTM.easting, centerNorthing);
            GeoUtility.UTM utm = GeoUtility.latLngToUtm(currentLatLng.latitude, longitudeGZDZone.getValue().first, zoneNumber);
            centerEasting = utm.easting + ((newEasting - utm.easting) / 2);
            easting = utm.easting;
        } else if (newEasting > upperRightUTM.easting) {
            LatLng currentLatLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, newNorthing);
            GeoUtility.UTM utm = GeoUtility.latLngToUtm(currentLatLng.latitude, longitudeGZDZone.getValue().second, zoneNumber);
            centerEasting = easting + ((utm.easting - easting) / 2);
            newEasting = utm.easting;
        }

        String id = GeoUtility.get100KId(centerEasting, centerNorthing, zoneNumber);
        LatLng centerLatLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, centerEasting, centerNorthing);
        Point center = new Point(centerLatLng);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(76, 175, 80));
        paint.setTextSize(TEXT_SIZE * context.getResources().getDisplayMetrics().density);

        // Determine the text bounds
        Rect textBounds = new Rect();
        paint.getTextBounds(id, 0, id.length(), textBounds);

        float x = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox, center.x);
        float y = TileBoundingBoxUtils.getYPixel(tileHeight, boundingBox, center.y);

        LatLng minZone = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, newNorthing);
        Point min = new Point(minZone);

        LatLng maxZone = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, newEasting, northing);
        Point max = new Point(maxZone);

        float minX = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox, min.x);
        float maxX = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox, max.x);
        float zoneWidth = maxX - minX;

        float minY = TileBoundingBoxUtils.getYPixel(tileWidth, boundingBox, max.y);
        float maxY = TileBoundingBoxUtils.getYPixel(tileWidth, boundingBox, min.y);
        float zoneHeight = maxY - minY;

        if (zoneWidth > textBounds.width() + 10 && zoneHeight > textBounds.height() + 10) {
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

    // Cell name stuff
//    private void latitudeLinesForGZDZone(Map.Entry<Character, Pair<Double, Double>> latitiudeGZDZone, Map.Entry<Integer, Pair<Double, Double>> longitudeGZDZone, double[] bbox, Canvas canvas) {
//
//        Character zoneLetter = latitiudeGZDZone.getKey();
//        Integer zoneNumber = longitudeGZDZone.getKey();
//
//        Double minLat = latitiudeGZDZone.getValue().first;
//        Double maxLat = latitiudeGZDZone.getValue().second;
//
//        Double minLon = longitudeGZDZone.getValue().first;
//        Double maxLon = longitudeGZDZone.getValue().second;
//
//        Double centerLongitude = Math.abs((maxLon - minLon) / 2) + minLon;
//        GeoUtility.UTM utm = GeoUtility.latLngToUtm(minLat, centerLongitude);
//        double northing = Math.ceil(utm.northing / ONE_HUNDRED_THOUSAND_METERS) * ONE_HUNDRED_THOUSAND_METERS;
//
//        double maxNorthing;
//        if (zoneLetter.equals('M')) {
//            maxNorthing = 10000000.0;
//        } else {
//            maxNorthing = GeoUtility.latLngToUtm(maxLat, centerLongitude).northing;
//        }
//
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
//                // Draw cell name
//                double centerEasting = easting - ((easting - newEasting) / 2);
//                double centerNorthing = ((northing - (northing + ONE_HUNDRED_THOUSAND_METERS)) / 2) + northing;
//                LatLng centerLatLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, centerEasting, centerNorthing);
//                double minZoneLon = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, newEasting, centerNorthing).longitude;
//                double maxZoneLon = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, centerNorthing).longitude;
//                String id = GeoUtility.get100KId(centerEasting, centerNorthing, zoneNumber);
//                drawId(id, centerLatLng, minZoneLon, maxZoneLon, bbox, canvas);
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
//                // Draw cell name
//                double centerEasting = easting + ((newEasting - easting) / 2);
//                double centerNorthing = ((northing - (northing + ONE_HUNDRED_THOUSAND_METERS)) / 2) + northing;
//                LatLng centerLatLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, centerEasting, centerNorthing);
//                double minZoneLon = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, centerNorthing).longitude;
//                double maxZoneLon = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, newEasting, centerNorthing).longitude;
//                String id = GeoUtility.get100KId(centerEasting, centerNorthing, zoneNumber);
//                drawId(id, centerLatLng, minZoneLon, maxZoneLon, bbox, canvas);
//
//                easting = newEasting;
//            } while (easting < maxEasting);
//
//            northing += ONE_HUNDRED_THOUSAND_METERS;
//        } while (northing < maxNorthing);
//
//        // go left
//        double easting = CENTER_EASTING;
//        LatLng latLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, utm.easting, northing);
//        double minEasting = GeoUtility.latLngToUtm(latLng.latitude, minLon).easting;
//        do {
//            double newEasting = easting - ONE_HUNDRED_THOUSAND_METERS > minEasting ? easting - ONE_HUNDRED_THOUSAND_METERS : minEasting;
//
//            // Draw cell name
//            double centerEasting = easting - ((easting - newEasting) / 2);
//            double centerNorthing = ((northing - (northing + ONE_HUNDRED_THOUSAND_METERS)) / 2) + northing;
//            LatLng centerLatLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, centerEasting, centerNorthing);
//            double minZoneLon = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, newEasting, centerNorthing).longitude;
//            double maxZoneLon = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, centerNorthing).longitude;
//            String id = GeoUtility.get100KId(centerEasting, centerNorthing, zoneNumber);
//            drawId(id, centerLatLng, minZoneLon, maxZoneLon, bbox, canvas);
//
//            easting = easting - ONE_HUNDRED_THOUSAND_METERS > minEasting ? easting - ONE_HUNDRED_THOUSAND_METERS : minEasting;
//        } while (easting > minEasting);
//
//        // go right
//        easting = CENTER_EASTING;
//        double maxEasting = (2 * utm.easting) - minEasting;
//        do {
//            double newEasting = easting + ONE_HUNDRED_THOUSAND_METERS < maxEasting ? easting + ONE_HUNDRED_THOUSAND_METERS : maxEasting;
//
//            // Draw cell name
//            double centerEasting = easting + ((newEasting - easting) / 2);
//            double centerNorthing = ((northing - (northing + ONE_HUNDRED_THOUSAND_METERS)) / 2) + northing;
//            LatLng centerLatLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, centerEasting, centerNorthing);
//            double minZoneLon = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, centerNorthing).longitude;
//            double maxZoneLon = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, newEasting, centerNorthing).longitude;
//            String id = GeoUtility.get100KId(centerEasting, centerNorthing, zoneNumber);
//            drawId(id, centerLatLng, minZoneLon, maxZoneLon, bbox, canvas);
//
//            easting = easting + ONE_HUNDRED_THOUSAND_METERS < maxEasting ? easting + ONE_HUNDRED_THOUSAND_METERS : maxEasting;
//        } while (easting < maxEasting);
//    }

}
