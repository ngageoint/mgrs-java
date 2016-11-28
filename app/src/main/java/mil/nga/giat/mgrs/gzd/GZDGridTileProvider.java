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

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import mil.nga.giat.mgrs.LatLng;
import mil.nga.giat.mgrs.R;
import mil.nga.giat.mgrs.TileBoundingBoxUtils;

/**
 * Created by wnewman on 11/17/16.
 */
public class GZDGridTileProvider implements TileProvider {

    /**
     * Half the world distance in either direction
     */
    public static double WEB_MERCATOR_HALF_WORLD_WIDTH = 20037508.342789244;

    private static int TEXT_SIZE = 16;

    private Context context;
    private int tileWidth;
    private int tileHeight;

    public GZDGridTileProvider(Context context) {
        this.context = context;

        tileWidth = context.getResources().getInteger(R.integer.tile_width);
        tileHeight = context.getResources().getInteger(R.integer.tile_height);
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {

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
            for (Map.Entry<Integer, Pair<Double, Double>> longitudeGZDZone : longitudeZones.entrySet()) {
                Double minLat = latitiudeGZDZone.getValue().first;
                Double maxLat = latitiudeGZDZone.getValue().second;

                Double minLon = longitudeGZDZone.getValue().first;
                Double maxLon = longitudeGZDZone.getValue().second;

                drawLine(webMercatorBoundingBox, canvas, new Line(new LatLng(maxLat, minLon), new LatLng(maxLat, maxLon)), Color.RED);
                drawLine(webMercatorBoundingBox, canvas, new Line(new LatLng(minLat, maxLon), new LatLng(maxLat, maxLon)), Color.RED);

                if (latitiudeGZDZone.getKey().equals('C')) {
                    drawLine(webMercatorBoundingBox, canvas, new Line(new LatLng(minLat, minLon), new LatLng(minLat, maxLon)), Color.RED);
                }

                if (zoom > 3) {
                    drawName(bitmap, longitudeGZDZone, latitiudeGZDZone, webMercatorBoundingBox, canvas);
                }
            }
        }

        return bitmap;
    }

    /**
     * Draw the shape on the canvas
     *
     * @param boundingBox
     * @param canvas
     */
    private void drawLine(double[] boundingBox, Canvas canvas, Line line, int color) {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setAlpha(64);

        Path linePath = new Path();
        addPolyline(boundingBox, linePath, line);
        canvas.drawPath(linePath, paint);

    }

    private void drawName(Bitmap bitmap, Map.Entry<Integer, Pair<Double, Double>> longitudeGZDZone, Map.Entry<Character, Pair<Double, Double>> latitudeGZDZone, double[] boundingBox, Canvas canvas) {

        String name = longitudeGZDZone.getKey().toString() + latitudeGZDZone.getKey();
        Log.e("GZD GRID NAME", name);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setAlpha(64);
        paint.setTextSize(TEXT_SIZE * context.getResources().getDisplayMetrics().density);

        // Determine the text bounds
        Rect textBounds = new Rect();
        paint.getTextBounds(name, 0, name.length(), textBounds);

        // Determine the center of the tile
        // TODO determine the center of the bounding box for this grid
        double centerLon = ((longitudeGZDZone.getValue().second - longitudeGZDZone.getValue().first) / 2.0) + longitudeGZDZone.getValue().first;
        double centerLat = ((latitudeGZDZone.getValue().second - latitudeGZDZone.getValue().first) / 2.0) + latitudeGZDZone.getValue().first;

        double[] meters = degreesToMeters(new LatLng(centerLat, centerLon));
        float x = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox, meters[0]);
        float y = TileBoundingBoxUtils.getYPixel(tileHeight, boundingBox, meters[1]);

        // Draw the text
        canvas.drawText(name, x - textBounds.exactCenterX(), y - textBounds.exactCenterY(), paint);
    }

    /**
     * Add the polyline to the path
     *
     * @param boundingBox
     * @param path
     * @param line
     */
    private void addPolyline(double[] boundingBox, Path path, Line line) {

        double[] meters = degreesToMeters(line.p1);
        float x = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox, meters[0]);
        float y = TileBoundingBoxUtils.getYPixel(tileHeight, boundingBox, meters[1]);
        path.moveTo(x, y);

        meters = degreesToMeters(line.p2);
        x = TileBoundingBoxUtils.getXPixel(tileWidth, boundingBox, meters[0]);
        y = TileBoundingBoxUtils.getYPixel(tileHeight, boundingBox, meters[1]);
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
            tileToLatitude(y+1, zoom),
            tileToLongitue(x+1, zoom),
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

    private boolean gzdWithinBounds(Pair<Double, Double> gzdZone, double[] bounds) {

        return false;
    }
}
