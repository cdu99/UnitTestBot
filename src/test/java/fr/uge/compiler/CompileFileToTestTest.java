package fr.uge.compiler;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class CompileFileToTestTest {

    @Test
    public void should_compile_java_file() throws IOException {
        File javaFileToBeCompiled = new File("src\\test\\java\\fr\\uge\\compiler\\ToBeCompiled.java");
        File expectedCompiledJavaFile = new File("src\\test\\java\\fr\\uge\\compiler\\ToBeCompiled.class");


        System.out.println(CompileFileToTest.compile(javaFileToBeCompiled));

        expectedCompiledJavaFile.delete();
    }
}