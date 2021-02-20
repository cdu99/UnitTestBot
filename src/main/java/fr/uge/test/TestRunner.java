package fr.uge.test;

import fr.uge.compiler.JavaByteObject;
import fr.uge.database.TestResult;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class TestRunner {
    private final ClassLoader classLoader;

    public TestRunner(String javaCompiledFileToLoad) throws MalformedURLException, ClassNotFoundException {
        File file = new File("test-sources");
        URL classUrl = file.toURI().toURL();
        URL[] classUrls = new URL[]{classUrl};
        ClassLoader classLoader = new URLClassLoader(classUrls, getClass().getClassLoader());
        classLoader.loadClass(javaCompiledFileToLoad);
        this.classLoader = classLoader;
    }

    public List<TestResult> run(String classFileName, String studentId) throws ClassNotFoundException {
        var oldContext = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            return runTests(classFileName, studentId);
        } finally {
            Thread.currentThread().setContextClassLoader(oldContext);
        }
    }

    private List<TestResult> runTests(String classFileName, String studentId) throws ClassNotFoundException {
        LauncherDiscoveryRequestBuilder builder = LauncherDiscoveryRequestBuilder.request();
        builder.selectors(selectClass(classLoader.loadClass(classFileName)));
        builder.configurationParameter("junit.jupiter.execution.parallel.enabled", "true");

        Launcher launcher = LauncherFactory.create();
        LauncherDiscoveryRequest launcherDiscoveryRequest = builder.build();
        var unitTestListener = new UnitTestListener(studentId);
        launcher.registerTestExecutionListeners(unitTestListener);
        launcher.execute(launcherDiscoveryRequest);

        return unitTestListener.getTestResults();
    }


    private static ClassLoader createClassLoader(final JavaByteObject byteObject) {
        return new ClassLoader() {
            @Override
            public Class<?> findClass(String name) throws ClassNotFoundException {
                //no need to search class path, we already have byte code.
                byte[] bytes = byteObject.getBytes();
                return defineClass(name, bytes, 0, bytes.length);
            }
        };
    }
}
