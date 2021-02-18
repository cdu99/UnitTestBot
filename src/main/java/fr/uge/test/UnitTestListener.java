package fr.uge.test;

import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

public class UnitTestListener implements TestExecutionListener {
    private TestPlan testPlan;

    public void testPlanExecutionStarted(TestPlan testPlan) {
        this.testPlan = testPlan;
    }
}
