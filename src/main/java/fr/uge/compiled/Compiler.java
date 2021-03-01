package fr.uge.compiled;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public class Compiler {
    private final File fileToCompile;
    private final JavaCompiler javac;

    public Compiler(File fileToCompile) {
        Objects.requireNonNull(fileToCompile);
        this.fileToCompile = fileToCompile;
        this.javac = ToolProvider.getSystemJavaCompiler();
    }

    public Map<String, byte[]> compile() throws IOException {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        var standardFileManager = javac.getStandardFileManager(diagnostics, null, null);
        var fileManager = new ByteFileManager(standardFileManager);
        var compilationUnit = standardFileManager.getJavaFileObjectsFromFiles(Collections.singletonList(fileToCompile));
        JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, diagnostics, null, null, compilationUnit);

        try {
            if (Boolean.TRUE.equals(task.call())) {
                return fileManager.getClassBytes();
            } else {
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    System.out.format("Error on line %d in %s%n", diagnostic.getLineNumber(), diagnostic.getSource().toUri());
                }
                return null;
            }
        } finally {
            standardFileManager.close();
            fileManager.close();
        }
    }
}