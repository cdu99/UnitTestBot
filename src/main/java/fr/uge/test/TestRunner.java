package fr.uge.test;

import fr.uge.compiler.ByteClassLoader;
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
    private final ByteClassLoader classLoader;

    public TestRunner(ByteClassLoader classLoader, String javaCompiledFileToLoad) throws MalformedURLException, ClassNotFoundException {
        this.classLoader = classLoader;
        classLoader.loadClass(javaCompiledFileToLoad);
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
        try {
            builder.selectors(selectClass(classLoader.loadClass(classFileName)));
        } catch (NoClassDefFoundError e) {
            String classBinaryName = getClassBinaryName(e.getMessage());
            builder.selectors(selectClass(classLoader.loadClass(classBinaryName)));
        }

        builder.configurationParameter("junit.jupiter.execution.parallel.enabled", "true");

        Launcher launcher = LauncherFactory.create();
        LauncherDiscoveryRequest launcherDiscoveryRequest = builder.build();
        var unitTestListener = new UnitTestListener(studentId);
        launcher.registerTestExecutionListeners(unitTestListener);
        launcher.execute(launcherDiscoveryRequest);

        return unitTestListener.getTestResults();
    }

    private static String getClassBinaryName(String errorMsg){

        // Start and end index of cutting

        int endIndex = errorMsg.indexOf(" ");

        // Let's save a substring
        String classPackage = errorMsg.substring(0, endIndex);

        // Replace char '/' to '.'
        classPackage = classPackage.replace('/', '.');

        return classPackage;
    }
}
