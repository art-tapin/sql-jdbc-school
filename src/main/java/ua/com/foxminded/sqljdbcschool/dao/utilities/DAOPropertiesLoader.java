package ua.com.foxminded.sqljdbcschool.dao.utilities;

import ua.com.foxminded.sqljdbcschool.dao.exception.DaoConfigurationException;

import java.io.IOException;
import java.util.Properties;

public class DAOPropertiesLoader {

    private static final String PROPERTIES_FILE = "database.properties";
    private static final Properties PROPERTIES = new Properties();
    private final String specificDBKeyPrefix;

    /**
     * Loads database properties from .properties file
     * @param specificKeyPrefix full prefix of each property in .properties file (ex: test_db.jdbc)
     * @throws DaoConfigurationException if wrong key-prefix added
     */
    public DAOPropertiesLoader(String specificKeyPrefix) throws DaoConfigurationException {
        this.specificDBKeyPrefix = specificKeyPrefix;
    }

    static {
        var inputStream = DAOPropertiesLoader.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
        if (inputStream == null) {
            throw new DaoConfigurationException("Properties file '" + PROPERTIES_FILE + "' is missing in classpath.");
        }
        try {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new DaoConfigurationException("Cannot load properties file '" + PROPERTIES_FILE + "'.", e);
        }
    }

    /**
     * Returns specific property form .properties file by requested key
     * @param key database property (url, username, password, etc.)
     * @return property value
     * @throws DaoConfigurationException if property is missing
     */
    public String getProperty(String key) throws DaoConfigurationException {
        String fullKey = specificDBKeyPrefix + "." + key;
        String property = PROPERTIES.getProperty(fullKey);
        if (property == null || property.trim().length() == 0) {
            throw new DaoConfigurationException("Required property '" + fullKey + "'"
                    + " is missing in properties file '" + PROPERTIES_FILE + "'.");
        }
        return property;
    }
}
