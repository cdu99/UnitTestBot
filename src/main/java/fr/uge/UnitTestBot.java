package fr.uge;

import fr.uge.bot.BotUtility;
import fr.uge.compiled.ByteClassLoader;
import fr.uge.compiled.CompileFileToTest;
import fr.uge.database.Database;
import fr.uge.database.TestResult;
import fr.uge.test.TestRunner;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO
// Don't keep files on disk ? Keep in memory : Delete files once we're done
public class UnitTestBot {
    private final Map<String, byte[]> testClassData = new HashMap();
    private final Database database;
    private List<TestResult> testResults;

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

    public void addTestToClassLoader(String name, byte[] testFileData) {
        if (testClassData.containsKey(name)) {
            testClassData.remove(name);
        }
        testClassData.put(name, testFileData);
    }

    public void compileAndTest(File file, MessageReceivedEvent event) throws IOException {
        String fileName = file.getName().split("\\.")[0];
        String expectedTestFileName = fileName + "Test";
        String studentId = event.getAuthor().getAsTag();

//        if (!CompileFileToTest.compile(file)) {
//            BotUtility.sendCompilationErrorMessage(event, fileName);
//            return;
//        }

        CompileFileToTest cp = new CompileFileToTest(file);
        Map<String, byte[]> data = cp.compile();
        if (data == null) {
            BotUtility.sendCompilationErrorMessage(event, fileName);
        }

        List<String> javaClassesToLoad = new ArrayList<>();
        var dataded = testClassData.get(expectedTestFileName);
        var currentClassLoader = new ByteClassLoader(expectedTestFileName, dataded);
        for (Map.Entry<String, byte[]> entry : data.entrySet()) {
            javaClassesToLoad.add(entry.getKey());
            currentClassLoader.addClassData(entry.getKey(), entry.getValue());
        }

        try {
            TestRunner testRunner = new TestRunner(currentClassLoader, javaClassesToLoad);
            testResults = testRunner.run(expectedTestFileName, studentId);
            testResults.forEach(testResult -> database.insertTestResultBean(testResult));
        } catch (ClassNotFoundException e) {
            event.getChannel().sendMessage("No unit test found for this class").queue();
            throw new AssertionError(e);
        }

        BotUtility.sendEmbedTestResult(event, testResults, fileName);
    }
}


