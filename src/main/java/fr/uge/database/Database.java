package fr.uge.database;

import org.jdbi.v3.core.Jdbi;

import java.util.Objects;

public class Database {
    public static final String IN_MEMORY_DB = "jdbc:sqlite::memory:";
    public static final String UNIT_TEST_DATABASE = "jdbc:sqlite:unit-test.db";
    private final Jdbi jdbi;

    public Database() {
        this.jdbi = Jdbi.create(UNIT_TEST_DATABASE);
    }

    public void createTable() {
        jdbi.useHandle(handle -> {
            handle.execute("drop table if exists test_result;");
            handle.execute("create table test_result(student String," +
                    "question String," +
                    "test String," +
                    "result boolean);");
        });
    }

    // TODO
    // Useless method??
    public void insertTestResult(String student, String question, String testName, boolean result) {
        jdbi.useHandle(handle -> handle.execute("insert into test_result values(?, ?, ?, ?)"
                , student, question, testName, result));
    }

    public void insertTestResultBean(TestResult testResult) {
        Objects.requireNonNull(testResult);
        jdbi.useHandle(handle -> handle.createUpdate("insert into test_result " +
                "(student, question, test, result) values " +
                "(:student, :question, :test, :result)")
                .bindBean(testResult)
                .execute());
    }
}
