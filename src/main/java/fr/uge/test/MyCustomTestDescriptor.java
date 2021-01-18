package fr.uge.test;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

import java.util.List;

public class MyCustomTestDescriptor extends AbstractTestDescriptor {

    public MyCustomTestDescriptor(UniqueId engineId, String displayName, List<String> fileContent) {
        super(engineId.append("my-custom-test", displayName), displayName);
        this.fileContent = fileContent;
    }

    private final List<String> fileContent;

    @Override
    public Type getType() {
        // either TEST or CONTAINER
        return Type.TEST;
    }

    public List<String> getFileContent() {
        return fileContent;
    }

}