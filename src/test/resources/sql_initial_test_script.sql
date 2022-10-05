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
    group_id   INT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(64) UNIQUE NOT NULL
);
CREATE TABLE courses
(
    course_id          INT AUTO_INCREMENT PRIMARY KEY,
    course_name        VARCHAR(64)         NOT NULL,
    course_description VARCHAR(255) UNIQUE NOT NULL
);
CREATE TABLE students
(
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(64) NOT NULL,
    last_name  VARCHAR(64) NOT NULL,
    group_id   INT         NOT NULL,
    foreign key (group_id) references groups (group_id)
);
CREATE TABLE students_courses
(
    student_id INT,
    foreign key (student_id) references students (student_id) ON UPDATE CASCADE ON DELETE CASCADE,
    course_id  INT,
    foreign key (course_id) references courses (course_id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT student_course_pk PRIMARY KEY (student_id, course_id)
);