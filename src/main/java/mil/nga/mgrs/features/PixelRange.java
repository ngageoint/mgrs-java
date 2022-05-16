package mil.nga.mgrs.features;

/**
 * Pixel Range
 * 
 * @author osbornb
 */
public class PixelRange {

	/**
	 * Top left pixel
	 */
	private Pixel topLeft;

	/**
	 * Bottom right pixel
	 */
	private Pixel bottomRight;

	/**
	 * Constructor
	 * 
	 * @param topLeft
	 *            top left pixel
	 * @param bottomRight
	 *            bottom right pixel
	 */
	public PixelRange(Pixel topLeft, Pixel bottomRight) {
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
	}

	/**
	 * Get the top left pixel
	 * 
	 * @return top left pixel
	 */
	public Pixel getTopLeft() {
		return topLeft;
	}

	/**
	 * Set the top left pixel
	 * 
	 * @param topLeft
	 *            top left pixel
	 */
	public void setTopLeft(Pixel topLeft) {
		this.topLeft = topLeft;
	}

	/**
	 * Get the bottom right pixel
	 * 
	 * @return bottom right pixel
	 */
	public Pixel getBottomRight() {
		return bottomRight;
	}

	/**
	 * Set the bottom right pixel
	 * 
	 * @param bottomRight
	 *            bottom right pixel
	 */
	public void setBottomRight(Pixel bottomRight) {
		this.bottomRight = bottomRight;
	}

	/**
	 * Get the minimum x pixel
	 * 
	 * @return minimum x pixel
	 */
	public float getMinX() {
		return topLeft.getX();
	}

	/**
	 * Get the minimum y pixel
	 * 
	 * @return minimum y pixel
	 */
	public float getMinY() {
		return topLeft.getY();
	}

	/**
	 * Get the maximum x pixel
	 * 
	 * @return maximum x pixel
	 */
	public float getMaxX() {
		return bottomRight.getX();
	}

	/**
	 * Get the maximum y pixel
	 * 
	 * @return maximum y pixel
	 */
	public float getMaxY() {
		return bottomRight.getY();
	}

	/**
	 * Get the left pixel
	 * 
	 * @return left pixel
	 */
	public float getLeft() {
		return getMinX();
	}

	/**
	 * Get the top pixel
	 * 
	 * @return top pixel
	 */
	public float getTop() {
		return getMinY();
	}

	/**
	 * Get the right pixel
	 * 
	 * @return right pixel
	 */
	public float getRight() {
		return getMaxX();
	}

	/**
	 * Get the bottom pixel
	 * 
	 * @return bottom pixel
	 */
	public float getBottom() {
		return getMaxY();
	}

	/**
	 * Get the pixel width
	 * 
	 * @return pixel width
	 */
	public float getWidth() {
		return getMaxX() - getMinX();
	}

	/**
	 * Get the pixel height
	 * 
	 * @return pixel height
	 */
	public float getHeight() {
		return getMaxY() - getMinY();
	}

}
