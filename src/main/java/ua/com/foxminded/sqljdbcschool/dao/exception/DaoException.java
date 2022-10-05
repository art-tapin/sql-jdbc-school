package ua.com.foxminded.sqljdbcschool.dao.exception;

/**
 * This class represents a generic DAO exception. It should wrap any exception to the underlying
 * code, such as SQLExceptions.
 */
public class DaoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a DAOException with the given detail message.
     *
     * @param message The detail message of the DAOException.
     */
    public DaoException(String message) {
        super(message);
    }

    /**
     * Constructs a DAOException with the given root cause.
     *
     * @param cause The root cause of the DAOException.
     */
    public DaoException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a DAOException with the given detail message and root cause.
     *
     * @param message The detail message of the DAOException.
     * @param cause   The root cause of the DAOException.
     */
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
