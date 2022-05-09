package mil.nga.mgrs;


import mil.nga.mgrs.wgs84.LatLng;

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
