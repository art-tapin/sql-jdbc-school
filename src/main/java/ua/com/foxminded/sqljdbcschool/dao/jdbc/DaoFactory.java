package ua.com.foxminded.sqljdbcschool.dao.jdbc;

import ua.com.foxminded.sqljdbcschool.dao.exception.DaoConfigurationException;
import ua.com.foxminded.sqljdbcschool.dao.utilities.DAOPropertiesLoader;
import ua.com.foxminded.sqljdbcschool.dao.interfaces.CourseDao;
import ua.com.foxminded.sqljdbcschool.dao.interfaces.GroupDao;
import ua.com.foxminded.sqljdbcschool.dao.interfaces.StudentDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class represents a DAO factory for a SQL database. You can use {@link #getInstance(String)}
 * to obtain a new instance for the given database name. The specific instance returned depends on
 * the properties file configuration. You can obtain DAO's for the DAO factory instance using the
 * DAO getters.
 */
public abstract class DaoFactory {

    public static final String PROPERTY_DRIVER = "driver";
    public static final String PROPERTY_URL = "url";
    public static final String PROPERTY_USERNAME = "username";
    public static final String PROPERTY_PASSWORD = "password";

    /**
     * Returns a new DAOFactory instance for the given database name.
     * @param databaseName The database name to return a new DAOFactory instance for.
     * @return A new DAOFactory instance for the given database name.
     * @throws DaoConfigurationException If the database name is null, or if the properties file is
     * missing in the classpath or cannot be loaded, or if a required property is missing in the
     * properties file, or if either the driver cannot be loaded or the datasource cannot be found.
     */
    public static DaoFactory getInstance(String databaseName) throws DaoConfigurationException {
        if (databaseName == null) {
            throw new DaoConfigurationException("Database name is NULL.");
        }

        DAOPropertiesLoader properties = new DAOPropertiesLoader(databaseName);
        String url = properties.getProperty(PROPERTY_URL);
        String driverClassName = properties.getProperty(PROPERTY_DRIVER);
        String password = properties.getProperty(PROPERTY_PASSWORD);
        String username = properties.getProperty(PROPERTY_USERNAME);

        DaoFactory instance;
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            throw new DaoConfigurationException("Driver class '" + driverClassName
                    + "'is missing in classpath", e);
        }
        instance = new DriverManagerDaoFactory(url, username, password);
        return instance;
    }

    abstract Connection getConnection() throws SQLException;

    abstract PreparedStatement prepareStatement(Connection connection, String sqlQuery, boolean returnGeneratedKeys, Object... values) throws SQLException;

    public StudentDao getStudentDAO() { return new JdbcStudentDAO(this); }

    public GroupDao getGroupDAO() {
        return new JdbcGroupDao(this);
    }

    public CourseDao getCourseDAO() {
        return new JdbcCourseDao(this);
    }
}
