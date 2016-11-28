package mil.nga.giat.mgrs.gzd;


import mil.nga.giat.mgrs.LatLng;

/**
 * Created by wnewman on 11/26/16.
 */
public class Line {
    public final LatLng p1;
    public final LatLng p2;

    public Line(LatLng p1, LatLng p2) {
        this.p1 = p1;
        this.p2 = p2;
    }
}
