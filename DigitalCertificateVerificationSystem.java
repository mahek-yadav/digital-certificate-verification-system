import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import javax.swing.*;

interface Verifiable {
    boolean verify();
}

class Recipient {
    String id, name, email;

    Recipient(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}

class Course {
    String code, name;

    Course(String code, String name) {
        this.code = code;
        this.name = name;
    }
}

class Certificate implements Verifiable {
    String id;
    Recipient recipient;
    Course course;
    LocalDate date;

    Certificate(String id, Recipient r, Course c) {
        this.id = id;
        recipient = r;
        course = c;
        date = LocalDate.now();
    }

    public boolean verify() {
        return id != null && recipient != null && course != null;
    }

    public String toString() {
        return "Certificate ID : " + id +
               "\nRecipient ID   : " + recipient.id +
               "\nRecipient Name : " + recipient.name +
               "\nEmail          : " + recipient.email +
               "\nCourse Code    : " + course.code +
               "\nCourse Name    : " + course.name +
               "\nIssue Date     : " + date;
    }
}

class Verifier {
    String check(Certificate c) {
        if (c != null && c.verify())
            return "CERTIFICATE VERIFIED\n\n" + c;
        return "CERTIFICATE INVALID OR NOT FOUND";
    }
}

public class DigitalCertificateVerificationSystem extends JFrame {

    ArrayList<Certificate> certificates = new ArrayList<>();
    LinkedList<String> history = new LinkedList<>();
    HashMap<String, Certificate> certificateMap = new HashMap<>();
    TreeMap<LocalDate, ArrayList<Certificate>> dateMap = new TreeMap<>();

    static int number = 1;

    JTextField recipientId = new JTextField();
    JTextField recipientName = new JTextField();
    JTextField email = new JTextField();
    JTextField courseCode = new JTextField();
    JTextField courseName = new JTextField();
    JTextField certificateId = new JTextField();
    JTextField search = new JTextField();
    JTextArea output = new JTextArea();

    DigitalCertificateVerificationSystem() {

        setTitle("Digital Certificate Verification System");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel(
            "DIGITAL CERTIFICATE VERIFICATION SYSTEM",
            SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel form = new JPanel(new GridLayout(4, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Certificate Details"));

        form.add(new JLabel("Recipient ID"));
        form.add(recipientId);
        form.add(new JLabel("Recipient Name"));
        form.add(recipientName);

        form.add(new JLabel("Email"));
        form.add(email);
        form.add(new JLabel("Course Code"));
        form.add(courseCode);

        form.add(new JLabel("Course Name"));
        form.add(courseName);
        form.add(new JLabel("Certificate ID"));
        form.add(certificateId);

        form.add(new JLabel("Search"));
        form.add(search);
        form.add(new JLabel(""));
        form.add(new JLabel(""));

        certificateId.setEditable(false);

        JPanel buttons = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 5, 5)
        );

        JButton generate = new JButton("Generate");
        JButton verify = new JButton("Verify");
        JButton find = new JButton("Search");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton showAll = new JButton("Show All");
        JButton sortDate = new JButton("Sort by Date");
        JButton sortCourse = new JButton("Sort by Course");
        JButton historyButton = new JButton("History");
        JButton report = new JButton("Report");
        JButton clear = new JButton("Clear");

        JButton[] list = {
            generate, verify, find, update, delete,
            showAll, sortDate, sortCourse,
            historyButton, report, clear
        };

        for (JButton b : list) {
            b.setPreferredSize(new Dimension(105, 30));
            b.setFocusPainted(false);
            buttons.add(b);
        }

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.NORTH);
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);

        output.setEditable(false);
        output.setFont(new Font("Monospaced", Font.PLAIN, 14));
        output.setLineWrap(true);
        output.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(output);
        scroll.setBorder(
            BorderFactory.createTitledBorder("System Output")
        );

        add(scroll, BorderLayout.CENTER);

        generate.addActionListener(e -> generate());
        verify.addActionListener(e -> verify());
        find.addActionListener(e -> find());
        update.addActionListener(e -> update());
        delete.addActionListener(e -> delete());
        showAll.addActionListener(e -> showAll());
        sortDate.addActionListener(e -> sortByDate());
        sortCourse.addActionListener(e -> sortByCourse());
        historyButton.addActionListener(e -> showHistory());
        report.addActionListener(e -> report());
        clear.addActionListener(e -> clear());

        setVisible(true);
    }

    void validateData() throws Exception {

        if (recipientId.getText().isEmpty() ||
            recipientName.getText().isEmpty() ||
            email.getText().isEmpty() ||
            courseCode.getText().isEmpty() ||
            courseName.getText().isEmpty()) {

            throw new Exception("All fields are required.");
        }

        if (!email.getText().contains("@"))
            throw new Exception("Invalid email.");

        if (!courseCode.getText().matches("[A-Za-z]+[0-9]+"))
            throw new Exception("Invalid course code. Example: JAVA101");
    }

    String generateID() {

        StringBuilder id = new StringBuilder();

        id.append("CERT-");
        id.append(courseCode.getText().toUpperCase());
        id.append("-");
        id.append(String.format("%03d", number++));

        return id.toString();
    }

    boolean duplicate() {

        for (Certificate c : certificates) {
            if (c.recipient.id.equalsIgnoreCase(recipientId.getText()) &&
                c.course.code.equalsIgnoreCase(courseCode.getText()))
                return true;
        }

        return false;
    }

