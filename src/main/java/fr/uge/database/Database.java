package fr.uge.database;

import org.jdbi.v3.core.Jdbi;

public class Database {
    public static final String IN_MEMORY_DB = "jdbc:sqlite::memory:";
    public static final String UNIT_TEST_DATABASE = "jdbc:sqlite:unit-test.db";
    private final Jdbi jdbi;

    public Database() {
        this.jdbi = Jdbi.create(IN_MEMORY_DB);
    }

    public void createTables() {
        jdbi.useHandle(handle -> {
            handle.execute("drop table if exists test_result;");
            handle.execute("create table test_result(student string," +
                    "question string," +
                    "test string," +
                    "result boolean);");
        });
    }

    public void insertTestResult(String student, String question, String testName, boolean result) {
        jdbi.useHandle(handle -> handle.execute("insert into test_result values(?, ?, ?, ?)"
                , student, question, testName, result));
    }
}
