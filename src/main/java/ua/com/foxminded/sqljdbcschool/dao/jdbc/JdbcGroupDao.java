package ua.com.foxminded.sqljdbcschool.dao.jdbc;

import ua.com.foxminded.sqljdbcschool.dao.exception.DaoException;
import ua.com.foxminded.sqljdbcschool.dao.interfaces.GroupDao;
import ua.com.foxminded.sqljdbcschool.models.Group;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcGroupDao implements GroupDao {

    private static final String SQL_INSERT = "INSERT INTO groups (group_name) VALUES (?);";
    private static final String SQL_FIND_ALL = "SELECT * FROM groups;";
    private static final String SQL_UPDATE = "UPDATE groups SET group_name = ? WHERE group_id = ?;";
    private static final String SQL_DELETE = "DELETE FROM groups WHERE group_id = ?;";


    private static final String SQL_SELECT_GROUPS_WITH_LESS_OR_EQUALS_STUDENTS =
            "SELECT gr.group_id, gr.group_name,  count(*) AS quantity " +
                    "FROM students AS st " +
                    "JOIN groups AS gr ON gr.group_id = st.group_id " +
                    "GROUP BY gr.group_id " +
                    "HAVING count(*) <= ? " +
                    "ORDER BY quantity DESC;";

    private final DaoFactory daoFactory;

    public JdbcGroupDao(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    public List<Group> getAllGroups() {
        List<Group> groups = new ArrayList<>();
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_ALL);
             ResultSet resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                groups.add(map(resultSet));
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return groups;
    }

    @Override
    public List<Group> getGroupsWithLessOrEqualsStudents(int numOfStudents) {
        if (numOfStudents < 0) {
            throw new IllegalArgumentException("Number of students cannot be less than zero!");
        }
        List<Group> groups = new ArrayList<>();
        Object[] values = {
                numOfStudents
        };
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = daoFactory.prepareStatement(connection, SQL_SELECT_GROUPS_WITH_LESS_OR_EQUALS_STUDENTS, false, values);
             ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                groups.add(map(resultSet));
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return groups;
    }

    @Override
    public void create(Group group) {
        if (group.getId() != null) {
            throw new IllegalArgumentException("Group is already created, the student ID is not NULL.");
        }
        Object[] values = {
                group.getName()
        };
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = daoFactory.prepareStatement(connection, SQL_INSERT, true, values)
        ) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Creating group failed, no rows affected.");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    group.setId(generatedKeys.getLong(1));
                } else {
                    throw new DaoException("Creating student failed, no generated key obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void update(Group group) {
        if (group.getId() == null) {
            throw new IllegalArgumentException("Group is not created yet, the group ID is null.");
        }
        Object[] values = {
                group.getName(),
                group.getId()
        };
        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = daoFactory.prepareStatement(connection, SQL_UPDATE, false, values)
        ) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Updating group failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void delete(Group group) {
        Object[] values = {
                group.getId()
        };
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = daoFactory.prepareStatement(connection, SQL_DELETE, false, values)
        ) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Deleting group failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static Group map(ResultSet resultSet) throws SQLException {
        Group group = new Group();
        group.setId(resultSet.getLong("group_id"));
        group.setName(resultSet.getString("group_name"));
        return group;
    }
}