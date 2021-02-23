package fr.uge.compiled;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ByteClassLoader extends ClassLoader {
    private final Map<String, byte[]> classData = new HashMap<>();

    public ByteClassLoader(String name, byte[] data) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(data);
        // Test data
        addClassData(name, data);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException, ClassFormatError {
        byte[] data = classData.get(name);
        if (data != null) {
            return defineClass(name, data, 0, data.length);
        } else {
            return super.findClass(name);
        }
    }

    public void addClassData(String name, byte[] data) {
        classData.put(name, data);
    }

    public void changeClassDataName(String oldName, String newName) {
        byte[] data = classData.get(oldName);
        classData.remove(oldName);
        classData.put(newName, data);
    }
}
