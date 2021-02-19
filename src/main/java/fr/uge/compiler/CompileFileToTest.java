package fr.uge.compiler;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class CompileFileToTest {

    public static void compile(File fileToCompile) throws IOException {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler
                .getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> compilationUnit = fileManager
                .getJavaFileObjectsFromFiles(Arrays.asList(fileToCompile));
        JavaCompiler.CompilationTask task = compiler
                .getTask(null, fileManager, diagnostics, null, null, compilationUnit);

        if (task.call()) {
            System.out.println("File compiled");
        } else {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                // TODO
                // Send discord notification: COMPILATION ERROR
                System.out.format("Error on line %d in %s%n", diagnostic.getLineNumber(), diagnostic.getSource().toUri());
            }
        }
        fileManager.close();
    }
}