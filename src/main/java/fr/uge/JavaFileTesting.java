package fr.uge;

import fr.uge.compiler.CompileFileToTest;
import fr.uge.test.TestRunner;

import java.io.File;
import java.io.IOException;

public class JavaFileTesting {

    public static void compileAndTest(File file) throws IOException, ClassNotFoundException {
        CompileFileToTest compiler = new CompileFileToTest();
        compiler.compile(file);

        String fileName = file.getName().split("\\.")[0];
        // Check if tests for provided file are available

        TestRunner testRunner = new TestRunner(fileName);
        testRunner.run("FifoTest");
    }
}
