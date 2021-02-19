package fr.uge.test;

import fr.uge.database.Database;
import fr.uge.database.TestResult;
import org.junit.platform.commons.PreconditionViolationException;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class UnitTestListener implements TestExecutionListener {
    private TestResult currentTestResult;
    private List<TestResult> testResults;
    private final String studentId;

    public UnitTestListener(String studentId) {
        this.studentId = studentId;
    }

    // TODO
    // There are <null> tests to fix...
    public List<TestResult> getTestResults() {
        return testResults;
    }

    public void testPlanExecutionStarted(TestPlan testPlan) {
        this.currentTestResult = new TestResult();
        this.testResults = new ArrayList<>();
    }

    public void testPlanExecutionFinished(TestPlan testPlan) {
        // TODO
        // Do something??
    }

    public void executionStarted(TestIdentifier testIdentifier) {
        if (testIdentifier.isTest()) {
            currentTestResult.setStudent(studentId);
            currentTestResult.setTest(testIdentifier.getDisplayName());
            currentTestResult.setQuestion(testIdentifier.getTags().iterator().next().getName());
        }
    }

    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        switch (testExecutionResult.getStatus()) {
            case SUCCESSFUL:
                if (testIdentifier.isTest()) {
                    currentTestResult.setResult(true);
                }
                testResults.add(currentTestResult);
                currentTestResult = new TestResult();
                break;
            case FAILED:
                if (testIdentifier.isTest()) {
                    currentTestResult.setResult(false);
                }
                testResults.add(currentTestResult);
                currentTestResult = new TestResult();
                break;
            case ABORTED:
                testResults.add(currentTestResult);
                currentTestResult = new TestResult();
                break;
            default:
                throw new PreconditionViolationException("Unsupported execution status:" + testExecutionResult.getStatus());
        }
    }
}
