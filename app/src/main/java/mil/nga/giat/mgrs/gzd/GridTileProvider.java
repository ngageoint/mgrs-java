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
import android.util.SparseIntArray;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import mil.nga.giat.mgrs.GeoUtility;
import mil.nga.giat.mgrs.R;
import mil.nga.giat.mgrs.TileBoundingBoxUtils;

/**
 * Created by wnewman on 11/28/16.
 */
public class GridTileProvider implements TileProvider {

    private static int TEXT_SIZE = 8;

    private static final SparseIntArray zoomToGridSize = new SparseIntArray() {{
        put(9, 10000);
        put(10, 10000);
        put(11, 10000);
        put(12, 1000);
        put(13, 1000);
        put(14, 1000);
        put(15, 100);
        put(16, 100);
        put(17, 100);
        put(18, 10);
        put(19, 10);
        put(20, 10);
    }};

    /**
     * Half the world distance in either direction
     */
    public static double WEB_MERCATOR_HALF_WORLD_WIDTH = 20037508.342789244;

    private static int CENTER_EASTING = 500000;

    private Context context;
    private int tileWidth;
    private int tileHeight;

    public GridTileProvider(Context context) {
        this.context = context;

        tileWidth = context.getResources().getInteger(R.integer.tile_width);
        tileHeight = context.getResources().getInteger(R.integer.tile_height);
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {

        if (zoom < 9) {
            return null;
        }

        // Garawlah, NQ, 35R
        if (x == 294 && y == 209 && zoom == 9) {
            Log.i("FOO", "TILE IT");
        } else {
//            return null;
        }

        Bitmap bitmap = drawTile(x, y, zoom);

        byte[] bytes = null;
        try {
            bytes = toBytes(bitmap);
        } catch (IOException e) {
            // uhhhh
            Log.e("FOO", "UHH", e);
        } finally {
            bitmap.recycle();
        }

        Tile tile = new Tile(tileWidth, tileHeight, bytes);

        return tile;
    }

    private Bitmap drawTile(int x, int y, int zoom) {
        int gridSize = zoomToGridSize.get(zoom);

        Bitmap bitmap = Bitmap.createBitmap(tileWidth, tileHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw the tile border
        Paint tileBorderPaint = new Paint();
        tileBorderPaint.setAntiAlias(true);
        tileBorderPaint.setStrokeWidth(2);
        tileBorderPaint.setStyle(Paint.Style.STROKE);
        tileBorderPaint.setColor(Color.YELLOW);
        tileBorderPaint.setAlpha(128);
        canvas.drawRect(0, 0, tileWidth, tileHeight, tileBorderPaint);

        double[] bbox = getBoundingBox(x, y, zoom);
        double[] webMercatorBoundingBox = getWebMercatorBoundingBox(x, y, zoom);

        Map<Character, Pair<Double, Double>> latitudeZones = GZDZones.latitudeGZDZonesForBBOX(bbox);
        Map<Integer, Pair<Double, Double>> longitudeZones = GZDZones.longitudeGZDZonesForBBOX(bbox);

        for (Map.Entry<Character, Pair<Double, Double>> latitiudeGZDZone : latitudeZones.entrySet()) {

            for (Map.Entry<Integer, Pair<Double, Double>> longitudeGZDZone : longitudeZones.entrySet()) {

                linesForGZDZone(latitiudeGZDZone, longitudeGZDZone, gridSize, webMercatorBoundingBox, bbox, canvas);
            }
        }

        // Draw the tile x,y,z
        int centerX = (int) (bitmap.getWidth() / 2.0f);
        int centerY = (int) (bitmap.getHeight() / 2.0f);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setTextSize(12 * context.getResources().getDisplayMetrics().density);

        // Determine the text bounds
        String text = "" + x + "," + y + "," + zoom;
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);

        // Draw the text
        canvas.drawText(text, centerX - 200, centerY, paint);
        return bitmap;
    }

    private void linesForGZDZone(Map.Entry<Character, Pair<Double, Double>> latitiudeGZDZone, Map.Entry<Integer, Pair<Double, Double>> longitudeGZDZone, int gridSize, double[] webMercatorBoundingBox, double[] bbox, Canvas canvas) {

        Double minLat = latitiudeGZDZone.getValue().first;
        Double maxLat = latitiudeGZDZone.getValue().second;

        Double minLon = longitudeGZDZone.getValue().first;
        Double maxLon = longitudeGZDZone.getValue().second;

        // Draw northing lines within GZD
        if (maxLat > 0) {
            longitudeLinesForGZDZone(latitiudeGZDZone.getKey(), longitudeGZDZone.getKey(), minLat, maxLat, minLon, maxLon, gridSize, webMercatorBoundingBox, bbox, canvas);
        } else {
            // Might need to reverse bbox here
            double adjustedBbox[] = new double[] {bbox[0], bbox[3], bbox[2], bbox[1]};
            longitudeLinesForGZDZone(latitiudeGZDZone.getKey(), longitudeGZDZone.getKey(), maxLat, minLat, minLon, maxLon, gridSize, webMercatorBoundingBox, bbox, canvas);
        }

        latitudeLinesForGZDZone(latitiudeGZDZone, longitudeGZDZone, gridSize, webMercatorBoundingBox, bbox, canvas);
    }

    private void longitudeLinesForGZDZone(Character zoneLetter, Integer zoneNumber, double startLat, double endLat, double startLon, double endLon, int gridSize, double[] webMercatorBoundingBox, double[] bbox, Canvas canvas) {
        double bb[] = bbox;
        int gs = gridSize;

        // TODO this might not be correct north of eq
        // TODO is bbox correct as well?
        Double minLat;
        Double maxLat;
        if (endLat > 0) {
            minLat = Math.max(bbox[1], startLat);
            maxLat = Math.min(bbox[3], endLat);
        } else {
            minLat = Math.min(bbox[3], startLat);
            maxLat = Math.max(bbox[1], endLat);
        }

        Double minLon = Math.max(bbox[0], startLon);
        Double maxLon = Math.min(bbox[2], endLon);

        Double centerLongitude = Math.abs((startLon - endLon) / 2) + startLon;
        GeoUtility.UTM utm = GeoUtility.latLngToUtm(minLat, centerLongitude);
        double startNorthing = GeoUtility.latLngToUtm(minLat, minLon).northing;
        if (zoneLetter.equals('M')) {
            startNorthing = 10000000.0;
        }

        double endNorthing = GeoUtility.latLngToUtm(maxLat, minLon).northing;

        double northing = Math.ceil(utm.northing / gridSize) * gridSize;
        LatLng latLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, utm.easting, northing);

        // go left
        if (minLon <= centerLongitude) {
            double easting = GeoUtility.latLngToUtm(latLng.latitude, maxLon).easting;
            easting = Math.ceil(easting / gridSize) * gridSize;
            easting = Math.min(easting, CENTER_EASTING);

            double minEasting = GeoUtility.latLngToUtm(latLng.latitude, minLon).easting;
            minEasting = Math.floor(minEasting / gridSize) * gridSize;

            double zoneMinEasting = GeoUtility.latLngToUtm(latLng.latitude, minLon).easting;
            minEasting = minEasting > zoneMinEasting ? minEasting : zoneMinEasting;

            do {
                LatLng p1 = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, startNorthing);
                LatLng p2 = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, endNorthing);
                PolylineOptions line = new PolylineOptions().add(p1).add(p2);

                LatLng verticalIntersection = intersect(line, new PolylineOptions().add(new LatLng(startLat, startLon)).add(new LatLng(startLat, endLon)));
                if (verticalIntersection != null) {
                    p1 = verticalIntersection;
                }

                verticalIntersection = intersect(line, new PolylineOptions().add(new LatLng(endLat, startLon)).add(new LatLng(endLat, endLon)));
                if (verticalIntersection != null) {
                    p2 = verticalIntersection;
                }

                line = new PolylineOptions().add(p1).add(p2);

                if (easting == CENTER_EASTING) {
                    drawLine(webMercatorBoundingBox, canvas, line, Color.RED);
                } else {
                    drawLine(webMercatorBoundingBox, canvas, line);
                }

                easting = easting - gridSize > minEasting ? easting - gridSize : minEasting;
            } while (easting > minEasting);
        }

        // go right, don't include center line
        if (maxLon >= centerLongitude) {
            double easting = GeoUtility.latLngToUtm(minLat, minLon).easting;
            easting = Math.floor(easting / gridSize) * gridSize;
            easting = Math.max(easting, CENTER_EASTING);

            double maxEasting = GeoUtility.latLngToUtm(minLat, maxLon).easting;
            maxEasting = Math.ceil(maxEasting / gridSize) * gridSize;

            double zoneMaxEasting = (2 * CENTER_EASTING) - GeoUtility.latLngToUtm(latLng.latitude, endLon).easting;
            maxEasting = maxEasting < zoneMaxEasting ? maxEasting : zoneMaxEasting;

            while (easting < maxEasting) {
                LatLng p1 = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, startNorthing);
                LatLng p2 = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, endNorthing);
                PolylineOptions line = new PolylineOptions().add(p1).add(p2);

                LatLng verticalIntersection = intersect(line, new PolylineOptions().add(new LatLng(minLat, minLon)).add(new LatLng(minLat, maxLon)));
                if (verticalIntersection != null) {
                    p1 = verticalIntersection;
                }

                verticalIntersection = intersect(line, new PolylineOptions().add(new LatLng(maxLat, minLon)).add(new LatLng(maxLat, maxLon)));
                if (verticalIntersection != null) {
                    p2 = verticalIntersection;
                }

                line = new PolylineOptions().add(p1).add(p2);

                drawLine(webMercatorBoundingBox, canvas, line);

                easting = easting + gridSize < maxEasting ? easting + gridSize : maxEasting;
            }
        }
    }

    private void latitudeLinesForGZDZone(Map.Entry<Character, Pair<Double, Double>> latitiudeGZDZone, Map.Entry<Integer, Pair<Double, Double>> longitudeGZDZone, int gridSize, double[] webMercatorBoundingBox, double[] bbox, Canvas canvas) {

        Character zoneLetter = latitiudeGZDZone.getKey();
        Integer zoneNumber = longitudeGZDZone.getKey();

        Double minLat = Math.max(bbox[1], latitiudeGZDZone.getValue().first);
        Double maxLat = Math.min(bbox[3], latitiudeGZDZone.getValue().second);

        Double minLon = Math.max(bbox[0], longitudeGZDZone.getValue().first);
        Double maxLon = Math.min(bbox[2], longitudeGZDZone.getValue().second);

        Double centerLongitude = Math.abs((maxLon - minLon) / 2) + minLon;
        GeoUtility.UTM utm = GeoUtility.latLngToUtm(minLat, centerLongitude);
        double northing = Math.ceil(utm.northing / gridSize) * gridSize;

        double maxNorthing;
        if (zoneLetter.equals('M')) {
            maxNorthing = 10000000.0;
        } else {
            maxNorthing = GeoUtility.latLngToUtm(maxLat, centerLongitude).northing;
        }

        do {
            // go left including center
            LatLng latLng = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, utm.easting, northing);

            double easting = GeoUtility.latLngToUtm(latLng.latitude, maxLon).easting;
            easting = Math.ceil(easting / gridSize) * gridSize;
            easting = Math.min(easting, CENTER_EASTING);

            double minEasting = GeoUtility.latLngToUtm(latLng.latitude, minLon).easting;
            minEasting = Math.floor(minEasting / gridSize) * gridSize;
            double zoneMinEasting = GeoUtility.latLngToUtm(latLng.latitude, longitudeGZDZone.getValue().first).easting;
            minEasting = minEasting > zoneMinEasting ? minEasting : zoneMinEasting;

            do {
                double newEasting = easting - gridSize > minEasting ? easting - gridSize : minEasting;

                LatLng p1 = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, easting, northing);
                LatLng p2 = GeoUtility.utmToLatLng(zoneNumber, zoneLetter, newEasting, northing);
                PolylineOptions line = new PolylineOptions().add(p1).add(p2);
                drawLine(webMercatorBoundingBox, canvas, line);

                easting = newEasting;
            } while (easting > minEasting);

            // go right
            easting = GeoUtility.latLngToUtm(latLng.latitude, minLon).easting;
            easting = Math.floor(easting / gridSize) * gridSize;
            easting = Math.max(easting, CENTER_EASTING);

            double maxEasting = GeoUtility.latLngToUtm(latLng.latitude, maxLon).easting;
            maxEasting = Math.ceil(maxEasting / gridSize) * gridSize;
            double zoneMaxEasting = (2 * CENTER_EASTING) - GeoUtility.latLngToUtm(latLng.latitude, longitudeGZDZone.getValue().second).easting;
            maxEasting = maxEasting < zoneMaxEasting ? maxEasting : zoneMaxEasting;

            do {
                double newEasting = easting + gridSize < maxEasting ? easting + gridSize : maxEasting;

                LatLng p1 = GeoUtility.utmToLatLng(utm.zoneNumber, utm.zoneLetter, easting, northing);
                LatLng p2 = GeoUtility.utmToLatLng(utm.zoneNumber, utm.zoneLetter, newEasting, northing);
                PolylineOptions line = new PolylineOptions().add(p1).add(p2);
                drawLine(webMercatorBoundingBox, canvas, line);

                easting = newEasting;
            } while (easting < maxEasting);

            northing += gridSize;
        } while (northing < maxNorthing);
    }

    /**
     * Draw the shape on the canvas
     *
     * @param boundingBox
     * @param canvas
     */
    private void drawLine(double[] boundingBox, Canvas canvas, PolylineOptions line) {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);

        Path linePath = new Path();
        addPolyline(boundingBox, linePath, line);
        canvas.drawPath(linePath, paint);
    }

    private void drawLine(double[] boundingBox, Canvas canvas, PolylineOptions line, int color) {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);

        Path linePath = new Path();
        addPolyline(boundingBox, linePath, line);
        canvas.drawPath(linePath, paint);
    }

    /**
     * Add the polyline to the path
     *
     * @param boundingBox
     * @param path
     * @param path
     * @param polylineOptions
     */
    private void addPolyline(double[] boundingBox, Path path, PolylineOptions polylineOptions) {
        List<LatLng> points = polylineOptions.getPoints();
        if (points.size() >= 2) {

            for (int i = 0; i < points.size(); i++) {
                LatLng latLng = points.get(i);
                double[] meters = degreesToMeters(latLng);

                float x = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox, meters[0]);
                float y = TileBoundingBoxUtils.getYPixel(tileHeight, boundingBox, meters[1]);

                if (i == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
        }
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
        return new double[] {x, y};
    }

    private static LatLng intersect(PolylineOptions line1, PolylineOptions line2) {
        List<LatLng> line1Pts = line1.getPoints();
        List<LatLng> line2Pts = line2.getPoints();

        LatLng pt1 = line1Pts.get(0);
        LatLng pt2 = line1Pts.get(line1Pts.size() - 1);
        LatLng pt3 = line2Pts.get(0);
        LatLng pt4 = line2Pts.get(line2Pts.size() - 1);

        double x1 = pt1.longitude;
        double y1 = pt1.latitude;
        double x2 = pt2.longitude;
        double y2 = pt2.latitude;
        double x3 = pt3.longitude;
        double y3 = pt3.latitude;
        double x4 = pt4.longitude;
        double y4 = pt4.latitude;

        double denominator = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (denominator == 0) {
            return null;
        }

        double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denominator;
        double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denominator;
        double x = x1 + ua * (x2 - x1);
        double y = y1 + ua * (y2 - y1);

        if (y < -90 || y > 90) {
            return null;
        } else if (x < -180 || x > 180) {
            return null;
        } else {
            return new LatLng(y, x);
        }
    }

    private static PolylineOptions horizontalIntersection(PolylineOptions line, double startLon, double endLon) {
        List<LatLng> pts = line.getPoints();
        LatLng pt1 = pts.get(0);
        LatLng pt2 = pts.get(pts.size() - 1);

        double slope = (pt1.latitude - pt2.latitude) / (pt1.longitude - pt2.longitude);
        if (pt2.longitude > endLon) {
            double latitude = pt1.latitude + (slope * (endLon - pt1.longitude));
            pt2 = new LatLng(latitude, endLon);
        }

        if (pt2.longitude < startLon) {
            double latitude = pt1.latitude + (slope * (startLon - pt1.longitude));
            pt2 = new LatLng(latitude, startLon);
        }

        return new PolylineOptions().add(pt1).add(pt2);
    }

}
