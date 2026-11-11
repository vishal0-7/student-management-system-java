public final class Undergraduate extends Student {
    public Undergraduate(String id, String name, double marks) {
        super(id, name, marks);
    }
    @Override public String getCategory() { return "UG"; }
    @Override public String calculateGrade() {
        double marks = getMarks();
        if (marks >= 80) return "A";
        if (marks >= 65) return "B";
        if (marks >= 50) return "C";
        if (marks >= 40) return "D";
        return "F";
    }
}
