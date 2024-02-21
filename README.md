# Java Backend + SQL

<aside>
💡 Create an application that inserts, updates, deletes data in a database using JDBC. Use PostgreSQL DB.

</aside>

---

Tables (these types are Java types, use the SQL equivalents that are best suited):

- groups (group_id int, group_name string)
- students (student_id int, group_id int, first_name string last_name string)
- courses (course_id int, course_name string, course_description string)

**Create SQL files with data:** 

1. Create a user and database. Assign the user all access rights to the database. (DB and user must be created before running the application) 
2. Create a text script file with SQL statements that create tables 

**Create a Java application:**

1. When launched, it should run a SQL script with the creation of tables from previously created files. If the tables already exist, drop them. 
2. Generate test data: 10 groups with randomly generated names. The name must contain 2 characters, a hyphen, 2 numbers. 

**Create 10 courses (mathematics, biology, etc.) 200 students:**

Take 20 first names and 20 last names and randomly combine them to get students. Randomly assign students to groups. Each group could have from 10 to 30 students. It is possible that some groups will be without students or students without groups. 

**Create a many-to-many relationship between the STUDENTS and COURSES tables:**

Randomly assign 1 to 3 courses for each student Write SQL queries, they should be accessible from the application menu (console): 

1. Find all groups with fewer or equal number of students; 
2. Find all students associated with the course with the given name; 
3. Add a new student; 
4. Delete student before STUDENT_ID; 
5. Add a student to the course (from the list); 
6. Remove a student from one of their courses.

---
