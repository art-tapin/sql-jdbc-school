package ua.com.foxminded.sqljdbcschool.dao.jdbc;

import java.sql.*;

/**
 * The DriverManager based DAOFactory.
 */
class DriverManagerDaoFactory extends DaoFactory {
    private final String url;
    private final String username;
    private final String password;

    public DriverManagerDaoFactory(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Returns a PreparedStatement of the given connection, set with the given SQL query and the
     * given parameter values.
     * @param connection The Connection to create the PreparedStatement from.
     * @param sqlQuery The SQL query to construct the PreparedStatement with.
     * @param returnGeneratedKeys Set whether to return generated keys or not.
     * @param values The parameter values to be set in the created PreparedStatement.
     * @throws SQLException If something fails during creating the PreparedStatement.
     */
    @Override
    PreparedStatement prepareStatement(Connection connection, String sqlQuery, boolean returnGeneratedKeys, Object... values) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sqlQuery,
                returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
        setValues(statement, values);
        return statement;
    }

    private static void setValues(PreparedStatement statement, Object... values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            int parameterIndex = i + 1;
            statement.setObject(parameterIndex, values[i]);
        }
    }
}
