package fr.uge;

import fr.uge.compiler.CompileFileToTest;

import java.io.File;
import java.io.IOException;

public class JavaFileTesting {

    public static void compileAndTest(File file) throws IOException {
        CompileFileToTest compiler = new CompileFileToTest();
        compiler.compile(file);
        System.out.println(file.getName());
    }
}
