package fr.uge.compiler;

public class ByteClassLoader extends ClassLoader {
    private final byte[] fileData;
    private Class<?> loadedClass;
    private String className;

    public ByteClassLoader(byte[] fileData) {
        this.fileData = fileData;

    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        //no need to search class path, we already have byte code.
        loadedClass = defineClass(name, fileData, 0, fileData.length);
        return loadedClass;
    }

    public String getClassCanonicalName() {
        return loadedClass.getCanonicalName();
    }
}
