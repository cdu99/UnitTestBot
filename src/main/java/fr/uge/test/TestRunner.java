package fr.uge.test;

import fr.uge.database.TestResult;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.File;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class TestRunner {
    private final ClassLoader classLoader;

    public TestRunner(String javaCompiledFileToLoad) throws MalformedURLException, ClassNotFoundException {
        File file = new File("src\\main\\java\\fr\\uge\\test");
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
//        var summaryGeneratingListener = new SummaryGeneratingListener();
        var unitTestListener = new UnitTestListener(studentId);
        launcher.registerTestExecutionListeners(unitTestListener);
        launcher.execute(launcherDiscoveryRequest);

        return unitTestListener.getTestResults();
//        var summary = summaryGeneratingListener.getSummary();
//        if (summary.getTotalFailureCount() != 0) {
//            var writer = new PrintWriter(System.err);
//            summary.printTo(writer);
//            summary.printFailuresTo(writer);
//        }
//        if (summary.getTestsFoundCount() == 0) {
//            System.out.println("No tests found");
//        } else {
//            System.out.println(summary.getTestsFoundCount() + " tests found");
//        }
    }
}
