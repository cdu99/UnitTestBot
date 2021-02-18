package fr.uge;

import fr.uge.compiler.CompileFileToTest;
import fr.uge.database.TestResult;
import fr.uge.test.TestRunner;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JavaFileTesting {
    private List<TestResult> testResults = new ArrayList<>();

    public static void compileAndTest(File file, MessageReceivedEvent event) throws IOException {
        String fileName = file.getName().split("\\.")[0];
        String expectedTestFileName = fileName + "Test";

        CompileFileToTest.compile(file);

        try {
            String studentId = event.getAuthor().getAsTag();
            TestRunner testRunner = new TestRunner(fileName);
            testRunner.run(expectedTestFileName, studentId);
        } catch (ClassNotFoundException e) {
            event.getChannel().sendMessage("No unit test found for this class").queue();
            throw new AssertionError(e);
        }
    }
}
