package fr.uge.test;

import fr.uge.bot.BotUtility;
import fr.uge.compiled.ByteClassLoader;
import fr.uge.database.TestResult;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import java.util.List;
import java.util.Objects;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class TestRunner {
    private final ByteClassLoader classLoader;

    public TestRunner(ByteClassLoader classLoader, List<String> compiledFilesToLoad) {
        Objects.requireNonNull(classLoader);
        Objects.requireNonNull(compiledFilesToLoad);
        this.classLoader = classLoader;
        compiledFilesToLoad.forEach(compiledFile -> {
            try {
                classLoader.loadClass(compiledFile);
            } catch (ClassNotFoundException e) {
                throw new AssertionError(e);
            }
        });
    }

    public List<TestResult> run(String testName, MessageReceivedEvent event) throws ClassNotFoundException {
        var oldContext = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            return runTests(testName, event);
        } finally {
            Thread.currentThread().setContextClassLoader(oldContext);
        }
    }

    private List<TestResult> runTests(String testName, MessageReceivedEvent event) throws ClassNotFoundException {
        var builder = LauncherDiscoveryRequestBuilder.request();
        try {
            builder.selectors(selectClass(classLoader.loadClass(testName)));
        } catch (NoClassDefFoundError e) {
            // Avoid NoClassDefFoundError if the test is in a package
            String testBinaryName = getClassBinaryName(e.getMessage());
            classLoader.changeClassDataName(testName, testBinaryName);
            builder.selectors(selectClass(classLoader.loadClass(testBinaryName)));
        } catch (ClassFormatError e) {
            BotUtility.sendErrorTestFileNotCorrectMessage(event, testName);
            throw new AssertionError(e);
        }
        builder.configurationParameter("junit.jupiter.execution.parallel.enabled", "true");
        var launcher = LauncherFactory.create();
        var launcherDiscoveryRequest = builder.build();
        var unitTestListener = new UnitTestListener(event.getAuthor().getAsTag(), testName);
        launcher.registerTestExecutionListeners(unitTestListener);
        try {
            launcher.execute(launcherDiscoveryRequest);
        } catch (JUnitException je) {
            BotUtility.sendErrorDuringTestMessage(event);
            throw new AssertionError(je);
        }

        return unitTestListener.getTestResults();
    }

    // Find binary name with NoClassDefFoundError error msg
    private String getClassBinaryName(String errorMsg) {
        int endIndex = errorMsg.indexOf(" ");
        String classBinaryName = errorMsg.substring(0, endIndex);
        classBinaryName = classBinaryName.replace('/', '.');
        return classBinaryName;
    }
}
