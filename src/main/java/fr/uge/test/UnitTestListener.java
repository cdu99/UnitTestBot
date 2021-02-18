package fr.uge.test;

import org.jdbi.v3.core.Jdbi;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import static fr.uge.database.Database.UNIT_TEST_DATABASE;

public class UnitTestListener implements TestExecutionListener {
    private TestPlan testPlan;
    private Jdbi jdbi;

    public void testPlanExecutionStarted(TestPlan testPlan) {
        this.testPlan = testPlan;
        this.jdbi = Jdbi.create(UNIT_TEST_DATABASE);
    }

    public void dynamicTestRegistered(TestIdentifier testIdentifier) {
        if (testIdentifier.isTest()) {
            testIdentifier.getTags();
        }
    }

    public void testPlanExecutionFinished(TestPlan testPlan) {

    }
}
