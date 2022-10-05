package ua.com.foxminded.sqljdbcschool.dao.jdbc;

import ua.com.foxminded.sqljdbcschool.dao.exception.DaoException;
import ua.com.foxminded.sqljdbcschool.dao.interfaces.StudentDao;
import ua.com.foxminded.sqljdbcschool.models.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcStudentDAO implements StudentDao {

    private static final String SQL_FIND = "SELECT student_id, first_name, last_name, group_id FROM students WHERE student_id = ?;";
    private static final String SQL_FIND_ALL = "SELECT * FROM students;";
    private static final String SQL_INSERT = "INSERT INTO students (first_name, last_name, group_id) VALUES (?, ?, ?);";
    private static final String SQL_UPDATE = "UPDATE students SET first_name = ?, last_name = ?, group_id = ? WHERE student_id = ?;";
    private static final String SQL_DELETE = "DELETE FROM students WHERE student_id = ?;";

    private static final String SQL_FIND_STUDENTS_RELATED_TO_COURSE_BY_ID = "SELECT s.student_id, first_name, last_name, group_id " +
            "FROM students_courses AS sc JOIN students AS s ON s.student_id = sc.student_id WHERE course_id = ?;";
    private static final String SQL_ADD_STUDENT_TO_COURSE_BY_ID = "INSERT INTO students_courses VALUES (?, ?);";
    private static final String SQL_DELETE_STUDENT_FROM_COURSE = "DELETE FROM students_courses WHERE student_id = ? AND course_id = ?";

    private final DaoFactory daoFactory;

    public JdbcStudentDAO(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }


    @Override
    public Student getStudentById(Long studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID is NULL!");
        }
        Object[] values = {
                studentId
        };
        Student student = null;
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = daoFactory.prepareStatement(connection, SQL_FIND, false, values);
             ResultSet resultSet = statement.executeQuery();
        ) {
            if (resultSet.next()) {
                student = map(resultSet);
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return student;
    }

    @Override
    public List<Student> getStudentsRelatedToCourse(Long courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("Course ID is NULL!");
        }
        Object[] values = {
                courseId
        };
        List<Student> students = new ArrayList<>();
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = daoFactory.prepareStatement(connection, SQL_FIND_STUDENTS_RELATED_TO_COURSE_BY_ID, false, values);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                students.add(map(resultSet));
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return students;
    }

    @Override
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_ALL);
             ResultSet resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                students.add(map(resultSet));
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return students;

    }

    @Override
    public void addStudentToCourse(Long studentId, Long courseId) {
        if (studentId == null) {
            throw new IllegalArgumentException("Student id is NULL!");
        }
        Object[] values = {
                studentId,
                courseId
        };
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = daoFactory.prepareStatement(connection, SQL_ADD_STUDENT_TO_COURSE_BY_ID, true, values)
        ) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Adding student to a course failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    @Override
    public void removeStudentFromCourse(Long studentId, Long courseId) {
        Object[] values = {
                studentId,
                courseId
        };
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = daoFactory.prepareStatement(connection, SQL_DELETE_STUDENT_FROM_COURSE, false, values)
        ) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Removing student from course failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void create(Student student) {
        if (student.getId() != null) {
            throw new IllegalArgumentException("Student is already created, the student ID is not NULL.");
        }
        Object[] values = {
                student.getFirstName(),
                student.getLastName(),
                student.getGroupId()
        };
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = daoFactory.prepareStatement(connection, SQL_INSERT, true, values)
        ) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Creating student failed, no rows affected.");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    student.setId(generatedKeys.getLong(1));
                } else {
                    throw new DaoException("Creating student failed, no generated key obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void update(Student student) {
        if (student.getId() == null) {
            throw new IllegalArgumentException("Student is not created yet, the student ID is null.");
        }
        Object[] values = {
                student.getFirstName(),
                student.getLastName(),
                student.getGroupId(),
                student.getId()
        };
        try (
                Connection connection = daoFactory.getConnection();
                PreparedStatement statement = daoFactory.prepareStatement(connection, SQL_UPDATE, false, values)
        ) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Updating student failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void delete(Student student) {
        Object[] values = {
                student.getId(),
        };
        try (Connection connection = daoFactory.getConnection();
             PreparedStatement statement = daoFactory.prepareStatement(connection, SQL_DELETE, false, values)
        ) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Deleting student failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static Student map(ResultSet resultSet) throws SQLException {
        Student student = new Student();
        student.setId(resultSet.getLong("student_id"));
        student.setFirstName(resultSet.getString("first_name"));
        student.setLastName(resultSet.getString("last_name"));
        student.setGroupId(resultSet.getLong("group_id"));
        return student;
    }
}
