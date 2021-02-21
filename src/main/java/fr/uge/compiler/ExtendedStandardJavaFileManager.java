package fr.uge.compiler;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import javax.tools.*;

/**
 * Created by trung on 5/3/15. Edited by turpid-monkey on 9/25/15, completed
 * support for multiple compile units.
 */
public class ExtendedStandardJavaFileManager extends
        ForwardingJavaFileManager<JavaFileManager> {

    private Map<String, byte[]> classBytes;

    protected ExtendedStandardJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
        classBytes = new HashMap<>();
    }

    public Map<String, byte[]> getClassBytes() {
        return classBytes;
    }

    @Override
    public void close() throws IOException {
        classBytes = null;
    }

//    @Override
//    public JavaFileObject getJavaFileForOutput(
//            JavaFileManager.Location location, String className,
//            JavaFileObject.Kind kind, FileObject sibling) throws IOException {
//
//        try {
//            CompiledCode innerClass = new CompiledCode(className);
//            System.out.println(className);
//            compiledCode.add(innerClass);
//            cl.addClassData(className, innerClass.getByteCode());
//            return innerClass;
//        } catch (Exception e) {
//            throw new RuntimeException(
//                    "Error while creating in-memory output file for " + className, e);
//        }
//    }

    @Override
    public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location,
                                               String className,
                                               JavaFileObject.Kind kind,
                                               FileObject sibling) throws IOException {
        if (kind == JavaFileObject.Kind.CLASS) {
            try {
                return new ClassOutputBuffer(className);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            return super.getJavaFileForOutput(location, className, kind, sibling);
        }
        return null;
    }

    private class ClassOutputBuffer extends SimpleJavaFileObject {
        private String name;

        ClassOutputBuffer(String name) throws URISyntaxException {
            super(new URI(name), Kind.CLASS);
            this.name = name;
        }

        @Override
        public OutputStream openOutputStream() {
            return new FilterOutputStream(new ByteArrayOutputStream()) {
                public void close() throws IOException {
                    out.close();
                    ByteArrayOutputStream bos = (ByteArrayOutputStream)out;
                    classBytes.put(name, bos.toByteArray());
                }
            };
        }
    }
}