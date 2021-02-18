package fr.uge;

import fr.uge.compiler.CompileFileToTest;
import fr.uge.test.TestRunner;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.io.IOException;

public class JavaFileTesting {

    public static void compileAndTest(File file, MessageReceivedEvent event) throws IOException {
        String fileName = file.getName().split("\\.")[0];
        String expectedTestFileName = fileName + "Test";

        CompileFileToTest.compile(file);

        try {
            TestRunner testRunner = new TestRunner(fileName);
            testRunner.run(expectedTestFileName);
        } catch (ClassNotFoundException e) {
            event.getChannel().sendMessage("No unit test found for this class").queue();
            throw new AssertionError(e);
        }
    }
}
