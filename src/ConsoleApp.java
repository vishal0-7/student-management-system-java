import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

/** Menu-based version matching the case study's Scanner requirement. */
public final class ConsoleApp {
    private static String read(Scanner input, String prompt) {
        System.out.print(prompt);
        if (!input.hasNextLine()) throw new java.util.NoSuchElementException();
        return input.nextLine().trim();
    }
    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            StudentStore store = new StudentStore(Paths.get(args.length == 0 ? "data/students.tsv" : args[0]));
            while (true) {
                System.out.println("\nSTUDENT MANAGEMENT SYSTEM\n1 Add student\n2 View all\n3 Search\n4 Update student\n5 Delete student\n6 Show grading rules\n0 Exit");
                String choice = read(input, "Choose: ");
                try {
                    switch (choice) {
                        case "1": case "4": {
                            String id = read(input, "Student ID: ");
                            String name = read(input, "Full name: ");
                            String category = read(input, "Category (UG/PG): ").toUpperCase(java.util.Locale.ROOT);
                            double marks = Double.parseDouble(read(input, "Marks (0-100): "));
                            Student student = Student.create(category, id, name, marks);
                            if (choice.equals("1")) store.add(student); else store.update(student);
                            System.out.println("Saved: " + student.display());
                            break;
                        }
                        case "2":
                            if (store.all().isEmpty()) System.out.println("No students yet.");
                            for (Student student : store.all()) System.out.println(student.display());
                            break;
                        case "3": {
                            java.util.List<Student> matches = store.search(read(input, "ID or name: "));
                            if (matches.isEmpty()) System.out.println("No matching students.");
                            for (Student student : matches) System.out.println(student.display());
                            break;
                        }
                        case "5": {
                            String id = read(input, "Student ID to delete: ");
                            if (read(input, "Type YES to confirm: ").equals("YES")) {
                                store.delete(id);
                                System.out.println("Student deleted.");
                            } else System.out.println("Cancelled.");
                            break;
                        }
                        case "6": System.out.println("Example grading rules (not specified in the case study):\nUG: A >=80, B >=65, C >=50, D >=40, otherwise F\nPG: A >=85, B >=70, C >=60, D >=50, otherwise F"); break;
                        case "0": System.out.println("Goodbye. All changes are saved."); return;
                        default: System.out.println("Please select a listed option.");
                    }
                } catch (IllegalArgumentException | IOException ex) {
                    System.out.println("Could not complete action: " + ex.getMessage());
                }
            }
        } catch (java.util.NoSuchElementException ex) {
            System.out.println("\nInput ended. Goodbye.");
        } catch (IOException ex) {
            System.err.println("Could not open data: " + ex.getMessage());
            System.exit(1);
        }
    }
}
