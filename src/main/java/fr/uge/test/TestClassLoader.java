package fr.uge.test;

public class TestClassLoader extends ClassLoader {
    @Override
    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> c = findLoadedClass(name);

        if (c == null) {
            try {
                c = findClass(name);
            } catch (ClassNotFoundException e) {
                c = super.loadClass(name, resolve);
                return c;
            }
        }

        if (resolve) {
            resolveClass(c);
        }

        return c;
    }


}
