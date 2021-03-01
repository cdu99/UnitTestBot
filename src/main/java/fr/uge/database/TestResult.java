package fr.uge.database;

import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.util.Objects;

public class TestResult {
    private String student;
    private String test_file;
    private String question;
    private String test;
    private boolean result;

    @ColumnName("student")
    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        Objects.requireNonNull(student);
        this.student = student;
    }

    @ColumnName("test_file")
    public String getTest_file() {
        return test_file;
    }

    public void setTest_file(String test_file) {
        Objects.requireNonNull(test_file);
        this.test_file = test_file;
    }

    @ColumnName("question")
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @ColumnName("test")
    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        Objects.requireNonNull(test);
        this.test = test;
    }

    @ColumnName("result")
    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return getQuestion() + ". " + getTest();
    }

    public int getQuestionTagNumber() {
        try {
            return Integer.parseInt(question.substring(question.lastIndexOf("Q") + 1));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
