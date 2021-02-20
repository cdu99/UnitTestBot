package fr.uge.compiler;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.util.HashMap;
import java.util.Map;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class TestFiles {
    public Map<String, ClassLoader> testClassLoaders = new HashMap();
    private ByteClassLoader cl;

    public void addTestFile(String name, byte[] classFile) {

        cl = createClassLoader(classFile, name);
    }

    private static ByteClassLoader createClassLoader(byte[] data, String name) {
        return new ByteClassLoader(name, data);
//        return new ClassLoader() {
//            @Override
//            public Class<?> findClass(String name) throws ClassNotFoundException {
//                //no need to search class path, we already have byte code.
//                return defineClass(name, data, 0, data.length);
//            }
//        };
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
        try {
            builder.selectors(selectClass(cl.loadClass(classFileName)));
        } catch (NoClassDefFoundError e) {
            String classBinaryName = getClassBinaryName(e.getMessage());
            builder.selectors(selectClass(cl.loadClass(classBinaryName)));
        }

        builder.configurationParameter("junit.jupiter.execution.parallel.enabled", "true");

        Launcher launcher = LauncherFactory.create();
        LauncherDiscoveryRequest launcherDiscoveryRequest = builder.build();
        var sum = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(sum);
        launcher.execute(launcherDiscoveryRequest);

        System.out.println(sum.getSummary().getTestsFoundCount());
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
