package fr.uge.test;

import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.FileSelector;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

import java.io.IOException;
import java.nio.file.Files;

public class MyCustomTestEngine implements TestEngine {
    @Override
    public String getId() {
        return "my-custom-test-engine";
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
        EngineDescriptor engineDescriptor = new EngineDescriptor(uniqueId, "My Custom Test Engine");
        discoveryRequest.getSelectorsByType(FileSelector.class).forEach(selector -> {
            try {
                engineDescriptor.addChild(new MyCustomTestDescriptor(engineDescriptor.getUniqueId(), selector.getFile().getName(), Files.readAllLines(selector.getFile().toPath())));
            } catch (IOException e) {
                throw new AssertionError("Should not happen!");
            }
        });
        return engineDescriptor;
    }

    @Override
    public void execute(ExecutionRequest request) {
        TestDescriptor engineDescriptor = request.getRootTestDescriptor();
        EngineExecutionListener listener = request.getEngineExecutionListener();

        listener.executionStarted(engineDescriptor);

        for (TestDescriptor testDescriptor : engineDescriptor.getChildren()) {
            // cast it to our own class
            MyCustomTestDescriptor descriptor = (MyCustomTestDescriptor) testDescriptor;
            listener.executionStarted(testDescriptor);
            // here you would add your super-complicated logic of how to actually run the test
            if (descriptor.getFileContent().get(0).equals("true")) {
                listener.executionFinished(testDescriptor, TestExecutionResult.successful());
            } else {
                listener.executionFinished(testDescriptor, TestExecutionResult.failed(new AssertionError("File content was incorrect.")));
            }
        }

        listener.executionFinished(engineDescriptor, TestExecutionResult.successful());
    }
}