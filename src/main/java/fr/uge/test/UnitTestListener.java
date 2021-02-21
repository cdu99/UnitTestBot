package fr.uge.test;

import fr.uge.database.TestResult;
import org.junit.platform.commons.PreconditionViolationException;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class UnitTestListener implements TestExecutionListener {
    private TestResult currentTestResult;
    private List<TestResult> testResults;
    private final String studentId;

    public UnitTestListener(String studentId) {
        this.studentId = studentId;
    }

    public List<TestResult> getTestResults() {
        return testResults;
    }

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        this.currentTestResult = new TestResult();
        this.testResults = new ArrayList<>();
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if (testIdentifier.isTest()) {
            currentTestResult.setStudent(studentId);
            currentTestResult.setTest(testIdentifier.getDisplayName());
            try {
                currentTestResult.setQuestion(testIdentifier.getTags().iterator().next().getName());
            } catch (NoSuchElementException e) {
                System.out.println("No question tag");
                currentTestResult.setQuestion("*");
            }
        }
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        TestExecutionResult.Status result = testExecutionResult.getStatus();
        if (testIdentifier.isTest()) {
            if (result == TestExecutionResult.Status.SUCCESSFUL) {
                currentTestResult.setResult(true);
            } else if (result == TestExecutionResult.Status.FAILED) {
                currentTestResult.setResult(false);
            } else {
                throw new PreconditionViolationException("Unsupported execution status:" + testExecutionResult.getStatus());
            }
            testResults.add(currentTestResult);
            currentTestResult = new TestResult();
        }
    }
}
