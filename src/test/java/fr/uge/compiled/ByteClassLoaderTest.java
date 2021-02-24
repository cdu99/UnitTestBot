package fr.uge.compiled;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ByteClassLoaderTest {

    @Test
    public void should_load_class() throws IOException {
        byte[] helloThere = Files.newInputStream
                (Path.of("src\\test\\resources\\HelloThere.class")).readAllBytes();
        var classLoader = new ByteClassLoader("HelloThere", helloThere);

        assertDoesNotThrow(() -> classLoader.loadClass("HelloThere"));
    }

    @Test
    public void should_load_class_given_by_addClassData() throws IOException {
        byte[] helloThere = Files.newInputStream
                (Path.of("src\\test\\resources\\HelloThere.class")).readAllBytes();
        byte[] helloThereTest = Files.newInputStream
                (Path.of("src\\test\\resources\\HelloThereTest.class")).readAllBytes();
        var classLoader = new ByteClassLoader("HelloThere", helloThere);
        classLoader.addClassData("HelloThereTest", helloThereTest);

        assertAll(
                () -> assertDoesNotThrow(() -> classLoader.loadClass("HelloThere")),
                () -> assertDoesNotThrow(() -> classLoader.loadClass("HelloThereTest"))
        );
    }

    @Test
    public void should_throw_class_not_found_exception_when_trying_to_load_inexistant_file() throws IOException {
        byte[] helloThere = Files.newInputStream
                (Path.of("src\\test\\resources\\HelloThere.class")).readAllBytes();
        var classLoader = new ByteClassLoader("HelloThere", helloThere);

        assertThrows(
                ClassNotFoundException.class,
                () -> classLoader.loadClass("doesNotExist")
        );
    }
}