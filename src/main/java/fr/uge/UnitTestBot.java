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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnitTestBot {
    private final Map<String, byte[]> testData = new HashMap<>();
    private final Database database;

    private UnitTestBot() {
        database = new Database();
        database.createTable();
    }

    private static UnitTestBot INSTANCE = null;

    public static synchronized UnitTestBot getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UnitTestBot();
        }
        return INSTANCE;
    }

    public void addUnitTest(String name, byte[] testFileData) {
        if (testData.containsKey(name)) {
            testData.remove(name);
        }
        testData.put(name, testFileData);
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
            BotUtility.sendCompilationErrorMessage(event, fileName);
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
}


