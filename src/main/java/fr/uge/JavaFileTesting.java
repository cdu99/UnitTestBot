package fr.uge;

import fr.uge.bot.BotUtility;
import fr.uge.compiler.CompileFileToTest;
import fr.uge.database.Database;
import fr.uge.database.TestResult;
import fr.uge.test.TestRunner;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
            BotUtility.sendCompilationErrorMessage(event, fileName);
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

        BotUtility.sendEmbedTestResult(event, testResults, fileName);
    }

    public static String getClassName(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        dis.readLong(); // skip header and class version
        int cpcnt = (dis.readShort()&0xffff)-1;
        int[] classes = new int[cpcnt];
        String[] strings = new String[cpcnt];
        for(int i=0; i<cpcnt; i++) {
            int t = dis.read();
            if(t==7) classes[i] = dis.readShort()&0xffff;
            else if(t==1) strings[i] = dis.readUTF();
            else if(t==5 || t==6) { dis.readLong(); i++; }
            else if(t==8) dis.readShort();
            else dis.readInt();
        }
        dis.readShort(); // skip access flags
        return strings[classes[(dis.readShort()&0xffff)-1]-1].replace('/', '.');
    }
}


