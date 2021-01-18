package fr.uge;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    @Test
    @Tag("Q1")
    public void alwaysTrue() {
        assertTrue(true);
    }
}