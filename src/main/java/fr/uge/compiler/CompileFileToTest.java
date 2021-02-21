package fr.uge.compiler;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class CompileFileToTest {

    public static byte[] compile(File fileToCompile) throws IOException {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler
                .getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> compilationUnit = fileManager
                .getJavaFileObjectsFromFiles(Arrays.asList(fileToCompile));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter( baos );
        JavaCompiler.CompilationTask task = compiler
                .getTask(osw, fileManager, diagnostics, null, null, compilationUnit);

        osw.flush();

        try {
            if (task.call()) {
                System.out.println("File compiled");
                return baos.toByteArray();
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