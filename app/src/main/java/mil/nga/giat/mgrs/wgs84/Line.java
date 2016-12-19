package mil.nga.giat.mgrs.wgs84;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wnewman on 12/5/16.
 */
public class Line {

    private List<Point> points = new ArrayList<>();

    public Point p1;
    public Point p2;

    public Line(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Line() {

    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public List<Point> getPoints() {
        return points;
    }

}
