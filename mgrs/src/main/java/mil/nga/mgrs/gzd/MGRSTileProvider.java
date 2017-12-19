package mil.nga.mgrs.gzd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import mil.nga.mgrs.Label;
import mil.nga.mgrs.MGRSTile;
import mil.nga.mgrs.R;
import mil.nga.mgrs.wgs84.Line;

/**
 * Created by wnewman on 1/5/17.
 */
public class MGRSTileProvider implements TileProvider {

    public enum GridName {
        TEN_METER(10),
        HUNDRED_METER(100),
        KILOMETER(1000),
        TEN_KILOMETER(10000),
        HUNDRED_KILOMETER(100000),
        GZD(0);

        public int precision;

        GridName(int precision) {
            this.precision = precision;
        }
    }

    private int tileWidth;
    private int tileHeight;

    private static Map<GridName, Grid> grids = new TreeMap<GridName, Grid>() {{
        put(GridName.GZD, new Grid(GridName.GZD, 0, Integer.MAX_VALUE, Color.rgb(239, 83, 80)));
        put(GridName.HUNDRED_KILOMETER, new Grid(GridName.HUNDRED_KILOMETER, 5, Integer.MAX_VALUE, Color.rgb(76, 175, 80)));
        put(GridName.TEN_KILOMETER, new Grid(GridName.TEN_KILOMETER, 9, 11, Color.GRAY));
        put(GridName.KILOMETER, new Grid(GridName.KILOMETER, 12, 14, Color.GRAY));
        put(GridName.HUNDRED_METER, new Grid(GridName.HUNDRED_METER, 15, 17, Color.GRAY));
        put(GridName.TEN_METER, new Grid(GridName.TEN_METER, 18, Integer.MAX_VALUE, Color.GRAY));
    }};

    public MGRSTileProvider(Context context) {
        tileWidth = context.getResources().getInteger(R.integer.tile_width);
        tileHeight = context.getResources().getInteger(R.integer.tile_height);
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {


        Bitmap bitmap = drawTile(x, y, zoom);
        if (bitmap == null) return null;

        byte[] bytes = null;
        try {
            bytes = toBytes(bitmap);
        } catch (IOException e) {
        }

        Tile tile = new Tile(tileWidth, tileHeight, bytes);

        return tile;
    }

    private Bitmap drawTile(int x, int y, int zoom) {
        Bitmap bitmap = Bitmap.createBitmap(tileWidth, tileHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        MGRSTile mgrsTile = new MGRSTile(tileWidth, tileHeight, x, y, zoom);

        Collection<GridZoneDesignator> zones = GZDZones.zonesWithin(mgrsTile.getBoundingBox());

        for (Grid grid : grids.values()) {
            if (grid.contains(zoom)) {
                // draw this grid for each zone
                for (GridZoneDesignator zone : zones) {
                    Collection<Line> lines = zone.lines(mgrsTile.getBoundingBox(), grid.getPrecision());
                    grid.drawLines(lines, mgrsTile, zone, canvas);

                    if (grid.getGridName() == GridName.GZD && zoom > 3) {
                        Collection<Label> labels = zone.labels(mgrsTile.getBoundingBox(), grid.getPrecision());
                        grid.drawLabels(labels, mgrsTile, zone, canvas);
                    }

                    if (grid.getGridName() == GridName.HUNDRED_KILOMETER && zoom > 5) {
                        Collection<Label> labels = zone.labels(mgrsTile.getBoundingBox(), grid.getPrecision());
                        grid.drawLabels(labels, mgrsTile, zone, canvas);
                    }
                }
            }
        }

        return bitmap;
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
}
