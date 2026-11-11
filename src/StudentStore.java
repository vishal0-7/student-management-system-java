import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Small local repository. Save succeeds before the in-memory data is changed. */
public final class StudentStore {
    private static final String HEADER = "category\tid\tname\tmarks";
    private final Path path;
    private final Map<String, Student> records = new LinkedHashMap<>();

    public StudentStore(Path path) throws IOException {
        this.path = path.toAbsolutePath();
        if (!Files.exists(this.path)) return;
        List<String> lines = Files.readAllLines(this.path, StandardCharsets.UTF_8);
        if (lines.isEmpty() || !HEADER.equals(lines.get(0)))
            throw new IOException("Unrecognized student data file: " + this.path);
        for (int i = 1; i < lines.size(); i++) {
            try {
                String[] fields = lines.get(i).split("\t", -1);
                if (fields.length != 4) throw new IllegalArgumentException("Expected four fields");
                Student s = Student.create(fields[0], fields[1], fields[2], Double.parseDouble(fields[3]));
                if (records.putIfAbsent(s.getId(), s) != null)
                    throw new IllegalArgumentException("Duplicate ID");
            } catch (IllegalArgumentException ex) {
                throw new IOException("Invalid data at line " + (i + 1) + ". Original file was not changed.", ex);
            }
        }
    }

    private static String key(String id) { return id.trim().toUpperCase(Locale.ROOT); }
    public List<Student> all() { return new ArrayList<>(records.values()); }
    public Student find(String id) { return records.get(key(id)); }
    public List<Student> search(String query) {
        String q = query.trim().toLowerCase(Locale.ROOT);
        List<Student> result = new ArrayList<>();
        for (Student s : records.values())
            if (s.getId().toLowerCase(Locale.ROOT).contains(q)
                    || s.getName().toLowerCase(Locale.ROOT).contains(q)) result.add(s);
        return result;
    }
    public void add(Student student) throws IOException {
        if (records.containsKey(student.getId())) throw new IllegalArgumentException("This student ID already exists.");
        Map<String, Student> next = new LinkedHashMap<>(records);
        next.put(student.getId(), student);
        commit(next);
    }
    public void update(Student student) throws IOException {
        if (!records.containsKey(student.getId())) throw new IllegalArgumentException("Student ID was not found.");
        Map<String, Student> next = new LinkedHashMap<>(records);
        next.put(student.getId(), student);
        commit(next);
    }
    public void delete(String id) throws IOException {
        if (!records.containsKey(key(id))) throw new IllegalArgumentException("Student ID was not found.");
        Map<String, Student> next = new LinkedHashMap<>(records);
        next.remove(key(id));
        commit(next);
    }
    private void commit(Map<String, Student> next) throws IOException {
        Files.createDirectories(path.getParent());
        Path temp = Files.createTempFile(path.getParent(), "students-", ".tmp");
        try {
            List<String> lines = new ArrayList<>();
            lines.add(HEADER);
            for (Student s : next.values()) lines.add(s.getCategory() + "\t" + s.getId() + "\t"
                    + s.getName() + "\t" + Double.toString(s.getMarks()));
            Files.write(temp, lines, StandardCharsets.UTF_8);
            try { Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
            catch (AtomicMoveNotSupportedException ex) {
                Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
            }
            records.clear();
            records.putAll(next);
        } finally { Files.deleteIfExists(temp); }
    }
}
