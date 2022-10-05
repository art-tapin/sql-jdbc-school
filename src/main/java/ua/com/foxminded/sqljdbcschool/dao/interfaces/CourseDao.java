package ua.com.foxminded.sqljdbcschool.dao.interfaces;

import ua.com.foxminded.sqljdbcschool.models.Course;

import java.util.List;

public interface CourseDao extends GenericDao<Course> {

    List<Course> getAllCourses();

    List<Course> getByStudentId(Long studentId);
}
