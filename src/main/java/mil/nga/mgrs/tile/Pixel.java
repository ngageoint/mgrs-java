package mil.nga.mgrs.tile;

/**
 * Tile Pixel
 * 
 * @author osbornb
 */
public class Pixel {

	/**
	 * X pixel
	 */
	private float x;

	/**
	 * Y pixel
	 */
	private float y;

	/**
	 * Constructor
	 * 
	 * @param x
	 *            x pixel
	 * @param y
	 *            y pixel
	 */
	public Pixel(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Get the x pixel
	 * 
	 * @return x pixel
	 */
	public float getX() {
		return x;
	}

	/**
	 * Set the x pixel
	 * 
	 * @param x
	 *            x pixel
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * Get the y pixel
	 * 
	 * @return y pixel
	 */
	public float getY() {
		return y;
	}

	/**
	 * Set the y pixel
	 * 
	 * @param y
	 *            y pixel
	 */
	public void setY(float y) {
		this.y = y;
	}

}
