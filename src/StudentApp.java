import java.awt.*;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/** Desktop presentation layer; both interfaces share the same OOP model and store. */
public final class StudentApp extends JFrame {
    private final StudentStore store;
    private final JTextField id = new JTextField(), name = new JTextField(), marks = new JTextField();
    private final JTextField search = new JTextField(24);
    private final JComboBox<String> category = new JComboBox<>(new String[]{"UG", "PG"});
    private final JLabel status = new JLabel("Ready. Add a student to get started.");
    private final JLabel stats = new JLabel();
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Student ID", "Name", "Category", "Marks", "Grade"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
        @Override public Class<?> getColumnClass(int column) { return column == 3 ? Double.class : String.class; }
    };
    private final JTable table = new JTable(model);

    private StudentApp(StudentStore store) {
        super("Student Management System | Java OOP Case Study");
        this.store = store;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 580));
        setSize(1100, 700);
        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 20, 24));
        setContentPane(root);
        JPanel header = new JPanel(new GridLayout(3, 1, 0, 6));
        JLabel title = new JLabel("Student Management System");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        header.add(title);
        header.add(new JLabel("Java case study  |  Inheritance, method overriding and runtime polymorphism"));
        header.add(stats);
        root.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setPreferredSize(new Dimension(270, 400));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 8, 0);
        int row = 0;
        String[] labels = {"Student ID", "Full name", "Student category", "Marks (0-100)"};
        JComponent[] fields = {id, name, category, marks};
        for (int i = 0; i < labels.length; i++) {
            c.gridy = row++; form.add(new JLabel(labels[i]), c);
            c.gridy = row++; fields[i].setPreferredSize(new Dimension(250, 30)); form.add(fields[i], c);
        }
        String[] actions = {"Add student", "Update selected", "Delete selected", "Clear form", "Explain OOP"};
        for (String action : actions) {
            JButton button = new JButton(action);
            button.addActionListener(e -> perform(action));
            c.gridy = row++; form.add(button, c);
        }
        c.gridy = row; c.weighty = 1; form.add(new JPanel(), c);
        root.add(form, BorderLayout.WEST);

        JPanel records = new JPanel(new BorderLayout(0, 12));
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.add(new JLabel("Search ID or name"), BorderLayout.WEST);
        toolbar.add(search, BorderLayout.CENTER);
        records.add(toolbar, BorderLayout.NORTH);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        records.add(new JScrollPane(table), BorderLayout.CENTER);
        records.add(new JLabel("<html>Example rules: UG A 80+, B 65+, C 50+, D 40+; PG A 85+, B 70+, C 60+, D 50+.<br>Below the D threshold: F. These boundaries are assumptions for this model.</html>"), BorderLayout.SOUTH);
        root.add(records, BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || table.getSelectedRow() < 0) return;
            int r = table.convertRowIndexToModel(table.getSelectedRow());
            Student student = store.find(model.getValueAt(r, 0).toString());
            id.setText(student.getId()); id.setEditable(false);
            name.setText(student.getName()); category.setSelectedItem(student.getCategory());
            marks.setText(Double.toString(student.getMarks()));
        });
        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refresh(); }
            public void removeUpdate(DocumentEvent e) { refresh(); }
            public void changedUpdate(DocumentEvent e) { refresh(); }
        });
        refresh();
        setLocationRelativeTo(null);
    }

    private void clear() {
        table.clearSelection(); id.setEditable(true); id.setText(""); name.setText(""); marks.setText(""); category.setSelectedIndex(0);
    }
    private void refresh() {
        model.setRowCount(0);
        List<Student> all = store.all();
        for (Student s : store.search(search.getText()))
            model.addRow(new Object[]{s.getId(), s.getName(), s.getCategory(), s.getMarks(), s.calculateGrade()});
        long passed = all.stream().filter(s -> !s.calculateGrade().equals("F")).count();
        stats.setText("Total students: " + all.size() + "     Passed: " + passed + "     Failed: " + (all.size() - passed) + "     Showing: " + model.getRowCount());
        clear();
    }
    private void perform(String action) {
        try {
            switch (action) {
                case "Clear form": clear(); return;
                case "Explain OOP":
                    JOptionPane.showMessageDialog(this,
                        "Student is the abstract parent class with private ID, name and marks.\n"
                        + "Undergraduate and Postgraduate extend Student and call super(...).\n"
                        + "Both override calculateGrade().\n\n"
                        + "Student s = new Undergraduate(\"UG01\", \"Asha\", 82);\n"
                        + "s.calculateGrade() returns A.\n"
                        + "A Postgraduate with the same marks returns B.\n\n"
                        + "The reference type is Student; the actual object chooses the method.\n"
                        + "This is runtime polymorphism. display() is inherited from Student.",
                        "How this project demonstrates OOP", JOptionPane.INFORMATION_MESSAGE); return;
                case "Delete selected":
                    requireSelection();
                    if (JOptionPane.showConfirmDialog(this, "Delete student " + id.getText() + "?", "Confirm deletion", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
                    store.delete(id.getText()); break;
                default:
                    if (action.equals("Update selected")) requireSelection();
                    Student s = Student.create(category.getSelectedItem().toString(), id.getText(), name.getText(), Double.parseDouble(marks.getText().trim()));
                    if (action.equals("Add student")) store.add(s); else store.update(s);
            }
            refresh();
            status.setText(action + " completed. Changes saved to data/students.tsv.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Could not complete action", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void requireSelection() {
        if (table.getSelectedRow() < 0) throw new IllegalArgumentException("Select a student from the table first.");
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new StudentApp(new StudentStore(Paths.get("data/students.tsv"))).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Unable to start: " + ex.getMessage(), "Startup error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
