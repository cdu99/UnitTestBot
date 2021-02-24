package fr.uge.database;

import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.Objects;

public class Database {
    private static final String UNIT_TEST_DATABSE = "jdbc:sqlite:unit-test.db";
    private final Jdbi jdbi;

    public Database() {
        this.jdbi = Jdbi.create(UNIT_TEST_DATABSE);
    }

    public void createTable() {
        jdbi.useHandle(handle -> {
            handle.execute("drop table if exists test_result;");
            handle.execute("create table test_result(student String," +
                    "test_file String," +
                    "question String," +
                    "test String," +
                    "result boolean);");
        });
    }

    public void insertTestResultBean(TestResult testResult) {
        Objects.requireNonNull(testResult);
        jdbi.useHandle(handle -> handle.createUpdate("insert into test_result " +
                "(student, test_file, question, test, result) values " +
                "(:student, :test_file, :question, :test, :result)")
                .bindBean(testResult)
                .execute());
    }

    public List<TestResult> getTestResultsForTest(String testFile) {
        Objects.requireNonNull(testFile);
        return jdbi.withHandle(handle ->
                handle.createQuery("select * from test_result where " +
                        "test_file = :test_file order by question , test")
                .bind("test_file", testFile)
                .mapToBean(TestResult.class)
                .list());
    }
}
