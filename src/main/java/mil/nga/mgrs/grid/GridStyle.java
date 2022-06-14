package mil.nga.mgrs.grid;

import mil.nga.mgrs.color.Color;

/**
 * Grid Line Style
 * 
 * @author osbornb
 */
public class GridStyle {

	/**
	 * Grid line color
	 */
	private Color color;

	/**
	 * Grid line width
	 */
	private double width;

	/**
	 * Create a new style
	 * 
	 * @param color
	 *            color
	 * @param width
	 *            width
	 * @return style
	 */
	public static GridStyle style(Color color, double width) {
		return new GridStyle(color, width);
	}

	/**
	 * Constructor
	 */
	public GridStyle() {

	}

	/**
	 * Constructor
	 * 
	 * @param color
	 *            color
	 * @param width
	 *            width
	 */
	public GridStyle(Color color, double width) {
		this.color = color;
		this.width = width;
	}

	/**
	 * Get the grid line color
	 * 
	 * @return grid line color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Set the grid line color
	 * 
	 * @param color
	 *            grid line color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Get the grid line width
	 * 
	 * @return grid line width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Set the grid line width
	 * 
	 * @param width
	 *            grid line width
	 */
	public void setWidth(double width) {
		this.width = width;
	}

}
