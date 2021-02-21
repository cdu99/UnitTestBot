package fr.uge.compiler;

import java.util.HashMap;
import java.util.Map;

public class ByteClassLoader extends ClassLoader {
    private final Map<String, byte[]> classData = new HashMap<>();

    public ByteClassLoader(String name, byte[] data) {
        // Test file
        addClassData(name, data);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] data = classData.get(name);
        System.out.println(classData.toString());
        if (data != null) {
            return defineClass(name, data, 0, data.length);
        } else {
            return super.findClass(name);
        }
    }

    public void addClassData(String name, byte[] data) {
        classData.put(name, data);
    }

    public void deleteClassData(String name) {
        classData.remove(name);
    }

    public byte[] getClassDataForKey(String key) {
        return classData.get(key);
    }
}
