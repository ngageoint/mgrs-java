package mil.nga.mgrs;

import mil.nga.mgrs.features.LatLng;

/**
 * Created by wnewman on 11/17/16.
 */
public class TileBoundingBoxUtils {

    /**
     * Half the world distance in either direction
     */
    public static double WEB_MERCATOR_HALF_WORLD_WIDTH = 20037508.342789244;
	
    /**
     * Get the X pixel for where the longitude fits into the bounding box
     *
     * @param width
     *            width
     * @param boundingBox
     *            bounding box
     * @param x
     *            x
     * @return x pixel
     */
    public static float getXPixel(long width, double[] boundingBox, double x) {

        double boxWidth = boundingBox[2] - boundingBox[0];
        double offset = x - boundingBox[0];
        double percentage = offset / boxWidth;
        float pixel = (float) (percentage * width);

        return pixel;
    }

    /**
     * Get the Y pixel for where the latitude fits into the bounding box
     *
     * @param height
     *            height
     * @param boundingBox
     *            bounding box
     * @param y
     *            y
     * @return y pixel
     */
    public static float getYPixel(long height, double[] boundingBox, double y) {

        double boxHeight = boundingBox[3] - boundingBox[1];
        double offset = boundingBox[3] - y;
        double percentage = offset / boxHeight;
        float pixel = (float) (percentage * height);

        return pixel;
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
    
}
