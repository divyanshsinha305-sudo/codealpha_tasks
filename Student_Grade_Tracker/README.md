Student Grade Tracker
====================

Overview
--------

Student Grade Tracker is a console-based Java application designed to record student names and marks, generate academic reports, and persist data across sessions. The program supports adding students, viewing all records, searching by name, calculating statistics, and saving data to a local file.

Features
--------

- Add student records with name and numeric marks
- Display all student entries with grade and pass/fail status
- Search for a student by name
- Generate statistics including average, highest, lowest marks, and top performer
- Save and load student data from `students.txt`

Requirements
------------

- Java Development Kit (JDK) 17 or later
- A terminal or integrated development environment with Java support

Usage
-----

1. Navigate to the project folder:

   ```bash
   cd codealpha_tasks/Student_Grade_Tracker
   ```

2. Compile the Java source file:

   ```bash
   javac StudentGradeTracker.java
   ```

3. Run the application:

   ```bash
   java StudentGradeTracker
   ```

Project Structure
-----------------

- `StudentGradeTracker.java`: main source file containing the application logic
- `students.txt`: runtime data file used for saving and loading student records
- `README.md`: project overview and usage instructions

Application Flow
----------------

- The application loads any existing student data from `students.txt` at startup.
- A menu is presented to the user for selecting operations.
- Student information is stored in memory and persisted when the save option is chosen or when exiting.
- Data is saved in comma-separated format, allowing the application to restore records in subsequent runs.

Notes
-----

Ensure that the working directory contains `StudentGradeTracker.java` when compiling and running the application. The file `students.txt` is created automatically when data is saved for the first time.