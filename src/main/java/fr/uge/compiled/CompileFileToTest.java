package fr.uge.compiled;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public record CompileFileToTest(File fileToCompile) {
    public CompileFileToTest {
        Objects.requireNonNull(fileToCompile);
    }

    public Map<String, byte[]> compile() throws IOException {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        var compiler = ToolProvider.getSystemJavaCompiler();
        var standardFileManager = compiler
                .getStandardFileManager(diagnostics, null, null);
        var fileManager = new ByteFileManager
                (compiler.getStandardFileManager(null, null, null));
        var compilationUnit = standardFileManager
                .getJavaFileObjectsFromFiles(Arrays.asList(fileToCompile));
        JavaCompiler.CompilationTask task = compiler
                .getTask(null, fileManager, diagnostics, null, null, compilationUnit);

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
            Files.delete(fileToCompile.getAbsoluteFile().toPath());
            fileManager.close();
        }
    }
}