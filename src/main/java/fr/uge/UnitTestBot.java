package fr.uge;

import fr.uge.bot.BotUtility;
import fr.uge.compiled.ByteClassLoader;
import fr.uge.compiled.Compiler;
import fr.uge.database.Database;
import fr.uge.database.TestResult;
import fr.uge.test.TestRunner;
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
    private static final int DEFAULT_LIFETIME = 10;
    private final Map<String, byte[]> testData = new HashMap<>();
    private final Map<String, ScheduledFuture<?>> testLifetime = new HashMap<>();
    private final Database database;
    private final ScheduledExecutorService testDeletionScheduledExecutor;

    private UnitTestBot() {
        database = new Database();
        database.createTable();
        testDeletionScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    private static UnitTestBot instance = null;

    public static synchronized UnitTestBot getInstance() {
        if (instance == null) {
            instance = new UnitTestBot();
        }
        return instance;
    }

    public void addUnitTest(String name, byte[] testFileData, MessageReceivedEvent event) {
        if (testData.containsKey(name)) {
            testData.remove(name);
        }
        testData.put(name, testFileData);
        testLifetime.put(name, testDeletionScheduledExecutor
                .schedule(new TestDeletionSchedule(name, event), DEFAULT_LIFETIME, TimeUnit.SECONDS));
        BotUtility.sendNewTestNotification(event, name.split("\\.")[0], DEFAULT_LIFETIME);
    }

    public void removeUnitTest(String name, MessageReceivedEvent event) {
        if (testData.containsKey(name)) {
            testData.remove(name);
            testLifetime.get(name).cancel(false);
            testLifetime.remove(name);
            BotUtility.sendSuccessfullyRemovedTest(event, name);
        } else {
            BotUtility.sendFailToRemoveTestMessage(event, name);
        }
    }

    public void compileAndTest(File file, MessageReceivedEvent event) throws IOException {
        String fileName = file.getName().split("\\.")[0];
        String expectedTestFileName = fileName + "Test";

        // Check if test exist
        if (!testData.containsKey(expectedTestFileName)) {
            Files.delete(file.getAbsoluteFile().toPath());
            BotUtility.sendNoAvailableTestForNowMessage(event, fileName);
            return;
        }
        // Compile
        Map<String, byte[]> compiled = compileSource(file);
        if (compiled == null) {
            BotUtility.sendCompilationErrorMessage(event);
            return;
        }
        // Create class loader and add all required data to load
        var currentClassLoader = createTestClassLoader(expectedTestFileName);
        List<String> classesToLoad = new ArrayList<>();
        for (Map.Entry<String, byte[]> entry : compiled.entrySet()) {
            classesToLoad.add(entry.getKey());
            currentClassLoader.addClassData(entry.getKey(), entry.getValue());
        }
        // Run the test with class loader
        var testRunner = new TestRunner(currentClassLoader, classesToLoad);
        List<TestResult> testResults = testing(testRunner, expectedTestFileName, event);
        // Add results to database
        addTestResultsToDatabase(testResults);
        BotUtility.sendEmbedTestResult(event, testResults, fileName);
    }

    private Map<String, byte[]> compileSource(File file) throws IOException {
        Compiler compiler = new Compiler(file);
        return compiler.compile();
    }

    private ByteClassLoader createTestClassLoader(String name) {
        var data = testData.get(name);
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

    private class TestDeletionSchedule implements Runnable {
        private String testScheduledToBeDeleted;
        private MessageReceivedEvent event;

        public TestDeletionSchedule(String name, MessageReceivedEvent event) {
            Objects.requireNonNull(name);
            this.testScheduledToBeDeleted = name;
            this.event = event;
        }

        @Override
        public void run() {
            removeUnitTest(testScheduledToBeDeleted, event);
        }
    }
}


