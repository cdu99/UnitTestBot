package fr.uge;

import fr.uge.bot.BotUtility;
import fr.uge.compiler.ByteClassLoader;
import fr.uge.compiler.CompileFileToTest;
import fr.uge.database.Database;
import fr.uge.database.TestResult;
import fr.uge.test.TestRunner;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO
// Don't keep files on disk ? Keep in memory : Delete files once we're done
public class UnitTestBot {
    private final Map<String, ByteClassLoader> testClassLoaders = new HashMap();
    private final Database database;
    private List<TestResult> testResults;

    private UnitTestBot() {
        database = new Database();
    }

    private static UnitTestBot INSTANCE = null;

    public static synchronized UnitTestBot getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UnitTestBot();
        }
        return INSTANCE;
    }

    public void addTestToClassLoader(String name, byte[] testFileData) {
        if (testClassLoaders.containsKey(name)) {
            testClassLoaders.remove(name);
        }
        testClassLoaders.put(name, new ByteClassLoader(name, testFileData));
    }

    public void compileAndTest(File file, MessageReceivedEvent event) throws IOException {
        String fileName = file.getName().split("\\.")[0];
        String expectedTestFileName = fileName + "Test";
        String studentId = event.getAuthor().getAsTag();

//        if (!CompileFileToTest.compile(file)) {
//            BotUtility.sendCompilationErrorMessage(event, fileName);
//            return;
//        }

        Map<String, byte[]> data = CompileFileToTest.compile(file);
        System.out.println(data.toString());
        if (data == null) {
            System.out.println("NSM");
        }

        List<String> javaClassToLoad = new ArrayList<>();
        var currentClassLoader = testClassLoaders.get(expectedTestFileName);
        for (Map.Entry<String, byte[]> entry : data.entrySet()) {
            javaClassToLoad.add(entry.getKey());
            currentClassLoader.addClassData(entry.getKey(), entry.getValue());
        }

        try {
            TestRunner testRunner = new TestRunner(currentClassLoader, javaClassToLoad);
            testResults = testRunner.run(expectedTestFileName, studentId);
            testResults.forEach(testResult -> database.insertTestResultBean(testResult));
        } catch (ClassNotFoundException e) {
            event.getChannel().sendMessage("No unit test found for this class").queue();
            throw new AssertionError(e);
        }

        BotUtility.sendEmbedTestResult(event, testResults, fileName);
    }
}


