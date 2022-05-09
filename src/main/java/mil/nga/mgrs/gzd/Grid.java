package mil.nga.mgrs.gzd;

/**
 * Created by wnewman on 1/5/17.
 */
public enum Grid {
	
    TEN_METER(10, 18, Integer.MAX_VALUE),
    HUNDRED_METER(100, 15, 17),
    KILOMETER(1000, 12, 14),
    TEN_KILOMETER(10000, 9, 11),
    HUNDRED_KILOMETER(100000, 5, Integer.MAX_VALUE),
    GZD(0, 0, Integer.MAX_VALUE);
	
    public int precision;
    private int minZoom;
    private int maxZoom;

    private Grid(int precision, int minZoom, int maxZoom) {
    	this.precision = precision;
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
    }

    public int getPrecision() {
        return precision;
    }

    public int getMinZoom() {
    	return minZoom;
    }

    public int getMaxZoom() {
    	return maxZoom;
    }
    
    public boolean contains(int zoom) {
        return zoom >= minZoom && zoom <= maxZoom;
    }

}
