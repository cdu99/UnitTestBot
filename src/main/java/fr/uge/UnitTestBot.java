package fr.uge;

import fr.uge.bot.BotUtility;
import fr.uge.compiled.ByteClassLoader;
import fr.uge.compiled.Compiler;
import fr.uge.database.Database;
import fr.uge.database.TestResult;
import fr.uge.test.TestRunner;
import fr.uge.xls.BuildTestResultXLS;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class UnitTestBot {
    private static final int DEFAULT_LIFETIME = 300;
    private final Map<String, byte[]> tests = new HashMap<>();
    private final Map<String, ScheduledFuture<?>> testsLifetime = new HashMap<>();
    private final Database database;
    private final ScheduledExecutorService executor;

    public UnitTestBot() {
        database = new Database();
        database.createTable();
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void addTest(String name, byte[] testData, MessageReceivedEvent event) {
        tests.put(name, testData);
        testsLifetime.put(name, executor
                .schedule(new TestDeletionSchedule(name, event), DEFAULT_LIFETIME, TimeUnit.SECONDS));
        BotUtility.sendNewTestNotification(event, name.split("\\.")[0], DEFAULT_LIFETIME);
    }

    public void removeTest(String name, MessageReceivedEvent event) {
        if (tests.containsKey(name)) {
            tests.remove(name);
            testsLifetime.get(name).cancel(false);
            testsLifetime.remove(name);
            BotUtility.sendSuccessfullyRemovedTest(event, name);
        } else {
            BotUtility.sendFailToRemoveTestMessage(event, name);
        }
    }

    public void compileAndTest(File file, MessageReceivedEvent event) throws IOException {
        String name = file.getName().split("\\.")[0];
        String expectedTestName = name + "Test";

        // Check if test exist
        if (!tests.containsKey(expectedTestName)) {
            Files.delete(file.getAbsoluteFile().toPath());
            BotUtility.sendNoAvailableTestMessage(event, name);
            return;
        }
        // Compile
        Map<String, byte[]> compiled = compileSource(file);
        if (compiled == null) {
            BotUtility.sendCompilationErrorMessage(event);
            return;
        }
        // Create class loader and add all required data to load
        var classLoader = createClassLoader(expectedTestName);
        List<String> classesToLoad = new ArrayList<>();
        for (Map.Entry<String, byte[]> entry : compiled.entrySet()) {
            classesToLoad.add(entry.getKey());
            classLoader.addClassData(entry.getKey(), entry.getValue());
        }
        // Run the test with class loader
        var testRunner = new TestRunner(classLoader, classesToLoad);
        List<TestResult> testResults = testing(testRunner, expectedTestName, event);
        // Add results to database
        addTestResultsToDatabase(testResults);
        BotUtility.sendEmbedTestResult(event, testResults, name);
    }

    private Map<String, byte[]> compileSource(File file) throws IOException {
        Compiler compiler = new Compiler(file);
        try {
            return compiler.compile();
        } finally {
            Files.delete(file.getAbsoluteFile().toPath());
        }
    }

    private ByteClassLoader createClassLoader(String name) {
        var data = tests.get(name);
        return new ByteClassLoader(name, data);
    }

    private List<TestResult> testing(TestRunner testRunner, String testFileName, MessageReceivedEvent event) {
        try {
            return testRunner.run(testFileName, event);
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    private void addTestResultsToDatabase(List<TestResult> testResults) {
        testResults.forEach(database::insertTestResultBean);
    }

    public void redefineLifetime(String testName, int newLifetime, MessageReceivedEvent event) {
        var schedule = testsLifetime.get(testName);
        if (schedule == null) {
            BotUtility.sendTestAlreadyRemovedMessage(testName, event);
            return;
        }
        schedule.cancel(false);
        testsLifetime.put(testName, executor
                .schedule(new TestDeletionSchedule(testName, event), newLifetime, TimeUnit.SECONDS));
        BotUtility.sendRedefiningLifetimeMessage(testName, event, newLifetime);
    }

    public byte[] createTestResultXLS(String testName) throws IOException {
        List<TestResult> testResults = getTestResultsFromDatabase(testName);
        if (testResults.isEmpty()) {
            // TODO When pas de test result
        }
        var xlsBuilder = new BuildTestResultXLS();
        return xlsBuilder.build(testResults);
    }

    private List<TestResult> getTestResultsFromDatabase(String testName) {
        return database.getTestResultsForTest(testName);
    }

    private class TestDeletionSchedule implements Runnable {
        private final String testName;
        private final MessageReceivedEvent event;

        public TestDeletionSchedule(String name, MessageReceivedEvent event) {
            Objects.requireNonNull(name);
            this.testName = name;
            this.event = event;
        }

        @Override
        public void run() {
            removeTest(testName, event);
        }
    }
}


