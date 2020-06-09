package com.apploidxxx.app.console;

import com.apploidxxx.app.console.impl.DefaultConsole;
import com.apploidxxx.app.console.impl.SystemConsole;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Arthur Kupriyanov on 09.06.2020
 */
class ConsoleManagerTest {

    @Test
    public void test () {

    }

    @SneakyThrows
    @Test
    public void createDefaultShell() {
        Console console = ConsoleManager.getConsoleByName(SystemConsole.class.getSimpleName());
        assertNotNull(console);
        assertEquals(SystemConsole.class, console.getClass());
    }

    @SneakyThrows
    @Test
    public void createShellWithCustomInOut() {
        Console console = ConsoleManager.getConsoleByName(DefaultConsole.class.getSimpleName());
        assertNotNull(console);
        assertEquals(DefaultConsole.class, console.getClass());
    }

}