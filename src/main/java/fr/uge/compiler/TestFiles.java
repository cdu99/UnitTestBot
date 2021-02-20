package fr.uge.compiler;

import fr.uge.database.TestResult;
import fr.uge.test.UnitTestListener;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class TestFiles {
    public Map<String, ClassLoader> testClassLoaders = new HashMap();
    private ClassLoader cl;

    public void addTestFile(String name, byte[] classFile) {

        cl = createClassLoader(classFile);
    }

    private static ClassLoader createClassLoader(byte[] data) {
        return new ClassLoader() {
            @Override
            public Class<?> findClass(String name) throws ClassNotFoundException {
                //no need to search class path, we already have byte code.
                return defineClass(name, data, 0, data.length);
            }
        };
    }

    public void run(String classFileName) throws ClassNotFoundException {
        var oldContext = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(cl);
        try {
            runTests(classFileName);
        } finally {
            Thread.currentThread().setContextClassLoader(oldContext);
        }
    }

    private void runTests(String classFileName) throws ClassNotFoundException {
        LauncherDiscoveryRequestBuilder builder = LauncherDiscoveryRequestBuilder.request();
        builder.selectors(selectClass(cl.loadClass(classFileName)));
        builder.configurationParameter("junit.jupiter.execution.parallel.enabled", "true");

        Launcher launcher = LauncherFactory.create();
        LauncherDiscoveryRequest launcherDiscoveryRequest = builder.build();
        var sum = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(sum);
        launcher.execute(launcherDiscoveryRequest);

        System.out.println(sum.getSummary().getTestsFoundCount());
    }
}
