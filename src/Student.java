import java.util.Locale;

/** Shared state and behaviour inherited by both student categories. */
public abstract class Student {
    private final String id;
    private final String name;
    private final double marks;

    protected Student(String id, String name, double marks) {
        if (id == null || !id.trim().matches("[A-Za-z0-9_-]{1,30}"))
            throw new IllegalArgumentException("ID must contain 1-30 letters, digits, underscores or hyphens.");
        if (name == null || name.trim().isEmpty() || name.trim().length() > 100
                || name.chars().anyMatch(Character::isISOControl))
            throw new IllegalArgumentException("Enter a name of 1-100 characters without control characters.");
        if (!Double.isFinite(marks) || marks < 0 || marks > 100)
            throw new IllegalArgumentException("Marks must be a finite number from 0 to 100.");
        this.id = id.trim().toUpperCase(Locale.ROOT);
        this.name = name.trim();
        this.marks = marks;
    }

    public final String getId() { return id; }
    public final String getName() { return name; }
    public final double getMarks() { return marks; }
    public abstract String getCategory();
    public abstract String calculateGrade();

    // Inherited method: calculateGrade() dispatches to the actual subclass at runtime.
    public String display() {
        return String.format(Locale.ROOT, "%s | %s | %s | Marks: %s | Grade: %s",
                id, name, getCategory(), Double.toString(marks), calculateGrade());
    }

    public static Student create(String category, String id, String name, double marks) {
        if ("UG".equals(category)) return new Undergraduate(id, name, marks);
        if ("PG".equals(category)) return new Postgraduate(id, name, marks);
        throw new IllegalArgumentException("Category must be UG or PG.");
    }
}
