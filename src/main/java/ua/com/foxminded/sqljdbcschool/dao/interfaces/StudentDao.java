package ua.com.foxminded.sqljdbcschool.dao.interfaces;

import ua.com.foxminded.sqljdbcschool.models.Student;

import java.util.List;

public interface StudentDao extends GenericDao<Student> {

    Student getStudentById(Long studentId);

    List<Student> getStudentsRelatedToCourse(Long courseId);

    List<Student> getAllStudents();

    void addStudentToCourse(Long studentId, Long courseId);

    void removeStudentFromCourse(Long studentId, Long courseId);
}
