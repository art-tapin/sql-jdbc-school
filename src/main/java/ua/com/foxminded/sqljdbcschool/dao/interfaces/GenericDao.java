package ua.com.foxminded.sqljdbcschool.dao.interfaces;

public interface GenericDao<T> {

    void create(T entity);

    void update(T entity);

    void delete(T entity);
}
