package mil.nga.giat.mgrs;

/**
 * Created by wnewman on 1/5/17.
 */
public class MGRSTile {

    /**
     * Half the world distance in either direction
     */
    private static double WEB_MERCATOR_HALF_WORLD_WIDTH = 20037508.342789244;

    private int width;
    private int height;

    private double[] boundingBox;
    private double[] webMercatorBoundingBox;

    public MGRSTile(int width, int height, int x, int y, int zoom) {
        this.width = width;
        this.height = height;

        boundingBox = getBoundingBox(x, y, zoom);
        webMercatorBoundingBox = getWebMercatorBoundingBox(x, y, zoom);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double[] getBoundingBox() {
        return boundingBox;
    }

    public double[] getWebMercatorBoundingBox() {
        return webMercatorBoundingBox;
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
    private double[] getBoundingBox(long x, long y, int zoom) {
        return new double[] {
                tileToLongitue(x, zoom),
                tileToLatitude(y + 1, zoom),
                tileToLongitue(x + 1, zoom),
                tileToLatitude(y, zoom),
        };
    }

    private double tileToLongitue(long x, long zoom) {
        return (x / Math.pow(2, zoom) * 360 - 180);
    }

    private double tileToLatitude(long y, long zoom) {
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
    private double[] getWebMercatorBoundingBox(long x, long y, int zoom) {

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
     * @param zoom zoom level
     * @return tiles per side
     */
    private int tilesPerSide(int zoom) {
        return (int) Math.pow(2, zoom);
    }

    /**
     * Get the tile size in meters
     *
     * @param tilesPerSide
     *            tiles per side
     * @return tile size
     */
    private double tileSize(int tilesPerSide) {
        return (2 * WEB_MERCATOR_HALF_WORLD_WIDTH) / tilesPerSide;
    }
}
