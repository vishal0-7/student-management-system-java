import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public final class StudentSystemTest {
    private static int checks;
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
    private static void invalid(Runnable action) {
        try { action.run(); } catch (IllegalArgumentException expected) { checks++; return; }
        throw new AssertionError("Expected invalid input to be rejected");
    }
    public static void main(String[] args) throws Exception {
        double[] boundaries = {0, 39.99, 40, 49.99, 50, 59.99, 60, 64.99, 65, 69.99, 70, 79.99, 80, 84.99, 85, 100};
        String[] ug = {"F","F","D","D","C","C","C","C","B","B","B","B","A","A","A","A"};
        String[] pg = {"F","F","F","F","D","D","C","C","C","C","B","B","B","B","A","A"};
        for (int i = 0; i < boundaries.length; i++) {
            Student a = new Undergraduate("U1", "Test", boundaries[i]);
            Student b = new Postgraduate("P1", "Test", boundaries[i]);
            check(a.calculateGrade().equals(ug[i]), "UG boundary " + boundaries[i]);
            check(b.calculateGrade().equals(pg[i]), "PG boundary " + boundaries[i]);
        }
        check(new Undergraduate("u1", " Test ", 82).display().contains("Grade: A"), "Inherited display dispatch");
        for (double bad : new double[]{-1, 101, Double.NaN, Double.POSITIVE_INFINITY})
            invalid(() -> new Undergraduate("U1", "Test", bad));
        invalid(() -> new Undergraduate("", "Test", 50));
        invalid(() -> new Undergraduate("U1", "  ", 50));
        invalid(() -> new Undergraduate("U1", "A\tB", 50));
        invalid(() -> Student.create("UNKNOWN", "U1", "Test", 50));
        Path folder = Files.createTempDirectory("student-system-test-");
        Path file = folder.resolve("students.tsv");
        try {
            StudentStore store = new StudentStore(file);
            check(store.all().isEmpty(), "Fresh store");
            store.add(new Undergraduate("u1", "Asha Sharma", 82));
            store.add(new Postgraduate("p1", "José Kumar", 82));
            check(store.search("ASHA").size() == 1, "Name search");
            check(store.search("p1").size() == 1, "ID search");
            check(new StudentStore(file).find("p1").getName().equals("José Kumar"), "Unicode round trip");
            try { store.add(new Undergraduate("U1", "Duplicate", 20)); throw new AssertionError("Duplicate accepted"); }
            catch (IllegalArgumentException expected) { checks++; }
            store.update(new Postgraduate("u1", "Asha Sharma", 82));
            check(new StudentStore(file).find("U1").calculateGrade().equals("B"), "Update category and persistence");
            store.delete("p1");
            check(new StudentStore(file).all().size() == 1, "Delete persistence");
            Files.write(file, java.util.Arrays.asList("bad data"), StandardCharsets.UTF_8);
            try { new StudentStore(file); throw new AssertionError("Corrupt file accepted"); }
            catch (java.io.IOException expected) { checks++; }
            check(Files.readAllLines(file).get(0).equals("bad data"), "Corrupt source preserved");
            Path blocked = folder.resolve("blocked");
            StudentStore failing = new StudentStore(blocked);
            Files.createDirectory(blocked);
            Files.write(blocked.resolve("keep.txt"), new byte[]{1});
            try { failing.add(new Undergraduate("U2", "Test", 50)); throw new AssertionError("Expected save failure"); }
            catch (java.io.IOException expected) { checks++; }
            check(failing.all().isEmpty(), "Failed save leaves memory unchanged");
            Files.delete(blocked.resolve("keep.txt")); Files.delete(blocked);
        } finally { Files.deleteIfExists(file); Files.deleteIfExists(folder); }
        System.out.println("PASS: " + checks + " checks (grading, validation, CRUD, persistence, failed save).");
    }
}
