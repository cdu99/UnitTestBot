package fr.uge.test;

import fr.uge.Main;
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

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

public class TestRunner {
    private final ClassLoader classLoader;

    public TestRunner() throws MalformedURLException, ClassNotFoundException {
        File file = new File("C:\\Users\\calvi\\IdeaProjects\\du-pietrac\\src\\main\\java");
        URL classUrl = file.toURI().toURL();
        URL[] urls = new URL[]{classUrl};
        ClassLoader ucl = new URLClassLoader(urls, getClass().getClassLoader());
        ucl.loadClass("fr.uge.test.MainTest");

        this.classLoader = ucl;
    }

    public int getAsInt() {
        var oldContext = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            return runTests();
        } finally {
            Thread.currentThread().setContextClassLoader(oldContext);
        }
    }

    private static int runTests() {
        LauncherDiscoveryRequestBuilder builder = LauncherDiscoveryRequestBuilder.request();

        // Select from...
        builder.selectors(selectPackage("fr.uge.test"));

        // Fine-tune configuration...
        builder.configurationParameter("junit.jupiter.execution.parallel.enabled", "true");

        Launcher launcher = LauncherFactory.create();
        LauncherDiscoveryRequest launcherDiscoveryRequest = builder.build();
        var summaryGeneratingListener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(summaryGeneratingListener);
        launcher.execute(launcherDiscoveryRequest);

        var summary = summaryGeneratingListener.getSummary();
        var success = summary.getTestsFailedCount() == 0 && summary.getTestsAbortedCount() == 0 &&
                summary.getContainersFailedCount() == 0 && summary.getContainersAbortedCount() == 0;
        if (success) {
            var succeeded = summary.getTestsSucceededCount();
            System.out.printf("success / tests found : " + summary.getTestsFoundCount());
        } else {
            var writer = new PrintWriter(System.err);
            summary.printTo(writer);
            summary.printFailuresTo(writer);
        }
        return success? 0: 1;
    }
}
