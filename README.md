# Student Management System

A working Java model for the case study **Student Management System Using Inheritance and Method Overriding**. Includes the specified Scanner-based menu and an additional Swing desktop interface. No external libraries, database server or internet connection are required to run it.

## Run on Windows

1. Extract the complete ZIP into a writable folder.
2. Use Java 17 or later. A JDK is needed only to rebuild or run the source tests.
3. Double-click `run-desktop.bat` for the graphical interface, or `run-console.bat` for the case study's menu-based interface.
4. Add your own students. The app starts empty and automatically saves successful changes to `data/students.tsv` inside the project folder.

Run only one instance at a time. The console and desktop share the same file; simultaneous instances could overwrite each other's changes. This is a local classroom model without authentication or multi-user database support. Close the application before manually editing or backing up its data file.

If Java is not on PATH, run with your Java installation's full path from the project folder, for example:

```powershell
& 'C:\Program Files\Java\jdk-26.0.1\bin\java.exe' -jar StudentManagementSystem.jar
```

The path above is the JDK found on the machine where this project was prepared; other computers may have a different path.

## Features

- Add undergraduate and postgraduate students; calculate grades immediately.
- View, search by ID/name, update and delete records.
- Automatic UTF-8 file persistence, with validation on loading and saving.
- Reject duplicate IDs, blank names, invalid categories, and marks outside 0-100, including NaN and infinity.
- Desktop table sorting, total/pass/fail counts, deletion confirmation and an Explain OOP dialog.

Select a table row to edit or delete it. Student IDs are normalized to uppercase and stay fixed during editing. Click **Clear form** before adding another student. Search changes clear the form; clear the search to see every record. Grades are derived from marks and category, never manually entered.

## Grading assumptions

The supplied document requires different grading criteria but does not give numerical thresholds. These are demonstration rules chosen for this implementation, not official institutional rules. Decimal marks are compared directly, without rounding.

| Grade | Undergraduate | Postgraduate |
|---|---|---|
| A | 80 to 100 | 85 to 100 |
| B | 65 to below 80 | 70 to below 85 |
| C | 50 to below 65 | 60 to below 70 |
| D | 40 to below 50 | 50 to below 60 |
| F | Below 40 | Below 50 |

A through D count as passed. Change the thresholds in `Undergraduate.java` and `Postgraduate.java` if your teacher provides another scheme; also update the displayed rules, documentation and tests, then rebuild.

## How it matches the case study

| Requirement | Implementation |
|---|---|
| Parent Student class | `Student.java` holds private ID, name and marks |
| Inheritance | `Undergraduate extends Student`, `Postgraduate extends Student` |
| Method overriding | Both subclasses implement `calculateGrade()` with `@Override` |
| Runtime polymorphism | A `Student` reference calls the actual subclass's grading method |
| Encapsulation | Private final fields and public getters; updates replace validated objects |
| Constructors, this and super | Parent initializes fields with `this`; child constructors call `super` |
| Scanner and menu | `ConsoleApp.java` reads each input with `Scanner` |
| Inherited display method | `Student.display()` prints details and calls the overridden method |

`Student` is abstract because a real record must belong to UG or PG. Its abstract grading method gives both categories the same contract. `StudentStore` handles records and files, while `ConsoleApp` and `StudentApp` handle interaction.

## Five-minute presentation

1. Launch the desktop app and add ID `UG01`, name `Asha Sharma`, UG, marks `82`. Grade is **A**.
2. Add ID `PG01`, name `Rohan Kumar`, PG, marks `82`. Grade is **B**.
3. Explain that identical marks produce different grades because each subclass overrides `calculateGrade()`.
4. Search for `Rohan`, select the row, change marks to `90`, and click **Update selected**. Grade becomes **A**.
5. Try a duplicate ID or marks `101`; show that invalid records are rejected.
6. Close and reopen the application to demonstrate saved records.
7. Click **Explain OOP**, then show the two subclass source files. Run the console version separately to demonstrate Scanner input and the menu.

Core example for your explanation:

```java
Student first = new Undergraduate("UG01", "Asha Sharma", 82);
Student second = new Postgraduate("PG01", "Rohan Kumar", 82);
System.out.println(first.calculateGrade());  // A
System.out.println(second.calculateGrade()); // B
```

The declared reference type is `Student`. Java selects the overridden method based on the actual object at runtime. Overriding uses the same method signature in a subclass; overloading would use different parameter lists.

## Build and test

Run `build.bat` after changing source code. Run `test.bat` for automated checks. Equivalent commands, from the project folder:

```text
javac --release 17 -encoding UTF-8 -d build src/*.java tests/*.java
java -cp build StudentSystemTest
jar --create --file StudentManagementSystem.jar --main-class StudentApp -C build .
java -jar StudentManagementSystem.jar
java -cp StudentManagementSystem.jar ConsoleApp
```

The test program uses temporary files and does not modify your student records. It checks exact grading boundaries, polymorphic display, validation, searching, CRUD persistence, corrupt-file preservation and consistency after a failed save.

## File guide

- `src/`: six readable Java source files.
- `tests/`: standalone automated test program.
- `StudentManagementSystem.jar`: compiled application, targeting Java 17.
- `build.bat`, `test.bat`: rebuild and verification scripts.
- `run-desktop.bat`, `run-console.bat`: launchers.
- `data/students.tsv`: generated after your first saved record.

Keep the entire extracted project folder together when sharing it. To start a fresh demonstration, close both interfaces and move the existing data file to a backup location before relaunching.
