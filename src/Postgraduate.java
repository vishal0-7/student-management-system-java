public final class Postgraduate extends Student {
    public Postgraduate(String id, String name, double marks) {
        super(id, name, marks);
    }
    @Override public String getCategory() { return "PG"; }
    @Override public String calculateGrade() {
        double marks = getMarks();
        if (marks >= 85) return "A";
        if (marks >= 70) return "B";
        if (marks >= 60) return "C";
        if (marks >= 50) return "D";
        return "F";
    }
}
