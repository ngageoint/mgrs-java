package mil.nga.mgrs.gzd;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.util.Log;

import java.util.Collection;

import mil.nga.mgrs.Label;
import mil.nga.mgrs.wgs84.LatLng;
import mil.nga.mgrs.MGRSTile;
import mil.nga.mgrs.TileBoundingBoxUtils;
import mil.nga.mgrs.wgs84.Line;
import mil.nga.mgrs.wgs84.Point;

/**
 * Created by wnewman on 1/5/17.
 */
public class Grid {

    private MGRSTileProvider.GridName gridName;
    private int minZoom;
    private int maxZoom;
    private int color;

    private Paint linePaint;
    private Paint gzdLabelPaint;
    private Paint oneHundredKLabelPaint;

    public Grid(MGRSTileProvider.GridName gridName, int minZoom, int maxZoom, int color) {
        this.gridName = gridName;
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
        this.color = color;

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(2);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(color);

        gzdLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gzdLabelPaint.setColor(color);
        gzdLabelPaint.setTextSize(32);

        oneHundredKLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        oneHundredKLabelPaint.setColor(color);
        oneHundredKLabelPaint.setTextSize(24);
        oneHundredKLabelPaint.setTypeface(Typeface.MONOSPACE);
    }

    public int getColor() {
        return color;
    }

    public MGRSTileProvider.GridName getGridName() {
        return gridName;
    }

    public int getPrecision() {
        return gridName.precision;
    }

    public boolean contains(int zoom) {
        return zoom >= minZoom && zoom <= maxZoom;
    }

    public void drawLines(Collection<Line> lines, MGRSTile tile, GridZoneDesignator zone, Canvas canvas) {
        double[] zoneBounds = zone.zoneBounds();
        Point zoneLowerLeft = new Point(new LatLng(zoneBounds[1], zoneBounds[0]));
        Point zoneUpperRight = new Point(new LatLng(zoneBounds[3], zoneBounds[2]));
        double[] zoneBoundingBox = new double[]{zoneLowerLeft.x, zoneLowerLeft.y, zoneUpperRight.x, zoneUpperRight.y};

        for (Line line : lines) {
            drawLine(line, tile, zoneBoundingBox, canvas);
        }
    }

    public void drawLabels(Collection<Label> labels, MGRSTile tile, GridZoneDesignator zone, Canvas canvas) {
        for (Label label : labels) {
            drawLabel(label, tile, canvas);
        }
    }

    /**
     * Draw the shape on the canvas
     */
    private void drawLine(Line line, MGRSTile tile, double[] clipBox, Canvas canvas) {
        canvas.save();

        double[] bbox = tile.getWebMercatorBoundingBox();
        float left = TileBoundingBoxUtils.getXPixel(tile.getWidth(), bbox, clipBox[0]);
        float top = TileBoundingBoxUtils.getYPixel(tile.getHeight(), bbox, clipBox[3]);
        float right = TileBoundingBoxUtils.getXPixel(tile.getWidth(), bbox, clipBox[2]);
        float bottom = TileBoundingBoxUtils.getYPixel(tile.getHeight(), bbox, clipBox[1]);
        canvas.clipRect(left, top, right, bottom, Region.Op.INTERSECT);

        Path linePath = new Path();
        addPolyline(tile, linePath, line);
        canvas.drawPath(linePath, linePaint);

        canvas.restore();
    }

    /**
     * Add the polyline to the path
     *
     * @param path
     * @param path
     * @param line
     */
    private void addPolyline(MGRSTile tile, Path path, Line line) {
        double[] bbox = tile.getWebMercatorBoundingBox();
        float x = TileBoundingBoxUtils.getXPixel(tile.getWidth(), bbox, line.p1.x);
        float y = TileBoundingBoxUtils.getYPixel(tile.getHeight(), bbox, line.p1.y);
        path.moveTo(x, y);

        x = TileBoundingBoxUtils.getXPixel(tile.getWidth(), bbox, line.p2.x);
        y = TileBoundingBoxUtils.getYPixel(tile.getHeight(), bbox, line.p2.y);
        path.lineTo(x, y);
    }

    private void drawLabel(Label label, MGRSTile tile, Canvas canvas) {
        // Determine the text bounds
        Rect textBounds = new Rect();
        oneHundredKLabelPaint.getTextBounds(label.getName(), 0, label.getName().length(), textBounds);

        double[] tileBoundingBox = tile.getWebMercatorBoundingBox();
        double[] labelBoundingBox = label.getBoundingBox();
        float minX = TileBoundingBoxUtils.getXPixel(tile.getWidth(), tileBoundingBox, labelBoundingBox[0]);
        float maxX = TileBoundingBoxUtils.getXPixel(tile.getWidth(), tileBoundingBox, labelBoundingBox[2]);
        float zoneWidth = maxX - minX;

        float minY = TileBoundingBoxUtils.getYPixel(tile.getHeight(), tileBoundingBox, labelBoundingBox[3]);
        float maxY = TileBoundingBoxUtils.getYPixel(tile.getHeight(), tileBoundingBox, labelBoundingBox[1]);
        float zoneHeight = maxY - minY;

        float x = TileBoundingBoxUtils.getXPixel(tile.getWidth(), tileBoundingBox, label.getCenter().x);
        float y = TileBoundingBoxUtils.getYPixel(tile.getHeight(), tileBoundingBox, label.getCenter().y);

        if (label.getName().equals("KV") || label.getName().equals("GE")) {
            Log.i("", "");
        }

        float textWidth =oneHundredKLabelPaint.measureText(label.getName(), 0, label.getName().length());

        double textWidthPercent = textWidth * 2 / zoneWidth;
        double textHeightPercent = textBounds.height() * 2 / zoneHeight;

//        if (zoneWidth > textBounds.width() + 20 && zoneHeight > textBounds.height() + 20) {
        if (textWidthPercent < .80 && textHeightPercent < .80 && textBounds.width() < zoneWidth && textBounds.height() < zoneHeight) {
            canvas.drawText(label.getName(), x - textBounds.exactCenterX(), y - textBounds.exactCenterY(), oneHundredKLabelPaint);
        }
    }
}
