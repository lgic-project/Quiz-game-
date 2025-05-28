package model;

import java.sql.*;
import java.util.*;

public class QuizResult {
    private Integer id;
    private Quiz quiz;
    private Student student;
    private Integer rightAnswers;
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public static class Constant {
        public static final String TABLE_NAME = "QUIZ_RESULTS";
        public static final String ID = "id";
        public static final String QUIZ_ID = "QUIZ_ID";
        public static final String STUDENT_ID = "STUDENT_ID";
        public static final String RIGHT_ANSWERS = "RIGHT_ANSWERS";
        public static final String TIMESTAMP = "date_time";
    }

    public QuizResult() {}

    public QuizResult(Integer id, Quiz quiz, Student student, Integer rightAnswers) {
        this.id = id;
        this.quiz = quiz;
        this.student = student;
        this.rightAnswers = rightAnswers;
    }

    public QuizResult(Quiz quiz, Student student, Integer rightAnswers) {
        this(null, quiz, student, rightAnswers);
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Integer getRightAnswers() { return rightAnswers; }
    public void setRightAnswers(Integer rightAnswers) { this.rightAnswers = rightAnswers; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public static void createTable() {
        String sql = String.format("""
            CREATE TABLE IF NOT EXISTS %s (
                %s INTEGER PRIMARY KEY AUTOINCREMENT,
                %s INTEGER NOT NULL,
                %s INTEGER NOT NULL,
                %s INTEGER NOT NULL,
                %s TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (%s) REFERENCES %s(%s),
                FOREIGN KEY (%s) REFERENCES %s(%s)
            );
        """,
        Constant.TABLE_NAME,
        Constant.ID,
        Constant.STUDENT_ID,
        Constant.QUIZ_ID,
        Constant.RIGHT_ANSWERS,
        Constant.TIMESTAMP,
        Constant.STUDENT_ID, Student.Constant.TABLE_NAME, Student.Constant.ID,
        Constant.QUIZ_ID, Quiz.Constant.TABLE_NAME, Quiz.Constant.QUIZ_ID
        );

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:quizzess.db");
             PreparedStatement ps = conn.prepareStatement(sql)) {

            Class.forName("org.sqlite.JDBC");
            ps.execute();
            System.out.println("✅ QuizResults table created.");
        } catch (Exception ex) {
            System.err.println("❌ Failed to create table: " + ex.getMessage());
        }
    }

    public boolean save(Map<QuizQuestion, String> userAnswers) {
        String sql = String.format("""
            INSERT INTO %s (%s, %s, %s, %s)
            VALUES (?, ?, ?, CURRENT_TIMESTAMP)
        """, Constant.TABLE_NAME, Constant.STUDENT_ID, Constant.QUIZ_ID, Constant.RIGHT_ANSWERS, Constant.TIMESTAMP);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:quizzess.db");
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Class.forName("org.sqlite.JDBC");
            ps.setInt(1, student.getId());
            ps.setInt(2, quiz.getQuizId());
            ps.setInt(3, rightAnswers);

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.id = rs.getInt(1);
                        return saveQuizResultDetails(userAnswers);
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("❌ Error saving QuizResult: " + ex.getMessage());
        }

        return false;
    }

    private boolean saveQuizResultDetails(Map<QuizQuestion, String> userAnswers) {
        return QuizResultDetails.saveQuizResultDetails(this, userAnswers);
    }

    public static Map<QuizResult, Quiz> getQuizzes(Student student) {
        Map<QuizResult, Quiz> resultMap = new HashMap<>();

        String sql = String.format("""
            SELECT qr.%s, qr.%s, q.%s AS quiz_id, q.%s
            FROM %s qr
            JOIN %s q ON qr.%s = q.%s
            WHERE qr.%s = ?
        """,
        Constant.ID, Constant.RIGHT_ANSWERS,
        Quiz.Constant.QUIZ_ID, Quiz.Constant.TITLE,
        Constant.TABLE_NAME, Quiz.Constant.TABLE_NAME,
        Constant.QUIZ_ID, Quiz.Constant.QUIZ_ID,
        Constant.STUDENT_ID);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:quizzess.db");
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, student.getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                QuizResult qr = new QuizResult();
                qr.setId(rs.getInt(1));
                qr.setRightAnswers(rs.getInt(2));

                Quiz qz = new Quiz();
                qz.setQuizId(rs.getInt(3));
                qz.setTitle(rs.getString(4));

                resultMap.put(qr, qz);
            }
        } catch (Exception ex) {
            System.err.println("❌ Error fetching quizzes: " + ex.getMessage());
        }

        return resultMap;
    }

    public static List<Student> getStudents(Quiz quiz) {
        List<Student> students = new ArrayList<>();

        String sql = String.format("""
            SELECT st.%s, st.%s, st.%s, st.%s
            FROM %s qr
            JOIN %s st ON st.%s = qr.%s
            WHERE qr.%s = ?
            GROUP BY st.%s
        """,
        Student.Constant.ID, Student.Constant.Name,
        Student.Constant.EMAIL, Student.Constant.GENDER,
        Constant.TABLE_NAME, Student.Constant.TABLE_NAME,
        Student.Constant.ID, Constant.STUDENT_ID,
        Constant.QUIZ_ID, Student.Constant.ID);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:quizzess.db");
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quiz.getQuizId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Student st = new Student();
                st.setId(rs.getInt(1));
                st.setName(rs.getString(2));
                st.setEmail(rs.getString(3));
                st.setGender(rs.getString(4));
                students.add(st);
            }
        } catch (Exception ex) {
            System.err.println("❌ Error fetching students: " + ex.getMessage());
        }

        return students;
    }

    public Integer getNumberOfAttemptedQuestions() {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?",
            QuizResultDetails.Constant.TABLE_NAME,
            QuizResultDetails.Constant.QUIZ_RESULT_ID);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:quizzess.db");
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, this.id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception ex) {
            System.err.println("❌ Error counting attempted questions: " + ex.getMessage());
            return 0;
        }
    }

    public static List<QuizResult> getResult(Student student) {
        return new ArrayList<>(getQuizzes(student).keySet());
    }
}
