package mil.nga.mgrs;

/**
 * Created by wnewman on 11/17/16.
 */
public class TileBoundingBoxUtils {

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
}
