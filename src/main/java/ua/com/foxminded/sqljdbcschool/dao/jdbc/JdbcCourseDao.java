package ua.com.foxminded.sqljdbcschool.dao.jdbc;

import ua.com.foxminded.sqljdbcschool.dao.exception.DaoException;
import ua.com.foxminded.sqljdbcschool.dao.interfaces.CourseDao;
import ua.com.foxminded.sqljdbcschool.models.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcCourseDao implements CourseDao {

    private static final String SQL_FIND_ALL = "SELECT * FROM courses;";
    private static final String SQL_INSERT = "INSERT INTO courses (course_name, course_description) VALUES (?, ?);";
    private static final String SQL_UPDATE = "UPDATE courses SET course_name = ?, course_description = ?, WHERE course_id = ?;";
    private static final String SQL_DELETE = "DELETE FROM courses WHERE course_id = ?;";

    private static final String SQL_FIND_ALL_COURSES_RELATED_TO_STUDENT = "SELECT c.course_id, c.course_name, c.course_description " +
            "FROM students_courses AS sc " +
            "JOIN courses AS c on c.course_id = sc.course_id " +
            "WHERE sc.student_id = ?;";

    private final DaoFactory daoFactory;

    JdbcCourseDao(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_ALL);
             ResultSet resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                courses.add(map(resultSet));
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return courses;
    }

    @Override
    public List<Course> getByStudentId(Long studentId) {
        List<Course> courses = new ArrayList<>();
        Object[] values = {
                studentId
        };
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = daoFactory.prepareStatement(
                     connection, SQL_FIND_ALL_COURSES_RELATED_TO_STUDENT, false, values);
             ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                courses.add(map(resultSet));
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return courses;
    }

    @Override
    public void create(Course course) {
        if (course.getId() != null) {
            throw new IllegalArgumentException("Course is already created, the student ID is not NULL.");
        }
        Object[] values = {
                course.getName(),
                course.getDescription()
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
                    course.setId(generatedKeys.getLong(1));
                } else {
                    throw new DaoException("Creating student failed, no generated key obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void update(Course course) {
        if (course.getId() == null) {
            throw new IllegalArgumentException("Course is not created yet, the course ID is null.");
        }
        Object[] values = {
                course.getName(),
                course.getDescription(),
                course.getId()
        };
        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = daoFactory.prepareStatement(connection, SQL_UPDATE, false, values)
        ) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Updating course failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void delete(Course course) {
        Object[] values = {
                course.getId()
        };
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = daoFactory.prepareStatement(connection, SQL_DELETE, false, values)
        ) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Deleting course failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static Course map(ResultSet resultSet) throws SQLException {
        Course course = new Course();
        course.setId(resultSet.getLong("course_id"));
        course.setName(resultSet.getString("course_name"));
        course.setDescription(resultSet.getString("course_description"));
        return course;
    }
}
