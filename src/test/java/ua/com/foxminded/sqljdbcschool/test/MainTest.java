package ua.com.foxminded.sqljdbcschool.test;

import ua.com.foxminded.sqljdbcschool.RandomDBDataGenerator;
import ua.com.foxminded.sqljdbcschool.SQLScriptExecutor;
import ua.com.foxminded.sqljdbcschool.dao.interfaces.CourseDao;
import ua.com.foxminded.sqljdbcschool.dao.jdbc.DaoFactory;
import ua.com.foxminded.sqljdbcschool.dao.interfaces.GroupDao;
import ua.com.foxminded.sqljdbcschool.dao.interfaces.StudentDao;
import ua.com.foxminded.sqljdbcschool.models.Course;
import ua.com.foxminded.sqljdbcschool.models.Group;
import ua.com.foxminded.sqljdbcschool.models.Student;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MainTest {

    private static final String STARTING_SQL_SCRIPT_FILE = "initial_sql_script.sql";
    private static final String DB_NAME = "school_db.jdbc";

    private static final DaoFactory daoFactory = DaoFactory.getInstance(DB_NAME);
    private static final StudentDao studentDAO = daoFactory.getStudentDAO();
    private static final GroupDao groupDAO = daoFactory.getGroupDAO();
    private static final CourseDao courseDAO = daoFactory.getCourseDAO();

    private static final String CLI_TABLE_DELIMITER = "-=-=-=-=-=-=-=-=-=-=-=-=-=-";
    private static final String CLI_ENTER_STUDENT_ID = "Enter student's ID: ";
    private static final String CLI_THERE_IS_NO_SUCH_STUDENT = "There is no such student.";
    private static final String CLI_ENTER_COURSE_ID = "Enter course ID: ";
    private static final String CLI_NONE = "None!";

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        prepareDataBase();

        int userOption = -1;
        while (userOption != 0) {
            showMenuItems();
            System.out.print("Your choice is: ");
            userOption = getValidInteger();
            executeOption(userOption);
        }
    }

    private static void prepareDataBase() {
        SQLScriptExecutor sqlScriptExecutor = new SQLScriptExecutor();
        sqlScriptExecutor.execute(STARTING_SQL_SCRIPT_FILE, DB_NAME);
        RandomDBDataGenerator randomDBDataGenerator = new RandomDBDataGenerator(DB_NAME);
        randomDBDataGenerator.generateGroups();
        randomDBDataGenerator.generateCourses();
        randomDBDataGenerator.generateStudents();
    }

    private static int getValidInteger() {
        int number;
        do {
            while (!scanner.hasNextInt()) {
                System.out.print("That's not a valid number! Your choice is: ");
                scanner.next();
            }
            number = scanner.nextInt();
        } while (number < 0);
        scanner.nextLine();
        return number;
    }

    private static void executeOption(int option) {
        switch (option) {
            case 0:
                break;
            case 1: {
                findAllGroupsWithLessOrEqualsStudentCount();
                break;
            }
            case 2: {
                findAllStudentsRelatedToCourse();
                break;
            }
            case 3: {
                addNewStudent();
                break;
            }
            case 4: {
                deleteStudentByStudentId();
                break;
            }
            case 5: {
                addStudentToCourse();
                break;
            }
            case 6: {
                removeStudentFromAttendedCourses();
                break;
            }
            default: {
                System.out.println("Please, enter a valid option!" + System.lineSeparator() + CLI_TABLE_DELIMITER);
                break;
            }
        }
    }

    private static void removeStudentFromAttendedCourses() {
        int studentId = getStudentId();
        List<Course> attendedCourses = courseDAO.getByStudentId((long) studentId);
        showCourses(attendedCourses);
        if (attendedCourses.isEmpty()) {
            return;
        }
        System.out.print(CLI_ENTER_COURSE_ID);
        int courseId = getValidInteger();
        if (attendedCourses.stream().noneMatch(course -> course.getId() == courseId)) {
            System.out.println("This student doesn't attend such course.");
            removeStudentFromAttendedCourses();
            return;
        }
        studentDAO.removeStudentFromCourse((long) studentId, (long) courseId);
        System.out.println("Student was successfully removed from the course.");
        System.out.println(CLI_TABLE_DELIMITER);
    }

    private static void addStudentToCourse() {
        int studentId = getStudentId();
        List<Course> allCourses = courseDAO.getAllCourses();
        showCourses(allCourses);
        System.out.print(CLI_ENTER_COURSE_ID);
        int courseId = getValidInteger();
        if (allCourses.stream().noneMatch(course -> course.getId() == courseId)) {
            System.out.println("There is no such course.");
            addStudentToCourse();
            return;
        }
        List<Course> attendedCourses = courseDAO.getByStudentId((long) studentId);
        if (attendedCourses.stream().anyMatch(course -> course.getId() == courseId)) {
            System.out.println("This student is already added to this course.");
            addStudentToCourse();
            return;
        }
        studentDAO.addStudentToCourse((long) studentId, (long) courseId);
        System.out.println("Student was successfully added to course!");
        System.out.println(CLI_TABLE_DELIMITER);
    }

    private static void deleteStudentByStudentId() {
        List<Student> allStudents = studentDAO.getAllStudents();
        System.out.println("Do you want to see a full list of students?" + System.lineSeparator() + "Type y/n (yes/no)");
        String answer = scanner.nextLine();
        System.out.println(answer);
        if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
            showStudents(allStudents);
        }
        System.out.print(CLI_ENTER_STUDENT_ID);
        int studentId = getValidInteger();
        if (allStudents.stream().noneMatch(student -> student.getId() == studentId)) {
            System.out.println(CLI_THERE_IS_NO_SUCH_STUDENT);
            deleteStudentByStudentId();
            return;
        }
        studentDAO.delete(studentDAO.getStudentById((long) studentId));
        System.out.println("Student was successfully deleted!");
        System.out.println(CLI_TABLE_DELIMITER);
    }

    private static void addNewStudent() {
        Student newStudent = new Student();
        System.out.print("Enter first name: ");
        newStudent.setFirstName(scanner.nextLine());
        System.out.print("Enter last name: ");
        newStudent.setLastName(scanner.nextLine());
        List<Group> allGroups = groupDAO.getAllGroups();
        showGroups(allGroups);
        System.out.println("Enter group ID: ");
        int groupId = getValidInteger();
        if (allGroups.stream().noneMatch(group -> group.getId() == groupId)) {
            System.out.println("There is no such group in Group List.");
            addNewStudent();
            return;
        }
        newStudent.setGroupId((long) groupId);
        studentDAO.create(newStudent);
        System.out.println("Student " + newStudent + "was successfully added!");
        System.out.println(CLI_TABLE_DELIMITER);
    }

    private static void findAllStudentsRelatedToCourse() {
        List<Course> allCourses = courseDAO.getAllCourses();
        showCourses(allCourses);
        System.out.print("Please, enter full course name: ");
        String courseName = scanner.nextLine();
        Optional<Course> requestedCourse = allCourses.stream()
                .filter(course -> course.getName().equalsIgnoreCase(courseName))
                .findFirst();
        Course course;
        if (requestedCourse.isPresent()) {
            course = requestedCourse.get();
        } else {
            System.out.println("There is no such course in Course List.");
            findAllStudentsRelatedToCourse();
            return;
        }
        List<Student> students = studentDAO.getStudentsRelatedToCourse(course.getId());
        showStudents(students);
        System.out.println(CLI_TABLE_DELIMITER);
    }

    private static void findAllGroupsWithLessOrEqualsStudentCount() {
        System.out.print("Number of students: ");
        int numberOfStudents = getValidInteger();
        List<Group> groups = groupDAO.getGroupsWithLessOrEqualsStudents(numberOfStudents);
        showGroups(groups);
        System.out.println(CLI_TABLE_DELIMITER);
    }

    private static int getStudentId() {

        List<Student> allStudents = studentDAO.getAllStudents();
        showStudents(allStudents);
        System.out.print(CLI_ENTER_STUDENT_ID);
        int studentId = getValidInteger();
        if (allStudents.stream().noneMatch(student -> student.getId() == studentId)) {
            System.out.println(CLI_THERE_IS_NO_SUCH_STUDENT);
            return getStudentId();
        }
        return studentId;
    }

    private static void showStudents(List<Student> students) {
        if (students.isEmpty()) {
            System.out.println(CLI_NONE);
        } else {
            System.out.println("-=List of selected students=-");
            System.out.println("ID\t\tName\t\tSurname");
            String studentMask = "%03d.\t%-12s%s";
            students.forEach(student -> System.out.println(String.format(studentMask, student.getId(), student.getFirstName(), student.getLastName())));
        }
    }

    private static void showGroups(List<Group> groups) {
        if (groups.isEmpty()) {
            System.out.println(CLI_NONE);
        } else {
            System.out.println("-=List of selected groups=-");
            System.out.println("ID\tName");
            String groupMask = "%02d.\t%s";
            groups.forEach(group -> System.out.println(String.format(groupMask, group.getId(), group.getName())));
        }
    }

    private static void showCourses(List<Course> courses) {
        if (courses.isEmpty()) {
            System.out.println(CLI_NONE);
        } else {
            System.out.println("-=List of selected courses=-");
            System.out.println("ID\tName\t\t\tDescription");
            String courseMask = "%02d.\t%-15s\t%s";
            courses.forEach(course -> System.out.println(String.format(courseMask, course.getId(), course.getName(), course.getDescription())));
        }
    }

    public static void showMenuItems() {
        System.out.println("Please, choose one of the following options (enter a number):" + System.lineSeparator() +
                "1. Find all groups with less or equals student count" + System.lineSeparator() +
                "2. Find all students related to course with given name" + System.lineSeparator() +
                "3. Add new student" + System.lineSeparator() + "" +
                "4. Delete student by his/her ID" + System.lineSeparator() +
                "5. Add a student to the course (from the following list)" + System.lineSeparator() +
                "6. Remove the student from one of his or her courses" + System.lineSeparator() +
                "0. Exit the application");
    }
}
