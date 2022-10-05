package ua.com.foxminded.sqljdbcschool;

import ua.com.foxminded.sqljdbcschool.dao.interfaces.CourseDao;
import ua.com.foxminded.sqljdbcschool.dao.jdbc.DaoFactory;
import ua.com.foxminded.sqljdbcschool.dao.interfaces.GroupDao;
import ua.com.foxminded.sqljdbcschool.dao.interfaces.StudentDao;
import ua.com.foxminded.sqljdbcschool.models.Course;
import ua.com.foxminded.sqljdbcschool.models.Group;
import ua.com.foxminded.sqljdbcschool.models.Student;

import java.util.*;

public class RandomDBDataGenerator {

    private static final int NUMBER_OF_GROUPS = 10;
    private static final int NUMBER_OF_COURSES = 10;
    private static final int NUMBER_OF_STUDENTS = 200;

    private static final int LEFT_CHAR_LIMIT = 97; // letter 'a'
    private static final int RIGHT_CHAR_LIMIT = 122; // letter 'z'

    private static final int CHARACTERS_IN_GROUP_NAME = 2;
    private static final int GROUP_NAME_LENGTH = 5;
    private static final String GROUP_NAME_DELIMITER = "-";

    private static final int LAST_NAME_LENGTH = 10;

    private final DaoFactory daoFactory;

    private static final Random random = new Random();

    public RandomDBDataGenerator(String dbName) {
        this.daoFactory = DaoFactory.getInstance(dbName);
    }

    public void generateGroups() {
        HashSet<String> generatedGroupNames = new HashSet<>(NUMBER_OF_GROUPS);
        for (int i = 0; i < NUMBER_OF_GROUPS; i++) {
            StringBuilder stringBuilder = new StringBuilder(GROUP_NAME_LENGTH);
            for (int j = 0; j < CHARACTERS_IN_GROUP_NAME; j++) {
                int randomLimitedInt = LEFT_CHAR_LIMIT + (int) (random.nextFloat() * (RIGHT_CHAR_LIMIT - LEFT_CHAR_LIMIT + 1));
                stringBuilder.append((char) randomLimitedInt);
            }
            stringBuilder.append(GROUP_NAME_DELIMITER)
                    .append(random.nextInt(10))
                    .append(random.nextInt(10));
            generatedGroupNames.add(stringBuilder.toString());
        }
        if (generatedGroupNames.size() != NUMBER_OF_GROUPS) {
            generateGroups();
            return;
        }

        GroupDao groupDAO = daoFactory.getGroupDAO();
        generatedGroupNames.forEach(name -> {
            Group group = new Group();
            group.setName(name);
            groupDAO.create(group);
        });
    }

    public void generateCourses() {
        List<String> coursesNames = Arrays.asList("MATH", "BIOLOGY", "MUSIC", "SOLFEGGIO", "CYBERNETICS", "GEOGRAPHY",
                "PHYSICS", "BASKETBALL", "FOOTBALL", "CULTURE", "MUSIC THEORY", "FRENCH", "ENGLISH", "UKRAINIAN", "HISTORY");
        List<String> postfixes = Arrays.asList("-1", "-2", "-3", "-4", "-5", "-6");
        String courseDescription = "This is a description of a course named ";

        HashSet<String> generatedCourseNames = new HashSet<>(NUMBER_OF_COURSES);
        for (int i = 0; i < NUMBER_OF_COURSES; i++) {
            String generatedName = coursesNames.get(random.nextInt(coursesNames.size())) +
                    postfixes.get(random.nextInt(postfixes.size()));
            generatedCourseNames.add(generatedName);
        }
        if (generatedCourseNames.size() != NUMBER_OF_COURSES) {
            generateCourses();
            return;
        }

        CourseDao courseDAO = daoFactory.getCourseDAO();
        generatedCourseNames.forEach(name -> {
            Course course = new Course();
            course.setName(name);
            course.setDescription(courseDescription + name);
            courseDAO.create(course);
        });
    }

    public void generateStudents() {
        List<String> firstNames = Arrays.asList(
                "Patrick", "Gina", "Jillian", "Pamela", "Mitchell", "Hannah", "Renee", "Denise", "Molly", "Jerry", "Misty", "Mario",
                "Johnathan", "Jaclyn", "Brenda", "Terry", "Lacey", "Shaun", "Devin", "Heidi", "Troy", "Lucas", "Desiree",
                "Jorge", "Andre", "Morgan", "Drew", "Sabrina", "Miranda", "Alyssa", "Alisha", "Teresa", "Johnny", "Meagan",
                "Allen", "Krista", "Marc", "Tabitha", "Lance", "Ricardo", "Martin", "Chase", "Theresa", "Melinda", "Monique",
                "Tanya", "Linda", "Kristopher", "Bobby", "Caleb", "Ashlee", "Kelli", "Henry", "Garrett", "Mallory", "Jill",
                "Jonathon", "Kristy", "Anne", "Francisco", "Danny", "Robin", "Lee", "Tamara", "Manuel", "Meredith", "Colleen",
                "Lawrence", "Christy", "Ricky", "Randall", "Marissa", "Ross", "Mathew", "Jimmy", "Abigail", "Kendra", "Carolyn",
                "Billy", "Deanna", "Jenny", "Jon", "Albert", "Taylor", "Lori", "Rebekah", "Cameron", "Ebony", "Wendy",
                "Angel", "Micheal", "Kristi", "Caroline", "Colin", "Dawn", "Kari", "Clayton", "Arthur", "Roger", "Roberto");

        List<String> generatedLastNames = generateLastNames(NUMBER_OF_STUDENTS / 10);

        List<Integer> groupIDs = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> courseIDs = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Collections.shuffle(groupIDs);
        for (int i = 0; i < NUMBER_OF_GROUPS; i++) {
            int groupID = groupIDs.get(i);
            int groupCapacity = random.nextInt((30 - 10 + 1)) + 10;
            for (int j = 0; j < groupCapacity; j++) {
                StudentDao studentDAO = daoFactory.getStudentDAO();
                Student student = new Student();
                student.setFirstName(firstNames.get(random.nextInt(firstNames.size())));
                student.setLastName(generatedLastNames.get(random.nextInt(generatedLastNames.size())));
                student.setGroupId((long) groupID);
                studentDAO.create(student);

                int numberOfCourses = random.nextInt(4);
                Collections.shuffle(courseIDs);
                for (int k = 0; k < numberOfCourses; k++) {
                    studentDAO.addStudentToCourse(student.getId(), (long) courseIDs.get(k));
                }
            }
        }
    }

    private List<String> generateLastNames(int numberOfLastNames) {
        List<String> lastNames = new ArrayList<>();
        for (int i = 0; i < numberOfLastNames; i++) {
            StringBuilder stringBuilder = new StringBuilder(LAST_NAME_LENGTH);
            for (int j = 0; j < LAST_NAME_LENGTH; j++) {
                int randomLimitedInt = LEFT_CHAR_LIMIT + (int) (random.nextFloat() * (RIGHT_CHAR_LIMIT - LEFT_CHAR_LIMIT + 1));
                stringBuilder.append((char) randomLimitedInt);
            }
            String lastName = stringBuilder.toString();
            lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
            lastNames.add(lastName);
        }
        if (lastNames.size() != numberOfLastNames) {
            lastNames = generateLastNames(numberOfLastNames);
        }
        return lastNames;
    }
}






