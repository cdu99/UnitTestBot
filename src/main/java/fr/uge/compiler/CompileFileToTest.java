package fr.uge.compiler;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class CompileFileToTest {

    public static Map<String, byte[]> compile(File fileToCompile) throws IOException {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManagerToGetFile = compiler
                .getStandardFileManager(diagnostics, null, null);

        ExtendedStandardJavaFileManager fileManager = new ExtendedStandardJavaFileManager(compiler.getStandardFileManager(null, null, null));
        Iterable<? extends JavaFileObject> compilationUnit = fileManagerToGetFile
                .getJavaFileObjectsFromFiles(Arrays.asList(fileToCompile));
        JavaCompiler.CompilationTask task = compiler
                .getTask(null, fileManager, diagnostics, null, null, compilationUnit);

        try {
            if (task.call()) {
                System.out.println("File compiled");
                Map<String, byte[]> classBytes = fileManager.getClassBytes();
                return classBytes;
            } else {
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    // TODO
                    // Send discord notification: COMPILATION ERROR
                    System.out.format("Error on line %d in %s%n", diagnostic.getLineNumber(), diagnostic.getSource().toUri());
                }
                return null;
            }
        } finally {
            fileToCompile.delete();
            fileManager.close();
        }
    }
}