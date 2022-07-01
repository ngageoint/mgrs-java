package mil.nga.mgrs.property;

import mil.nga.grid.property.GridProperties;

/**
 * MGRS property loader
 * 
 * @author osbornb
 */
public class MGRSProperties extends GridProperties {

	/**
	 * Property file name
	 */
	public static final String PROPERTIES_FILE = "mgrs.properties";

	/**
	 * Singleton instance
	 */
	public static MGRSProperties instance = new MGRSProperties();

	/**
	 * Get the singleton instance
	 * 
	 * @return instance
	 */
	public static MGRSProperties getInstance() {
		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFile() {
		return PROPERTIES_FILE;
	}

}