    void generate() {

        try {
            validateData();

            if (duplicate())
                throw new Exception("Duplicate certificate not allowed.");

            Recipient r = new Recipient(
                recipientId.getText(),
                recipientName.getText(),
                email.getText()
            );

            Course c = new Course(
                courseCode.getText(),
                courseName.getText()
            );

            Certificate certificate =
                new Certificate(generateID(), r, c);

            certificates.add(certificate);
            certificateMap.put(certificate.id, certificate);
            history.add(certificate.toString());

            if (!dateMap.containsKey(certificate.date))
                dateMap.put(certificate.date, new ArrayList<>());

            dateMap.get(certificate.date).add(certificate);

            certificateId.setText(certificate.id);

            output.setText(
                "CERTIFICATE GENERATED\n\n" + certificate
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this, e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    void verify() {

        String id = certificateId.getText();

        if (id.isEmpty())
            id = search.getText();

        output.setText(
            new Verifier().check(certificateMap.get(id))
        );
    }

    void find() {

        String value = search.getText().trim();

        if (value.isEmpty()) {
            output.setText(
                "Enter Certificate ID, Recipient ID or Name."
            );
            return;
        }

        Certificate found = certificateMap.get(value);

        if (found == null) {
            for (Certificate c : certificates) {
                if (c.recipient.id.equalsIgnoreCase(value) ||
                    c.recipient.name.equalsIgnoreCase(value)) {
                    found = c;
                    break;
                }
            }
        }

        if (found == null) {
            output.setText("Certificate not found.");
        } else {
            certificateId.setText(found.id);
            output.setText("CERTIFICATE FOUND\n\n" + found);
        }
    }

    // UPDATE WITH USER INPUT
    void update() {

        String id = certificateId.getText();

        if (id.isEmpty()) {
            output.setText("Search or generate a certificate first.");
            return;
        }

        Certificate c = certificateMap.get(id);

        if (c == null) {
            output.setText("Certificate not found.");
            return;
        }

        String newName = JOptionPane.showInputDialog(
            this,
            "Enter new recipient name:",
            c.recipient.name
        );

        if (newName == null || newName.trim().isEmpty())
            return;

        String newEmail = JOptionPane.showInputDialog(
            this,
            "Enter new email:",
            c.recipient.email
        );

        if (newEmail == null || newEmail.trim().isEmpty())
            return;

        if (!newEmail.contains("@")) {
            JOptionPane.showMessageDialog(
                this,
                "Invalid email.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        c.recipient.name = newName;
        c.recipient.email = newEmail;

        output.setText("CERTIFICATE UPDATED\n\n" + c);
    }

    void delete() {

        String id = certificateId.getText();
        Certificate c = certificateMap.get(id);

        if (c == null) {
            output.setText("Certificate not found.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Delete this certificate?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION)
            return;

        certificates.remove(c);
        certificateMap.remove(id);
        history.remove(c.toString());

        if (dateMap.containsKey(c.date)) {
            dateMap.get(c.date).remove(c);

            if (dateMap.get(c.date).isEmpty())
                dateMap.remove(c.date);
        }

        output.setText("Certificate deleted successfully.");
        clear();
    }

    void showAll() {

        StringBuilder result = new StringBuilder();

        result.append("ALL CERTIFICATES\n");
        result.append("================\n\n");

        if (certificates.isEmpty())
            result.append("No certificates available.");

        for (Certificate c : certificates)
            result.append(c).append("\n\n");

        output.setText(result.toString());
    }

    // SORT BY DATE
    void sortByDate() {

        certificates.sort(Comparator.comparing(c -> c.date));

        StringBuilder result = new StringBuilder();

        result.append("CERTIFICATES SORTED BY ISSUE DATE\n");
        result.append("=================================\n\n");

        for (Certificate c : certificates)
            result.append(c).append("\n\n");

        output.setText(result.toString());
    }

    // SORT BY COURSE
    void sortByCourse() {

        certificates.sort(
            Comparator.comparing(c -> c.course.name)
        );

        StringBuilder result = new StringBuilder();

        result.append("CERTIFICATES SORTED BY COURSE\n");
        result.append("=============================\n\n");

        for (Certificate c : certificates)
            result.append(c).append("\n\n");

        output.setText(result.toString());
    }

    void showHistory() {

        StringBuilder result = new StringBuilder();

        result.append("ISSUANCE HISTORY\n");
        result.append("================\n\n");

        if (history.isEmpty())
            result.append("No issuance history.");

        for (String h : history)
            result.append(h).append("\n\n");

        output.setText(result.toString());
    }

    void report() {

        StringBuilder result = new StringBuilder();
        int valid = 0;

        for (Certificate c : certificates) {
            if (c.verify())
                valid++;
        }

        result.append("VERIFICATION REPORT\n");
        result.append("===================\n\n");
        result.append("Total Certificates : ")
              .append(certificates.size()).append("\n");
        result.append("Valid Certificates : ")
              .append(valid).append("\n");
        result.append("Invalid Certificates : ")
              .append(certificates.size() - valid).append("\n\n");

        result.append("Certificates by Date:\n\n");

        for (LocalDate date : dateMap.keySet()) {
            result.append(date)
                  .append(" : ")
                  .append(dateMap.get(date).size())
                  .append(" certificate(s)\n");
        }

        output.setText(result.toString());
    }

    void clear() {

        recipientId.setText("");
        recipientName.setText("");
        email.setText("");
        courseCode.setText("");
        courseName.setText("");
        certificateId.setText("");
        search.setText("");
        output.setText("");
    }

    public static void main(String[] args) {
        new DigitalCertificateVerificationSystem();
    }
}