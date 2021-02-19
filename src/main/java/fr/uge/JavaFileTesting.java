package fr.uge;

import fr.uge.bot.BotService;
import fr.uge.compiler.CompileFileToTest;
import fr.uge.database.Database;
import fr.uge.database.TestResult;
import fr.uge.test.TestRunner;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;

// TODO
// Don't keep files on disk ? Keep in memory : Delete files once we're done
public class JavaFileTesting {
    private List<TestResult> testResults;
    private final Database database;

    public JavaFileTesting() {
        database = new Database();
    }

    public void compileAndTest(File file, MessageReceivedEvent event) throws IOException {
        String fileName = file.getName().split("\\.")[0];
        String expectedTestFileName = fileName + "Test";
        String studentId = event.getAuthor().getAsTag();

        if (!CompileFileToTest.compile(file)) {
            BotService.sendCompilationErrorMessage(event, fileName);
            return;
        }

        try {
            TestRunner testRunner = new TestRunner(fileName);
            testResults = testRunner.run(expectedTestFileName, studentId);
            testResults.forEach(testResult -> database.insertTestResultBean(testResult));
        } catch (ClassNotFoundException e) {
            event.getChannel().sendMessage("No unit test found for this class").queue();
            throw new AssertionError(e);
        }

        BotService.sendEmbedTestResult(event, testResults, fileName);
    }
}
