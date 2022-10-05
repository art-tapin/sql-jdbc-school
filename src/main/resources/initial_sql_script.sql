DROP
    TABLE IF EXISTS groups CASCADE;
DROP
    TABLE IF EXISTS courses CASCADE;
DROP
    TABLE IF EXISTS students CASCADE;
DROP
    TABLE IF EXISTS students_courses CASCADE;
CREATE TABLE groups
(
    group_id   SERIAL PRIMARY KEY,
    group_name VARCHAR(64) UNIQUE NOT NULL
);
CREATE TABLE courses
(
    course_id          SERIAL PRIMARY KEY,
    course_name        VARCHAR(64)  NOT NULL,
    course_description VARCHAR(255) UNIQUE NOT NULL
);
CREATE TABLE students
(
    student_id SERIAL PRIMARY KEY,
    first_name VARCHAR(64)                          NOT NULL,
    last_name  VARCHAR(64)                          NOT NULL,
    group_id   INTEGER REFERENCES groups (group_id) NOT NULL
);
CREATE TABLE students_courses
(
    student_id INTEGER REFERENCES students (student_id) ON UPDATE CASCADE ON DELETE CASCADE,
    course_id  INTEGER REFERENCES courses (course_id) ON UPDATE CASCADE ON DELETE CASCADE,
    UNIQUE (student_id, course_id)
);
