package ua.com.foxminded.sqljdbcschool.dao.interfaces;

import ua.com.foxminded.sqljdbcschool.models.Group;

import java.util.List;

public interface GroupDao extends GenericDao<Group> {

    List<Group> getAllGroups();

    List<Group> getGroupsWithLessOrEqualsStudents(int numOfStudents);
}
