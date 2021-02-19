package fr.uge.database;

import org.jdbi.v3.core.mapper.reflect.ColumnName;

public class TestResult {
    private String student;
    private String question;
    private String test;
    private boolean result;

    @ColumnName("student")
    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
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
        this.test = test;
    }

    @ColumnName("result")
    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
