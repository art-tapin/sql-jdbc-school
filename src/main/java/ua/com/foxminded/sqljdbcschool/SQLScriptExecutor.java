package ua.com.foxminded.sqljdbcschool;

import ua.com.foxminded.sqljdbcschool.dao.utilities.DAOPropertiesLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SQLScriptExecutor {

    private static final String URL = "url";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    public void execute(String scriptFilename, String databaseName) {
        DAOPropertiesLoader propertiesLoader = new DAOPropertiesLoader(databaseName);
        String script = readScriptFromFile(scriptFilename);

        Properties properties = new Properties();
        properties.setProperty(URL, propertiesLoader.getProperty(URL));
        properties.setProperty(USERNAME, propertiesLoader.getProperty(USERNAME));
        properties.setProperty(PASSWORD, propertiesLoader.getProperty(PASSWORD));
        try (Connection connection = getConnection(properties)) {
            if (connection == null) {
                throw new SQLException("Connection is NULL!");
            }
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(script);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection(Properties properties) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    properties.getProperty(URL),
                    properties.getProperty(USERNAME),
                    properties.getProperty(PASSWORD)
            );
        } catch (SQLException e) {
            System.err.println("Cannot get a connection");
            e.printStackTrace();
        }
        return connection;
    }

    private String readScriptFromFile(String fileName) {
        String script;
        try (Stream<String> fileStream = getFileFromResourcesAsStream(fileName)) {
            script = fileStream.collect(Collectors.joining(System.lineSeparator()));
        }
        return script;
    }

    private Stream<String> getFileFromResourcesAsStream(String fileName) {
        var inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found! File: " + fileName);
        } else {
            return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines();
        }
    }
}
