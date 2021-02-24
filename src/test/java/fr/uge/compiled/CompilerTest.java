package fr.uge.compiled;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CompilerTest {

    @Test
    public void should_compile() throws IOException {
        var compiler = new Compiler(new File("src\\test\\resources\\HelloThere.java"));

        Map<String, byte[]> result = compiler.compile();

        assertAll(
                () -> assertTrue(result.containsKey("HelloThere")),
                () -> assertNotNull(result.get("HelloThere"))
        );
    }

    @Test
    public void should_not_compile() throws IOException {
        var compiler = new Compiler(new File("src\\test\\resources\\WrongHelloThere.java"));

        Map<String, byte[]> result = compiler.compile();

        assertNull(result);
    }
}