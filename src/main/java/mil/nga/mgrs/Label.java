package mil.nga.mgrs;


import mil.nga.mgrs.features.Point;

/**
 * Created by wnewman on 1/5/17.
 */
public class Label {

    private String name;
    private Point center;
    double[] boundingBox;
    Character zoneLetter;
    Integer zoneNumber;

    public Label(String name, Point center, double[] boundingBox, Character zoneLetter, Integer zoneNumber) {
        this.name = name;
        this.center = center;
        this.boundingBox = boundingBox;

        this.zoneLetter = zoneLetter;
        this.zoneNumber = zoneNumber;
    }

    public String getName() {
        return name;
    }

    public Point getCenter() {
        return center;
    }

    public double[] getBoundingBox() {
        return boundingBox;
    }
}
